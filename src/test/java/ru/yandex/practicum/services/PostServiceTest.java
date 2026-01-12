package ru.yandex.practicum.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.common.RepositoryConfig;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.PostRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryConfig.class)
class PostServiceTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;


    @Test
    void testSavePost_success() {
        Post validPost = new Post(1L, "Test Post");

        postService.createPost(validPost);
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
        postService.createPost(validPost);
        validPost.setTitle("Test Post");
        postService.updatePost(validPost);

        Optional<Post> savedPost = postRepository.findById(2L);

        assertNotNull(savedPost.orElse(null), "Saved order should not be null");
        assertEquals("Test Post", savedPost.get().getTitle(), "Order description should match");
    }
}