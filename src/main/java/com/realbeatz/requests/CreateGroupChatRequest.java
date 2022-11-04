package com.realbeatz.requests;

import lombok.Data;

@Data
public class CreateGroupChatRequest {

    private Long userId;
    private String chatName;
}
