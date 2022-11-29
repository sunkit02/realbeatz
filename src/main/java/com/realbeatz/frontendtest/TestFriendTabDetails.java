package com.realbeatz.frontendtest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestFriendTabDetails {
    private String username;
    private String firstName;
    private String lastName;
}
