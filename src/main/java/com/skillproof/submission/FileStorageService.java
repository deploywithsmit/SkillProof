package com.skillproof.submission;

import com.skillproof.common.BadRequestException;
import com.skillproof.common.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private final Path rootLocation;
    private final List<String> allowedExtensions;
    private final long maxSizeBytes = 5 * 1024 * 1024; // 5 MB

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir,
                              @Value("${app.upload.allowed-extensions:zip,pdf,png,jpg,jpeg,txt,md}") List<String> allowedExtensions) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.allowedExtensions = allowedExtensions.stream().map(String::toLowerCase).toList();

        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory", e);
        }
    }

    public StoredFile store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        if (file.getSize() > maxSizeBytes) {
            throw new BadRequestException("Uploaded file exceeds 5MB size limit.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BadRequestException("File name is invalid.");
        }

        // Clean filename and check extension
        String extension = getFileExtension(originalFilename);
        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new BadRequestException("File extension '." + extension + "' is not permitted. Allowed: " + String.join(", ", allowedExtensions));
        }

        String storedFilename = UUID.randomUUID() + "." + extension;
        Path destinationFile = this.rootLocation.resolve(Paths.get(storedFilename)).normalize().toAbsolutePath();

        // Prevent path traversal outside storage directory
        if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
            throw new BadRequestException("Cannot store file outside directory boundaries.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Stored uploaded file: {} as {}", originalFilename, storedFilename);

            return new StoredFile(
                    storedFilename,
                    originalFilename,
                    file.getContentType(),
                    file.getSize()
            );
        } catch (IOException e) {
            throw new BadRequestException("Failed to store uploaded file: " + e.getMessage());
        }
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Could not read file: " + filename);
        }
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        }
        return "";
    }

    public record StoredFile(
            String storedPath,
            String originalFilename,
            String contentType,
            long sizeBytes
    ) {}
}
