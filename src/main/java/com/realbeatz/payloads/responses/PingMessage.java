package com.realbeatz.payloads.responses;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class PingMessage extends Message{
    public PingMessage() {
        this.messageType = MessageType.PING;
        this.message = null;
    }

    public static PingMessage ping() {
        return new PingMessage();
    }
}
