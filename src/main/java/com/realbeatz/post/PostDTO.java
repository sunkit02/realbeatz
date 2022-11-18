package com.realbeatz.post;

import com.realbeatz.post.comment.CommentDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostDTO {

    private Long id;
    private String content;
    private String songTitle;
    private String artists;
    private Integer likes;
    private List<CommentDTO> comments;
    private Long userId;
    private LocalDateTime postTime;

    public static PostDTO map(Post post) {
        List<CommentDTO> comments = post.getComments().stream()
                .map(CommentDTO::map)
                .toList();

        return PostDTO.builder()
                .id(post.getId())
                .content(post.getContent())
                .songTitle(post.getSongTitle())
                .artists(post.getArtists())
                .likes(post.getLikes())
                .comments(comments)
                .userId(post.getCreator().getId())
                .postTime(post.getPostTime())
                .build();
    }
}
