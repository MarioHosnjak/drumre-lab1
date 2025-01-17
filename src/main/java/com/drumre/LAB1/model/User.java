package com.drumre.LAB1.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    @Indexed(unique = true)
    private String facebookId;
    private String name;
    @Indexed(unique = true)
    private String email;
    private List<String> following;

    // Constructors, Getters, and Setters
    public User() {}

    public User(String facebookId, String name, String email, List<String> following) {
        this.facebookId = facebookId;
        this.name = name;
        this.email = email;
        this.following = following;
    }

    public String getId() { return id; }
    public String getFacebookId() { return facebookId; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public void setId(String id) {
        this.id = id;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getFollowing() { return following; }
    public void setFollowing(List<String> following) { this.following = following; }
}
