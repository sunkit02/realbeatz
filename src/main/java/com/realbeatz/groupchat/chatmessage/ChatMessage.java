package com.realbeatz.groupchat.chatmessage;

import com.realbeatz.groupchat.GroupChat;
import com.realbeatz.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "chat_messages_gen")
    @SequenceGenerator(
            name = "chat_messages_gen",
            sequenceName = "chat_messages_seq",
            initialValue = 3000,
            allocationSize = 1)
    @Column(name = "msg_id", nullable = false)
    private Long id;

    @Column(length = 1000)
    private String content;

    @Column(name = "time_sent")
    private LocalDateTime timeSent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_chat_id", nullable = false)
    private GroupChat groupChat;

    @ManyToOne
    @JoinColumn(
            name = "sender_id", nullable = false)
    private User sender;
}
