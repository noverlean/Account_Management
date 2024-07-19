package unicore.api.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import unicore.api.service.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    private UserService userService;
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setJwtRequestFilter(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors(Customizer.withDefaults())
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(requests -> requests
                    .requestMatchers("/user").authenticated()
                    .requestMatchers("/users").hasRole("ADMIN")

                    .requestMatchers("/environment/create").authenticated()
                    .requestMatchers("/environment/add").authenticated()
                    .requestMatchers("/environment/add/confirm").authenticated()
                    .requestMatchers("/environment/exit").authenticated()
                    .requestMatchers("/device/add").authenticated()
                    .requestMatchers("/device/remove").authenticated()
                    .requestMatchers("/device/ip").authenticated()
                    .requestMatchers("/device/lightning/set").authenticated()
                    .requestMatchers("/device/switch/set").authenticated()

                    .requestMatchers("/ticket/create").authenticated()
                    .requestMatchers("/tickets").hasRole("ADMIN")
                    .requestMatchers("/ticket/accept/{id}").hasRole("ADMIN")
                    .requestMatchers("/ticket/delete/{id}").authenticated()
                    .requestMatchers("/ticket/send/{id}").authenticated()
//                    .requestMatchers(HttpMethod.PUT).hasRole("ADMIN")
                .anyRequest().permitAll()
            );

        return http.build();
    }
    
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userService);
        return daoAuthenticationProvider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
