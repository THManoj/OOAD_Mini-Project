package com.example.blog.service;

import com.example.blog.exception.AppException;
import com.example.blog.model.Comment;

import java.util.List;

public interface CommentService {
    void addComment(Comment comment) throws AppException;
    void editComment(Comment comment) throws AppException;
    void deleteComment(Long commentId) throws AppException;
    List<Comment> getCommentsByPostId(Long postId);
}