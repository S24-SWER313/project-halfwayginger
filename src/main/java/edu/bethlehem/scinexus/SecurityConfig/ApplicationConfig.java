package edu.bethlehem.scinexus.SecurityConfig;

import edu.bethlehem.scinexus.User.UserRepository;
import edu.bethlehem.scinexus.User.UserNotFoundException;
import edu.bethlehem.scinexus.User.UserService;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@Configuration
public class ApplicationConfig {

    private final UserRepository userRepository;
    private final UserService userDetailsService;

    // @Bean
    // public UserDetailsService userDetailsService() {
    // return email -> userRepository.findByEmail(email)
    // .orElseThrow(() -> new UserNotFoundException("User Not Found",
    // HttpStatus.NOT_FOUND));
    // }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
