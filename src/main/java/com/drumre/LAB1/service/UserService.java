package com.drumre.LAB1.service;

import com.drumre.LAB1.model.User;
import com.drumre.LAB1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public Optional<User> findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public void followUser(String userId, String followUserId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.getFollowing().add(followUserId);
        userRepository.save(user);
    }

    public void unfollowUser(String userId, String unfollowUserId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.getFollowing().remove(unfollowUserId);
        userRepository.save(user);
    }

    public void likeMovie(String userId, String movieId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!user.getLikedMovies().contains(movieId)) {
                user.getLikedMovies().add(movieId);
                userRepository.save(user);
            }
        } else {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
    }

    public void unlikeMovie(String userId, String movieId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getLikedMovies().contains(movieId)) {
                user.getLikedMovies().remove(movieId);
                userRepository.save(user);
            }
        } else {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
    }
}
