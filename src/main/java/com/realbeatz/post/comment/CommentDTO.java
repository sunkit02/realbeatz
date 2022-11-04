package com.realbeatz.post.comment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDTO {
    private Long id;
    private String content;
    private LocalDateTime timePosted;
    private Long userId;

    public static CommentDTO map(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .timePosted(comment.getTimePosted())
                .userId(comment.getUser().getId())
                .build();
    }
}
