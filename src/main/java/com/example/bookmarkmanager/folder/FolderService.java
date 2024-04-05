package com.example.bookmarkmanager.folder;

import com.example.bookmarkmanager.auth.AuthenticationService;
import com.example.bookmarkmanager.bookmark.Bookmark;
import com.example.bookmarkmanager.bookmark.BookmarkService;
import com.example.bookmarkmanager.exception.DuplicateFolderException;
import com.example.bookmarkmanager.exception.FolderNotFoundException;
import com.example.bookmarkmanager.exception.NoFoldersCreatedException;
import com.example.bookmarkmanager.exception.UnauthorizedException;
import com.example.bookmarkmanager.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FolderService {

    private final FolderRepository folderRepository;
    private final AuthenticationService authenticationService;
    private final FolderMapper folderMapper;
    private final BookmarkService bookmarkService;

    public List<FolderDTO> getAllFolders() {
        User owner = authenticationService.getAuthenticatedUser("Unable to show all folders: User not found");

        List<FolderDTO> folderDTOs = folderRepository
                .findByOwner(owner)
                .stream()
                .map(folderMapper::mapToDto).toList();

        if (folderDTOs.isEmpty()) {
            throw new NoFoldersCreatedException(
                    "Unable to show all folders: No folders created"
            );
        }

        return folderDTOs;
    }

    @Transactional
    public void addFolder(String name) {
        User owner = authenticationService.getAuthenticatedUser("Unable to add the folder: User not found");

        Optional<Folder> folderOptional = folderRepository.findByNameAndOwner(name, owner);
        if (folderOptional.isPresent()) {
            throw new DuplicateFolderException(
                    "Unable to add the folder: Folder with this name already exists"
            );
        }

        Folder folder = new Folder();
        folder.setName(name);
        folder.setOwner(owner);

        folderRepository.save(folder);
    }

    @Transactional
    public void updateFolder(Long folderId, String name) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException(
                        "Unable to update the folder: Folder with id " + folderId + " does not exist"
                ));

        if (!folder.getOwner().getUsername().equals(currentPrincipalName)) {
            throw new UnauthorizedException("You are not authorized to update this folder");
        }

        if (name != null && name.length() > 0 && !Objects.equals(folder.getName(), name)) {
            folder.setName(name);
        }
    }

    public void deleteFolder(Long folderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException(
                        "Unable to delete the folder: Folder with id " + folderId + " does not exist"
                ));

        if (!folder.getOwner().getUsername().equals(currentPrincipalName)) {
            throw new UnauthorizedException("You are not authorized to delete this folder");
        }

        if (!folder.getBookmarks().isEmpty()) {
            bookmarkService.deleteBookmarksByFolder(folderId);
        }

        folderRepository.deleteById(folderId);
    }

    public void deleteAllFoldersByOwner() {
        User owner = authenticationService.getAuthenticatedUser("Unable to delete all folders : User not found");

        List<Folder> folders = folderRepository.findByOwner(owner);
        for (Folder folder : folders) {
            deleteFolder(folder.getId());
        }
    }

}
