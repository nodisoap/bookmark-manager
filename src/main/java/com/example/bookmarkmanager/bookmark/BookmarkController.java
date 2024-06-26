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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addBookmark(@RequestBody BookmarkDTO bookmarkDTO) {
        bookmarkService.addBookmark(bookmarkDTO);
    }

    @GetMapping
    public List<BookmarkDTO> getAllBookmarks() {
        return bookmarkService.getAllBookmarks();
    }

    @PatchMapping("/{bookmarkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBookmark(
            @PathVariable("bookmarkId") Long bookmarkId,
            @RequestBody BookmarkDTO bookmarkDTO
    ) {
        bookmarkService.updateBookmark(bookmarkId, bookmarkDTO);
    }

    @DeleteMapping("/{bookmarkId}")
    public void deleteBookmark(@PathVariable("bookmarkId") Long bookmarkId) {
        bookmarkService.deleteBookmark(bookmarkId);
    }

    @DeleteMapping
    public void deleteAllBookmarksByOwner() {
        bookmarkService.deleteAllBookmarksByOwner();
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
