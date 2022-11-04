package com.realbeatz.groupchat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.realbeatz.groupchat.chatmessage.ChatMessage;
import com.realbeatz.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "group_chats")
public class GroupChat {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "group_chat_seq")
    @SequenceGenerator(
            name = "group_chat_seq",
            sequenceName = "group_chat_seq",
            initialValue = 2000,
            allocationSize = 1)
    @Column(name = "chat_id", nullable = false)
    private Long id;

    @Column(
            name = "chat_name",
            length = 40,
            unique = true,
            nullable = false)
    private String chatName;

    @Column(name = "time_created", nullable = false)
    private LocalDate timeCreated;

    @OneToOne(
            fetch = FetchType.EAGER,
            cascade = CascadeType.DETACH)
    @JoinColumn(
            name = "owner_id",
            referencedColumnName = "user_id",
            nullable = false
    )
    @JsonIgnore
    private User owner;

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.DETACH)
    @JoinTable(
            name = "member_of",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
    @OrderBy("username asc")
    @JsonIgnore
    private Set<User> members = new HashSet<>();

    @OneToMany(
            mappedBy = "groupChat",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @OrderBy("timeSent asc")
    @JsonIgnore
    private Set<ChatMessage> chatMessages = new HashSet<>();

}