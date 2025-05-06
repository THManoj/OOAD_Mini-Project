package com.example.blog.service;

import com.example.blog.exception.AppException;
import com.example.blog.model.User;
import com.example.blog.repository.InMemoryStore;
import com.example.blog.util.Hasher;
import com.example.blog.util.PasswordHasherAdapter;

public class AuthService {
    private final InMemoryStore store;
    private final Hasher passwordHasher;

    public AuthService(InMemoryStore store) {
        this.store = store;
        // Use the adapter pattern for password hashing
        this.passwordHasher = new PasswordHasherAdapter();
    }

    public User login(String username, String password) throws AppException {
        User user = store.getUser(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new AppException("Invalid username or password.");
        }
        return user;
    }

    public void logout() {
        // Logic for logging out the user (if needed)
        store.setCurrentUser(null);
    }
}