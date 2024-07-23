package com.example.account_management.services;

import com.example.account_management.entity.Account;
import com.example.account_management.entity.User;
import com.example.account_management.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public Account createAccount(User user) {
        Account account = new Account();
        account.setAmount(1L);
        account.setOwner(user);
        account.setIsBlocked(false);

        return accountRepository.save(account);
    }
}
