package com.example.account_management.controller;

import com.example.account_management.mapper.UserMapper;
import com.example.account_management.services.AuthService;
import com.example.account_management.services.UserService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.UsersApi;
import org.openapitools.model.AccountDto;
import org.openapitools.model.AuthDto;
import org.openapitools.model.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin(origins = "/")
@RequiredArgsConstructor
public class DefaultController implements UsersApi {
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthService authService;

    @Override
    public ResponseEntity<UserDto> createUser(UserDto userDto) {
        System.out.println(userDto);
        return authService.createUser(userDto);
    }

    @Override
    public ResponseEntity<AuthDto> loginUser(UserDto userDto) {
        System.out.println(userDto);
        return authService.loginUser(userDto);
    }

    @GetMapping("/users/fetch")
    public ResponseEntity<UserDto> getUser(Principal principal) {
        UserDto userDto = userMapper.modelToDto(userService.getUser(principal.getName()));
        System.out.println(userDto);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/users/accounts/{accountId}/add/{addAmount}")
    public ResponseEntity<UserDto> addAccountAmount(@PathVariable Integer accountId, @PathVariable Integer addAmount, Principal principal) throws Exception {
        return userService.addAccountAmount(principal.getName(), addAmount, accountId);
    }

    @PostMapping("/users/accounts/{accountId}/withdraw/{withdrawAmount}")
    public ResponseEntity<UserDto> withdrawAccountAmount(@PathVariable Integer accountId, @PathVariable Integer withdrawAmount, Principal principal) throws Exception {
        return userService.withdrawAccountAmount(principal.getName(), withdrawAmount, accountId);
    }

    @Override
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        return userService.getAllAccounts();
    }

    @Override
    public ResponseEntity<List<AccountDto>> blockAccount(Integer accountId) {
        return userService.blockAccount(accountId);
    }

    @Override
    public ResponseEntity<List<AccountDto>> unblockAccount(Integer accountId) {
        return userService.unblockAccount(accountId);
    }
}
