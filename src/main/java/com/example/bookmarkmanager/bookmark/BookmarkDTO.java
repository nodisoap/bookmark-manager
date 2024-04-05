package com.example.bookmarkmanager.bookmark;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDTO {

    private String name;
    private String url;
    private String description;
    private Long folderId;

}
