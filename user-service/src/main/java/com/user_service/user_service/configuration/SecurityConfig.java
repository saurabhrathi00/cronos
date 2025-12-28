package com.user_service.user_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // strength 10 is default and fine; increase for more CPU cost (e.g., 12)
        return new BCryptPasswordEncoder(10);
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // curl/postman ke liye CSRF off
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/v1/user/**").permitAll() // signup/signin open
//                        .anyRequest().authenticated() // baki sab secure
//                );
//
//        return http.build();
//    }
}
