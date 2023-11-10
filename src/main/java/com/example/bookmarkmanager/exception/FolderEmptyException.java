package com.example.bookmarkmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FolderEmptyException extends ResponseStatusException {

    public FolderEmptyException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
