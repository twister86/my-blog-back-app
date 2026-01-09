package ru.practicum.my_blog_back_app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.my_blog_back_app.model.Comment;
import ru.practicum.my_blog_back_app.model.PagedPostsResponse;
import ru.practicum.my_blog_back_app.model.Post;
import ru.practicum.my_blog_back_app.services.PostService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

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
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        Post post = postService.getPost(id);
        return ResponseEntity.ok(post);
    }

    // 3. Создание поста
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post createdPost = postService.createPost(post);
        return ResponseEntity.status(201).body(createdPost);
    }

    // 4. Обновление поста
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @RequestBody Post post) {

        post.setId(id);
        Post updatedPost = postService.updatePost(post);
        return ResponseEntity.ok(updatedPost);
    }

    // 5. Удаление поста
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // 6. Увеличение числа лайков
    @PostMapping("/{id}/likes")
    public ResponseEntity<Integer> incrementLikes(@PathVariable Long id) {
        int newLikesCount = postService.incrementLikes(id);
        return ResponseEntity.ok(newLikesCount);
    }

    // 7. Обновление картинки поста
    @PutMapping("/{id}/image")
    public ResponseEntity<Void> updateImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file) throws IOException {

        byte[] imageBytes = file.getBytes();
        postService.updateImage(id, imageBytes);
        return ResponseEntity.ok().build();
    }

    // 8. Получение картинки поста
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        byte[] image = postService.getImage(id);

        if (image == null || image.length == 0) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg") // Уточните MIME-тип при необходимости
                .body(image);
    }

    // 9. Получение комментариев поста
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        List<Comment> comments = postService.getComments(postId);
        return ResponseEntity.ok(comments);
    }

    // 10. Создание комментария к посту
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> createComment(
            @PathVariable Long postId,
            @RequestBody Comment comment) {

        Comment createdComment = postService.createComment(postId, comment);
        return ResponseEntity.status(201).body(createdComment);
    }

    // 11. Обновление комментария
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody Comment comment) {

        comment.setId(commentId);
        postService.updateComment(commentId, comment);
        return ResponseEntity.ok().build();
    }

    // 12. Удаление комментария
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        postService.deleteComment(commentId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
