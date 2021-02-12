package com.springboot.jobmanagement.domain.repo;

import com.springboot.jobmanagement.domain.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository which uses Spring Data JPA to manage Job entities
 */
@Repository
public interface JobRepository extends JpaRepository<JobEntity, Long>, QueryByExampleExecutor<JobEntity> {
}