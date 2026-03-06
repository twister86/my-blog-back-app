package ru.yandex.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.model.Post;

import java.util.List;

/**
 * Ответ с пагинированным списком постов.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedPostsResponse {

    private List<Post> posts;
    private boolean hasPrev;
    private boolean hasNext;
    private int lastPage;
}
