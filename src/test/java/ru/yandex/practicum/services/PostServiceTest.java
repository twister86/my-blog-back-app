package ru.yandex.practicum.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.practicum.dto.PagedPostsResponse;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(
        classes = PostService.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class
})
class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockitoBean
    private PostRepository postRepository;

    @MockitoBean
    private CommentRepository commentRepository;

    // ---------- 1. getPosts ----------
    @Test
    void getPosts_shouldReturnPagedResponse() {
        Post post = new Post(1L, "Title", "Very long text ".repeat(20), List.of(), 0, 0);

        when(postRepository.findAll("")).thenReturn(List.of(post));

        PagedPostsResponse response = postService.getPosts("", 1, 5);

        assertThat(response.getPosts()).hasSize(1);
        assertThat(response.getPosts().get(0).getText()).endsWith("...");
        assertThat(response.isHasPrev()).isFalse();
        assertThat(response.isHasNext()).isFalse();
        assertThat(response.getLastPage()).isEqualTo(1);
    }

    // ---------- 2. getPost ----------
    @Test
    void getPost_shouldReturnPost() {
        Post post = new Post(1L, "Title", "Text", List.of(), 0, 0);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Post result = postService.getPost(1L);

        assertThat(result.getTitle()).isEqualTo("Title");
    }

    // ---------- 3. createPost ----------
    @Test
    void createPost_shouldInitializeCounters() {
        Post post = new Post(null, "Title", "Text", List.of(), 5, 3);
        Post saved = new Post(1L, "Title", "Text", List.of(), 0, 0);

        when(postRepository.save(any())).thenReturn(saved);

        Post result = postService.createPost(post);

        assertThat(result.getLikesCount()).isZero();
        assertThat(result.getCommentsCount()).isZero();
    }

    // ---------- 4. updatePost ----------
    @Test
    void updatePost_shouldUpdateFields() {
        Post existing = new Post(1L, "Old", "Old text", List.of("old"), 0, 0);
        Post updated = new Post(1L, "New", "New text", List.of("new"), 0, 0);

        when(postRepository.findById(1L)).thenReturn(Optional.of(existing));

        Post result = postService.updatePost(updated);

        assertThat(result.getTitle()).isEqualTo("New");
        assertThat(result.getText()).isEqualTo("New text");
        verify(postRepository).update(existing);
    }

    // ---------- 5. deletePost ----------
    @Test
    void deletePost_shouldCallRepository() {
        postService.deletePost(1L);

        verify(postRepository).delete(1L);
    }

    // ---------- 6. incrementLikes ----------
    @Test
    void incrementLikes_shouldIncreaseCounter() {
        Post post = new Post(1L, "Title", "Text", List.of(), 10, 0);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        int result = postService.incrementLikes(1L);

        verify(postRepository).incrementLikes(1L);
        assertThat(result).isEqualTo(10);
    }

    // ---------- 9. getComments ----------
    @Test
    void getComments_shouldReturnList() {
        Comment comment = new Comment(1L, "Nice", 1L);

        when(commentRepository.findByPostId(1L)).thenReturn(List.of(comment));

        List<Comment> result = postService.getComments(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getText()).isEqualTo("Nice");
    }

    // ---------- 10. createComment ----------
    @Test
    void createComment_shouldSaveAndIncrementCounter() {
        Comment comment = new Comment(null, "Comment", null);
        Comment saved = new Comment(1L, "Comment", 1L);

        when(commentRepository.save(any())).thenReturn(saved);

        Comment result = postService.createComment(1L, comment);

        assertThat(result.getPostId()).isEqualTo(1L);
        verify(postRepository).incrementComments(1L);
    }

    // ---------- 11. updateComment ----------
    @Test
    void updateComment_shouldCallRepository() {
        Comment comment = new Comment(1L, "Updated", 1L);

        when(commentRepository.update(comment)).thenReturn(comment);

        Comment result = postService.updateComment(comment);

        assertThat(result.getText()).isEqualTo("Updated");
    }

    // ---------- 12. deleteComment ----------
    @Test
    void deleteComment_shouldDecrementCounterAndDelete() {
        Comment comment = new Comment(1L, "Text", 1L);

        when(commentRepository.findById(1L)).thenReturn(comment);

        postService.deleteComment(1L);

        verify(postRepository).decrementComments(1L);
        verify(commentRepository).delete(1L);
    }
}
