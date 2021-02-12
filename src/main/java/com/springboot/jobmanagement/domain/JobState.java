package com.springboot.jobmanagement.domain;

/**
 * Reflects the status of a Job as it progresses from creation to completion
 * @author squillopas
 */
public enum JobState {
    UNALLOCATED,
    ALLOCATED,
    STATE_A,
    STATE_B,
    COMPLETED,
    DELETED
}
