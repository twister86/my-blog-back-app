package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.PostResponse;
import ru.yandex.practicum.model.Post;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostResponse toDto(Post post);

    Post toEntity(PostResponse postResponse);

    List<PostResponse> toDtoList(List<Post> posts);

    List<Post> toEntityList(List<PostResponse> postResponses);
}
