package com.realbeatz.groupchat;

import com.realbeatz.payloads.responses.ErrorMessage;
import com.realbeatz.exceptions.InvalidUserIdException;
import com.realbeatz.payloads.requests.CreateGroupChatRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/group-chat")
public class GroupChatController {

    private final GroupChatService groupChatService;

    @GetMapping
    public ResponseEntity<List<GroupChat>> getAllGroupChats() {
        return ResponseEntity.ok(groupChatService.getAllGroupChats());
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<?> getGroupChatById(
            @PathVariable(name = "chatId") Long chatId) {
     return ResponseEntity.ok(groupChatService.getGroupChat(chatId));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNewGroupChat(
            @RequestBody CreateGroupChatRequest request) {

        GroupChatDTO newGroupChat;
        try {
            newGroupChat = groupChatService.
                    createNewGroupChat(request.getUserId(), request.getChatName());
        } catch (InvalidUserIdException e) {
            log.error("Error creating new group chat: {}", request, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.of(e.getMessage()));
        }

        return ResponseEntity.ok(newGroupChat);
    }
}
