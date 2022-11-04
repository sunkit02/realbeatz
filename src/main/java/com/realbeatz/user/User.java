package com.realbeatz.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.realbeatz.groupchat.GroupChat;
import com.realbeatz.post.Post;
import com.realbeatz.user.profile.UserProfile;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static com.realbeatz.util.UserUtils.MAX_PASSWORD_LENGTH;
import static com.realbeatz.util.UserUtils.MAX_USERNAME_LENGTH;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "users")
public class User {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "users_gen")
    @SequenceGenerator(
            name = "users_gen",
            sequenceName = "users_seq",
            initialValue = 1000,
            allocationSize = 1)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(length = MAX_USERNAME_LENGTH,nullable = false, unique = true)
    private String username;

    @Column(length = MAX_PASSWORD_LENGTH, nullable = false)
    private String password;

    @Column(nullable = false, updatable = false)
    private LocalDate registrationDate;

    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "profile_id", referencedColumnName = "profile_id")
    private UserProfile profile;

    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinTable(
            name = "friends_of",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    @JsonIgnore
    @ToString.Exclude
    @Builder.Default
    private Set<User> friends = new HashSet<>();

    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinTable(
            name = "friends_of",
            joinColumns = @JoinColumn(name = "friend_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    @ToString.Exclude
    @Builder.Default
    private Set<User> friendsOf = new HashSet<>();

    @ManyToMany(mappedBy = "members", cascade = CascadeType.DETACH)
    @ToString.Exclude
    @Builder.Default
    private Set<GroupChat> groupChats = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    @Builder.Default
    private Set<Post> posts = new HashSet<>();


}
