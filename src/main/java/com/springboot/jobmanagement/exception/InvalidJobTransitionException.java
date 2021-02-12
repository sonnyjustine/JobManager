package com.springboot.jobmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown for Job transitions that are not valid
 */
public class InvalidJobTransitionException extends ResponseStatusException {

    public InvalidJobTransitionException(HttpStatus status) {
        super(status);
    }

    @Override
    public String getMessage() {
        return "Invalid job transition";
    }
}
