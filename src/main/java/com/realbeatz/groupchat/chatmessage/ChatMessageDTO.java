package com.realbeatz.groupchat.chatmessage;

import com.realbeatz.user.UserDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageDTO {
    private Long id;
    private String content;
    private LocalDateTime timeSent;
    private UserDTO sender;

    public static ChatMessageDTO map(ChatMessage message) {
        return ChatMessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .timeSent(message.getTimeSent())
                .sender(UserDTO.map(message.getSender()))
                .build();
    }
}
