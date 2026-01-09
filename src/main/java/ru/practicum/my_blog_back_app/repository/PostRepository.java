package ru.practicum.my_blog_back_app.repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.my_blog_back_app.model.Post;

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
        post.setLikesCount(rs.getInt("likes_count"));
        post.setCommentsCount(rs.getInt("comments_count"));
        post.setImage(rs.getBytes("image"));
        post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        post.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
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
            INSERT INTO posts (title, text, likes_count, comments_count, image, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, NOW(), NOW())
            RETURNING id
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            java.sql.PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getText());
            ps.setInt(3, post.getLikesCount());
            ps.setInt(4, post.getCommentsCount());
            ps.setBytes(5, post.getImage());
            return ps;
        }, keyHolder);

        post.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return post;
    }

    // Обновление поста
    public void update(Post post) {
        String sql = """
            UPDATE posts
            SET title = ?, text = ?, image = ?, updated_at = NOW()
            WHERE id = ?
            """;

        jdbcTemplate.update(sql,
                post.getTitle(),
                post.getText(),
                post.getImage(),
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

    // Получение картинки поста
    public byte[] getImage(Long id) {
        String sql = "SELECT image FROM posts WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, byte[].class, id);
        } catch (Exception e) {
            return null;
        }
    }

    // Установка картинки поста
    public void setImage(Long id, byte[] image) {
        String sql = "UPDATE posts SET image = ?, updated_at = NOW() WHERE id = ?";
        jdbcTemplate.update(sql, image, id);
    }

    // Пагинированный поиск постов
    public List<Post> findAll(String search, int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;

        String sql = """
            SELECT * FROM posts
            WHERE title ILIKE ? OR text ILIKE ?
            ORDER BY created_at DESC
            LIMIT ? OFFSET ?
            """;

        return jdbcTemplate.query(
                sql,
                POST_ROW_MAPPER,
                "%" + search + "%",
                "%" + search + "%",
                pageSize,
                offset
        );
    }

    // Подсчёт общего числа постов по поиску
    public int countBySearch(String search) {
        String sql = """
            SELECT COUNT(*) FROM posts
            WHERE title ILIKE ? OR text ILIKE ?
            """;

        return jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                "%" + search + "%",
                "%" + search + "%"
        );
    }
}