package com.example.bookmarkmanager.bookmark;

import com.example.bookmarkmanager.exception.*;
import com.example.bookmarkmanager.folder.Folder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/create/bookmark")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBookmark(@RequestBody Bookmark bookmark) {
        bookmarkService.addBookmark(bookmark);
    }

    @PostMapping("/create/folder")
    @ResponseStatus(HttpStatus.CREATED)
    public void addFolder(@RequestBody Folder folder) {
        bookmarkService.addFolder(folder);
    }

    @GetMapping("/view")
    public List<Bookmark> getAllBookmarks() {
        return bookmarkService.getAllBookmarks();
    }

    @GetMapping("/view/{folderId}")
    public List<Bookmark> getBookmarksByFolder(@PathVariable Long folderId) {
        return bookmarkService.getBookmarksByFolder(folderId);
    }

    @GetMapping("/view/folders")
    public List<Folder> getAllFolders() {
        return bookmarkService.getAllFolders();
    }

    @PatchMapping("/edit/bookmark/{bookmarkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBookmark(
            @PathVariable("bookmarkId") Long bookmarkId,
            @RequestBody BookmarkUpdateRequest updateRequest
    ) {
        bookmarkService.updateBookmark(bookmarkId, updateRequest);
    }

    @PatchMapping("/edit/folder/{folderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateFolder(
            @PathVariable("folderId") Long folderId,
            @RequestParam String name
    ) {
        bookmarkService.updateFolder(folderId, name);
    }

    @DeleteMapping("/delete/bookmark/{bookmarkId}")
    public void deleteBookmark(@PathVariable("bookmarkId") Long bookmarkId) {
        bookmarkService.deleteBookmark(bookmarkId);
    }

    @DeleteMapping("/delete/bookmarks")
    public void deleteAllBookmarksByOwner() {
        bookmarkService.deleteAllBookmarksByOwner();
    }

    @DeleteMapping("/delete/folder/{folderId}")
    public void deleteFolder(@PathVariable("folderId") Long folderId) {
        bookmarkService.deleteFolder(folderId);
    }

    @DeleteMapping("/delete/folders")
    public void deleteAllFoldersByOwner() {
        bookmarkService.deleteAllFoldersByOwner();
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalStateException(IllegalStateException e) {
        return e.getMessage();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFoundException(UserNotFoundException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getStatusCode().value(), e.getReason());
        return new ResponseEntity<>(exceptionResponse, e.getStatusCode());
    }

    @ExceptionHandler(NoBookmarksCreatedException.class)
    public ResponseEntity<ExceptionResponse> handleNoBookmarksFoundException(NoBookmarksCreatedException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getStatusCode().value(), e.getReason());
        return new ResponseEntity<>(exceptionResponse, e.getStatusCode());
    }

    @ExceptionHandler(FolderEmptyException.class)
    public ResponseEntity<ExceptionResponse> handleFolderEmptyException(FolderEmptyException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getStatusCode().value(), e.getReason());
        return new ResponseEntity<>(exceptionResponse, e.getStatusCode());
    }

    @ExceptionHandler(FolderNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleFolderNotFoundException(FolderNotFoundException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getStatusCode().value(), e.getReason());
        return new ResponseEntity<>(exceptionResponse, e.getStatusCode());
    }

    @ExceptionHandler(NoFoldersCreatedException.class)
    public ResponseEntity<ExceptionResponse> handleNoFoldersCreatedException(NoFoldersCreatedException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getStatusCode().value(), e.getReason());
        return new ResponseEntity<>(exceptionResponse, e.getStatusCode());
    }

    @ExceptionHandler(BookmarkNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleBookmarkNotFoundException(BookmarkNotFoundException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getStatusCode().value(), e.getReason());
        return new ResponseEntity<>(exceptionResponse, e.getStatusCode());
    }

    @ExceptionHandler(DuplicateBookmarkException.class)
    public ResponseEntity<ExceptionResponse> handleDuplicateBookmarkException(DuplicateBookmarkException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getStatusCode().value(), e.getReason());
        return new ResponseEntity<>(exceptionResponse, e.getStatusCode());
    }

    @ExceptionHandler(DuplicateFolderException.class)
    public ResponseEntity<ExceptionResponse> handleDuplicateFolderException(DuplicateFolderException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getStatusCode().value(), e.getReason());
        return new ResponseEntity<>(exceptionResponse, e.getStatusCode());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionResponse> handleUnauthorizedException(UnauthorizedException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getStatusCode().value(), e.getReason());
        return new ResponseEntity<>(exceptionResponse, e.getStatusCode());
    }

}
