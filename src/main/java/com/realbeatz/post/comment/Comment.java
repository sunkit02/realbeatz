package com.realbeatz.post.comment;

import com.realbeatz.post.Post;
import com.realbeatz.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "comments_gen")
    @SequenceGenerator(
            name = "comments_gen",
            sequenceName = "comments_seq",
            initialValue = 4000,
            allocationSize = 1)
    @Column(name = "comment_id", nullable = false)
    private Long id;

    @Column(length = 100, nullable = false)
    private String content;
    @Column(nullable = false)
    private LocalDateTime timePosted;

    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = CascadeType.DETACH)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id",
            nullable = false)
    private User user;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(
            name = "post_id",
            referencedColumnName = "post_id",
            updatable = false,
            nullable = false)
    private Post post;

}
