package com.realbeatz.requests;

import lombok.Value;

@Value
public class AddFriendRequest {
    Long newFriendId;
    String message;
}
