package com.dinar.spring_app.rest;

import com.dinar.spring_app.database.entity.File;
import com.dinar.spring_app.database.entity.User;
import com.dinar.spring_app.dto.FileDto;
import com.dinar.spring_app.security.annotation.IsAdmin;
import com.dinar.spring_app.security.annotation.IsModerator;
import com.dinar.spring_app.security.annotation.IsUser;
import com.dinar.spring_app.service.FileService;
import com.dinar.spring_app.service.UserService;
import com.dinar.spring_app.utill.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "Files API", description = "Управление файлами")
public class FileRestController {

    private final FileService fileService;
    private final UserService userService;

    public FileRestController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @GetMapping("/my-files")
    @IsUser
    @Operation(summary = "Получить мои файлы")
    public ResponseEntity<List<FileDto>> getMyFiles() {
        String username = SecurityUtils.getCurrentUsername();
        User currentUser = userService.findByUserName(username)
                                      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var files = fileService.findAllByUserId(currentUser.getId());

        return files.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(files.stream()
                                         .map(FileDto::from)
                                         .toList());
    }

    @GetMapping(value = "/id/{id}")
    @IsModerator
    @Operation(summary = "Получить файл по Id")
    public ResponseEntity<FileDto> getFileById(@PathVariable("id") Long fileId) {
        var file = fileService.findById(fileId)
                              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(FileDto.from(file));
    }

    @GetMapping(value = "/filename/{filename}")
    @IsModerator
    @Operation(summary = "Получить файл по имени файла")
    public ResponseEntity<FileDto> getFileByFileName(@PathVariable String filename) {
        var file = fileService.findByName(filename)
                              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(FileDto.from(file));
    }

    @GetMapping()
    @IsModerator
    @Operation(summary = "Получить список всех файлов")
    public ResponseEntity<List<FileDto>> getAll() {
        var files = fileService.findAll();

        return files.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(files.stream()
                                         .map(FileDto::from)
                                         .toList());
    }

    @GetMapping(value = "/user/{userId}")
    @IsModerator
    @Operation(summary = "Получить список всех файлов по ID пользователя")
    public ResponseEntity<List<FileDto>> getAllFilesByUserId(@PathVariable Long userId) {
        var files = fileService.findAllByUserId(userId);

        return files.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(files.stream()
                                         .map(FileDto::from)
                                         .toList());
    }

    @DeleteMapping(value = "/id/{id}")
    @IsAdmin
    @Operation(summary = "Удалить файл по ID файла")
    public ResponseEntity<Void> deleteFileById(@PathVariable("id") Long fileId) {
        fileService.deleteById(fileId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/filename/{fileName}")
    @IsAdmin
    @Operation(summary = "Удалить файл по имени файла")
    public ResponseEntity<Void> deleteFileByFileName(@PathVariable String fileName) {
        fileService.deleteByName(fileName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload")
    @IsUser
    @Operation(summary = "Загрузить файл")
    public ResponseEntity<FileDto> uploadFile(
            @RequestParam("file") MultipartFile file) {

        String username = SecurityUtils.getCurrentUsername();
        User currentUser = userService.findByUserName(username)
                                      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        try {
            File uploadedFile = fileService.uploadFile(
                    file.getOriginalFilename(),
                    file.getInputStream(),
                    currentUser.getId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(FileDto.from(uploadedFile));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed");
        }
    }
}
