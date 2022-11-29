package com.realbeatz.payloads.requests;

import lombok.Value;

@Value
public class NewCommentRequest {
    Long userId;
    String content;
}
