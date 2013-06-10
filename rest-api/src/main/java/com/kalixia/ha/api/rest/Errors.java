package com.kalixia.ha.api.rest;

public class Errors {
    private final ErrorMessage[] errors;

    public static Errors withErrors(ErrorMessage... errors) {
        return new Errors(errors);
    }

    private Errors(ErrorMessage... errors) {
        this.errors = errors;
    }

    public ErrorMessage[] getErrors() {
        return errors;
    }

}
