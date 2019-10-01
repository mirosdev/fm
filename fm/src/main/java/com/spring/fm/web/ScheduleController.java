package com.spring.fm.web;

import com.spring.fm.dto.ScheduleItemPayload;
import com.spring.fm.security.limit.RateLimit;
import com.spring.fm.service.interfaces.IListValidationErrorService;
import com.spring.fm.service.interfaces.IScheduleItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.UUID;

import static com.spring.fm.security.FmAuthorities.Privileges.READ_PRIVILEGE;
import static com.spring.fm.security.FmAuthorities.Privileges.WRITE_PRIVILEGE;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final IScheduleItemService iScheduleItemService;
    private final IListValidationErrorService iListValidationErrorService;

    @Autowired
    public ScheduleController(IScheduleItemService iScheduleItemService,
                              IListValidationErrorService iListValidationErrorService) {
        this.iScheduleItemService = iScheduleItemService;
        this.iListValidationErrorService = iListValidationErrorService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('" + WRITE_PRIVILEGE + "')")
    @RateLimit(20)
    public ResponseEntity<?> create(@Valid @RequestBody ScheduleItemPayload scheduleItemPayload,
                                    BindingResult result,
                                    Principal principal) {
        if ((scheduleItemPayload.getWorkouts() == null || scheduleItemPayload.getWorkouts().size() == 0) &&
                (scheduleItemPayload.getMeals() == null || scheduleItemPayload.getMeals().size() == 0)) {
            return ResponseEntity.ok("");
        }
        ResponseEntity<?> errorList = iListValidationErrorService.listValidationService(result);
        if(errorList != null){
            return errorList;
        }
        return new ResponseEntity<>(this.iScheduleItemService.create(scheduleItemPayload, UUID.fromString(principal.getName())),
                HttpStatus.CREATED);
    }

    @PutMapping("/update/{uuid}")
    @PreAuthorize("hasAuthority('" + WRITE_PRIVILEGE + "')")
    @RateLimit(20)
    public ResponseEntity<?> update(@Valid @RequestBody ScheduleItemPayload scheduleItemPayload,
                                    @PathVariable UUID uuid,
                                    BindingResult result,
                                    Principal principal) {
        if (uuid == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        ResponseEntity<?> errorList = iListValidationErrorService.listValidationService(result);
        if(errorList != null){
            return errorList;
        }
        return new ResponseEntity<>(this.iScheduleItemService.update(scheduleItemPayload, uuid, UUID.fromString(principal.getName())),
                HttpStatus.OK);
    }

    @GetMapping("/{startAt}/{endAt}")
    @PreAuthorize("hasAuthority('" + READ_PRIVILEGE + "')")
    @RateLimit(20)
    public ResponseEntity<?> findBetweenTimestamps(@PathVariable String startAt,
                                                   @PathVariable String endAt,
                                                   Principal principal) {
        if (startAt == null || endAt == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(this.iScheduleItemService.findBetweenTimestamps(startAt, endAt, UUID.fromString(principal.getName())),
                HttpStatus.OK);
    }
}
