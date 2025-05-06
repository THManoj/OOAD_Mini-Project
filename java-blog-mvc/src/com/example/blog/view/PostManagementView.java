package com.example.blog.view;

import com.example.blog.controller.PostController;
import com.example.blog.model.Post;
import com.example.blog.model.Post.PostStatus;
import com.example.blog.model.User;
import com.example.blog.model.Role;
import com.example.blog.repository.InMemoryStore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PostManagementView extends JPanel {
    private JTable postTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private PostController postController;

    public PostManagementView(PostController postController) {
        this.postController = postController;
        setLayout(new BorderLayout());
        
        initializeComponents();
        layoutComponents();
        registerListeners();
        
        // Load initial posts
        updatePostTable();
    }

    private void initializeComponents() {
        // Create table with columns
        String[] columns = {"ID", "Title", "Author", "Status", "Created At"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        postTable = new JTable(tableModel);
        
        // Create buttons
        addButton = new JButton("Add Post");
        editButton = new JButton("Edit Post");
        deleteButton = new JButton("Delete Post");

        // Restrict delete access to admins only
        User currentUser = InMemoryStore.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.hasRole(Role.ADMIN)) {
            deleteButton.setEnabled(false);
            deleteButton.setToolTipText("Only administrators can delete posts");
        }

        setupPostSelectionListener();
    }

    private void layoutComponents() {
        // Add table with scrolling
        JScrollPane scrollPane = new JScrollPane(postTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void registerListeners() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPostDialog(null); // null for new post
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = postTable.getSelectedRow();
                if (row >= 0) {
                    Long postId = Long.parseLong(tableModel.getValueAt(row, 0).toString());
                    Post post = postController.getPost(postId);
                    showPostDialog(post);
                } else {
                    JOptionPane.showMessageDialog(PostManagementView.this, 
                                               "Please select a post to edit.",
                                               "No Selection", 
                                               JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = postTable.getSelectedRow();
                if (row >= 0) {
                    Long postId = Long.parseLong(tableModel.getValueAt(row, 0).toString());
                    int confirm = JOptionPane.showConfirmDialog(PostManagementView.this,
                                                           "Are you sure you want to delete this post?",
                                                           "Confirm Delete",
                                                           JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        postController.deletePost(postId);
                        updatePostTable();
                    }
                } else {
                    JOptionPane.showMessageDialog(PostManagementView.this,
                                               "Please select a post to delete.",
                                               "No Selection",
                                               JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    private void showPostDialog(Post post) {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), 
                                   post == null ? "Add Post" : "Edit Post", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Add form fields
        JTextField titleField = new JTextField(post != null ? post.getTitle() : "", 20);
        JTextArea contentArea = new JTextArea(post != null ? post.getContent() : "", 10, 20);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        
        JComboBox<PostStatus> statusCombo = new JComboBox<>(PostStatus.values());
        if (post != null) {
            statusCombo.setSelectedItem(post.getStatus());
        }
        
        // Layout components
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Title:"), gbc);
        
        gbc.gridx = 1;
        panel.add(titleField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Content:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridheight = 2;
        panel.add(new JScrollPane(contentArea), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        panel.add(new JLabel("Status:"), gbc);
        
        gbc.gridx = 1;
        panel.add(statusCombo, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            PostStatus status = (PostStatus)statusCombo.getSelectedItem();
            
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Title cannot be empty", "Error", 
                                            JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                if (post == null) {
                    // Create new post - would need the current user's username in a real app
                    postController.createPost(title, content, "currentUser");
                } else {
                    post.setTitle(title);
                    post.setContent(content);
                    post.setStatus(status);
                    postController.editPost(post.getId(), title, content);
                }
                updatePostTable();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", 
                                            JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    public void updatePostTable() {
        tableModel.setRowCount(0); // Clear existing rows
        
        List<Post> posts = postController.getAllPosts();
        for (Post post : posts) {
            tableModel.addRow(new Object[]{
                post.getId(),
                post.getTitle(),
                post.getAuthor(),
                post.getStatus(),
                post.getCreatedAt()
            });
        }
    }

    public JTable getPostTable() {
        return postTable;
    }

    private void setupPostSelectionListener() {
        postTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Avoid duplicate events
                int selectedRow = postTable.getSelectedRow();
                if (selectedRow >= 0) {
                    try {
                        Long postId = Long.parseLong(tableModel.getValueAt(selectedRow, 0).toString());
                        Post post = postController.getPost(postId);
                        if (post != null) {
                            showPostDetails(post);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                                                   "Error selecting post: " + ex.getMessage(),
                                                   "Error", 
                                                   JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void showPostDetails(Post post) {
        JDialog detailDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), 
                                         "Post Details", false);
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
}