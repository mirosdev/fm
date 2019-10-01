package com.spring.fm.model;

import com.fasterxml.uuid.Generators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.spring.fm.security.FmAuthorities.Privileges.READ_MONITORING_PRIVILEGE;
import static com.spring.fm.security.FmAuthorities.Roles.ROLE_ADMIN;
import static com.spring.fm.security.FmAuthorities.Roles.ROLE_USER;

@Entity
@Getter
@Setter
public class Role {
    @Id
    @Column(name = "ROLE_ID")
    private UUID uuid;

    @Column(nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = { @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID") }, inverseJoinColumns = { @JoinColumn(name = "PRIV_ID", referencedColumnName = "PRIV_ID") })
    private Collection<Privilege> privileges;

    @PrePersist
    public void generateOnCreate() {
        this.uuid = Generators.timeBasedGenerator().generate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return name.equals(role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public static Set<Role> singleUserRole(Set<Privilege> privileges) {
        Role fmUserRole = new Role();
        fmUserRole.setName(ROLE_USER);
        fmUserRole.setPrivileges(privileges.stream()
                .filter(privilege -> !privilege.getName().equals(READ_MONITORING_PRIVILEGE))
                .collect(Collectors.toSet()));
        Set<Role> fmUserRoles = new HashSet<>();
        fmUserRoles.add(fmUserRole);

        return fmUserRoles;
    }

    public static Set<Role> singleAdminRole(Set<Privilege> privileges) {
        Role fmAdminRole = new Role();
        fmAdminRole.setName(ROLE_ADMIN);
        fmAdminRole.setPrivileges(privileges);
        Set<Role> fmUserAdminRoles = new HashSet<>();
        fmUserAdminRoles.add(fmAdminRole);

        return fmUserAdminRoles;
    }
}
