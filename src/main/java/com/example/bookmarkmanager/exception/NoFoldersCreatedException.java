package com.example.bookmarkmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoFoldersCreatedException extends ResponseStatusException {

    public NoFoldersCreatedException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
