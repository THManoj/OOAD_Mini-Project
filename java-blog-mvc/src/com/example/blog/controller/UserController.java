package com.example.blog.controller;

import com.example.blog.model.User;
import com.example.blog.model.Role;
import com.example.blog.service.UserService;
import com.example.blog.exception.AppException;
import com.example.blog.util.NotificationFacade;
import com.example.blog.repository.InMemoryStore; // Fixed import

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserController {
    private UserService userService;
    private NotificationFacade notificationFacade;

    public UserController(UserService userService) {
        this.userService = userService;
        this.notificationFacade = NotificationFacade.getInstance();
    }

    public void createUser(String username, String password) throws AppException {
        // Create with default READER role
        Set<Role> roles = new HashSet<>();
        roles.add(Role.READER);
        User user = new User(username, password, roles);
        userService.addUser(user);
        notificationFacade.notifyUserRegistration(username);
    }

    public void editUser(String username, String newPassword) throws AppException {
        User user = userService.getUser(username)
                     .orElseThrow(() -> new AppException("User not found"));
        user.setPassword(newPassword);
        userService.updateUser(user);
    }

    public void deleteUser(String username) throws AppException {
        User currentUser = InMemoryStore.getInstance().getCurrentUser();
        if (currentUser == null) {
            throw new AppException("User not authenticated");
        }
        
        if (!currentUser.hasRole(Role.ADMIN)) {
            throw new AppException("Only administrators can delete users");
        }
        
        userService.deleteUser(username);
    }

    public List<User> listUsers() {
        return userService.getAllUsers();
    }
}