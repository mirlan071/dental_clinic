package com.dentalclinic.exception;

public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String message) {
        super("DUPLICATE_RESOURCE", message);
    }

    public DuplicateResourceException(String resource, String field, String value) {
        super("DUPLICATE_RESOURCE", String.format("%s already exists with %s: %s", resource, field, value));
    }
}
