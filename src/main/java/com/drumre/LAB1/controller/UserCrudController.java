package com.drumre.LAB1.controller;

import com.drumre.LAB1.model.User;
import com.drumre.LAB1.repository.UserRepository;
import com.drumre.LAB1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserCrudController {

    private final UserService userService;

    @Autowired
    public UserCrudController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/by-email")
    public Optional<User> getUserByEmail(@RequestParam String email) {
        return userService.findUserByEmail(email);
    }
}
