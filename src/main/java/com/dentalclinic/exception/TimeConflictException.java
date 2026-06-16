package com.dentalclinic.exception;

public class TimeConflictException extends BusinessException {

    public TimeConflictException(String message) {
        super("TIME_CONFLICT", message);
    }
}
