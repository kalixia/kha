package com.kalixia.ha.api.rest;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("error")
public class ErrorMessage {
    private final String message;

    public ErrorMessage(String message, Object... args) {
        if (args.length > 0)
            this.message = String.format(message, args);
        else
            this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
