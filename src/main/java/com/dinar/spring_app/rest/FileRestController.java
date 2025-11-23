package com.dinar.spring_app.rest;

import com.dinar.spring_app.database.entity.File;
import com.dinar.spring_app.security.annotation.IsModerator;
import com.dinar.spring_app.security.annotation.IsUser;
import com.dinar.spring_app.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

import static com.dinar.spring_app.utill.SecurityUtils.getCurrentUserFromSecurity;

@RestController
@RequestMapping("/api/v1/files")
public class FileRestController {

    private final FileService fileService;

    public FileRestController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/my-files")
    @IsUser
    public ResponseEntity<List<File>> getMyFiles() {
        var currentUser = getCurrentUserFromSecurity();
        var files = fileService.findAllByUserId(currentUser.getId());
        return files.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(files);
    }

    @GetMapping(value = "id/{id}")
    @IsModerator
    public ResponseEntity<File> getFileById(@PathVariable("id") Long fileId) {
        var file = fileService.findById(fileId)
                              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(file);
    }

    @GetMapping(value = "filename/{filename}")
    @IsModerator
    public ResponseEntity<File> getFileByFileName(@PathVariable String filename) {
        var file = fileService.findByName(filename)
                              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(file);
    }

    @GetMapping
    @IsModerator
    public ResponseEntity<List<File>> getAll() {
        var files = fileService.findAll();

        return files.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(files);
    }

    @GetMapping(value = "user/{userId}")
    @IsModerator
    public ResponseEntity<List<File>> getAllFilesByUserId(@PathVariable Long userId) {
        var files = fileService.findAllByUserId(userId);

        return files.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(files);
    }

    @DeleteMapping(value = "id/{id}")
    @IsModerator
    public ResponseEntity<Void> deleteFileById(@PathVariable("id") Long fileId) {
        fileService.deleteById(fileId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "filename/{fileName}")
    @IsModerator
    public ResponseEntity<Void> deleteFileByFileName(@PathVariable String fileName) {
        fileService.deleteByName(fileName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload")
    @IsUser
    public ResponseEntity<File> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {

        var currentUser = getCurrentUserFromSecurity();
        if (!currentUser.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Can only upload files for yourself");
        }

        try {
            File uploadedFile = fileService.uploadFile(
                    file.getOriginalFilename(),
                    file.getInputStream(),
                    userId
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(uploadedFile);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed");
        }
    }
}
