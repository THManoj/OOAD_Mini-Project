package com.example.blog.service;

import com.example.blog.model.User;
import com.example.blog.repository.InMemoryStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final InMemoryStore store;

    public UserService(InMemoryStore store) {
        this.store = store;
    }

    public void addUser(User user) {
        store.addUser(user); // Use store's method which saves to file
    }

    public void updateUser(User user) {
        store.updateUser(user); // Use store's method which saves to file
    }

    public void deleteUser(String username) {
        store.deleteUser(username); // Use store's method which saves to file
    }

    public Optional<User> getUser(String username) {
        return Optional.ofNullable(store.getUsers().get(username));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(store.getUsers().values());
    }
}