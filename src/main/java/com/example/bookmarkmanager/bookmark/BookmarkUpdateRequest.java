package com.example.bookmarkmanager.bookmark;

import lombok.Data;

@Data
public class BookmarkUpdateRequest {
    private String name;
    private String url;
    private String description;
    private Long folderId;
}
