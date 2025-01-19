package com.drumre.LAB1.config;

import com.drumre.LAB1.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable()) // Disable CSRF protection for simplicity
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/", "/login").permitAll(); // Allow access to home and login page
                    auth.requestMatchers("/api/movies/delete/**").authenticated(); // Protect certain paths
                    auth.anyRequest().authenticated(); // All other requests require authentication
                }).oauth2Login(oauth2 -> oauth2
                        .loginPage("/login") // Specify custom login page
                        .successHandler(successHandler) // Custom success handler after login
                ).logout(logout -> logout
                        .logoutSuccessUrl("/") // Redirect to home page on logout
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                ).build();
    }


    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return userRequest -> {
            OAuth2User user = delegate.loadUser(userRequest);
            // Map roles or additional processing here if needed
            return user;
        };
    }

}
