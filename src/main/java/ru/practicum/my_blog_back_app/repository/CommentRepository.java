package ru.practicum.my_blog_back_app.repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.my_blog_back_app.model.Comment;

import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository
public class CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    // RowMapper для преобразования строки БД в объект Comment
    private static final RowMapper<Comment> COMMENT_ROW_MAPPER = (rs, rowNum) -> {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setText(rs.getString("text"));
        comment.setPostId(rs.getLong("post_id"));
        comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return comment;
    };

    public CommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Получение всех комментариев поста
    public List<Comment> findByPostId(Long postId) {
        String sql = "SELECT * FROM comments WHERE post_id = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, COMMENT_ROW_MAPPER, postId);
    }

    // Получение комментария по ID
    public Comment findById(Long commentId) {
        String sql = "SELECT * FROM comments WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, COMMENT_ROW_MAPPER, commentId);
    }

    // Создание комментария
    public Comment save(Comment comment) {
        String sql = """
            INSERT INTO comments (text, post_id, created_at)
            VALUES (?, ?, NOW())
            RETURNING id
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            java.sql.PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, comment.getText());
            ps.setLong(2, comment.getPostId());
            return ps;
        }, keyHolder);

        comment.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return comment;
    }

    // Обновление комментария
    public void update(Comment comment) {
        String sql = "UPDATE comments SET text = ? WHERE id = ?";
        jdbcTemplate.update(sql, comment.getText(), comment.getId());
    }

    // Удаление комментария
    public void delete(Long commentId) {
        String sql = "DELETE FROM comments WHERE id = ?";
        jdbcTemplate.update(sql, commentId);
    }
}