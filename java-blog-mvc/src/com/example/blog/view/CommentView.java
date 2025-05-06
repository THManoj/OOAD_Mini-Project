package com.example.blog.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.example.blog.controller.CommentController;
import com.example.blog.exception.AppException;
import com.example.blog.model.Comment;
import com.example.blog.repository.InMemoryStore;
import com.example.blog.model.User;

public class CommentView extends JPanel {
    private JTextArea commentTextArea;
    private JButton submitButton;
    private JList<String> commentList;
    private DefaultListModel<String> commentListModel;
    private InMemoryStore store;
    private CommentController commentController;
    private Long currentPostId = 1L; // Default to first post

    public CommentView() {
        this.store = InMemoryStore.getInstance();
        setLayout(new BorderLayout());

        // Initialize components
        commentTextArea = new JTextArea(5, 30);
        submitButton = new JButton("Submit Comment");
        commentListModel = new DefaultListModel<>();
        commentList = new JList<>(commentListModel);
        
        // Add components to panel
        JScrollPane commentScrollPane = new JScrollPane(commentList);
        commentScrollPane.setBorder(BorderFactory.createTitledBorder("Comments"));
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JScrollPane(commentTextArea), BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Comment"));

        add(commentScrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        
        // Initial comment display message
        commentListModel.addElement("Select a post to view comments");
    }

    public void setCommentController(CommentController controller) {
        this.commentController = controller;
        
        // Clear previous action listeners and add the new one
        for (ActionListener al : submitButton.getActionListeners()) {
            submitButton.removeActionListener(al);
        }
        
        submitButton.addActionListener(e -> {
            String commentText = commentTextArea.getText().trim();
            if (commentText.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                                              "Comment cannot be empty!", 
                                              "Error", 
                                              JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                User currentUser = store.getCurrentUser();
                if (currentUser == null) {
                    JOptionPane.showMessageDialog(this,
                                                "You must be logged in to comment",
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Comment comment = new Comment(0L, currentPostId, 
                                             currentUser.getUsername(), 
                                             commentText);
                controller.addComment(comment);
                commentTextArea.setText("");
                
                // Refresh the comment list
                refreshComments();
            } catch (AppException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), 
                                              "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                                            "Error: " + ex.getMessage(),
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void refreshComments() {
        try {
            if (commentController == null) {
                commentListModel.clear();
                commentListModel.addElement("Comment controller not initialized");
                return;
            }
            
            List<Comment> comments = commentController.getCommentsForPost(currentPostId);
            if (comments == null) {
                comments = new ArrayList<>();
            }
            
            displayComments(comments.stream()
                           .map(c -> c.getAuthor() + ": " + c.getText())
                           .collect(Collectors.toList()));
        } catch (Exception ex) {
            commentListModel.clear();
            commentListModel.addElement("Error loading comments: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void displayComments(List<String> comments) {
        commentListModel.clear();
        if (comments == null || comments.isEmpty()) {
            commentListModel.addElement("No comments for this post");
        } else {
            for (String comment : comments) {
                commentListModel.addElement(comment);
            }
        }
    }

    public void setCurrentPostId(Long postId) {
        this.currentPostId = postId;
        commentListModel.clear();
        commentListModel.addElement("Loading comments...");
        
        if (commentController != null) {
            refreshComments();
        }
    }
}