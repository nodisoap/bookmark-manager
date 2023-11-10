package com.example.bookmarkmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoBookmarksCreatedException extends ResponseStatusException {

    public NoBookmarksCreatedException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
