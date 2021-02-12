package com.springboot.jobmanagement.domain.entity;

import com.springboot.jobmanagement.domain.JobState;
import com.springboot.jobmanagement.domain.JobType;
import com.springboot.jobmanagement.webapi.model.JobModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * Entity class for persisting Jobs
 * @author squillopas
 */
@Entity
@Getter @Setter
@ToString(callSuper=true, includeFieldNames=true)
@NoArgsConstructor
public class JobEntity extends BaseEntity {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JobType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JobState state;

    public JobEntity(JobModel job) {
        this.type = job.getType();
        this.state = job.getState();
    }

}
