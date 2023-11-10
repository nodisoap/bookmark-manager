package com.example.bookmarkmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DuplicateFolderException extends ResponseStatusException {

    public DuplicateFolderException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
