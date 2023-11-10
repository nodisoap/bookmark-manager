package com.example.bookmarkmanager.folder;

import com.example.bookmarkmanager.bookmark.Bookmark;
import com.example.bookmarkmanager.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(exclude = {"owner", "bookmarks"})
@Table(name = "folder", uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "owner" }) })
public class Folder {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne()
    @JoinColumn(name = "owner")
    @ToString.Exclude
    @JsonIgnore
    private User owner;

    @OneToMany(mappedBy = "folder", fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnore
    private Set<Bookmark> bookmarks;
}
