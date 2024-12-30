package com.example.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.entity.Message;
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
        if (newAccount == null || newAccount.getUsername() == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Account> accountWithTakenUsername = accountService.findAccountByUsername(newAccount.getUsername());

        if (accountWithTakenUsername.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Optional<Account> registeredNewAccount = accountService.registerNewAccountFor(newAccount);

        if (registeredNewAccount.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(registeredNewAccount.get());
    }

    @PostMapping("/login")
    public ResponseEntity<Account> loginUser(@RequestBody Account returningAccount) {
        if (returningAccount == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<Account> verifiedAccount = accountService.verifyAccountExistsFor(returningAccount);

        if (verifiedAccount.isEmpty()) { // some failure
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(verifiedAccount.get());
    }

    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message newMessage) {
        if (newMessage == null) {
            return ResponseEntity.badRequest().build();
        }

        // check to see if account is already in data store
        Integer id = newMessage.getPostedBy();
        Optional<Account> postedBy = accountService.findAccountById(id);

        if (postedBy.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // add message to data store
        Optional<Message> addedMessage = messageService.createMessage(newMessage);

        if (addedMessage.isEmpty()) { // some failure
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(addedMessage.get());
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable Integer messageId) {
        Long id = Long.valueOf(messageId);
        Optional<Message> message = messageService.getMessageById(id);
        if (message.isEmpty()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(message.get());
    }

    @PatchMapping("/message/{messageId}")
    public ResponseEntity<Integer> updateMessage(@PathVariable Integer messageId, @RequestBody Message modifiedMessage) {
        modifiedMessage.setMessageId(messageId);

        int updatedMessage = messageService.updateMessage(modifiedMessage);

        if (updatedMessage < 1) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(1);
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Integer> deleteMessage(@PathVariable Integer messageId) {
        Long id = Long.valueOf(messageId);
        int deletedRows = messageService.deleteMessageById(id);
        if (deletedRows < 1) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(deletedRows);
    }

    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesFromUserId(@PathVariable Integer accountId) {
        return ResponseEntity.ok(messageService.getMessagesFromUserId(accountId));
    }
}
