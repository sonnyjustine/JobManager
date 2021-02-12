package com.springboot.jobmanagement.webapi;

import com.springboot.jobmanagement.config.RestExceptionHandler;
import com.springboot.jobmanagement.domain.entity.JobEntity;
import com.springboot.jobmanagement.exception.InvalidJobTransitionException;
import com.springboot.jobmanagement.exception.JobNotFoundException;
import com.springboot.jobmanagement.service.JobProcessService;
import com.springboot.jobmanagement.webapi.model.JobAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.springboot.jobmanagement.webapi.model.JobModel;
import com.springboot.jobmanagement.service.JobService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import static com.springboot.jobmanagement.config.RestExceptionHandler.ErrorResponse;

/**
 * Controller for managing Jobs
 * @author squillopas
 */
@RestController
@RequestMapping(value = "/api/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private JobProcessService jobProcessService;

    @Autowired
    private JobAssembler jobAssembler;

    @Operation(summary = "Create a Job")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Job was created",
                content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JobModel.class)) }),
        @ApiResponse(responseCode = "400", description = "Job not created. Invalid job field is supplied",
                content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }) })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobModel createJob(@RequestBody @Valid final JobModel jobModel) {
        JobEntity createdJob = jobService.create(jobModel);

        // Start the activiti bpmn process
        jobProcessService.startProcess(jobAssembler.toModel(createdJob));

        JobEntity createdJobWithProcessLink = jobService.get(createdJob.getId());
        return jobAssembler.toModel(createdJobWithProcessLink);
    }

    @Operation(summary = "Get Job by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the Job",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JobModel.class)) }),
            @ApiResponse(responseCode = "404", description = "Job not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }) })
    @GetMapping("/{id}")
    public JobModel getJob(@PathVariable final Long id) {
        JobEntity job = jobService.get(id);
        if(job == null) {
            throw new JobNotFoundException(id);
        }

        log.debug(job.toString());
        return jobAssembler.toModel(job);
    }

    @Operation(summary = "Process the Job with the given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job transition was successful and the Job's state was successfully updated",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JobModel.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid Job transition",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }) })
    @PostMapping("/{id}/process/{taskName}")
    public JobModel processJob(@PathVariable final Long id, @PathVariable final String taskName) {
        if(!jobProcessService.processTask(Long.toString(id), taskName)) {
            throw new InvalidJobTransitionException(HttpStatus.BAD_REQUEST);
        }

        JobEntity processedJob = jobService.get(id);
        if(processedJob == null) {
            throw new JobNotFoundException(id);
        }

        return jobAssembler.toModel(processedJob);
    }

    @Operation(summary = "Search jobs using the given URL parameters. The Job's fields are the valid parameters. Invalid parameters are just ignored")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of Jobs matching the given parameters returned",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)) }) })
    @GetMapping("/search")
    public List<JobModel> searchJobs(@RequestParam Map<String, String> params) {
        return jobService.findByExample(params)
                .stream()
                .map(job -> jobAssembler.toModel(job))
                .collect(Collectors.toList());
    }

    @Operation(summary = "Search for all Jobs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all Jobs returned",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)) }) })
    @GetMapping
    public List<JobModel> getAllJobs() {
        return jobService.findAll()
                .stream()
                .map(job -> jobAssembler.toModel(job))
                .collect(Collectors.toList());
    }

    // API endpoints currently not being used
    /*@PutMapping("/{id}")
    public JobModel updateJob(@PathVariable final Long id, @RequestBody @Valid final JobModel jobModel) {
        JobEntity updatedJob = jobService.update(id, jobModel);
        return jobAssembler.toModel(updatedJob);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJob(@PathVariable final Long id) {
        jobService.delete(id);
    }*/

}
