package com.springboot.jobmanagement.service;

import com.springboot.jobmanagement.webapi.model.JobModel;

import java.util.List;

public interface JobProcessService {
    void startProcess(JobModel model);

    List<String> getTasks(String key);

    boolean processTask(String key, String taskName);
}
