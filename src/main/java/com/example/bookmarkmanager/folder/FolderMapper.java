package com.example.bookmarkmanager.folder;

import com.example.bookmarkmanager.auth.AuthenticationService;
import com.example.bookmarkmanager.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FolderMapper {

    public final AuthenticationService authenticationService;

    public FolderDTO mapToDto(Folder folder) {

        FolderDTO folderDTO = new FolderDTO(folder.getName());

        return folderDTO;

    }

    public Folder mapToFolder(FolderDTO folderDTO, User owner) {

        if (folderDTO == null) {
            return null;
        }

        Folder folder = new Folder(
                folderDTO.getName(),
                owner
        );

        return folder;

    }
}
