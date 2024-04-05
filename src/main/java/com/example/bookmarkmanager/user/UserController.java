package com.example.bookmarkmanager.user;

import com.example.bookmarkmanager.auth.AuthenticationService;
import com.example.bookmarkmanager.config.JwtService;
import com.example.bookmarkmanager.exception.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PatchMapping("/{userId}")
    public ResponseEntity<?> updateAccount(
            @PathVariable("userId") Long userId,
            @RequestBody UserUpdateRequest userUpdateRequest,
            HttpServletResponse response) {

        User updatedUser = userService.updateUser(userId, userUpdateRequest);
        String jwtToken = jwtService.generateToken(updatedUser);

        if (jwtToken != null) {
            return ResponseEntity.ok().body(jwtToken);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping("/{userId}")
    public void deleteAccount(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ExceptionResponse> handleDuplicateUsernameException(DuplicateUsernameException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getStatusCode().value(), e.getReason());
        return new ResponseEntity<>(exceptionResponse, e.getStatusCode());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ExceptionResponse> handleDuplicateEmailException(DuplicateEmailException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getStatusCode().value(), e.getReason());
        return new ResponseEntity<>(exceptionResponse, e.getStatusCode());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionResponse> handleUnauthorizedException(UnauthorizedException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getStatusCode().value(), e.getReason());
        return new ResponseEntity<>(exceptionResponse, e.getStatusCode());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFoundException(UserNotFoundException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getStatusCode().value(), e.getReason());
        return new ResponseEntity<>(exceptionResponse, e.getStatusCode());
    }

}
