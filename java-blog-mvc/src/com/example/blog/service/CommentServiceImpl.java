package com.example.blog.service;

import com.example.blog.exception.AppException;
import com.example.blog.model.Comment;
import com.example.blog.repository.InMemoryStore;

import java.util.List;
import java.util.stream.Collectors;

public class CommentServiceImpl implements CommentService {

    private final InMemoryStore store;

    public CommentServiceImpl(InMemoryStore store) {
        this.store = store;
    }

    @Override
    public void addComment(Comment comment) throws AppException {
        store.addComment(comment); // Use store's method which saves to file
    }

    @Override
    public void editComment(Comment comment) throws AppException {
        if (store.getComments().containsKey(comment.getId())) {
            store.updateComment(comment); // Use store's method which saves to file
        } else {
            throw new AppException("Comment not found");
        }
    }

    @Override
    public void deleteComment(Long commentId) throws AppException {
        if (store.getComments().containsKey(commentId)) {
            store.deleteComment(commentId); // Use store's method which saves to file
        } else {
            throw new AppException("Comment not found");
        }
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        return store.getComments().values().stream()
                .filter(comment -> comment.getPostId().equals(postId))
                .collect(Collectors.toList());
    }
}