package ru.yandex.practicum.repository;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.model.Search;
import ru.yandex.practicum.utils.ArrayUtils;

import java.sql.Array;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    // RowMapper для преобразования строки БД в объект Post
    private static final RowMapper<Post> POST_ROW_MAPPER = (rs, rowNum) -> {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setText(rs.getString("text"));
        post.setTags(ArrayUtils.sqlArrayToList(rs.getArray("tags")));
        post.setLikesCount(rs.getInt("likes_count"));
        post.setCommentsCount(rs.getInt("comments_count"));
        return post;
    };

    public PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Получение поста по ID
    public Optional<Post> findById(Long id) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        try {
            Post post = jdbcTemplate.queryForObject(sql, POST_ROW_MAPPER, id);
            return Optional.of(post);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // Создание нового поста
    public Post save(Post post) {
        String sql = """
                INSERT INTO posts (title, text, tags, likes_count, comments_count)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            java.sql.PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getText());
            ps.setArray(3, ArrayUtils.listToSqlArray(connection, "VARCHAR", post.getTags()));
            ps.setInt(4, post.getLikesCount());
            ps.setInt(5, post.getCommentsCount());
            return ps;
        }, keyHolder);

        post.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return post;
    }

    // Обновление поста
    public void update(Post post) {
        String sql = """
                UPDATE posts
                SET title = ?, text = ?, updated_at = NOW()
                WHERE id = ?
                """;

        jdbcTemplate.update(sql,
                post.getTitle(),
                post.getText(),
                post.getId()
        );
    }

    // Удаление поста (и всех его комментариев через CASCADE)
    public void delete(Long id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // Увеличение числа лайков
    public void incrementLikes(Long id) {
        String sql = "UPDATE posts SET likes_count = likes_count + 1 WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // Пагинированный поиск постов
    public List<Post> findAll(String search) {

        String sql = """
                SELECT * FROM posts
                WHERE title ILIKE ?
                AND ( string_to_array(?,',')::varchar[] <@ tags or string_to_array(?,',')::varchar[] = ARRAY[]::varchar[])
                ORDER BY created_at DESC
                """;
        Search searchObject = ArrayUtils.splitStringAndTags(search, jdbcTemplate);

        return jdbcTemplate.query(
                sql,
                POST_ROW_MAPPER,
                "%" + searchObject.getSearchString() + "%",
                searchObject.getTagsString(),
                searchObject.getTagsString()
        );
    }



    public void incrementComments(Long postId) {
        String sql = "UPDATE posts SET comments_count = comments_count + 1 WHERE id = ?";
        jdbcTemplate.update(sql, postId);
    }

    public void decrementComments(Long postId) {
        String sql = "UPDATE posts SET comments_count = comments_count - 1 WHERE id = ?";
        jdbcTemplate.update(sql, postId);
    }
}