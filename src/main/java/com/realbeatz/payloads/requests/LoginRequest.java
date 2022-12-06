package com.realbeatz.payloads.requests;

import lombok.Data;

@Data
public class LoginRequest {
    String username;
    String password;
}
