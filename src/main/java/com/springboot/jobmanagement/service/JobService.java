package com.springboot.jobmanagement.service;

import java.util.List;
import java.util.Map;

import com.springboot.jobmanagement.domain.entity.JobEntity;
import com.springboot.jobmanagement.webapi.model.JobModel;

/**
 * Service interface used for managing jobs
 * @author squillopas
 */
public interface JobService {

    /**
     * Create a new job
     * @param jobModel Job representation model
     * @return the saved job
     */
    JobEntity create(final JobModel jobModel);

    /**
     * Get job by id
     * @param id ID of the Job
     * @return the saved job
     */
    JobEntity get(final Long id);

    /**
     * Return all jobs that matches the fields from the given map
     * @param params map of the fields to be queried
     * @return
     */
    List<JobEntity> findByExample(final Map<String, String> params);

    /**
     * Update an existing job
     * @param id ID of the job
     * @param jobModel Job representation model
     * @return
     */
    JobEntity update(final Long id, final JobModel jobModel);

    /**
     * Delete an existing job
     * @param id ID of the job
     */
    void delete(final Long id);

    /**
     * List all jobs
     * @return list of all jobs
     */
    List<JobEntity> findAll();

    /**
     * Update value of the Job's state field
     * @param id Job id
     * @param stateString Job State string value
     * @return Updated job
     */
    JobEntity updateJobState(Long id, String stateString);

}