package com.example.blog.service;

import com.example.blog.model.Post;
import com.example.blog.repository.InMemoryStore;

import java.util.List;
import java.util.Optional;

public class PostService {
    private final InMemoryStore store;

    public PostService(InMemoryStore store) {
        this.store = store;
    }

    public void createPost(Post post) {
        store.addPost(post);
    }

    public void updatePost(Post post) {
        store.updatePost(post);
    }

    public void deletePost(Long postId) {
        store.deletePost(postId);
    }

    public Optional<Post> getPostById(Long postId) {
        return store.getPostById(postId);
    }

    public List<Post> getAllPosts() {
        return store.getAllPosts();
    }

    public List<Post> getPostsByAuthor(String author) {
        return store.getPostsByAuthor(author);
    }

    public List<Post> getPostsByStatus(String status) {
        return store.getPostsByStatus(status);
    }
}