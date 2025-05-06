package com.example.blog.controller;

import com.example.blog.model.Post;
import com.example.blog.model.Post.PostStatus;
import com.example.blog.service.PostService;
import com.example.blog.util.NotificationFacade;
import com.example.blog.model.User;
import com.example.blog.model.Role;
import com.example.blog.repository.InMemoryStore; // Fixed import

import java.util.List;

public class PostController {
    private final PostService postService;
    private final NotificationFacade notificationFacade;

    public PostController(PostService postService) {
        this.postService = postService;
        this.notificationFacade = NotificationFacade.getInstance();
    }

    public void createPost(String title, String content, String author) {
        Post post = new Post(0, title, content, author, PostStatus.DRAFT);
        postService.createPost(post);
        notificationFacade.notifyNewPost(author);
    }

    public void editPost(Long postId, String title, String content) {
        Post post = postService.getPostById(postId)
                      .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        post.setTitle(title);
        post.setContent(content);
        postService.updatePost(post);
    }

    public void deletePost(Long postId) {
        User currentUser = InMemoryStore.getInstance().getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("User not authenticated");
        }
        
        if (!currentUser.hasRole(Role.ADMIN)) {
            throw new IllegalStateException("Only administrators can delete posts");
        }
        
        postService.deletePost(postId);
    }

    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    public Post getPost(Long postId) {
        return postService.getPostById(postId).orElse(null);
    }
}