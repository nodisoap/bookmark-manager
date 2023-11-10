package com.example.bookmarkmanager.folder;

import com.example.bookmarkmanager.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    Optional<Folder> findByNameAndOwner(String name, User owner);

    List<Folder> findByOwner(User owner);
}
