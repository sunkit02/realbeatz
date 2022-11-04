package com.realbeatz.groupchat;

import com.realbeatz.exception.InvalidUserIdException;
import com.realbeatz.user.User;
import com.realbeatz.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
@AllArgsConstructor
public class GroupChatService {

    private final GroupChatRepository groupChatRepository;
    private final UserService userService;
    public List<GroupChat> getAllGroupChats() {
        return groupChatRepository.findAll();
    }

    public GroupChat getGroupChat(Long chatId) {
        return groupChatRepository.findById(chatId).orElseThrow();
    }

    public GroupChatDTO createNewGroupChat(Long userId, String chatName) throws InvalidUserIdException {
        User owner = userService.getUserById(userId);
        GroupChat groupChat = GroupChat.builder()
                .chatName(chatName)
                .owner(owner)
                .members(new HashSet<>())
                .chatMessages(new HashSet<>())
                .timeCreated(LocalDate.now())
                .build();

        groupChat.getMembers().add(owner);

        groupChatRepository.save(groupChat);
        return GroupChatDTO.map(groupChat);
    }
}
