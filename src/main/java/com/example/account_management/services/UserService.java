package com.example.account_management.services;

import com.example.account_management.entity.Account;
import com.example.account_management.entity.User;
import com.example.account_management.mapper.AccountMapper;
import com.example.account_management.mapper.UserMapper;
import com.example.account_management.repository.AccountRepository;
import com.example.account_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.AccountDto;
import org.openapitools.model.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final UserMapper userMapper;
    private final AccountMapper accountMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByNickname(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", username)
        ));
        return new org.springframework.security.core.userdetails.User(
                user.getNickname(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    public User getUser(String nickname)
    {
        return userRepository.findByNickname(nickname).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", nickname)
        ));
    }

    @Transactional
    public ResponseEntity<UserDto> addAccountAmount(String nickname, Integer addAmount, Integer accountId)
    {
        User user = userRepository.findByNickname(nickname).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", nickname)
        ));
        Optional<Account> accountOpt = user.getAccounts().stream()
                .filter(account -> account.getId().equals(accountId))
                .findAny();
        if (accountOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Account account = accountOpt.get();
        if (account.getIsBlocked()) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        account.setAmount(account.getAmount() + addAmount);
        accountRepository.save(account);
        return ResponseEntity.ok(userMapper.modelToDto(userRepository.save(user)));
    }

    @Transactional
    public ResponseEntity<UserDto> withdrawAccountAmount(String nickname, Integer withdrawAmount, Integer accountId)
    {
        User user = userRepository.findByNickname(nickname).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", nickname)
        ));
        Optional<Account> accountOpt = user.getAccounts().stream()
                .filter(account -> account.getId().equals(accountId))
                .findAny();

        if (accountOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Account account = accountOpt.get();
        if (account.getIsBlocked()) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        if (account.getAmount() < withdrawAmount)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        account.setAmount(account.getAmount() - withdrawAmount);
        accountRepository.save(account);
        return ResponseEntity.ok(userMapper.modelToDto(userRepository.save(user)));
    }

    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        List<AccountDto> accountDtos = accountRepository.findAllByOrderByIdDesc()
                .stream()
                .map(accountMapper::modelToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(accountDtos);
    }

    public ResponseEntity<List<AccountDto>> blockAccount(Integer accountId) {
        return changeBlockState(accountId, true);
    }

    public ResponseEntity<List<AccountDto>> unblockAccount(Integer accountId) {
        return changeBlockState(accountId, false);
    }

    private ResponseEntity<List<AccountDto>> changeBlockState(Integer accountId, Boolean newState)
    {
        Optional<Account> accountOct = accountRepository.findById(accountId);
        if (accountOct.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Account account = accountOct.get();
        if (account.getIsBlocked() == newState) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        account.setIsBlocked(newState);
        accountRepository.save(account);
        return getAllAccounts();
    }
}
