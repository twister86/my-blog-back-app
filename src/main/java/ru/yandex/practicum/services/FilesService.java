package ru.yandex.practicum.services;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class FilesService {

    public static final String UPLOAD_DIR = "uploads/";

    public static final String FILE_NAME = "image.jpg";

    public String upload(MultipartFile file, long postId) {
        try {
            Path uploadDir = Paths.get(UPLOAD_DIR + postId + "/");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Сохраняем файл
            Path filePath = uploadDir.resolve(FILE_NAME);
            file.transferTo(filePath);

            return file.getOriginalFilename();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Resource download(Long postId) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR+ postId + "/").resolve(FILE_NAME).normalize();
            byte[] content = Files.readAllBytes(filePath);

            return new ByteArrayResource(content);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}