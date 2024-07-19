package unicore.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unicore.api.dto.RegistrationCredentials;
import unicore.api.dto.UserEmailCodeDto;
import unicore.api.entities.Environment;
import unicore.api.entities.User;
import unicore.api.mappers.UserMapper;
import unicore.api.repository.UserRepository;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Optional<User> findByEmail(String username) {
        return userRepository.findByEmail(username);
    }

    //there using username like email for care working of auth hidden code
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", email)
        ));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    public User createNewUser(RegistrationCredentials registrationCredentials) {
        User user = new User();
        user.setEmail(registrationCredentials.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(registrationCredentials.getPassword()));
        user.setRoles(List.of(roleService.getUserRole()));
        return userRepository.save(user);
    }

    public User getUser(String email)
    {
        return findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", email)
        ));
    }

    public User linkTo(String email, Environment environment) {
        User user = getUser(email);
        user.setEnvironment(environment);
        return userRepository.save(user);
    }

    public List<User> getAllUsers(String email) {
        return (List<User>) userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseGet(null);
    }

    public ResponseEntity<InputStreamResource> downloadUser(Long id) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(getUserById(id));

        ByteArrayInputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=data.txt")
                .body(new InputStreamResource(stream));
    }


    /* public ResponseEntity<?> setAsAdmin(String username) {
        try {
            User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                    String.format("Пользователь '%s' не найден", username)
            ));

            DBRequestUtils dBRequestUtils = new DBRequestUtils();
            dBRequestUtils.setAsAdmin(user.getId());
            return ResponseEntity.ok("ok");
        }
        catch (Exception e) {
            return new ResponseEntity<>(new AppError(HttpStatus.PROCESSING.value(), "Ошибка при попытке получения взаимных лайков пользователя!"), HttpStatus.PROCESSING);
        }
    }


    public ResponseEntity<?> setAsUser(String username) {
        try {
            User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                    String.format("Пользователь '%s' не найден", username)
            ));

            DBRequestUtils dBRequestUtils = new DBRequestUtils();
            dBRequestUtils.setAsUser(user.getId());
            return ResponseEntity.ok("ok");
        }
        catch (Exception e) {
            return new ResponseEntity<>(new AppError(HttpStatus.PROCESSING.value(), "Ошибка при попытке получения взаимных лайков пользователя!"), HttpStatus.PROCESSING);
        }
    }

     */
}
