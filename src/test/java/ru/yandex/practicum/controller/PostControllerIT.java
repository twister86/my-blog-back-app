package ru.yandex.practicum.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.common.ControllerConfig;
import ru.yandex.practicum.common.RepositoryConfig;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.PostRepository;

import java.util.Optional;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RepositoryConfig.class, ControllerConfig.class})
@WebAppConfiguration
class PostControllerIT {

    @Autowired
    private PostController postController;

    @Autowired
    private PostRepository postRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Настроим MockMvc с контроллером
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }

    @Test
    public void testGetPost() throws Exception {
        Post validPost = new Post(1L, "Test Post");

        postController.createPost(validPost);
        Optional<Post> expectedResult = Optional.of(validPost);
        doReturn(expectedResult).when(postRepository).findById(1L);

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk());
    }

}