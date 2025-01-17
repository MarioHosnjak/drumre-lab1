package com.drumre.LAB1.controller;

import com.drumre.LAB1.model.User;
import com.drumre.LAB1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Controller
@RequestMapping("/users")
class UserController {

    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String getProfileInfo(Model model, OAuth2AuthenticationToken authentication) {
        if(authentication != null) {
            model.addAttribute("profileInfo", authentication.getPrincipal().getAttributes());
        }
        return "profile";
    }

    @GetMapping("/community")
    public String getCommunityInfo(Model model, OAuth2AuthenticationToken authentication) {
        if(authentication != null) {
            Map<String, Object> attributes = authentication.getPrincipal().getAttributes();
            model.addAttribute("profileInfo", attributes);
            // Get the currently logged-in user's email
            String email = authentication.getPrincipal().getAttributes().get("email").toString();
            Optional<User> currentUser = userService.findUserByEmail(email);

            if (currentUser.isPresent()) {
                List<String> followedUserIds = currentUser.get().getFollowing();
                List<User> allUsers = userService.getAllUsers();

                model.addAttribute("allUsers", allUsers);
                model.addAttribute("followedUserIds", followedUserIds);
                model.addAttribute("currentUserId", currentUser.get().getId());
            }
        }
        return "community";
    }
}
