package com.spring.fm.web;

import com.spring.fm.dto.WorkoutDto;
import com.spring.fm.security.limit.RateLimit;
import com.spring.fm.service.interfaces.IListValidationErrorService;
import com.spring.fm.service.interfaces.IWorkoutService;
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
public class WorkoutsController {
    private final IWorkoutService iWorkoutService;
    private final IListValidationErrorService iListValidationErrorService;

    @Autowired
    public WorkoutsController(IWorkoutService iWorkoutService,
                              IListValidationErrorService iListValidationErrorService) {
        this.iWorkoutService = iWorkoutService;
        this.iListValidationErrorService = iListValidationErrorService;
    }

    @GetMapping("/workouts/all")
    @PreAuthorize("hasAuthority('" + READ_PRIVILEGE + "')")
    @RateLimit(20)
    public ResponseEntity<List<WorkoutDto>> findAll(Principal principal) {
        return new ResponseEntity<>(this.iWorkoutService.findAllByFmUserUuid(UUID.fromString(principal.getName())),
                HttpStatus.OK);
    }

    @PostMapping("/workouts/add")
    @PreAuthorize("hasAuthority('" + WRITE_PRIVILEGE + "')")
    @RateLimit(20)
    public ResponseEntity<?> addWorkout(@Valid @RequestBody WorkoutDto workoutDto,
                                        BindingResult result,
                                        Principal principal) {
        ResponseEntity<?> errorList = iListValidationErrorService.listValidationService(result);
        if(errorList != null){
            return errorList;
        }
        return new ResponseEntity<>(this.iWorkoutService.addWorkout(workoutDto, UUID.fromString(principal.getName())),
                HttpStatus.CREATED);
    }

    @DeleteMapping("/workouts/delete/{uuid}")
    @PreAuthorize("hasAuthority('" + WRITE_PRIVILEGE + "')")
    @RateLimit(20)
    public ResponseEntity<Boolean> deleteWorkout(@PathVariable UUID uuid,
                                                 Principal principal) {
        if (uuid == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(this.iWorkoutService.deleteWorkout(uuid, UUID.fromString(principal.getName())),
                HttpStatus.OK);
    }

    @PutMapping("/workouts/update")
    @PreAuthorize("hasAuthority('" + WRITE_PRIVILEGE + "')")
    @RateLimit(20)
    public ResponseEntity<?> updateWorkout(@Valid @RequestBody WorkoutDto workoutDto,
                                           BindingResult result,
                                           Principal principal) {
        if (workoutDto.getUuid() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (workoutDto.getType().equals("strength")) {
            if (workoutDto.getStrength() == null ||
                    workoutDto.getStrength().getReps() == null ||
                    workoutDto.getStrength().getSets() == null ||
                    workoutDto.getStrength().getWeight() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }
        if (workoutDto.getType().equals("endurance")) {
            if (workoutDto.getEndurance() == null ||
                    workoutDto.getEndurance().getDistance() == null ||
                    workoutDto.getEndurance().getDuration() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }
        ResponseEntity<?> errorList = iListValidationErrorService.listValidationService(result);
        if(errorList != null){
            return errorList;
        }
        return new ResponseEntity<>(this.iWorkoutService.updateWorkout(workoutDto, UUID.fromString(principal.getName())),
                HttpStatus.OK);
    }
}
