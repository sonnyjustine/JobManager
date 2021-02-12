package com.springboot.jobmanagement.webapi.model;

import com.springboot.jobmanagement.domain.entity.JobEntity;
import com.springboot.jobmanagement.service.JobProcessService;
import com.springboot.jobmanagement.webapi.JobController;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Job entity to model converter
 * @author squillopas
 */
@RequiredArgsConstructor
public class JobAssembler implements RepresentationModelAssembler<JobEntity, JobModel> {

    private final JobProcessService jobProcessService;

    @Override
    public JobModel toModel(JobEntity entity) {
        JobModel jobModel = JobModel.builder()
                .id(entity.getId())
                .type(entity.getType())
                .state(entity.getState())
                .build();
        jobModel.add(linkTo(methodOn(JobController.class).getJob(entity.getId())).withSelfRel());

        List<String> tasks = jobProcessService.getTasks(Long.toString(entity.getId()));
        for(String nextTask : tasks) {
            String linkRel = nextTask.contains("delete") ? "last" : "next";
            jobModel.add(linkTo(methodOn(JobController.class).processJob(entity.getId(), nextTask)).withRel(linkRel));
        }

        return jobModel;
    }

}
