package com.realbeatz.requests;

import lombok.Value;

@Value
public class CreatePostRequest {
    Long userId;
    String content;
    String songTitle;
    String artists;
}
