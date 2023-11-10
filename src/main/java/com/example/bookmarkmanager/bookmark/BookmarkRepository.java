package com.example.bookmarkmanager.bookmark;


import com.example.bookmarkmanager.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findByOwner(User owner);

    Optional<Bookmark> findByUrlAndOwner(String url, User owner);

    List<Bookmark> findByFolderId(Long folderId);
}
