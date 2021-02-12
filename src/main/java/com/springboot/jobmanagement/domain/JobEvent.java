package com.springboot.jobmanagement.domain;

public enum JobEvent {
    ALLOCATE("allocateJob"),
    TO_STATE_A("toStateA"),
    TO_STATE_B("toStateB"),
    COMPLETE_JOB("completeJob"),
    DELETE_JOB("deleteJob");

    private String value;

    JobEvent(String value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return value;
    }
}
