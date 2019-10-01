package com.spring.fm.web;

import com.spring.fm.service.interfaces.IFmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final IFmUserService IFmUserService;

    @Autowired
    public UserController(IFmUserService IFmUserService) {
        this.IFmUserService = IFmUserService;
    }
}
