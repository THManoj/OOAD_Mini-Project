package com.example.blog.model;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;

public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long postId;
    private String author;
    private String text;
    private transient LocalDateTime timestamp; // Mark as transient

    public Comment(Long id, Long postId, String author, String text) {
        this.id = id;
        this.postId = postId;
        this.author = author;
        this.text = text;
        this.timestamp = LocalDateTime.now();
    }

    // Custom serialization for LocalDateTime
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(timestamp.toString());
    }

    // Custom deserialization for LocalDateTime
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        String dateTimeStr = (String) in.readObject();
        this.timestamp = LocalDateTime.parse(dateTimeStr);
    }

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }
    
    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}