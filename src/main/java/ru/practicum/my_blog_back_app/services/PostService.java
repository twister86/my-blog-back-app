package ru.practicum.my_blog_back_app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.my_blog_back_app.model.Comment;
import ru.practicum.my_blog_back_app.model.PagedPostsResponse;
import ru.practicum.my_blog_back_app.model.Post;
import ru.practicum.my_blog_back_app.repository.CommentRepository;
import ru.practicum.my_blog_back_app.repository.PostRepository;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public PostService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    // 1. Получение пагинированного списка постов с поиском
    @Transactional(readOnly = true)
    public PagedPostsResponse getPosts(String search, int pageNumber, int pageSize) {
        int totalCount = postRepository.countBySearch(search);
        int lastPage = (int) Math.ceil((double) totalCount / pageSize);
        boolean hasPrev = pageNumber > 1;
        boolean hasNext = pageNumber < lastPage;

        List<Post> posts = postRepository.findAll(search, pageNumber, pageSize);

        return new PagedPostsResponse(posts, hasPrev, hasNext, lastPage);
    }

    // 2. Получение поста по ID
    @Transactional(readOnly = true)
    public Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пост не найден"));
    }

    // 3. Создание поста
    @Transactional
    public Post createPost(Post post) {
        // Инициализация счётчиков
        post.setLikesCount(0);
        post.setCommentsCount(0);

        return postRepository.save(post);
    }

    // 4. Обновление поста
    @Transactional
    public Post updatePost(Post post) {
        Post existingPost = getPost(post.getId());

        existingPost.setTitle(post.getTitle());
        existingPost.setText(post.getText());
        existingPost.setImage(post.getImage()); // Обновляем картинку, если передана

        postRepository.update(existingPost);
        return existingPost;
    }

    // 5. Удаление поста (и всех его комментариев через CASCADE в БД)
    @Transactional
    public void deletePost(Long id) {
        postRepository.delete(id);
    }

    // 6. Увеличение числа лайков
    @Transactional
    public int incrementLikes(Long id) {
        postRepository.incrementLikes(id);
        return getPost(id).getLikesCount(); // Возвращаем новое значение
    }

    // 7. Получение картинки поста
    @Transactional(readOnly = true)
    public byte[] getImage(Long id) {
        return postRepository.getImage(id);
    }

    // 8. Обновление картинки поста
    @Transactional
    public void updateImage(Long id, byte[] image) {
        postRepository.setImage(id, image);
    }

    // 9. Получение комментариев поста
    @Transactional(readOnly = true)
    public List<Comment> getComments(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    // 10. Создание комментария к посту
    @Transactional
    public Comment createComment(Long postId, Comment comment) {
        comment.setPostId(postId);
        Comment savedComment = commentRepository.save(comment);

        // Увеличиваем счётчик комментариев у поста
        Post post = getPost(postId);
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.update(post);

        return savedComment;
    }

    // 11. Обновление комментария
    @Transactional
    public void updateComment(Long commentId, Comment comment) {
        commentRepository.update(comment);
    }

    // 12. Удаление комментария
    @Transactional
    public void deleteComment(Long commentId) {
        // Сначала уменьшаем счётчик комментариев у поста
        Comment comment = commentRepository.findById(commentId);
        Long postId = comment.getPostId();

        Post post = getPost(postId);
        post.setCommentsCount(Math.max(0, post.getCommentsCount() - 1));
        postRepository.update(post);

        // Затем удаляем комментарий
        commentRepository.delete(commentId);
    }
}
