package com.spring.fm.service.interfaces;

import com.spring.fm.dto.FmUserDto;
import com.spring.fm.model.FmUser;
import com.spring.fm.security.UserRegistrationDto;

import java.util.UUID;

public interface IFmUserService {
    FmUserDto registerNewUser(UserRegistrationDto userRegistrationDto);
    FmUserDto findFmUserByUUID(UUID uuid);
    FmUser findByUuid(UUID uuid);
    UUID findUuidByEmail(String email);
}
