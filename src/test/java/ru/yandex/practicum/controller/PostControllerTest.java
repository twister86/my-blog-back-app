package ru.yandex.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.dto.CommentResponse;
import ru.yandex.practicum.dto.PagedPostsResponse;
import ru.yandex.practicum.dto.PostResponse;
import ru.yandex.practicum.mapper.CommentMapper;
import ru.yandex.practicum.mapper.PostMapper;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.PostRepository;
import ru.yandex.practicum.services.PostService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class
})
@ActiveProfiles("test")
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private PostMapper postMapper;

    @MockitoBean
    private CommentMapper commentMapper;

    @MockitoBean
    private PostRepository postRepository;

    @MockitoBean
    private CommentRepository commentRepository;

    // ---------- 1. GET /api/posts ----------
    @Test
    void getPosts_shouldReturnPagedResponse() throws Exception {
        PagedPostsResponse response = new PagedPostsResponse(List.of(), false, false, 1);

        when(postService.getPosts("", 1, 5)).thenReturn(response);

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastPage").value(1));
    }

    // ---------- 2. GET /api/posts/{id} ----------
    @Test
    void getPost_shouldReturnPost() throws Exception {
        Post post = new Post(1L, "Title", "Text", List.of(), 10, 2);
        PostResponse dto = new PostResponse(1L, "Title", "Text", List.of(), 10, 2);

        when(postService.getPost(1L)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(dto);

        mockMvc.perform(get("/api/posts/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Title"));
    }

    // ---------- 3. POST /api/posts ----------
    @Test
    void createPost_shouldReturnCreatedPost() throws Exception {
        PostResponse request = new PostResponse(null, "New", "Text", List.of(), 0, 0);
        Post entity = new Post(null, "New", "Text", List.of(), 0, 0);
        Post saved = new Post(1L, "New", "Text", List.of(), 0, 0);
        PostResponse response = new PostResponse(1L, "New", "Text", List.of(), 0, 0);

        when(postMapper.toEntity(any())).thenReturn(entity);
        when(postService.createPost(entity)).thenReturn(saved);
        when(postMapper.toDto(saved)).thenReturn(response);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // ---------- 4. PUT /api/posts/{id} ----------
    @Test
    void updatePost_shouldReturnUpdatedPost() throws Exception {
        PostResponse request = new PostResponse(null, "Updated", "Text", List.of(), 1, 1);
        Post entity = new Post(1L, "Updated", "Text", List.of(), 1, 1);

        when(postMapper.toEntity(any())).thenReturn(entity);
        when(postService.updatePost(entity)).thenReturn(entity);
        when(postMapper.toDto(entity)).thenReturn(request);

        mockMvc.perform(put("/api/posts/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    // ---------- 5. DELETE /api/posts/{id} ----------
    @Test
    void deletePost_shouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/posts/{id}", 1L))
                .andExpect(status().isOk());
    }

    // ---------- 6. POST /api/posts/{id}/likes ----------
    @Test
    void incrementLikes_shouldReturnNewCount() throws Exception {
        when(postService.incrementLikes(1L)).thenReturn(11);

        mockMvc.perform(post("/api/posts/{id}/likes", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("11"));
    }

    // ---------- 9. GET /api/posts/{id}/comments ----------
    @Test
    void getComments_shouldReturnList() throws Exception {
        Comment comment = new Comment(1L, "Nice", 1L);
        CommentResponse dto = new CommentResponse(1L, "Nice", 1L);

        when(postService.getComments(1L)).thenReturn(List.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(dto);

        mockMvc.perform(get("/api/posts/{id}/comments", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Nice"));
    }

    // ---------- 10. POST /api/posts/{id}/comments ----------
    @Test
    void createComment_shouldReturnCreatedComment() throws Exception {
        CommentResponse request = new CommentResponse(null, "Comment", null);
        Comment entity = new Comment(null, "Comment", 1L);
        Comment saved = new Comment(1L, "Comment", 1L);
        CommentResponse response = new CommentResponse(1L, "Comment", 1L);

        when(commentMapper.toEntity(any())).thenReturn(entity);
        when(postService.createComment(eq(1L), any())).thenReturn(saved);
        when(commentMapper.toDto(saved)).thenReturn(response);

        mockMvc.perform(post("/api/posts/{id}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // ---------- 11. PUT /api/posts/{postId}/comments/{commentId} ----------
    @Test
    void updateComment_shouldReturnUpdatedComment() throws Exception {
        CommentResponse request = new CommentResponse(null, "Updated", null);
        Comment entity = new Comment(1L, "Updated", 1L);

        when(commentMapper.toEntity(any())).thenReturn(entity);
        when(postService.updateComment(entity)).thenReturn(entity);
        when(commentMapper.toDto(entity)).thenReturn(request);

        mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Updated"));
    }

    // ---------- 12. DELETE /api/posts/{postId}/comments/{commentId} ----------
    @Test
    void deleteComment_shouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", 1L, 1L))
                .andExpect(status().isOk());
    }
}
