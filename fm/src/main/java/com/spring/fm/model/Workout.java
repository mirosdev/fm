package com.spring.fm.model;

import com.fasterxml.uuid.Generators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Workout {

    @Id
    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(nullable = false)
    private UUID fmUserUuid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Embedded
    private Strength strength;

    @Embedded
    private Endurance endurance;

    @PrePersist
    public void generateAndDefaultsOnCreate() {
        this.uuid = Generators.timeBasedGenerator().generate();

        this.strength = this.type.equals("strength") ? this.strength : null;
        this.endurance = this.type.equals("endurance") ? this.endurance : null;
    }

    @PreUpdate
    public void setOnUpdate() {
        this.strength = this.type.equals("strength") ? this.strength : null;
        this.endurance = this.type.equals("endurance") ? this.endurance : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Workout)) return false;
        Workout workout = (Workout) o;
        return Objects.equals(getUuid(), workout.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }
}
