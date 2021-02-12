package com.springboot.jobmanagement.domain.factory;

import com.springboot.jobmanagement.domain.entity.JobEntity;
import com.springboot.jobmanagement.webapi.model.JobModel;

import java.util.Map;

public interface JobFactory {

    JobEntity newJob(JobModel jobModel);

    JobEntity newJob(Map<String, String> params);
}
