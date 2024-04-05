package com.example.bookmarkmanager.bookmark;

import com.example.bookmarkmanager.auth.AuthenticationService;
import com.example.bookmarkmanager.exception.FolderNotFoundException;
import com.example.bookmarkmanager.folder.Folder;
import com.example.bookmarkmanager.folder.FolderRepository;
import com.example.bookmarkmanager.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookmarkMapper {

    public final FolderRepository folderRepository;
    public final AuthenticationService authenticationService;

    public BookmarkDTO mapToDto(Bookmark bookmark) {
        BookmarkDTO bookmarkDTO = new BookmarkDTO(
                bookmark.getName(),
                bookmark.getUrl(),
                bookmark.getDescription(),
                bookmark.getFolder().getId()
        );

        return bookmarkDTO;
    }

    public Bookmark mapToBookmark(BookmarkDTO bookmarkDTO, User owner, Folder folder) {
        if (bookmarkDTO == null) {
            return null;
        }

        Bookmark bookmark = new Bookmark(
                bookmarkDTO.getName(),
                bookmarkDTO.getUrl(),
                bookmarkDTO.getDescription(),
                owner,
                folder
        );

        return bookmark;
    }

}
