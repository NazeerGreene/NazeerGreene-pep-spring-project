package com.example.controller;

import com.example.service.AccountService;

public class UserRegisterLoginController {

    private final AccountService service;

    public UserRegisterLoginController(AccountService service) {
        this.service = service;
    }

    public void registerUser() {
        // 409 username conflict
        // 400 otherwise

    }

    public void loginUser() {
        // login failure 401

    }
    
}
