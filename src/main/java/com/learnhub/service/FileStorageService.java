package com.learnhub.service;

import com.learnhub.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${learnhub.upload.dir:uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new BusinessException("Could not create upload directory: " + ex.getMessage());
        }
    }

    public String storeFile(MultipartFile file, String subFolder) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Cannot store empty file");
        }

        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "file"
        );

        if (originalFilename.contains("..")) {
            throw new BusinessException("Filename contains invalid path sequence: " + originalFilename);
        }

        try {
            Path targetDir = this.fileStorageLocation;
            if (subFolder != null && !subFolder.trim().isEmpty()) {
                targetDir = this.fileStorageLocation.resolve(subFolder);
                Files.createDirectories(targetDir);
            }

            // Generate collision-proof file name
            String fileExtension = "";
            int extIndex = originalFilename.lastIndexOf(".");
            if (extIndex > 0) {
                fileExtension = originalFilename.substring(extIndex);
            }
            String storedFileName = UUID.randomUUID().toString() + fileExtension;

            Path targetLocation = targetDir.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return (subFolder != null && !subFolder.trim().isEmpty())
                    ? subFolder + "/" + storedFileName
                    : storedFileName;

        } catch (IOException ex) {
            throw new BusinessException("Failed to store file: " + ex.getMessage());
        }
    }

    public Resource loadFileAsResource(String relativePath) {
        try {
            Path filePath = this.fileStorageLocation.resolve(relativePath).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new BusinessException("File not found or unreadable: " + relativePath);
            }
        } catch (MalformedURLException ex) {
            throw new BusinessException("Invalid file URL: " + relativePath);
        }
    }
}
