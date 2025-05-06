package com.example.blog.repository;

import com.example.blog.model.Category;
import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.model.Post.PostStatus;
import com.example.blog.model.Role;
import com.example.blog.model.Tag;
import com.example.blog.model.User;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class InMemoryStore implements Serializable {
    private static final long serialVersionUID = 1L;
    private static InMemoryStore instance;
    private static final String DATA_FILE = "blog_data.ser";

    private Map<String, User> users = new HashMap<>();
    private Map<Long, Post> posts = new HashMap<>();
    private Map<Long, Comment> comments = new HashMap<>();
    private Map<Long, Set<String>> likes = new HashMap<>();
    
    // Flyweight pattern implementation
    private transient Map<String, Tag> tagCache = new HashMap<>();
    private transient Map<String, Category> categoryCache = new HashMap<>();
    
    private long nextPostId = 1;
    private long nextCommentId = 1;
    private transient User currentUser;

    private InMemoryStore() {
        // Private constructor for singleton
    }

    public static synchronized InMemoryStore getInstance() {
        if (instance == null) {
            instance = loadFromFile();
            if (instance == null) {
                instance = new InMemoryStore();
                
                // Initialize with default data if it's a new instance
                instance.initializeDefaultData();
            }
        }
        return instance;
    }
    
    /**
     * Initializes the store with default admin and author users
     */
    private void initializeDefaultData() {
        // Create admin role set
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(Role.ADMIN);
        
        // Add admin user
        User adminUser = new User("admin", "admin123", adminRoles);
        users.put(adminUser.getUsername(), adminUser);
        
        // Create author user for testing
        Set<Role> authorRoles = new HashSet<>();
        authorRoles.add(Role.AUTHOR);
        User authorUser = new User("author", "author123", authorRoles);
        users.put(authorUser.getUsername(), authorUser);
    }

    /**
     * Saves the current state to file
     */
    public void saveToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {
            out.writeObject(this);
            System.out.println("Data saved successfully");
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads the store state from file
     * @return the loaded instance or null if none exists
     */
    private static InMemoryStore loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return null;
        }
        
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(DATA_FILE))) {
            InMemoryStore store = (InMemoryStore) in.readObject();
            System.out.println("Data loaded successfully");
            
            // Reinitialize transient fields
            store.tagCache = new HashMap<>();
            store.categoryCache = new HashMap<>();
            
            return store;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Flyweight factory method for Tag
    public Tag getTag(String name) {
        return tagCache.computeIfAbsent(name, Tag::new);
    }

    // Flyweight factory method for Category
    public Category getCategory(String name) {
        return categoryCache.computeIfAbsent(name, Category::new);
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public Map<Long, Post> getPosts() {
        return posts;
    }

    public Map<Long, Comment> getComments() {
        return comments;
    }

    public Map<Long, Set<String>> getLikes() {
        return likes;
    }
    
    public User getUser(String username) {
        return users.get(username);
    }
    
    public Post getPost(Long id) {
        return posts.get(id);
    }
    
    public Comment getComment(Long id) {
        return comments.get(id);
    }
    
    public void addPost(Post post) {
        if (post.getId() <= 0) {
            post = new Post(nextPostId++, post.getTitle(), post.getContent(), 
                           post.getAuthor(), post.getStatus());
        }
        posts.put(post.getId(), post);
        saveToFile(); // Save changes
    }
    
    public void updatePost(Post post) {
        if (posts.containsKey(post.getId())) {
            posts.put(post.getId(), post);
            saveToFile(); // Save changes
        }
    }
    
    public void deletePost(Long postId) {
        posts.remove(postId);
        // Also remove associated comments and likes
        comments.entrySet().removeIf(entry -> {
            Long commentPostId = entry.getValue().getPostId();
            return commentPostId != null && commentPostId.equals(postId);
        });
        likes.remove(postId);
        saveToFile(); // Save changes
    }
    
    public Optional<Post> getPostById(Long postId) {
        return Optional.ofNullable(posts.get(postId));
    }
    
    public List<Post> getAllPosts() {
        return new ArrayList<>(posts.values());
    }
    
    public List<Post> getPostsByAuthor(String author) {
        List<Post> result = new ArrayList<>();
        for (Post post : posts.values()) {
            if (post.getAuthor().equals(author)) {
                result.add(post);
            }
        }
        return result;
    }
    
    public List<Post> getPostsByStatus(String status) {
        List<Post> result = new ArrayList<>();
        for (Post post : posts.values()) {
            if (post.getStatus().toString().equals(status)) {
                result.add(post);
            }
        }
        return result;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void addUser(User user) {
        users.put(user.getUsername(), user);
        saveToFile(); // Save changes
    }
    
    public void updateUser(User user) {
        if (users.containsKey(user.getUsername())) {
            users.put(user.getUsername(), user);
            saveToFile(); // Save changes
        }
    }
    
    public void deleteUser(String username) {
        users.remove(username);
        saveToFile(); // Save changes
    }
    
    public void addComment(Comment comment) {
        if (comment.getId() <= 0) {
            comment.setId(nextCommentId++);
        }
        comments.put(comment.getId(), comment);
        saveToFile(); // Save changes
    }
    
    public void updateComment(Comment comment) {
        if (comments.containsKey(comment.getId())) {
            comments.put(comment.getId(), comment);
            saveToFile(); // Save changes
        }
    }
    
    public void deleteComment(Long commentId) {
        comments.remove(commentId);
        saveToFile(); // Save changes
    }
}