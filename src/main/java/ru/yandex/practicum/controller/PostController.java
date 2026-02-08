package ru.yandex.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.CommentResponse;
import ru.yandex.practicum.dto.PostResponse;
import ru.yandex.practicum.mapper.CommentMapper;
import ru.yandex.practicum.mapper.PostMapper;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.dto.PagedPostsResponse;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.services.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin
@AllArgsConstructor
public class PostController {

    private final PostMapper postMapper;

    private final PostService postService;

    private final CommentMapper commentMapper;

    // 1. Получение пагинированного списка постов (поиск + пагинация)
    @GetMapping
    public ResponseEntity<PagedPostsResponse> getPosts(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize) {

        PagedPostsResponse response = postService.getPosts(search, pageNumber, pageSize);
        return ResponseEntity.ok(response);
    }


    // 2. Получение поста по ID
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        PostResponse post = postMapper.toDto(postService.getPost(id));
        return ResponseEntity.ok(post);
    }

    // 3. Создание поста
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostResponse post) {
        PostResponse createdPost = postMapper.toDto(postService.createPost(postMapper.toEntity(post)));
        return ResponseEntity.ok(createdPost);
    }

    // 4. Обновление поста
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @RequestBody PostResponse post) {

        post.setId(id);
        PostResponse updatedPost = postMapper.toDto(postService.updatePost(postMapper.toEntity(post)));
        return ResponseEntity.ok(updatedPost);
    }

    // 5. Удаление поста
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build(); // 204 No Content
    }

    // 6. Увеличение числа лайков
    @PostMapping("/{id}/likes")
    public ResponseEntity<Integer> incrementLikes(@PathVariable Long id) {
        int newLikesCount = postService.incrementLikes(id);
        return ResponseEntity.ok(newLikesCount);
    }


    // 9. Получение комментариев поста
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        List<CommentResponse> comments = postService.getComments(postId).stream().map(commentMapper::toDto).toList();
        return ResponseEntity.ok(comments);
    }

    // 10. Создание комментария к посту
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @RequestBody CommentResponse comment) {

        CommentResponse createdComment = commentMapper.toDto(postService.createComment(postId, commentMapper.toEntity(comment)));
        return ResponseEntity.ok(createdComment);
    }

    // 11. Обновление комментария
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentResponse comment) {

        comment.setId(commentId);
        comment.setPostId(postId);
        return ResponseEntity.ok(commentMapper.toDto(postService.updateComment(commentMapper.toEntity(comment))));
    }

    // 12. Удаление комментария
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        postService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }
}
