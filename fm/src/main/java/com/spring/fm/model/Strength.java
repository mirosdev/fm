package com.spring.fm.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Embeddable
@Getter
@Setter
public class Strength {
    private Long reps;
    private Long sets;
    private Long weight;

    public static Strength emptyStrength() {
        Strength strength = new Strength();
        strength.setReps(0L);
        strength.setSets(0L);
        strength.setWeight(0L);
        return strength;
    }
}
