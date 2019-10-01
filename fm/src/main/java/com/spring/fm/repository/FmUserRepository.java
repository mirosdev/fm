package com.spring.fm.repository;

import com.spring.fm.model.FmUser;
import com.spring.fm.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FmUserRepository extends JpaRepository<FmUser, UUID> {
    Optional<FmUser> findByUuid(UUID uuid);
//    Optional<FmUser> findByEmail(String email);

    @Query("SELECT u.roles FROM FmUser u WHERE u.uuid = ?1")
    Collection<Role> customFindUserRolesByUserUuid(UUID uuid);

    @Query("SELECT u.uuid FROM FmUser u WHERE u.email = ?1")
    Optional<UUID> customFindUserUuidByUserEmail(String email);

    Boolean existsByEmail(String email);
}
