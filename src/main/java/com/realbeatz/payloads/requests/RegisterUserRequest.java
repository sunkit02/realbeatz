package com.realbeatz.payloads.requests;

import lombok.Value;

import java.time.LocalDate;

@Value
public class RegisterUserRequest {
    String username;
    String password;
    String lastName;
    String firstName;
    LocalDate dob;
    String bio;
}
