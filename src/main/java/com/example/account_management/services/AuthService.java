package com.example.account_management.services;

import com.example.account_management.entity.Account;
import com.example.account_management.entity.User;
import com.example.account_management.mapper.UserMapper;
import com.example.account_management.repository.UserRepository;
import com.example.account_management.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.AuthDto;
import org.openapitools.model.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RoleService roleService;
    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenUtils jwtTokenUtils;

    public ResponseEntity<UserDto> createUser(UserDto userDto) {
        if (userRepository.findByNickname(userDto.getNickname()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setNickname(userDto.getNickname());
        user.setFullname(userDto.getFullname());
        user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        user.setRoles(new ArrayList<>());
        user.getRoles().add(roleService.getUserRole());
        user.setAccounts(new ArrayList<>());
        user = userRepository.save(user);

        Account account = accountService.createAccount(user);
        user.getAccounts().add(account);
        user = userRepository.save(user);
        return ResponseEntity.ok(userMapper.modelToDto(user));
    }

    public ResponseEntity<AuthDto> loginUser(UserDto userDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getNickname(), userDto.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(userDto.getNickname());
        String token = jwtTokenUtils.generateToken(userDetails);
        AuthDto authDto = new AuthDto();
        authDto.setJwt(token);
        return ResponseEntity.ok(authDto);
    }
}
