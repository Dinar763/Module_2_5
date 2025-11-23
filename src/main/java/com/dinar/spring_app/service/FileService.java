package com.dinar.spring_app.service;

import com.dinar.spring_app.database.entity.File;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface FileService extends GenericService <File> {

    File uploadFile(String filename, InputStream fileContent, Long userId) throws IOException;
    Optional<File> findByName(String fileName);
    boolean existByName(String fileName);
    void deleteByName(String fileName);
    List<File> findAllByUserId(Long userId);
}
