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

    // ACCOUNT SERVICES -------------------------------

    /*
     * Registration controller
     * @param newAccount the JSON containing the info
     * @return Account the confirmed account credentials if service rules met, 200
     * 409 if username taken
     * 400 if account registration fails
     */
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

        if (registeredNewAccount.isEmpty()) { // some failure
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(registeredNewAccount.get());
    }

    /*
     * Login controller
     * @param returningAccount the JSON containing the username and password
     * @return Account with credentials confirming login if account already registered, 200
     * 401 if login fails
     * 400 if credentials missing
     */
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

    // MESSAGE SERVICES -------------------------------

    /*
     * Create a new message
     * @param newMessage containing the message details
     * @return Message the message details if service rules met, 200
     * 400 if message details or JSON is missing
     * 400 if postedBy is missing
     */
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

    /*
     * Return all messages in data store
     * @return List<Message> all the messages, 200 always
     */
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    /*
     * Return message according to message id
     * @param messageId the id
     * @return Message the requested message, 200
     * 200 if message is missing
     */
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable Integer messageId) {
        Optional<Message> message = messageService.getMessageById(messageId);
        if (message.isEmpty()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(message.get());
    }

    /*
     * Update message according to id
     * @param messageId the id
     * @param Message the JSON containing message details
     * @return Integer the number of messages updated (1), 200
     * 400 if no messages updated
     * 
     * Only message text can be updated.
     */
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<Integer> updateMessage(@PathVariable Integer messageId, @RequestBody Message modifiedMessage) {
        modifiedMessage.setMessageId(messageId);

        int updatedRows = messageService.updateMessage(modifiedMessage);

        if (updatedRows == 0) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(updatedRows);
    }

    /*
     * Delete message according to id
     * @param messageId the id
     * @return Integer the number of messages deleted (1), 200 always
     */
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Integer> deleteMessage(@PathVariable Integer messageId) {
        int deletedRows = messageService.deleteMessageById(messageId);
        if (deletedRows < 1) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(deletedRows);
    }

    /*
     * Get all messages from user according to user id
     * @param accountId the user id
     * @return List<Message> the messages from the user, 200 always
     */
    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesFromUserId(@PathVariable Integer accountId) {
        return ResponseEntity.ok(messageService.getMessagesFromUserId(accountId));
    }
}
