package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import ru.yandex.practicum.common.ControllerConfig;
import ru.yandex.practicum.common.RepositoryConfig;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.PostRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RepositoryConfig.class, ControllerConfig.class})
class PostControllerTest {

    @Autowired
    private PostController postController;

    @Autowired
    private PostRepository postRepository;


    @Test
    void testSavePost_success() {
        Post validPost = new Post(1L, "Test Post");

        postController.createPost(validPost);
        Optional<Post> expectedResult = Optional.of(validPost);
        doReturn(expectedResult).when(postRepository).findById(1L);
        Optional<Post> savedPost = postRepository.findById(1L);

        assertNotNull(savedPost.orElse(null), "Saved order should not be null");
        assertEquals("Test Post", savedPost.get().getTitle(), "Order description should match");
    }

    @Test
    void testUpdatePost_successe() {
        Post validPost = new Post(2L, "");
        Optional<Post> expectedResult = Optional.of(validPost);
        doReturn(expectedResult).when(postRepository).findById(2L);
        // Выполнение метода
        postController.createPost(validPost);
        validPost.setTitle("Test Post");
        postController.updatePost(2L, validPost);

        Optional<Post> savedPost = postRepository.findById(2L);

        assertNotNull(savedPost.orElse(null), "Saved order should not be null");
        assertEquals("Test Post", savedPost.get().getTitle(), "Order description should match");
    }
}