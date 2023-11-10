package com.example.bookmarkmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DuplicateUsernameException extends ResponseStatusException {

    public DuplicateUsernameException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
