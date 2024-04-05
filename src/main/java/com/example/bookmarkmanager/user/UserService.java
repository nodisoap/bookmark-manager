package com.example.bookmarkmanager.user;

import com.example.bookmarkmanager.bookmark.BookmarkService;
import com.example.bookmarkmanager.exception.DuplicateEmailException;
import com.example.bookmarkmanager.exception.DuplicateUsernameException;
import com.example.bookmarkmanager.exception.UnauthorizedException;
import com.example.bookmarkmanager.exception.UserNotFoundException;
import com.example.bookmarkmanager.config.JwtService;
import com.example.bookmarkmanager.folder.FolderService;
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

    private final FolderService folderService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public User updateUser(Long userId, UserUpdateRequest userUpdateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "Unable to update user info: User not found"
                ));

        if (!user.getUsername().equals(currentPrincipalName)) {
            throw new UnauthorizedException("You are not authorized to update this user's info");
        }

        if (userUpdateRequest.getUsername() != null && userUpdateRequest.getUsername().length() > 0 &&
                !Objects.equals(user.getUsername(), userUpdateRequest.getUsername())) {
            Optional<User> userOptional = userRepository.findByUsername(userUpdateRequest.getUsername());
            if (userOptional.isPresent()) {
                throw new DuplicateUsernameException(
                        "Unable to update the username: User with this username already exists"
                );
            }
            user.setUsername(userUpdateRequest.getUsername());
        }
        if (userUpdateRequest.getEmail() != null && userUpdateRequest.getEmail().length() > 0 &&
                !Objects.equals(user.getEmail(), userUpdateRequest.getEmail())) {
            Optional<User> userOptional = userRepository.findByEmail(userUpdateRequest.getEmail());
            if (userOptional.isPresent()) {
                throw new DuplicateEmailException(
                        "Unable to update the email: User with email " + userUpdateRequest.getEmail() + " already exists"
                );
            }
            user.setEmail(userUpdateRequest.getEmail());
        }
        if (userUpdateRequest.getPassword() != null && userUpdateRequest.getPassword().length() > 0) {
            String passwordEncoded = passwordEncoder.encode(userUpdateRequest.getPassword());
            if (!Objects.equals(user.getPassword(), passwordEncoded)) {
                user.setPassword(passwordEncoded);
            }
        }
        return user;
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

        folderService.deleteAllFoldersByOwner();
        userRepository.deleteById(userId);
    }
}
