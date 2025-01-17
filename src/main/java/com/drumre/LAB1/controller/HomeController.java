package com.drumre.LAB1.controller;

import com.drumre.LAB1.model.User;
import com.drumre.LAB1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class HomeController {

    private final UserService userService;
    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String homepage(Model model, @AuthenticationPrincipal OidcUser principal) {
        return "index";
    }
    @GetMapping("/movies")
    public String moviesPage(Model model, OAuth2AuthenticationToken authentication) {
        if (authentication != null) {
            String email = authentication.getPrincipal().getAttributes().get("email").toString();
            Optional<User> currentUser = userService.findUserByEmail(email);
            currentUser.ifPresent(user -> model.addAttribute("currentUserId", user.getId()));
        }
        return "movies";
    }
}
