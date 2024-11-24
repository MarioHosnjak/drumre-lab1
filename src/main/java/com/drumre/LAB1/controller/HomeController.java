package com.drumre.LAB1.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HomeController {
    @GetMapping("/")
    public String homepage(Model model, @AuthenticationPrincipal OidcUser principal) {
        return "index";
    }
    @GetMapping("/movies")
    public String moviesPage(Model model, @AuthenticationPrincipal OidcUser principal) {
        return "movies";
    }
}
