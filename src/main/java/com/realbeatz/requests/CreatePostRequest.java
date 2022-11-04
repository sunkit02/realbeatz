package com.realbeatz.requests;

import lombok.Data;

@Data
public class CreatePostRequest {

    private Long userId;
    private String content;
    private String songTitle;
    private String artists;
}
