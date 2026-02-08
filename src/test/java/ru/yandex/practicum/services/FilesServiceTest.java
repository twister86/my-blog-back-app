package ru.yandex.practicum.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = FilesService.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class FilesServiceTest {

    @Autowired
    private FilesService filesService;

    private static final long POST_ID = 1L;

    @AfterEach
    void cleanUp() throws IOException {
        Path uploadDir = Path.of(FilesService.UPLOAD_DIR + POST_ID);
        if (Files.exists(uploadDir)) {
            Files.walk(uploadDir)
                    .sorted((a, b) -> b.compareTo(a)) // сначала файлы, потом папки
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    @Test
    void upload_shouldSaveFileAndReturnOriginalName() {
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test-image.jpg",
                "image/jpeg",
                "test-content".getBytes()
        );

        String result = filesService.upload(file, POST_ID);

        Path savedFile = Path.of(
                FilesService.UPLOAD_DIR + POST_ID + "/" + FilesService.FILE_NAME
        );

        assertThat(result).isEqualTo("test-image.jpg");
        assertThat(Files.exists(savedFile)).isTrue();
    }

    @Test
    void download_shouldReturnSavedFileContent() throws IOException {
        // сначала загрузим файл
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test-image.jpg",
                "image/jpeg",
                "image-bytes".getBytes()
        );
        filesService.upload(file, POST_ID);

        // затем скачиваем
        Resource resource = filesService.download(POST_ID);

        assertThat(resource).isNotNull();
        assertThat(resource.getInputStream().readAllBytes())
                .isEqualTo("image-bytes".getBytes());
    }
}
