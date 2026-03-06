package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.CommentResponse;
import ru.yandex.practicum.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentResponse toDto(Comment comment);

    Comment toEntity(CommentResponse commentResponse);

    List<CommentResponse> toDtoList(List<Comment> comments);

    List<Comment> toEntityList(List<CommentResponse> commentResponses);
}
