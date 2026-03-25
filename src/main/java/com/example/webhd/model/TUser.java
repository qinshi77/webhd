package com.example.webhd.model;

public class TUser {
    private Long id;
    private String username;
    private String password;
    // 移除 createdAt 字段，因为表中没有这个字段

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}