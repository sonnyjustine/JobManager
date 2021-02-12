package com.springboot.jobmanagement.exception;

/**
 * Exceptions for Job IDs that do not exist in the database
 */
public class JobNotFoundException extends RuntimeException {

    private Long id;

    public JobNotFoundException(Long id) {
        super();
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Job with id " + id + " not found.";
    }
}
