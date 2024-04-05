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
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final BookmarkMapper bookmarkMapper;
    private final FolderRepository folderRepository;
    private final AuthenticationService authenticationService;

    public List<BookmarkDTO> getAllBookmarks() {
        User owner = authenticationService.getAuthenticatedUser("Unable to show all bookmarks: User not found");

        List<BookmarkDTO> bookmarkDTOs = bookmarkRepository
                .findByOwner(owner)
                .stream()
                .map(bookmarkMapper::mapToDto).toList();

        if (bookmarkDTOs.isEmpty()) {
            throw new NoBookmarksCreatedException(
                    "Unable to show all bookmarks: No bookmarks created"
            );
        }

        return bookmarkDTOs;
    }

    public List<BookmarkDTO> getBookmarksByFolder(Long folderId) {
        Optional<Folder> folderOptional = folderRepository.findById(folderId);
        if (folderOptional.isEmpty()) {
            throw new FolderNotFoundException(
                    "Unable to show bookmarks in given folder: Folder with id " + folderId + " not found"
            );
        }

        List<BookmarkDTO> bookmarkDTOs = bookmarkRepository
                .findByFolderId(folderId)
                .stream()
                .map(bookmarkMapper::mapToDto).toList();
        if (bookmarkDTOs.isEmpty()) {
            throw new FolderEmptyException(
                    "Unable to show bookmarks in given folder: Folder with id " + folderId + " is empty"
            );
        }

        return bookmarkDTOs;
    }

    @Transactional
    public void addBookmark(BookmarkDTO bookmarkDTO) {
        User owner = authenticationService.getAuthenticatedUser("Unable to add the bookmark: User not found");

        Folder folder = folderRepository.findById(bookmarkDTO.getFolderId())
                .orElseThrow(() -> new FolderNotFoundException(
                        "Unable to add the bookmark: Folder with id " + bookmarkDTO.getFolderId() +
                                " does not exist"
                ));

        Bookmark bookmark = bookmarkMapper.mapToBookmark(bookmarkDTO, owner, folder);
        Optional<Bookmark> bookmarkOptional = bookmarkRepository.findByUrlAndFolder(bookmark.getUrl(), folder);
        if (bookmarkOptional.isPresent()) {
            throw new DuplicateBookmarkException(
                    "Unable to add the bookmark: Bookmark with this url already exists in given folder"
            );
        }

        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void updateBookmark(Long bookmarkId, BookmarkDTO bookmarkDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new BookmarkNotFoundException(
                        "Unable to update the bookmark: Bookmark with id " + bookmarkId + " does not exist."
                ));

        if (!bookmark.getOwner().getUsername().equals(currentPrincipalName)) {
            throw new UnauthorizedException("You are not authorized to update this bookmark");
        }

        if (bookmarkDTO.getFolderId() != null &&
                !Objects.equals(bookmark.getFolder().getId(), bookmarkDTO.getFolderId())
        ) {
            Folder folder = folderRepository.findById(bookmarkDTO.getFolderId())
                    .orElseThrow(() -> new FolderNotFoundException(
                            "Unable to update the bookmark: Folder with id " + bookmarkDTO.getFolderId() +
                                    " does not exist."
                    ));
            Optional<Bookmark> bookmarkOptional = bookmarkRepository
                    .findByUrlAndFolder(bookmarkDTO.getUrl(), folder);
            if (bookmarkOptional.isPresent()) {
                throw new DuplicateBookmarkException(
                        "Unable to update the bookmark: Bookmark with this url already exists in given folder"
                );
            }
            bookmark.setFolder(folder);
        }

        if (bookmarkDTO.getName() != null && bookmarkDTO.getName().length() > 0 &&
                !Objects.equals(bookmark.getName(), bookmarkDTO.getName())
        ) {
            bookmark.setName(bookmarkDTO.getName());
        }

        if (bookmarkDTO.getUrl() != null && bookmarkDTO.getUrl().length() > 0 &&
                !Objects.equals(bookmark.getUrl(), bookmarkDTO.getUrl())
        ) {
            Folder folder = bookmark.getFolder();
            Optional<Bookmark> bookmarkOptional = bookmarkRepository
                    .findByUrlAndFolder(bookmarkDTO.getUrl(), folder);
            if (bookmarkOptional.isPresent()) {
                throw new DuplicateBookmarkException(
                        "Unable to update the bookmark: Bookmark with this url already exists in given folder"
                );
            }
            bookmark.setUrl(bookmarkDTO.getUrl());
        }

        if (bookmarkDTO.getDescription() != null &&
                !Objects.equals(bookmark.getDescription(), bookmarkDTO.getDescription())) {
            bookmark.setDescription(bookmarkDTO.getDescription());
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

    public void deleteBookmarksByFolder(Long folderId) {
        List<Bookmark> bookmarks = bookmarkRepository.findByFolderId(folderId);
        if (bookmarks.isEmpty()) {
            throw new FolderEmptyException(
                    "Unable to delete bookmarks in given folder: Folder with id " + folderId + " is empty"
            );
        } else {
            for (Bookmark bookmark : bookmarks) {
                deleteBookmark(bookmark.getId());
            }
        }
    }
}
