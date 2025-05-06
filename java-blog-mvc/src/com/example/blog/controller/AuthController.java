package com.example.blog.controller;

import com.example.blog.exception.AppException;
import com.example.blog.model.User;
import com.example.blog.service.AuthService;
import com.example.blog.service.UserService;
import com.example.blog.service.PostService;
import com.example.blog.service.CommentService;
import com.example.blog.service.CommentServiceImpl;
import com.example.blog.service.CommentServiceProxy;
import com.example.blog.repository.InMemoryStore;
import com.example.blog.view.LoginView;
import com.example.blog.view.MainView;
import javax.swing.*;
import java.awt.*;

public class AuthController {
    private AuthService authService;
    private UserService userService;
    private PostService postService;
    private CommentService commentService;
    private InMemoryStore store;

    public AuthController(AuthService authService) {
        this.authService = authService;
        this.store = InMemoryStore.getInstance();
    }
    
    public AuthController(AuthService authService, UserService userService, 
                         PostService postService, CommentService commentService) {
        this.authService = authService;
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
        this.store = InMemoryStore.getInstance();
    }

    public void login(String username, String password) {
        try {
            User user = authService.login(username, password);
            if (user != null) {
                // Set the current user in the store
                store.setCurrentUser(user);
                
                // Create controllers for services if they weren't injected
                UserController userController = new UserController(userService != null ? 
                    userService : new UserService(store));
                    
                PostController postController = new PostController(postService != null ? 
                    postService : new PostService(store));
                    
                // Use proxy for comment service for authorization
                CommentService actualCommentService = commentService != null ? 
                    commentService : new CommentServiceImpl(store);
                CommentServiceProxy commentServiceProxy = new CommentServiceProxy(actualCommentService, store);
                CommentController commentController = new CommentController(commentServiceProxy);
                
                // Open main view
                SwingUtilities.invokeLater(() -> {
                    try {
                        MainView mainView = new MainView(userController, postController, 
                                                       commentController, user, this);
                        mainView.setVisible(true);
                        
                        // Close any existing login windows
                        for (Window window : Window.getWindows()) {
                            if (window instanceof LoginView) {
                                window.dispose();
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, 
                            "Error creating main view: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
                
                JOptionPane.showMessageDialog(null, "Login successful!");
            }
        } catch (AppException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void logout() {
        // Clear current user in store
        store.setCurrentUser(null);
        JOptionPane.showMessageDialog(null, "Logged out successfully.");
    }
}