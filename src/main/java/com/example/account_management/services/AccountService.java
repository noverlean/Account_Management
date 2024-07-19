package com.example.account_management.services;

import com.example.account_management.entity.Account;
import com.example.account_management.entity.User;
import com.example.account_management.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.UserDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        User user = userRepository.findByNickname(nickname).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", nickname)
        ));
        return new org.springframework.security.core.userdetails.User(
                user.getNickname(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    public ResponseEntity<User> createUser(UserDto userDto) {
        if (userRepository.findByNickname(userDto.getNickname()).isPresent())
        {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }



        User user = new User();
        user.setNickname(userDto.getNickname());
        user.setFulname(userDto.getFulname());
        user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        user.setAccounts();
        user.setRoles(List.of(roleService.getUserRole()));
        return userRepository.save(user);
    }
//
//    public User getUser(String email)
//    {
//        return findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
//                String.format("Пользователь '%s' не найден", email)
//        ));
//    }
}
