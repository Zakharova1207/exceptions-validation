package com.example.demo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public class User {
    Long id;
    String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotBlank
    String email;
    int orders = 0;

    public User(String name, String email, int orders) {
        this.name = name;
        this.email = email;
        this.orders = orders;
    }
}
