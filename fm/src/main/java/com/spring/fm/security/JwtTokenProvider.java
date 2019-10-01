package com.spring.fm.security;

import com.spring.fm.model.Role;
import com.spring.fm.repository.FmUserRepository;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.spring.fm.security.SecurityConstants.*;

@Component
public class JwtTokenProvider {
    private final FmUserRepository fmUserRepository;

    @Autowired
    public JwtTokenProvider(FmUserRepository fmUserRepository) {
        this.fmUserRepository = fmUserRepository;
    }

    public String generateToken(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Collection<Role> userRoles = getUserRoles(userDetails.getUsername());

        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        Map<String, Object> claims = new HashMap<>();
        claims.put("uuid", userDetails.getUsername());
        claims.put("authorities", userDetails.getAuthorities());
        claims.put("roles", userRoles);

        return Jwts.builder()
//                .setSubject(userUuid)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.RS512, EncryptionUtil.getPrivateKey())
                .compact();
    }

    boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(EncryptionUtil.getPublicKey()).parseClaimsJws(token);
            return true;
        }catch (SignatureException ex){
            System.out.println("Invalid JWT Signature");
        }catch (MalformedJwtException ex){
            System.out.println("Invalid JWT Token");
        }catch (ExpiredJwtException ex){
            System.out.println("Expired JWT Token");
        }catch (UnsupportedJwtException ex){
            System.out.println("Unsupported JWT Token");
        }catch (IllegalArgumentException ex){
            System.out.println("JWT claims string is empty");
        }

        return false;
    }

    String getUserUuidFromJWT(String token){
        Claims claims = Jwts.parser().setSigningKey(EncryptionUtil.getPublicKey()).parseClaimsJws(token).getBody();
        return (String)claims.get("uuid");
    }

    private Collection<Role> getUserRoles(String username) {
        Collection<Role> userRoles = this.fmUserRepository.customFindUserRolesByUserUuid(UUID.fromString(username));
        if (userRoles != null && userRoles.size() > 0) {
            for (Role role : userRoles) {
                role.setPrivileges(null);
            }
        }
        return userRoles;
    }
}
