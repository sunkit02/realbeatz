package com.realbeatz.groupchat;

import com.realbeatz.groupchat.chatmessage.ChatMessageDTO;
import com.realbeatz.user.UserDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class GroupChatDTO {

    Long id;
    String chatName;
    LocalDate timeCreated;
    UserDTO owner;
    List<UserDTO> members;
    List<ChatMessageDTO> chatMessages;

    public static GroupChatDTO map(GroupChat groupChat) {

        List<UserDTO> memberDTOs = groupChat.getMembers().stream()
                .map(UserDTO::map)
                .toList();

        UserDTO ownerDTO = UserDTO.map(groupChat.getOwner());

        List<ChatMessageDTO> chatMessageDTOs =
                groupChat.getChatMessages().stream()
                        .map(ChatMessageDTO::map)
                        .toList();

        return GroupChatDTO.builder()
                .id(groupChat.getId())
                .chatName(groupChat.getChatName())
                .owner(ownerDTO)
                .timeCreated(groupChat.getTimeCreated())
                .members(memberDTOs)
                .chatMessages(chatMessageDTOs)
                .build();
    }
}
