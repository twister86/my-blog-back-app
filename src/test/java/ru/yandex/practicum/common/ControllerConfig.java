package ru.yandex.practicum.common;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.yandex.practicum.controller.PostController;
import ru.yandex.practicum.services.FilesService;
import ru.yandex.practicum.services.PostService;

@Configuration
@ComponentScan("ru.yandex.practicum.controller")
public abstract class ControllerConfig {

    @Bean
    @Primary
    public FilesService filesService() {
        return Mockito.mock(FilesService.class);
    }

    @Bean
    public PostController postController(PostService postService) {
        return new PostController(postService);
    }


}