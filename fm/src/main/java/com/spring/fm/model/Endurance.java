package com.spring.fm.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Embeddable
@Getter
@Setter
public class Endurance {
    private Long distance;
    private Long duration;

    public static Endurance emptyEndurance() {
        Endurance endurance = new Endurance();
        endurance.setDistance(0L);
        endurance.setDuration(0L);
        return endurance;
    }
}
