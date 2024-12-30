package com.example.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.service.AccountService;
import com.example.service.MessageService;


@RestController
public class SocialMediaController {
    private final MessageService messageService;
    private final AccountService accountService;
   
    @Autowired
    SocialMediaController(AccountService accountService, MessageService messageService) {
       this.accountService = accountService;
       this.messageService = messageService;
    }

    @PostMapping("/register")
    public ResponseEntity<Account> registerUser(@RequestBody Account newAccount) {
        Optional<Account> accountWithTakenUsername = accountService.findAccountByUsername(newAccount.getUsername());

        if (accountWithTakenUsername.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Optional<Account> registeredNewAccount = accountService.registerNewAccountFor(newAccount);

        if (registeredNewAccount.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.of(registeredNewAccount);
    }

    @PostMapping("/login")
    public ResponseEntity<Account> loginUser(@RequestBody Account returningAccount) {
        Optional<Account> verifiedAccount = accountService.verifyAccountExistsFor(returningAccount);

        if (verifiedAccount.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.of(verifiedAccount);

    }

    public void createMessage() {
        // return 400 if bad
    }

    public void getAllMessages() {

    }

    public void getMessageById() {
        // always 200; empty if no message
    }

    public void updateMessage() {
        //. 400 on bad; updated rows otherwise
        
    }

    public void deleteMessage() {
        // always 200; either number of rows deleted or empty
    }

    public void getMessagesFromUserId() {
        // always 200; empty or list
    }
}
