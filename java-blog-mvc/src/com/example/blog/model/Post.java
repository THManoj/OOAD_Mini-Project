package com.example.blog.model;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.util.HashSet;
import java.time.LocalDateTime;

public class Post implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String title;
    private String content;
    private String author;
    private PostStatus status;
    private Set<Tag> tags;
    private Set<Category> categories;
    private transient LocalDateTime createdAt; // Mark as transient

    public Post(long id, String title, String content, String author, PostStatus status) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.status = status;
        this.tags = new HashSet<>();
        this.categories = new HashSet<>();
        this.createdAt = LocalDateTime.now();
    }

    // Custom serialization for LocalDateTime
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(createdAt.toString());
    }

    // Custom deserialization for LocalDateTime
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        String dateTimeStr = (String) in.readObject();
        this.createdAt = LocalDateTime.parse(dateTimeStr);
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public PostStatus getStatus() {
        return status;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void addCategory(Category category) {
        this.categories.add(category);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public enum PostStatus {
        DRAFT,
        PUBLISHED
    }
}