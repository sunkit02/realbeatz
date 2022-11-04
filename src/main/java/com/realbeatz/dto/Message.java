package com.realbeatz.dto;

public record Message(String message) {
    public static Message of(String message) {
        return new Message(message);
    }
}
