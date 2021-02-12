package com.springboot.jobmanagement.domain.factory;

import com.springboot.jobmanagement.domain.JobState;
import com.springboot.jobmanagement.domain.JobType;
import com.springboot.jobmanagement.domain.entity.JobEntity;
import com.springboot.jobmanagement.webapi.model.JobModel;
import org.springframework.stereotype.Component;

import java.util.Map;

public class JobFactoryImpl implements JobFactory {

    @Override
    public JobEntity newJob(JobModel jobModel) {
        JobEntity newJob = new JobEntity(jobModel);
        newJob.setState(JobState.UNALLOCATED);
        return newJob;
    }

    @Override
    public JobEntity newJob(Map<String, String> params) {
        JobEntity newJob = new JobEntity();
        newJob.setType((params.get("type") == null ? null : JobType.valueOf(params.get("type"))));
        newJob.setState((params.get("state") == null ? null : JobState.valueOf(params.get("state"))));
        return newJob;
    }

}
