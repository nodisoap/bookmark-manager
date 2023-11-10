package com.example.bookmarkmanager.user;

import lombok.Data;

@Data
public class UserUpdateRequest {
    String username;
    String email;
    String password;
}
