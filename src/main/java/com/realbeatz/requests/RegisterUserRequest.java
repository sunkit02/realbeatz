package com.realbeatz.requests;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterUserRequest {

    String username;
    String password;
    String lastName;
    String firstName;
    LocalDate dob;
    String bio;
}
