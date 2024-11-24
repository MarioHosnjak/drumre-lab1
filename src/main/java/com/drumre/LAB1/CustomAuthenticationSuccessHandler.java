package com.drumre.LAB1;

import com.drumre.LAB1.model.User;
import com.drumre.LAB1.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository; // Your repository to interact with the database

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        // Retrieve the email from the OAuth2 provider (e.g., Facebook)
        String email = oauth2User.getAttribute("email");

        // Check if the user exists in the database
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isEmpty()) {
            // If the user doesn't exist, save the new user
            System.out.println("User doesn't exist!");
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(oauth2User.getAttribute("name"));
            newUser.setFacebookId(oauth2User.getAttribute("id"));

            userRepository.save(newUser);
        } else {
            System.out.println("User already exists!");
        }

        // Redirect the user to their profile page or wherever you want after login
        response.sendRedirect("/users/profile");
    }
}
