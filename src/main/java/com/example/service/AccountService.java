package com.example.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;

@Service
public class AccountService {
    private final AccountRepository repository;

    @Autowired
    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    /*
     * Add a new account to the data store
     * @param newAccount the new account to store
     * @return Optional account if persisted to data store
     * 
     * username cannot be blank.
     * password cannot be blank, must be at least 4 characters
     */
    public Optional<Account> registerNewAccountFor(Account newAccount) {
        if (newAccount == null) {
            throw new IllegalArgumentException("Account object expected, no null values.");
        }

        // check service rules
        if (!isValidUsername(newAccount.getUsername()) ||
        !isValidPassword(newAccount.getPassword()) ||
        !usernameAvailable(newAccount.getUsername())) {
            return Optional.empty();
        }

        // we can assume its safe to register by now - Id ignored if present
        newAccount.setAccountId(null); // should not be present
        Account registeredAccount = repository.save(newAccount);

        return Optional.of(registeredAccount);
    }

    /*
     * Returns account if in data store
     * @param account the account to look for
     * @return Optional the account if present
     * 
     * username cannot be blank.
     * password cannot be blank, must be at least 4 characters     
     */
    public Optional<Account> verifyAccountExistsFor(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account object expected, no null values");
        }

        // check service rules
        if (!isValidUsername(account.getUsername()) ||
        !isValidPassword(account.getPassword())) {
            return Optional.empty();
        }

        // username and password should match data store
        Account foundAccount = repository.findByUsernameAndPassword(account.getUsername(), account.getPassword());

        return Optional.ofNullable(foundAccount);
    }

    /*
     * Find account according to username
     * @param username the username to look for
     * @return Optional the account if present
     */
    public Optional<Account> findAccountByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }
        return Optional.ofNullable(repository.findByUsername(username));
    }

    /*
     * Find account according to id
     * @param id the user id
     * @return Optional the account if present
     */
    public Optional<Account> findAccountById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        return repository.findById(id);
    }

    /*
     * Checks if password meets service rules
     * @param password the password to check
     * @return boolean True if service rules are met
     */
    private boolean isValidPassword(String password) {
        return password != null && password.length() > 3;

    }
    /*
     * Checks if username meets service rules
     * @param username the username to check
     * @return boolean True if service rules are met
     */
    private boolean isValidUsername(String username) {
        return username != null && username.length() > 0;
    }

    /*
     * Helper
     */
    private boolean usernameAvailable(String username) {
        return repository.findByUsername(username) == null;
    }

}
