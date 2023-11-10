package com.example.bookmarkmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BookmarkNotFoundException extends ResponseStatusException {

    public BookmarkNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
