package com.realbeatz.payloads.requests;

import lombok.Value;

@Value
public class CreatePostRequest {
    String content;
    String songTitle;
    String artists;
}
