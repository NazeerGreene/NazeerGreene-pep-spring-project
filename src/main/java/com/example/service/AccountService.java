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
        Account registeredAccount = repository.save(newAccount);

        return Optional.ofNullable(registeredAccount);
    }

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
     * Helper
     */
    public Optional<Account> findAccountByUsername(String username) {
        if (username == null) {
            throw new IllegalStateException("username cannot be null");
        }
        return Optional.ofNullable(repository.findByUsername(username));
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
