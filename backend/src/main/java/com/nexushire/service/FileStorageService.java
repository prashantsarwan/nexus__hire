package com.nexushire.service;

import com.nexushire.exception.ApiException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path rootPath;

    @PostConstruct
    void init() {
        try {
            rootPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(rootPath.resolve("resumes"));
            Files.createDirectories(rootPath.resolve("logos"));
        } catch (IOException ex) {
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }

    public String storeResume(MultipartFile file, Long userId) {
        validate(file);
        String filename = userId + "_" + UUID.randomUUID() + ".pdf";
        try {
            Files.copy(file.getInputStream(),
                    rootPath.resolve("resumes").resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store file");
        }
        return "/uploads/resumes/" + filename;
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "File is empty");
        }
        String ct = file.getContentType();
        if (ct == null || !ct.equals("application/pdf")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only PDF files are allowed");
        }
        if (file.getSize() > 10L * 1024 * 1024) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "File size must not exceed 10 MB");
        }
    }
}
