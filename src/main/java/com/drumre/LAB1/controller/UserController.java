package com.drumre.LAB1.controller;

import com.drumre.LAB1.model.User;
import com.drumre.LAB1.repository.UserRepository;
import com.drumre.LAB1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/users")
class UserController {

/*    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }*/

    @GetMapping("/profile")
    public String getProfileInfo(Model model, OAuth2AuthenticationToken authentication) {
        if(authentication != null) {
            model.addAttribute("profileInfo", authentication.getPrincipal().getAttributes());
        }
        return "profile";
    }
}
