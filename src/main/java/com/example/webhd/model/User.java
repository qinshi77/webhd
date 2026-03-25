package com.example.webhd.model;

import com.baomidou.mybatisplus.annotation.TableName;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class User {
    private Long id;
    private String username;
    private String password;
    private Integer isAdmin;

    // Getters and Setters
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

    public void setIsAdmin(Integer isAdmin){this.isAdmin=isAdmin;}

    public Integer getIsAdmin(){return isAdmin;}

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", isAdmin='" + isAdmin + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}