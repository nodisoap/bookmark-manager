package com.example.bookmarkmanager.user;

import com.example.bookmarkmanager.bookmark.BookmarkService;
import com.example.bookmarkmanager.exception.DuplicateEmailException;
import com.example.bookmarkmanager.exception.DuplicateUsernameException;
import com.example.bookmarkmanager.exception.UnauthorizedException;
import com.example.bookmarkmanager.exception.UserNotFoundException;
import com.example.bookmarkmanager.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BookmarkService bookmarkService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public String updateUser(Long userId, UserUpdateRequest updateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "Unable to update user info: User not found"
                ));

        if (!user.getUsername().equals(currentPrincipalName)) {
            throw new UnauthorizedException("You are not authorized to update this user's info");
        }

        String jwtToken = null;

        if (updateRequest.getUsername() != null && updateRequest.getUsername().length() > 0 &&
                !Objects.equals(user.getUsername(), updateRequest.getUsername())) {
            Optional<User> userOptional = userRepository.findByUsername(updateRequest.getUsername());
            if (userOptional.isPresent()) {
                throw new DuplicateUsernameException(
                        "Unable to update the username: User with this username already exists"
                );
            }
            user.setUsername(updateRequest.getUsername());
            jwtToken = jwtService.generateToken(user);
        }
        if (updateRequest.getEmail() != null && updateRequest.getEmail().length() > 0 &&
                !Objects.equals(user.getEmail(), updateRequest.getEmail())) {
            Optional<User> userOptional = userRepository.findByEmail(updateRequest.getEmail());
            if (userOptional.isPresent()) {
                throw new DuplicateEmailException(
                        "Unable to update the email: User with email " + updateRequest.getEmail() + " already exists"
                );
            }
            user.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getPassword() != null && updateRequest.getPassword().length() > 0) {
            String passwordEncoded = passwordEncoder.encode(updateRequest.getPassword());
            if (!Objects.equals(user.getPassword(), passwordEncoded)) {
                user.setPassword(passwordEncoded);
            }
        }
        return jwtToken;
    }

    public void deleteUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "Unable to delete the user: User with id " + userId + " does not exist"
                ));

        if (!user.getUsername().equals(currentPrincipalName)) {
            throw new UnauthorizedException("You are not authorized to delete this user");
        }

        bookmarkService.deleteAllFoldersByOwner();
        userRepository.deleteById(userId);
    }
}
