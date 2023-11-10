package com.example.bookmarkmanager.auth;

import com.example.bookmarkmanager.exception.DuplicateEmailException;
import com.example.bookmarkmanager.exception.DuplicateUsernameException;
import com.example.bookmarkmanager.exception.ExceptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ExceptionResponse> handleDuplicateUsernameException(DuplicateUsernameException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getStatusCode().value(), e.getReason());
        return new ResponseEntity<>(exceptionResponse, e.getStatusCode());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ExceptionResponse> handleDuplicateEmailException(DuplicateEmailException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getStatusCode().value(), e.getReason());
        return new ResponseEntity<>(exceptionResponse, e.getStatusCode());
    }

}
