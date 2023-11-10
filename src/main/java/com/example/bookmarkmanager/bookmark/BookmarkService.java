package com.example.bookmarkmanager.bookmark;

import com.example.bookmarkmanager.auth.AuthenticationService;
import com.example.bookmarkmanager.exception.*;
import com.example.bookmarkmanager.folder.Folder;
import com.example.bookmarkmanager.folder.FolderRepository;
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
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final FolderRepository folderRepository;
    private final AuthenticationService authenticationService;

    public List<Bookmark> getAllBookmarks() {
        User owner = authenticationService.getAuthenticatedUser("Unable to show all bookmarks: User not found");
        List<Bookmark> bookmarks = bookmarkRepository.findByOwner(owner);
        if (bookmarks.isEmpty()) {
            throw new NoBookmarksCreatedException(
                    "Unable to show all bookmarks: No bookmarks created"
            );
        }
        return bookmarks;
    }

    public List<Bookmark> getBookmarksByFolder(Long folderId) {
        Optional<Folder> folderOptional = folderRepository.findById(folderId);
        if (folderOptional.isEmpty()) {
            throw new FolderNotFoundException(
                    "Unable to show bookmarks in given folder: Folder with id " + folderId + " not found"
            );
        }
        List<Bookmark> bookmarks = bookmarkRepository.findByFolderId(folderId);
        if (bookmarks.isEmpty()) {
            throw new FolderEmptyException(
                    "Unable to show bookmarks in given folder: Folder with id " + folderId + " is empty"
            );
        }
        return bookmarks;
    }

    public List<Folder> getAllFolders() {
        User owner = authenticationService.getAuthenticatedUser("Unable to show all folders: User not found");
        List<Folder> folders = folderRepository.findByOwner(owner);
        if (folders.isEmpty()) {
            throw new NoFoldersCreatedException(
                    "Unable to show all folders: No folders created"
            );
        }
        return folders;
    }

    @Transactional
    public void addBookmark(Bookmark bookmark) {
        User owner = authenticationService.getAuthenticatedUser("Unable to add the bookmark: User not found");
        Optional<Bookmark> bookmarkOptional = bookmarkRepository.findByUrlAndOwner(bookmark.getUrl(), owner);
        if (bookmarkOptional.isPresent()) {
            throw new DuplicateBookmarkException(
                    "Unable to add the bookmark: Bookmark with this url already exists"
            );
        }
        Optional<Folder> folderOptional = folderRepository.findById(bookmark.getFolder().getId());
        if (folderOptional.isEmpty()) {
            throw new FolderNotFoundException(
                    "Unable to add the bookmark: Folder with id " + bookmark.getFolder().getId() +
                            " does not exist"
            );
        }
        bookmark.setOwner(owner);
        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void addFolder(Folder folder) {
        User owner = authenticationService.getAuthenticatedUser("Unable to add the folder: User not found");
        Optional<Folder> folderOptional = folderRepository.findByNameAndOwner(folder.getName(), owner);
        if (folderOptional.isPresent()) {
            throw new DuplicateFolderException(
                    "Unable to add the folder: Folder with this name already exists"
            );
        }
        folder.setOwner(owner);
        folderRepository.save(folder);
    }

    @Transactional
    public void updateBookmark(Long bookmarkId, BookmarkUpdateRequest updateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new BookmarkNotFoundException(
                        "Unable to update the bookmark: Bookmark with id " + bookmarkId + " does not exist."
                ));

        if (!bookmark.getOwner().getUsername().equals(currentPrincipalName)) {
            throw new UnauthorizedException("You are not authorized to update this bookmark");
        }

        if (updateRequest.getFolderId() != null &&
                !Objects.equals(bookmark.getFolder().getId(), updateRequest.getFolderId())
        ) {
            Folder folder = folderRepository.findById(updateRequest.getFolderId())
                    .orElseThrow(() -> new FolderNotFoundException(
                            "Unable to update the bookmark: Folder with id " + updateRequest.getFolderId() +
                                    " does not exist."
                    ));
            bookmark.setFolder(folder);
        }

        if (updateRequest.getName() != null && updateRequest.getName().length() > 0 &&
                !Objects.equals(bookmark.getName(), updateRequest.getName())
        ) {
            bookmark.setName(updateRequest.getName());
        }

        if (updateRequest.getUrl() != null && updateRequest.getUrl().length() > 0 &&
                !Objects.equals(bookmark.getUrl(), updateRequest.getUrl())
        ) {
            User owner = bookmark.getOwner();
            Optional<Bookmark> bookmarkOptional = bookmarkRepository.findByUrlAndOwner(updateRequest.getUrl(), owner);
            if (bookmarkOptional.isPresent()) {
                throw new DuplicateBookmarkException(
                        "Unable to update the bookmark: Bookmark with this url already exists"
                );
            }
            bookmark.setUrl(updateRequest.getUrl());
        }

        if (updateRequest.getDescription() != null &&
                !Objects.equals(bookmark.getDescription(), updateRequest.getDescription())) {
            bookmark.setDescription(updateRequest.getDescription());
        }
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

    public void deleteBookmark(Long bookmarkId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                        .orElseThrow(() -> new BookmarkNotFoundException(
                                "Unable to delete the bookmark: Bookmark with id " + bookmarkId +
                                        " does not exist"
                        ));

        if (!bookmark.getOwner().getUsername().equals(currentPrincipalName)) {
            throw new UnauthorizedException("You are not authorized to delete this bookmark");
        }

        bookmarkRepository.deleteById(bookmarkId);
    }

    public void deleteAllBookmarksByOwner() {
        User owner = authenticationService.getAuthenticatedUser("Unable to delete all bookmarks: User not found");

        List<Bookmark> bookmarks = bookmarkRepository.findByOwner(owner);
        if (bookmarks.isEmpty()) {
            throw new NoBookmarksCreatedException(
                    "Unable to delete all bookmarks: No bookmarks created"
            );
        }

        for (Bookmark bookmark : bookmarks) {
            deleteBookmark(bookmark.getId());
        }
    }

    public void deleteFolder(Long folderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException(
                        "Unable to delete the folder: Folder with id " + folderId +
                                " does not exist"
                ));

        if (!folder.getOwner().getUsername().equals(currentPrincipalName)) {
            throw new UnauthorizedException("You are not authorized to delete this folder");
        }

        List<Bookmark> bookmarks = bookmarkRepository.findByFolderId(folderId);
        for (Bookmark bookmark : bookmarks) {
            deleteBookmark(bookmark.getId());
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
