package com.example.bookmarkmanager.bookmark;

import com.example.bookmarkmanager.folder.Folder;
import com.example.bookmarkmanager.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(exclude = {"owner", "folder"})
@Table(name = "bookmark", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "url", "owner" }),
        @UniqueConstraint(columnNames = { "url", "folder" })
})
public class Bookmark {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String url;
    private String description;

    @ManyToOne()
    @JoinColumn(name = "owner")
    @ToString.Exclude
    @JsonIgnore
    private User owner;

    @ManyToOne()
    @JoinColumn(name = "folder")
    private Folder folder;

    @PrePersist
    public void prePersist() {
        if (name == null || name.isEmpty()) {
            name = "Без имени";
        }
    }

}
