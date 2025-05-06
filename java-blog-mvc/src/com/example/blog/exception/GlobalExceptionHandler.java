package com.example.blog.exception;

import javax.swing.JOptionPane;

public class GlobalExceptionHandler {

    public static void handleException(Exception e) {
        // Log the exception (optional)
        e.printStackTrace();
        
        // Display an error dialog to the user
        JOptionPane.showMessageDialog(null, 
            "An error occurred: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}