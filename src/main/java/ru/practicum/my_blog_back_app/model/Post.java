package ru.practicum.my_blog_back_app.model;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Post {
    private Long id;
    private String title;
    private String text;
    private List<String> tags;
    private int likesCount;
    private int commentsCount;
    private byte[] image; // Для хранения картинки
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}