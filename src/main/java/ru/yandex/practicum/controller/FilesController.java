package ru.yandex.practicum.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.services.FilesService;

@RestController
@RequestMapping("/api/posts")
public class FilesController {

    private final FilesService filesService;

    public FilesController(FilesService filesService) {
        this.filesService = filesService;
    }

    // Put-эндпоинт для загрузки файла
    @PutMapping("/{id}/image")
    public ResponseEntity<String> uploadFile(@PathVariable Long id,
                                             @RequestParam("image") MultipartFile file) {
        return ResponseEntity.ok(filesService.upload(file, id));
    }

    // GET-эндпоинт для скачивания файла
    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        Resource file = filesService.download(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(file);
    }
}
