package com.dinar.spring_app.service.impl;

import com.dinar.spring_app.database.entity.Event;
import com.dinar.spring_app.database.entity.File;
import com.dinar.spring_app.database.entity.User;
import com.dinar.spring_app.database.entity.enums.EventStatus;
import com.dinar.spring_app.database.entity.enums.FileStatus;
import com.dinar.spring_app.database.repository.EventRepository;
import com.dinar.spring_app.database.repository.FileRepository;
import com.dinar.spring_app.database.repository.UserRepository;
import com.dinar.spring_app.exception.ServiceException;
import com.dinar.spring_app.service.FileService;
import io.minio.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final MinioClient minioClient;
    private final String bucketName = "my-bucket";

    public FileServiceImpl(FileRepository fileRepository,
                           UserRepository userRepository,
                           EventRepository eventRepository,
                           MinioClient minioClient) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.minioClient = minioClient;
    }

    @Transactional
    @Override
    public File uploadFile(String filename, InputStream fileContent, Long userId) throws IOException {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new ServiceException("user not found"));

        var s3key = "files/" + userId + "/" + filename;

        try {
            var exists = minioClient.bucketExists(BucketExistsArgs.builder()
                                            .bucket(bucketName)
                                                             .build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                                   .bucket(bucketName)
                                                     .build());
            }

            minioClient.putObject(PutObjectArgs.builder()
                               .bucket(bucketName)
                               .object(s3key)
                               .stream(fileContent, fileContent.available(), -1)
                                               .build());
        } catch (Exception e) {
            throw  new ServiceException("Failed to upload file to MiniOЖ " + filename, e);
        }



        File file = File.builder()
                        .name(filename)
                        .location(s3key)
                        .user(user)
                        .status(FileStatus.ACTIVE)
                        .build();
        var saved = fileRepository.save(file);

        var event = Event.builder()
                         .user(user)
                         .file(saved)
                         .status(EventStatus.CREATED)
                         .build();
        eventRepository.save(event);

        return saved;
    }

    @Override
    public Optional<File> findByName(String fileName) {
        return fileRepository.findByName(fileName);
    }

    @Override
    public boolean existByName(String fileName) {
        return fileRepository.existsByName(fileName);
    }

    @Override
    @Transactional
    public void deleteByName(String fileName) {
        File file = fileRepository.findByName(fileName)
                                  .orElseThrow(() -> new ServiceException("File not found with fileName: " + fileName));
        deleteFileWithEvent(file);
        fileRepository.delete(file);

    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new ServiceException("File not found with id: " + id));
        deleteFileWithEvent(file);
        fileRepository.deleteById(id);
    }

    @Override
    public List<File> findAllByUserId(Long userId) {
        return fileRepository.findAllByUserId(userId);
    }

    @Override
    public Optional<File> findById(Long id) {
        return fileRepository.findById(id);
    }

    @Override
    public List<File> findAll() {
        return fileRepository.findAll();
    }

    @Override
    public boolean existById(Long id) {
        return fileRepository.existsById(id);
    }

    private void deleteFileWithEvent(File file) {
        User user = file.getUser();

        if (user == null) {
            throw new ServiceException("User not found for file: " + file.getName());
        }

        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                               .bucket(bucketName)
                               .object(file.getLocation())
                                                     .build());
        } catch (Exception e) {
            throw new ServiceException("Failed to delete file from storage: " + file.getName(), e);
        }

        Event eventEntity = Event.builder()
                                 .user(user)
                                 .file(file)
                                 .status(EventStatus.DELETED)
                                 .build();
        eventRepository.save(eventEntity);
    }
}
