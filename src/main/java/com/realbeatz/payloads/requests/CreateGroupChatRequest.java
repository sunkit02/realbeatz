package com.realbeatz.payloads.requests;

import lombok.Value;

@Value
public class CreateGroupChatRequest {
    Long userId;
    String chatName;
}
