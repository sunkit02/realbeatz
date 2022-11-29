package com.realbeatz.payloads.requests;

import lombok.Value;

@Value
public class AddFriendRequest {
    Long newFriendId;
    String message;
}
