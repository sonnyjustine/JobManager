package com.springboot.jobmanagement.activiti;

import com.springboot.jobmanagement.domain.factory.JobFactory;
import com.springboot.jobmanagement.domain.factory.JobFactoryImpl;
import com.springboot.jobmanagement.domain.repo.JobRepository;
import com.springboot.jobmanagement.service.JobService;
import com.springboot.jobmanagement.service.JobServiceImpl;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ActivitiAbstractSpringTest.ActivitiEngineTestConfiguration.class)
@EntityScan(basePackages = {"com.springboot.jobmanagement.domain.entity"})
@EnableJpaRepositories(basePackages = {"com.springboot.jobmanagement.domain.repo"})
@ActiveProfiles("test")
public abstract class ActivitiAbstractSpringTest {

    @Autowired
    @Rule
    public ActivitiRule activitiRule;

    @SpringBootApplication
    @EnableAutoConfiguration(exclude={org.activiti.spring.boot.SecurityAutoConfiguration.class})
    public static class ActivitiEngineTestConfiguration {

        @Bean
        public ActivitiRule activitiRule(ProcessEngine processEngine) {
            return new ActivitiRule(processEngine);
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
}
