package com.realbeatz.dto;

import lombok.*;

@Value
@EqualsAndHashCode(callSuper = true)
public class ErrorMessage extends Message{

    public ErrorMessage(String message) {
        this.messageType = MessageType.ERROR;
        this.message = message;
    }

    public static ErrorMessage of(String message) {
        return new ErrorMessage(message);
    }

}
