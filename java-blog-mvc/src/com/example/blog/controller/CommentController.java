package com.example.blog.controller;

import com.example.blog.model.Comment;
import com.example.blog.service.CommentService;
import com.example.blog.service.CommentServiceImpl;
import com.example.blog.service.CommentServiceProxy;
import com.example.blog.repository.InMemoryStore;
import com.example.blog.exception.AppException;

import java.util.List;

public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    public void addComment(Comment comment) throws AppException {
        commentService.addComment(comment);
    }

    public void editComment(Long commentId, String newText) throws AppException {
        Comment comment = InMemoryStore.getInstance().getComment(commentId);
        if (comment != null) {
            comment.setText(newText);
            commentService.editComment(comment);
        } else {
            throw new AppException("Comment not found");
        }
    }

    public void deleteComment(Long commentId) throws AppException {
        commentService.deleteComment(commentId);
    }

    public List<Comment> getCommentsForPost(Long postId) {
        return commentService.getCommentsByPostId(postId);
    }
}