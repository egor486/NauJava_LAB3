package ru.Dovgan_Egor.NauJava.TEST;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.Dovgan_Egor.NauJava.SECURITY.CustomUserDetailService;

/**
 * Тестовая конфигурация Spring Security для UI-тестов.
 * Активируется только в тестах с профилем "test".
 * Использует CustomUserDetailService для аутентификации пользователей из БД.
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    @Primary
    public UserDetailsService testUserDetailsService() {
        CustomUserDetailService service = new CustomUserDetailService();
        // UserRepository будет автоматически инжектирован через @Autowired в CustomUserDetailService
        return service;
    }

    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public DaoAuthenticationProvider testAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(testUserDetailsService());
        authProvider.setPasswordEncoder(testPasswordEncoder());
        return authProvider;
    }

    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/reports/**").permitAll()
                        .requestMatchers("/login", "/perform_login", "/users/add").permitAll()
                        .requestMatchers("/registration", "/login", "/perform_login", "/users/add").permitAll()
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/users/all").hasRole("ADMIN")
                        .requestMatchers("/users/findByName/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/tasks-page").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login")
                        .defaultSuccessUrl("/tasks-page", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                );

        return http.build();
    }
}

