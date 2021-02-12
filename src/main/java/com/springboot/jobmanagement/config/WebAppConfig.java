package com.springboot.jobmanagement.config;

import com.springboot.jobmanagement.domain.factory.JobFactory;
import com.springboot.jobmanagement.domain.factory.JobFactoryImpl;
import com.springboot.jobmanagement.domain.repo.JobRepository;
import com.springboot.jobmanagement.service.JobProcessService;
import com.springboot.jobmanagement.service.JobProcessServiceImpl;
import com.springboot.jobmanagement.service.JobService;
import com.springboot.jobmanagement.service.JobServiceImpl;
import com.springboot.jobmanagement.webapi.model.JobAssembler;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan(basePackages = {"com.springboot.jobmanagement.domain.entity"})
@EnableJpaRepositories(basePackages = {"com.springboot.jobmanagement.domain.repo"})
@EnableTransactionManagement
public class WebAppConfig {

    @Bean
    public JobProcessService jobProcessService(RuntimeService runtimeService, TaskService taskService) {
        return new JobProcessServiceImpl(runtimeService, taskService);
    }

    @Bean
    public JobAssembler jobAssembler(JobProcessService jobProcessService) {
        return new JobAssembler(jobProcessService);
    }

    @Bean
    public JobFactory jobFactory() {
        return new JobFactoryImpl();
    }

    @Bean
    public JobService jobService(JobRepository jobRepository, JobFactory jobFactory) {
        return new JobServiceImpl(jobRepository, jobFactory);
    }
}
