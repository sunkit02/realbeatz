package com.realbeatz.requests;

import lombok.Data;

@Data
public class NewCommentRequest {

    private String content;
    private Long userId;
}
