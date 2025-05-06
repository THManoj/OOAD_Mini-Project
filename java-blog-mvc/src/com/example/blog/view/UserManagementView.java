package com.example.blog.view;

import com.example.blog.model.User;
import com.example.blog.controller.UserController;
import com.example.blog.model.Role;
import com.example.blog.repository.InMemoryStore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserManagementView extends JPanel {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private UserController userController;

    public UserManagementView(UserController userController) {
        this.userController = userController;
        initializeComponents();
        layoutComponents();
    }

    private void initializeComponents() {
        String[] columnNames = {"Username", "Roles"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel);

        addButton = new JButton("Add User");
        editButton = new JButton("Edit User");
        deleteButton = new JButton("Delete User");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logic to add a user
                String username = JOptionPane.showInputDialog("Enter username:");
                String password = JOptionPane.showInputDialog("Enter password:");
                
                if (username != null && !username.isEmpty() && 
                    password != null && !password.isEmpty()) {
                    try {
                        userController.createUser(username, password);
                        updateUserTable();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(UserManagementView.this, 
                                                    ex.getMessage(), 
                                                    "Error", 
                                                    JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow != -1) {
                    String username = (String) tableModel.getValueAt(selectedRow, 0);
                    String newPassword = JOptionPane.showInputDialog("Enter new password:");
                    
                    if (newPassword != null && !newPassword.isEmpty()) {
                        try {
                            userController.editUser(username, newPassword);
                            JOptionPane.showMessageDialog(UserManagementView.this, 
                                                        "User updated successfully");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(UserManagementView.this, 
                                                        ex.getMessage(), 
                                                        "Error", 
                                                        JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(UserManagementView.this, 
                                                "Please select a user to edit.");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow != -1) {
                    String username = (String) tableModel.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(UserManagementView.this, 
                                                             "Are you sure you want to delete this user?",
                                                             "Confirm Delete", 
                                                             JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            userController.deleteUser(username);
                            updateUserTable();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(UserManagementView.this, 
                                                        ex.getMessage(), 
                                                        "Error", 
                                                        JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(UserManagementView.this, 
                                                "Please select a user to delete.");
                }
            }
        });

        // Restrict delete access to admins only
        User currentUser = InMemoryStore.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.hasRole(Role.ADMIN)) {
            deleteButton.setEnabled(false);
            deleteButton.setToolTipText("Only administrators can delete users");
        }
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        add(new JScrollPane(userTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void updateUserTable() {
        List<User> users = userController.listUsers();
        tableModel.setRowCount(0); // Clear existing rows
        for (User user : users) {
            tableModel.addRow(new Object[]{user.getUsername(), user.getRoles().toString()});
        }
    }
}