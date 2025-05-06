package com.example.blog;

import com.example.blog.repository.InMemoryStore;
import com.example.blog.service.AuthService;
import com.example.blog.service.UserService;
import com.example.blog.service.PostService;
import com.example.blog.service.CommentService;
import com.example.blog.service.CommentServiceImpl;
import com.example.blog.controller.AuthController;
import com.example.blog.view.LoginView;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App {
    public static void main(String[] args) {
        try {
            // Set system look and feel for better appearance
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set look and feel: " + e.getMessage());
        }

        // Initialize InMemoryStore - will load from file if available or create defaults
        InMemoryStore store = InMemoryStore.getInstance();
        
        // Initialize services
        AuthService authService = new AuthService(store);
        UserService userService = new UserService(store);
        PostService postService = new PostService(store);
        CommentService commentService = new CommentServiceImpl(store);
        
        // Initialize controller with all services
        AuthController authController = new AuthController(
            authService, userService, postService, commentService);
        
        // Launch the LoginView
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView(authController);
            loginView.setVisible(true);
        });
    }
}