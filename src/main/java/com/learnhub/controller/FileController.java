package com.learnhub.controller;

import com.learnhub.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Files", description = "Local file upload and media streaming endpoints")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/api/files/upload")
    @Operation(summary = "Upload a file locally (images, assignments, videos, documents)")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder
    ) {
        String storedPath = fileStorageService.storeFile(file, folder);

        Map<String, String> response = new HashMap<>();
        response.put("fileName", file.getOriginalFilename());
        response.put("filePath", storedPath);
        response.put("fileUrl", "/api/files/" + storedPath);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/files/**")
    @Operation(summary = "Download or stream an uploaded file")
    public ResponseEntity<Resource> serveFile(HttpServletRequest request) {
        String path = request.getRequestURI().substring("/api/files/".length());
        Resource resource = fileStorageService.loadFileAsResource(path);

        String contentType = "application/octet-stream";
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
        } catch (IOException ex) {
            // fallback content type
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
