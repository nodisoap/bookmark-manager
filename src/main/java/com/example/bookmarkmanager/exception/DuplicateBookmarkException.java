package com.example.bookmarkmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DuplicateBookmarkException extends ResponseStatusException {

    public DuplicateBookmarkException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
