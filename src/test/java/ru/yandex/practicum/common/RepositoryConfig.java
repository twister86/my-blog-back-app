package ru.yandex.practicum.common;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.PostRepository;
import ru.yandex.practicum.services.PostService;

@Configuration
@ComponentScan("ru.yandex.practicum.repository")
public abstract class RepositoryConfig {
    @Bean
    @Primary
    public PostRepository postRepository() {
        return Mockito.mock(PostRepository.class);
    }

    @Bean
    @Primary
    public CommentRepository commentRepository() {
        return Mockito.mock(CommentRepository.class);
    }

    @Bean
    @Primary
    public PostService postService(PostRepository postRepository, CommentRepository commentRepository) {
        return new PostService(postRepository, commentRepository);
    }

}