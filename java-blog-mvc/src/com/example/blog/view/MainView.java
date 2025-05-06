package com.example.blog.view;

import com.example.blog.controller.AuthController;
import com.example.blog.controller.UserController;
import com.example.blog.controller.PostController;
import com.example.blog.controller.CommentController;
import com.example.blog.model.User;
import com.example.blog.model.Post;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainView extends JFrame {
    private JTabbedPane tabbedPane;
    private UserManagementView userManagementView;
    private PostManagementView postManagementView;
    private CommentView commentView;
    private User currentUser;
    private AuthController authController;
    private PostController postController;

    public MainView(UserController userController, PostController postController, 
                   CommentController commentController, User currentUser, 
                   AuthController authController) {
        this.currentUser = currentUser;
        this.authController = authController;
        this.postController = postController;
        
        setTitle("Blog Application - " + currentUser.getUsername());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        userManagementView = new UserManagementView(userController);
        postManagementView = new PostManagementView(postController);
        commentView = new CommentView();
        commentView.setCommentController(commentController);

        // Add listener to post management view to update comments
        setupPostSelectionListener();

        tabbedPane.addTab("Users", userManagementView);
        tabbedPane.addTab("Posts", postManagementView);
        tabbedPane.addTab("Comments", commentView);

        add(tabbedPane, BorderLayout.CENTER);
        add(createLogoutButton(), BorderLayout.SOUTH);
        
        // Initialize the user table
        userManagementView.updateUserTable();
        
        // Load some sample posts for testing
        createSamplePosts();
    }

    private void setupPostSelectionListener() {
        // Add a listener to the post table
        postManagementView.getPostTable().getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) { // Avoid duplicate events
                        int selectedRow = postManagementView.getPostTable().getSelectedRow();
                        if (selectedRow >= 0) {
                            try {
                                Long postId = Long.parseLong(
                                    postManagementView.getPostTable().getValueAt(selectedRow, 0).toString());
                                
                                // Get the post
                                Post selectedPost = postController.getPost(postId);
                                
                                // Show post details in a dialog
                                showPostDetails(selectedPost);
                                
                                // Also switch to Comments tab and load comments
                                tabbedPane.setSelectedIndex(2); // Switch to Comments tab
                                commentView.setCurrentPostId(postId);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(MainView.this,
                                                           "Error selecting post: " + ex.getMessage(),
                                                           "Error", 
                                                           JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            });
    }

    private void showPostDetails(Post post) {
        if (post == null) return;
        
        JDialog detailDialog = new JDialog(this, "Post Details", false);
        detailDialog.setSize(500, 400);
        detailDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create a title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(post.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel authorLabel = new JLabel("By: " + post.getAuthor() + " | " + post.getCreatedAt().toString());
        authorLabel.setForeground(Color.DARK_GRAY);
        
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(authorLabel, BorderLayout.SOUTH);
        
        // Create content area
        JTextArea contentArea = new JTextArea(post.getContent());
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        contentArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(contentArea);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add a close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> detailDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        detailDialog.add(panel);
        detailDialog.setVisible(true);
    }

    private void createSamplePosts() {
        // Create a couple of sample posts if none exist
        List<Post> posts = postController.getAllPosts();
        if (posts.isEmpty()) {
            postController.createPost("Welcome to the Blog", 
                                     "This is your first post. Feel free to edit or delete it.",
                                     currentUser.getUsername());
            postController.createPost("Getting Started", 
                                     "Here are some tips to get you started with this blog application.",
                                     currentUser.getUsername());
            postManagementView.updatePostTable();
        }
    }

    private JButton createLogoutButton() {
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authController.logout();
                dispose(); // Close the main view
                new LoginView(authController).setVisible(true); // Show login view
            }
        });
        return logoutButton;
    }
}