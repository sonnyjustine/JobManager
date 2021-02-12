package com.springboot.jobmanagement.webapi.model;

import com.springboot.jobmanagement.domain.JobState;
import com.springboot.jobmanagement.domain.JobType;
import com.springboot.jobmanagement.domain.entity.JobEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;

/**
 * Job model designed for HATEOAS
 */
@Builder
@Getter @Setter
public class JobModel extends RepresentationModel<JobModel> {

    private Long id;

    @NotNull
    private JobType type;

    private JobState state;

}
