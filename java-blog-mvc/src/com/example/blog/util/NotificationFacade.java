package com.example.blog.util;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Implementation of the Facade pattern that simplifies notification operations
 */
public class NotificationFacade {
    private static NotificationFacade instance;
    
    private NotificationFacade() {
        // Private constructor for singleton pattern
    }
    
    public static synchronized NotificationFacade getInstance() {
        if (instance == null) {
            instance = new NotificationFacade();
        }
        return instance;
    }

    public void notifyCommentReply(String parentCommentAuthor, String replyAuthor) {
        // Logic to notify the parent comment author about the reply
        logNotification("Notifying " + parentCommentAuthor + " about a reply from " + replyAuthor);
        // Here you could add more complex logic, such as sending an email or displaying a dialog
    }

    public void notifyNewPost(String author) {
        // Logic to notify users about a new post
        logNotification("Notifying users about a new post by " + author);
        // Additional notification logic can be added here
    }

    public void notifyUserRegistration(String username) {
        // Logic to notify about a new user registration
        logNotification("Notifying about new user registration: " + username);
        // Additional notification logic can be added here
    }
    
    public void notifyError(String errorMessage) {
        logError("ERROR: " + errorMessage);
        showDialog(errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void notifySuccess(String message) {
        logNotification("SUCCESS: " + message);
        showDialog(message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logNotification(String message) {
        System.out.println(message);
    }
    
    private void logError(String message) {
        System.err.println(message);
    }
    
    private void showDialog(String message, String title, int messageType) {
        // Ensure dialog is shown on the Event Dispatch Thread
        if (SwingUtilities.isEventDispatchThread()) {
            JOptionPane.showMessageDialog(null, message, title, messageType);
        } else {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, message, title, messageType);
            });
        }
    }
}