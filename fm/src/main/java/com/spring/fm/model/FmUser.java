package com.spring.fm.model;

import com.fasterxml.uuid.Generators;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
public class FmUser {

    @Id
    @Column(nullable = false, unique = true, name = "USER_ID")
    private UUID uuid;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = { @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID") }, inverseJoinColumns = { @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID") })
//    @Column(nullable = false)
    private Collection<Role> roles;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .flatMap(role -> role.getPrivileges().stream()).map(p -> new SimpleGrantedAuthority(p.getName()))
                .collect(Collectors.toSet());
    }

    @PrePersist
    public void generateOnCreate() {
        this.uuid = Generators.timeBasedGenerator().generate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FmUser)) return false;
        FmUser fmUser = (FmUser) o;
        return Objects.equals(getUuid(), fmUser.getUuid()) &&
                Objects.equals(getEmail(), fmUser.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid(), getEmail());
    }
}
