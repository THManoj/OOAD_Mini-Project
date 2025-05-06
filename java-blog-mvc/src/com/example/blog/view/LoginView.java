package com.example.blog.view;

import com.example.blog.controller.AuthController;
import com.example.blog.controller.UserController;
import com.example.blog.exception.AppException;
import com.example.blog.exception.GlobalExceptionHandler;
import com.example.blog.service.UserService;
import com.example.blog.repository.InMemoryStore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel;
    private AuthController authController;
    private UserController userController;

    public LoginView(AuthController authController) {
        this.authController = authController;
        this.userController = new UserController(new UserService(InMemoryStore.getInstance()));
        
        setTitle("Blog Application Login");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initializeComponents();
        layoutComponents();
        registerListeners();
        
        // Apply modern look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        
        // Make buttons more attractive with BLACK text color
        loginButton.setBackground(new Color(66, 139, 202));
        loginButton.setForeground(new Color(0, 0, 0)); // Black text color
        loginButton.setFont(new Font("Arial", Font.BOLD, 14)); // Bold text
        loginButton.setFocusPainted(false);
        
        registerButton.setBackground(new Color(92, 184, 92));
        registerButton.setForeground(new Color(0, 0, 0)); // Black text color
        registerButton.setFont(new Font("Arial", Font.BOLD, 14)); // Bold text
        registerButton.setFocusPainted(false);
        
        messageLabel = new JLabel("Please enter your credentials");
        messageLabel.setForeground(new Color(66, 139, 202));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Add hover effects to buttons with font color changes
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(51, 122, 183));
                loginButton.setForeground(new Color(0, 0, 0)); // Keep black on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(66, 139, 202));
                loginButton.setForeground(new Color(0, 0, 0)); // Keep black when not hovering
            }
        });
        
        registerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(76, 168, 76));
                registerButton.setForeground(new Color(0, 0, 0)); // Keep black on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(92, 184, 92));
                registerButton.setForeground(new Color(0, 0, 0)); // Keep black when not hovering
            }
        });
    }
    
    private void layoutComponents() {
        // Create a panel with padding
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Add blog application logo/title
        JLabel titleLabel = new JLabel("Blog Application", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        // Username field with label
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);
        
        // Password field with label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);
        
        // Button panel for login and register
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(buttonPanel, gbc);
        
        // Message label
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        mainPanel.add(messageLabel, gbc);
        
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    private void registerListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                
                if (username.isEmpty() || password.isEmpty()) {
                    messageLabel.setText("Username and password cannot be empty");
                    messageLabel.setForeground(Color.RED);
                    return;
                }
                
                try {
                    authController.login(username, password);
                } catch (Exception ex) {
                    messageLabel.setText("Login failed: " + ex.getMessage());
                    messageLabel.setForeground(Color.RED);
                    GlobalExceptionHandler.handleException(ex);
                }
            }
        });
        
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerNewUser();
            }
        });
    }
    
    private void registerNewUser() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JPasswordField confirmPasswordField = new JPasswordField(15);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Confirm Password:"), gbc);
        
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);
        
        int result = JOptionPane.showConfirmDialog(
            this, panel, "Register New User", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Username and password cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, 
                    "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                userController.createUser(username, password);
                JOptionPane.showMessageDialog(this, 
                    "Registration successful! You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Pre-fill the login fields with the new username
                this.usernameField.setText(username);
                this.passwordField.setText("");
                
            } catch (AppException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Registration failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}