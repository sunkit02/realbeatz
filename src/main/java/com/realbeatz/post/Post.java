package com.realbeatz.post;

import com.realbeatz.post.comment.Comment;
import com.realbeatz.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.realbeatz.utils.PostUtils.*;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "posts")
public class Post {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "posts_gen")
    @SequenceGenerator(
            name = "posts_gen",
            sequenceName = "posts_seq",
            initialValue = 3000,
            allocationSize = 1)
    @Column(name = "post_id", nullable = false)
    private Long id;

    @Column(length = MAX_CONTENT_LENGTH) // max = 280
    private String content;

    @Column(length = MAX_SONG_TITLE_LENGTH) // max = 100
    private String songTitle;

    @Column(length = MAX_ARTISTS_LENGTH) // max = 100
    private String artists;

    @Column
    @Builder.Default
    private Integer likes = 0;

    @OneToMany(
            mappedBy = "post",
            fetch = FetchType.EAGER,
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Comment> comments = new HashSet<>();


    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(
            name = "creator_id",
            referencedColumnName = "user_id",
            nullable = false
    )
    private User creator;
    private LocalDateTime postTime;
}
