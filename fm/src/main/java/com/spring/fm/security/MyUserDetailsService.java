package com.spring.fm.security;

import com.google.common.base.Preconditions;
import com.spring.fm.model.FmUser;
import com.spring.fm.service.interfaces.IFmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private IFmUserService iFmUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Preconditions.checkNotNull(username);

        final FmUser fmUser = iFmUserService.findByUuid(UUID.fromString(username));
        if (fmUser == null) {
            throw new UsernameNotFoundException(username);
        }
        return new org.springframework.security.core.userdetails.User(fmUser.getUuid().toString(), fmUser.getPassword(), fmUser.getAuthorities());
    }
}
