package com.example.bookmarkmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FolderNotFoundException extends ResponseStatusException {

    public FolderNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
