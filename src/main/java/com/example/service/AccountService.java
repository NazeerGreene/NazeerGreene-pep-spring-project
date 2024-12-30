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

    public Optional<Account> registerNewUser(Account newAccount) {
        if (newAccount == null) {
            throw new IllegalArgumentException("Account object expected, no null values.");
        }

        // check service rules
        if (!isValidUsername(newAccount.getUsername()) ||
        !isValidPassword(newAccount.getPassword()) ||
        usernameTaken(newAccount.getUsername())) {
            return Optional.empty();
        }

        // we can assume its safe to register by now - Id ignored if present
        Account registeredAccount = repository.save(newAccount);

        return Optional.of(registeredAccount);
    }

    public Optional<Account> verifyExistingUser(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account object expected, no null values");
        }

        // check service rules
        if (!isValidUsername(account.getUsername()) ||
        !isValidPassword(account.getPassword())) {
            return Optional.empty();
        }

        // username and password should match data store
        Account verifiedAccount = repository.findByUsernameAndPassword(account.getUsername(), account.getPassword());

        if (verifiedAccount.getUsername().equals(account.getUsername()) &&
        verifiedAccount.getPassword().equals(account.getPassword())) {
            return Optional.of(verifiedAccount);
        }

        return Optional.empty();
    }

    /*
     * Helper
     */
    public Account findAccountByUsername(String username) {
        return repository.findByUsername(username);
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
        return username != null;
    }

    /*
     * Helper
     */
    private boolean usernameTaken(String username) {
        return repository.findByUsername(username) != null;
    }

}
