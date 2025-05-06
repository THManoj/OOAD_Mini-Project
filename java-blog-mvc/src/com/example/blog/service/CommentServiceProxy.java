package com.example.blog.service;

import com.example.blog.exception.AppException;
import com.example.blog.model.Comment;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import com.example.blog.repository.InMemoryStore;

import java.util.List;

/**
 * Implementation of the Proxy pattern for comment access control
 */
public class CommentServiceProxy implements CommentService {
    private final CommentService commentService;
    private final InMemoryStore store;

    public CommentServiceProxy(CommentService commentService, InMemoryStore store) {
        this.commentService = commentService;
        this.store = store;
    }

    @Override
    public void addComment(Comment comment) throws AppException {
        // Anyone can add comments
        commentService.addComment(comment);
    }

    @Override
    public void editComment(Comment comment) throws AppException {
        Comment existingComment = store.getComment(comment.getId());
        if (existingComment == null) {
            throw new AppException("Comment not found");
        }
        
        User currentUser = store.getCurrentUser();
        if (currentUser == null) {
            throw new AppException("User not authenticated");
        }
        
        // Check if user is the comment author or an admin
        if (currentUser.getUsername().equals(existingComment.getAuthor()) || 
            currentUser.hasRole(Role.ADMIN)) {
            commentService.editComment(comment);
        } else {
            throw new AppException("You can only edit your own comments");
        }
    }

    @Override
    public void deleteComment(Long commentId) throws AppException {
        Comment comment = store.getComment(commentId);
        if (comment == null) {
            throw new AppException("Comment not found");
        }
        
        User currentUser = store.getCurrentUser();
        if (currentUser == null) {
            throw new AppException("User not authenticated");
        }
        
        // ONLY ADMINS can delete comments now
        if (currentUser.hasRole(Role.ADMIN)) {
            commentService.deleteComment(commentId);
        } else {
            throw new AppException("Only administrators can delete comments");
        }
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        // Anyone can view comments
        return commentService.getCommentsByPostId(postId);
    }
}