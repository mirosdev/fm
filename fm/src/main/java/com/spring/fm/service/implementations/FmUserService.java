package com.spring.fm.service.implementations;

import com.spring.fm.dto.FmUserDto;
import com.spring.fm.mapper.IDtoMapper;
import com.spring.fm.model.FmUser;
import com.spring.fm.model.Privilege;
import com.spring.fm.model.Role;
import com.spring.fm.ops.counters.UserCounterService;
import com.spring.fm.repository.FmUserRepository;
import com.spring.fm.repository.PrivilegeRepository;
import com.spring.fm.repository.RoleRepository;
import com.spring.fm.security.UserRegistrationDto;
import com.spring.fm.service.interfaces.IFmUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

@Service
public class FmUserService implements IFmUserService {

    private final IDtoMapper iDtoMapper;
    private final FmUserRepository fmUserRepository;
    private final PrivilegeRepository privilegeRepository;
    private final RoleRepository roleRepository;
    private final UserCounterService userCounterService;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public FmUserService(IDtoMapper iDtoMapper,
                         FmUserRepository fmUserRepository,
                         PrivilegeRepository privilegeRepository,
                         RoleRepository roleRepository,
                         UserCounterService userCounterService) {
        this.iDtoMapper = iDtoMapper;
        this.fmUserRepository = fmUserRepository;
        this.privilegeRepository = privilegeRepository;
        this.roleRepository = roleRepository;
        this.userCounterService = userCounterService;
    }

    @Override
    public FmUserDto registerNewUser(UserRegistrationDto userRegistrationDto) {
        FmUser fmUser = iDtoMapper.userRegistrationDtoToFmUser(userRegistrationDto);
        Set<Role> fmUserRoles = getUserRoles();
        fmUser.setRoles(fmUserRoles);

        try {
            FmUser saved = fmUserRepository.save(fmUser);
            this.userCounterService.countUserRegister();
            return iDtoMapper.fmUserToFmUserDto(saved);
        } catch (Exception e) {
            logger.error(e.toString().concat(Arrays.asList(e.getStackTrace()).toString()));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email: " + userRegistrationDto.getEmail() + " already exists");
        }
    }

    private Set<Role> getUserRoles() {
        Set<Privilege> userPrivileges = Privilege.userPrivilegeSet();
        Set<Role> userRoles = Role.singleUserRole(userPrivileges);

        try {
            privilegeRepository.saveAll(userPrivileges);
            roleRepository.saveAll(userRoles);
        } catch (Exception e) {
            logger.error(e.toString().concat(Arrays.asList(e.getStackTrace()).toString()));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return userRoles;
    }

    @Override
    public FmUserDto findFmUserByUUID(UUID uuid) {
        return this.iDtoMapper
                .fmUserToFmUserDto(fmUserRepository
                        .findById(uuid)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public FmUser findByUuid(UUID uuid) {
        return this.fmUserRepository.findByUuid(uuid)
                .orElse(null);
    }

    @Override
    public UUID findUuidByEmail(String email) {
        return this.fmUserRepository.customFindUserUuidByUserEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.valueOf(401), "Invalid email or password"));
    }
}
