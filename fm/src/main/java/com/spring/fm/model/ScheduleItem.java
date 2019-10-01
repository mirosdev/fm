package com.spring.fm.model;

import com.fasterxml.uuid.Generators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
public class ScheduleItem {

    @Id
    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(nullable = false)
    private UUID fmUserUuid;

    @Column(nullable = false)
    private String timestamp;

    @Column(nullable = false)
    private String section;

    @ElementCollection
    private Set<UUID> mealUuids;

    @ElementCollection
    private Set<UUID> workoutUuids;

    @PrePersist
    public void generateOnCreate() {
        this.uuid = Generators.timeBasedGenerator().generate();
    }
}
