package com.example.bookmarkmanager.folder;

import com.example.bookmarkmanager.bookmark.Bookmark;
import com.example.bookmarkmanager.bookmark.BookmarkDTO;
import com.example.bookmarkmanager.bookmark.BookmarkService;
import com.example.bookmarkmanager.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;
    private final BookmarkService bookmarkService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addFolder(@RequestParam String name) {
        folderService.addFolder(name);
    }

    @GetMapping
    public List<FolderDTO> getAllFolders() {
        return folderService.getAllFolders();
    }

    @GetMapping("/{folderId}")
    public List<BookmarkDTO> getBookmarksByFolder(@PathVariable Long folderId) {
        return bookmarkService.getBookmarksByFolder(folderId);
    }

    @PatchMapping("/{folderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateFolder(
            @PathVariable("folderId") Long folderId,
            @RequestParam String name
    ) {
        folderService.updateFolder(folderId, name);
    }

    @DeleteMapping("/{folderId}")
    public void deleteFolder(@PathVariable("folderId") Long folderId) {
        folderService.deleteFolder(folderId);
    }

    @DeleteMapping
    public void deleteAllFoldersByOwner() {
        folderService.deleteAllFoldersByOwner();
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
