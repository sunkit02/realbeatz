package com.realbeatz.requests;

import lombok.Value;

@Value
public class NewCommentRequest {
    Long userId;
    String content;
}
