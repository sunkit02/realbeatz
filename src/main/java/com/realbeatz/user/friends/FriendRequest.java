package com.realbeatz.user.friends;

import com.realbeatz.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "friend_requests")
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "friend_request_gen")
    @SequenceGenerator(name = "friend_request_gen", sequenceName = "friend_request_seq", initialValue = 6000, allocationSize = 1)
    @Column(name = "friend_request_id", nullable = false)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(
            name = "requester_id",
            referencedColumnName = "user_id",
            nullable = false
    )
    @ToString.Exclude
    private User requester;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(
            name = "new_friend_id",
            referencedColumnName = "user_id",
            nullable = false
    )
    @ToString.Exclude
    private User newFriend;

    @Column(length = 100, nullable = false)
    @Builder.Default
    private String message = "";

    @Column(nullable = false)
    private FriendRequestStatus status;
}
