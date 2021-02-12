package com.springboot.jobmanagement.service;

import com.springboot.jobmanagement.domain.JobState;
import com.springboot.jobmanagement.domain.entity.JobEntity;
import com.springboot.jobmanagement.domain.factory.JobFactory;
import com.springboot.jobmanagement.webapi.model.JobModel;
import com.springboot.jobmanagement.domain.repo.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    private final JobFactory jobFactory;

    @Override
    public JobEntity create(final JobModel jobModel) {
        JobEntity jobEntity = jobFactory.newJob(jobModel);
        JobEntity savedJob = jobRepository.save(jobEntity);
        return savedJob;
    }

    @Override
    public JobEntity get(final Long id) {
        return jobRepository.findById(id).orElse(null);
    }

    @Override
    public List<JobEntity> findByExample(Map<String, String> params) {
        JobEntity jobEntity = jobFactory.newJob(params);
        return jobRepository.findAll(Example.of(jobEntity));
    }

    @Override
    public JobEntity update(final Long id, final JobModel jobModel) {
        final JobEntity jobEntity = jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job with id " + id + " not found."));
        BeanUtils.copyProperties(jobModel, jobEntity);
        JobEntity updatedJob = jobRepository.save(jobEntity);
        return updatedJob;
    }

    @Override
    public void delete(final Long id) {
        jobRepository.deleteById(id);
    }

    @Override
    public List<JobEntity> findAll() {
        return jobRepository.findAll();
    }

    @Override
    public JobEntity updateJobState(Long id, String stateString) {
        final JobEntity jobEntity = jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job with id " + id + " not found."));

        String oldState = jobEntity.getState().toString();
        JobState jobState = JobState.valueOf(stateString);
        jobEntity.setState(jobState);

        JobEntity updatedJob = jobRepository.save(jobEntity);
        log.debug(updatedJob.toString());
        log.info( "Job " + id + " transitioned from " + oldState + " to " + stateString);
        return updatedJob;
    }
}
