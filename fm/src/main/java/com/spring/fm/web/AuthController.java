package com.spring.fm.web;

import com.spring.fm.model.FmUser;
import com.spring.fm.security.*;
import com.spring.fm.security.limit.RateLimit;
import com.spring.fm.service.interfaces.IFmUserService;
import com.spring.fm.service.interfaces.IListValidationErrorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;

import javax.validation.Valid;

import java.security.Principal;
import java.util.Arrays;
import java.util.UUID;

import static com.spring.fm.security.SecurityConstants.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final IFmUserService iFmUserService;
    private final UserDetailsService userDetailsService;
    private final IListValidationErrorService iListValidationErrorService;
    private final PasswordEncoder passwordEncoder;
    private Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          IFmUserService iFmUserService,
                          UserDetailsService userDetailsService,
                          IListValidationErrorService iListValidationErrorService,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.iFmUserService = iFmUserService;
        this.userDetailsService = userDetailsService;
        this.iListValidationErrorService = iListValidationErrorService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    @RateLimit(20)
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result){

        ResponseEntity<?> errorList = iListValidationErrorService.listValidationService(result);
        if(errorList != null){
            return errorList;
        }

        UUID fmUserUuid = this.iFmUserService.findUuidByEmail(loginRequest.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        fmUserUuid.toString(),
                        loginRequest.getPassword()
                ));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = TOKEN_PREFIX + jwtTokenProvider.generateToken(authentication);

        return new ResponseEntity<>(new JwtLoginSuccessResponse(jwt), HttpStatus.OK);
    }

    @PostMapping("/register")
    @RateLimit(20)
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto, BindingResult result) {

        if (userRegistrationDto == null ||
                userRegistrationDto.getConfirmPassword() == null ||
                userRegistrationDto.getPassword() == null ||
                userRegistrationDto.getEmail() == null ||
                userRegistrationDto.getConfirmPassword().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        ResponseEntity<?> errorList = iListValidationErrorService.listValidationService(result);
        if(errorList != null){
            return errorList;
        }

        if (userRegistrationDto.getPassword().length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters.");
        }

        if (!userRegistrationDto.getPassword().equals(userRegistrationDto.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password and confirm password fields must match");
        }

        userRegistrationDto.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));

        return new ResponseEntity<>(iFmUserService.registerNewUser(userRegistrationDto), HttpStatus.CREATED);
    }

    @PostMapping("/google/login")
    @RateLimit(20)
    public ResponseEntity<?> googleLogin(@Valid @RequestBody GoogleLoginRequest googleLoginRequest, BindingResult result) {

        ResponseEntity<?> errorList = iListValidationErrorService.listValidationService(result);
        if(errorList != null){
            return errorList;
        }

        RestTemplate restTemplate = new RestTemplate();
        String googleTokenVerifyResourceUrl = GOOGLE_TOKEN_VERIFY_RESOURCE_URL.concat(googleLoginRequest.getToken());

        try {
            // Throws Internal Server Error, message: "400 Bad Request"
            GoogleLoginResponse response = restTemplate
                    .getForObject(googleTokenVerifyResourceUrl, GoogleLoginResponse.class);

            if (response != null) {
                if (response.getIssued_to().equals(GOOGLE_CLIENT_ID)) {
                    try {

                        UUID fmUserUuid = this.iFmUserService.findUuidByEmail(response.getEmail());

                        Authentication authentication =
                                new UsernamePasswordAuthenticationToken(this.userDetailsService.loadUserByUsername(fmUserUuid.toString()), null);

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        String jwt = TOKEN_PREFIX + jwtTokenProvider.generateToken(authentication);

                        return new ResponseEntity<>(new JwtLoginSuccessResponse(jwt), HttpStatus.OK);
                    } catch (Exception e) {
                        logger.error(Arrays.asList(e.getStackTrace()).toString());
                        return new ResponseEntity<>("Couldn't find your account. There's no account for that email. Try logging in with a different email.",
                                HttpStatus.UNAUTHORIZED);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            logger.error(Arrays.asList(e.getStackTrace()).toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/role/test")
    @PreAuthorize("hasAuthority('WRITE_PRIVILEGE') or hasAuthority('READ_PRIVILEGE')")
    @RateLimit(20)
    public ResponseEntity<?> testingRoles(Principal principal) {
        return new ResponseEntity<>(principal, HttpStatus.OK);
    }
}
