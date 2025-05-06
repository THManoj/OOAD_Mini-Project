package com.example.blog.model;

import java.io.Serializable;

public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}