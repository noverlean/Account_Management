package unicore.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import unicore.api.dto.*;
import unicore.api.entities.User;
import unicore.api.senders.MailSender;
import unicore.api.utils.JwtTokenUtils;
import unicore.api.utils.CodeGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    private static Map<String, UserEmailCodeDto> registerAcceptionCodes = new HashMap<String, UserEmailCodeDto>();

    public ResponseEntity<JwtResponse> logIn(@RequestBody JwtRequest jwtRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getEmail(), jwtRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(jwtRequest.getEmail());
        String token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    public ResponseEntity<UserEmailCodeDto> signUp(@RequestBody JwtRequest jwtRequest) {
        Optional<User> user = userService.findByEmail(jwtRequest.getEmail());
        if (user.isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        String code = CodeGenerator.generateCode();
        MailSender.sendCode(jwtRequest.getEmail(), code);
        UserEmailCodeDto userEmailCodeDto = new UserEmailCodeDto(
                jwtRequest.getPassword(),
                jwtRequest.getEmail(),
                code
        );
        registerAcceptionCodes.put(jwtRequest.getEmail(), userEmailCodeDto);
        return ResponseEntity.ok(userEmailCodeDto);
    }

    public ResponseEntity<JwtResponse> createNewUser(@RequestBody RegistrationCredentials registrationCredentials) {
        if (userService.findByEmail(registrationCredentials.getEmail()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        userService.createNewUser(registrationCredentials);
        return logIn(new JwtRequest(registrationCredentials));
    }

    public ResponseEntity<JwtResponse> confirmEmail(@RequestBody ConfirmEmailDto confirmEmailDto) {
        boolean isConfirmed = false;
        try {
            for (Map.Entry<String, UserEmailCodeDto> entry : registerAcceptionCodes.entrySet()) {
                if (entry.getKey().equals(confirmEmailDto.getEmail())) {
                    isConfirmed = entry.getValue().getCode().equals(confirmEmailDto.getCode().toLowerCase());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        if (isConfirmed) {
            return createNewUser(getRegistrationCredentials(confirmEmailDto.getEmail()));
        }
        else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    public RegistrationCredentials getRegistrationCredentials(String email){
        for (Map.Entry<String, UserEmailCodeDto> entry : registerAcceptionCodes.entrySet()) {
            if (entry.getKey().equals(email)) {
                return new RegistrationCredentials(
                        entry.getValue().getEmail(),
                        entry.getValue().getPassword()
                );
            }
        }
        return null;
    }
}
