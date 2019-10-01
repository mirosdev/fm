package com.spring.fm.web;

import com.spring.fm.dto.MealDto;
import com.spring.fm.security.limit.RateLimit;
import com.spring.fm.service.interfaces.IListValidationErrorService;
import com.spring.fm.service.interfaces.IMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static com.spring.fm.security.FmAuthorities.Privileges.READ_PRIVILEGE;
import static com.spring.fm.security.FmAuthorities.Privileges.WRITE_PRIVILEGE;

@RestController
@RequestMapping("/api")
public class MealsController {

    private final IMealService iMealService;
    private final IListValidationErrorService iListValidationErrorService;

    @Autowired
    public MealsController(IMealService iMealService,
                           IListValidationErrorService iListValidationErrorService) {
        this.iMealService = iMealService;
        this.iListValidationErrorService = iListValidationErrorService;
    }

    @GetMapping("/meals/all")
    @PreAuthorize("hasAuthority('" + READ_PRIVILEGE + "')")
    @RateLimit(20)
    public ResponseEntity<List<MealDto>> findAll(Principal principal) {
        return new ResponseEntity<>(this.iMealService.findAllByFmUserUuid(UUID.fromString(principal.getName())),
                HttpStatus.OK);
    }

    @PostMapping("/meals/add")
    @PreAuthorize("hasAuthority('" + WRITE_PRIVILEGE + "')")
    @RateLimit(20)
    public ResponseEntity<?> addMeal(@Valid @RequestBody MealDto mealDto,
                                     BindingResult result,
                                     Principal principal) {
        ResponseEntity<?> errorList = iListValidationErrorService.listValidationService(result);
        if(errorList != null){
            return errorList;
        }
        return new ResponseEntity<>(this.iMealService.addMeal(mealDto, UUID.fromString(principal.getName())),
                HttpStatus.CREATED);
    }

    @DeleteMapping("/meals/delete/{uuid}")
    @PreAuthorize("hasAuthority('" + WRITE_PRIVILEGE + "')")
    @RateLimit(20)
    public ResponseEntity<Boolean> deleteMeal(@PathVariable UUID uuid,
                                        Principal principal) {
        if (uuid == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(this.iMealService.deleteMeal(uuid, UUID.fromString(principal.getName())),
                HttpStatus.OK);
    }

    @PutMapping("/meals/update")
    @PreAuthorize("hasAuthority('" + WRITE_PRIVILEGE + "')")
    @RateLimit(20)
    public ResponseEntity<?> updateMeal(@Valid @RequestBody MealDto mealDto,
                                        BindingResult result,
                                        Principal principal) {
        if (mealDto.getUuid() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        ResponseEntity<?> errorList = iListValidationErrorService.listValidationService(result);
        if(errorList != null){
            return errorList;
        }
        return new ResponseEntity<>(this.iMealService.updateMeal(mealDto, UUID.fromString(principal.getName())),
                HttpStatus.OK);
    }
}
