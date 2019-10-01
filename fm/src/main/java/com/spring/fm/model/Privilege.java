package com.spring.fm.model;

import com.fasterxml.uuid.Generators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

import static com.spring.fm.security.FmAuthorities.Privileges.*;

@Entity
@Getter
@Setter
public class Privilege {
    @Id
    @Column(name = "PRIV_ID")
    private UUID uuid;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "privileges")
    private Collection<Role> roles;

    @PrePersist
    public void generateOnCreate() {
        this.uuid = Generators.timeBasedGenerator().generate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Privilege)) return false;
        Privilege privilege = (Privilege) o;
        return name.equals(privilege.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public static Set<Privilege> userPrivilegeSet() {
        Privilege readPrivilege = new Privilege();
        readPrivilege.setName(READ_PRIVILEGE);
        Privilege writePrivilege = new Privilege();
        writePrivilege.setName(WRITE_PRIVILEGE);
        Set<Privilege> privileges = new HashSet<>();
        privileges.add(readPrivilege);
        privileges.add(writePrivilege);

        return privileges;
    }

    public static Set<Privilege> adminPrivilegeSet() {
        Privilege readPrivilege = new Privilege();
        readPrivilege.setName(READ_PRIVILEGE);
        Privilege writePrivilege = new Privilege();
        writePrivilege.setName(WRITE_PRIVILEGE);
        Privilege readMonitoringPrivilege = new Privilege();
        readMonitoringPrivilege.setName(READ_MONITORING_PRIVILEGE);
        Set<Privilege> privileges = new HashSet<>();
        privileges.add(readPrivilege);
        privileges.add(writePrivilege);
        privileges.add(readMonitoringPrivilege);

        return privileges;
    }
}
