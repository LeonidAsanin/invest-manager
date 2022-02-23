package org.lennardjones.investmanager.services;

import org.lennardjones.investmanager.entities.Account;
import org.lennardjones.investmanager.repositories.AccountRepository;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

/**
 * Service for convenient working with {@link org.lennardjones.investmanager.entities.Account Account} objects.
 *
 * @since 1.0
 * @author lennardjones
 */
@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Long getUserIdByUsername(String username) {
        var account = new Account();
        account.setUsername(username);
        account = accountRepository.findOne(Example.of(account)).orElseThrow();
        return account.getId();
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id).orElseThrow();
    }

    public boolean existsByUsername(String username) {
        return accountRepository.existsByUsernameIgnoreCase(username);
    }

    public boolean exists(Account account) {
        return accountRepository.exists(Example.of(account));
    }

    public void registerNewAccount(Account account) {
        accountRepository.save(account);
    }
}
