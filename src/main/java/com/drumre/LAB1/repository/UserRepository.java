package com.drumre.LAB1.repository;

import com.drumre.LAB1.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    // Custom method to find a user by email
    Optional<User> findByEmail(String email);

    // Custom method to find a user by Facebook ID
    Optional<User> findByFacebookId(String facebookId);
}
