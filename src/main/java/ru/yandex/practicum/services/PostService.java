package ru.yandex.practicum.services;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.PagedPostsResponse;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.PostRepository;
import ru.yandex.practicum.utils.ArrayUtils;

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
    public PagedPostsResponse getPosts(String search, int pageNumber, int pageSize) {
        if (search == null) {
            search = "''";
        }

        List<Post> posts = postRepository.findAll(search);

        List<Post> page = ArrayUtils.paginate(posts, pageNumber, pageSize);

        page.forEach(p->{
            if (p.getText() != null && !p.getText().isEmpty() && p.getText().length() > 128) {
                p.setText(p.getText().substring(0, 127) + "...");
            }
        });

        int totalCount = posts.size();
        int lastPage = (int) Math.ceil((double) totalCount / pageSize);
        boolean hasPrev = pageNumber > 1;
        boolean hasNext = pageNumber < lastPage;

        return new PagedPostsResponse(page, hasPrev, hasNext, lastPage);
    }


    // 2. Получение поста по ID
    public Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пост не найден"));
    }

    // 3. Создание поста
    public Post createPost(Post post) {
        // Инициализация счётчиков
        post.setLikesCount(0);
        post.setCommentsCount(0);

        return postRepository.save(post);
    }

    // 4. Обновление поста
    public Post updatePost(Post post) {
        Post existingPost = getPost(post.getId());

        existingPost.setTitle(post.getTitle());
        existingPost.setText(post.getText());
        existingPost.setTags(post.getTags());

        postRepository.update(existingPost);
        return existingPost;
    }

    // 5. Удаление поста (и всех его комментариев через CASCADE в БД)
    public void deletePost(Long id) {
        postRepository.delete(id);
    }

    // 6. Увеличение числа лайков
    public int incrementLikes(Long id) {
        postRepository.incrementLikes(id);
        return getPost(id).getLikesCount(); // Возвращаем новое значение
    }

    // 9. Получение комментариев поста
    public List<Comment> getComments(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    // 10. Создание комментария к посту
    public Comment createComment(Long postId, Comment comment) {
        comment.setPostId(postId);
        Comment savedComment = commentRepository.save(comment);

        // Увеличиваем счётчик комментариев у поста
        postRepository.incrementComments(postId);

        return savedComment;
    }

    // 11. Обновление комментария
    public void updateComment(Comment comment) {
        commentRepository.update(comment);
    }

    // 12. Удаление комментария
    public void deleteComment(Long commentId) {
        // Сначала уменьшаем счётчик комментариев у поста
        Comment comment = commentRepository.findById(commentId);
        Long postId = comment.getPostId();

        // Увеличиваем счётчик комментариев у поста
        postRepository.decrementComments(postId);

        // Затем удаляем комментарий
        commentRepository.delete(commentId);
    }
}
