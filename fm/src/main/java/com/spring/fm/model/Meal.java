package com.spring.fm.model;

import com.fasterxml.uuid.Generators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
public class Meal {

    @Id
    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(nullable = false)
    private UUID fmUserUuid;

    @Column(nullable = false)
    private String name;

    @ElementCollection
    private List<String> ingredients;

    @PrePersist
    public void generateAndDefaultsOnCreate() {
        this.uuid = Generators.timeBasedGenerator().generate();

        if (this.ingredients != null && this.ingredients.size() == 0) {
            this.ingredients = null;
        }
    }

    @PreUpdate
    public void defaultsOnUpdate() {
        if (this.ingredients != null && this.ingredients.size() == 0) {
            this.ingredients = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Meal)) return false;
        Meal meal = (Meal) o;
        return Objects.equals(getUuid(), meal.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }
}
