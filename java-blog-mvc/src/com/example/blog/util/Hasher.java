package com.example.blog.util;

public interface Hasher {
    String hash(String password);
    
    default boolean verify(String password, String hashedPassword) {
        return hash(password).equals(hashedPassword);
    }
}