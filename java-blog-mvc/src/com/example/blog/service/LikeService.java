package com.example.blog.service;

import com.example.blog.repository.InMemoryStore;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LikeService {
    private InMemoryStore store;

    public LikeService(InMemoryStore store) {
        this.store = store;
    }

    public void likePost(Long postId, String username) {
        if (store.getPosts().containsKey(postId)) {
            store.getLikes().computeIfAbsent(postId, k -> new HashSet<>()).add(username);
        } else {
            throw new IllegalArgumentException("Post not found");
        }
    }

    public void unlikePost(Long postId, String username) {
        if (store.getLikes().containsKey(postId)) {
            store.getLikes().get(postId).remove(username);
        } else {
            throw new IllegalArgumentException("Post not found or not liked by user");
        }
    }

    public Set<String> getLikesForPost(Long postId) {
        return store.getLikes().getOrDefault(postId, Collections.emptySet());
    }

    public int getLikeCount(Long postId) {
        return store.getLikes().getOrDefault(postId, Collections.emptySet()).size();
    }

    public boolean hasUserLikedPost(Long postId, String username) {
        return store.getLikes().getOrDefault(postId, Collections.emptySet()).contains(username);
    }
}