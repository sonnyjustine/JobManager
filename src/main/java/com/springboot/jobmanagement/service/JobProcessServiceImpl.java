package com.springboot.jobmanagement.service;

import com.springboot.jobmanagement.webapi.model.JobModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JobProcessServiceImpl implements JobProcessService {

    private final static String JOB_PROCESS_ID = "jobManagementProcess";

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    @Override
    public void startProcess(JobModel jobModel) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("jobType", jobModel.getType().toString());

            runtimeService.startProcessInstanceByKey(JOB_PROCESS_ID, jobModel.getId().toString(), variables);
        } catch (ActivitiException e) {
            log.warn("Event " + JOB_PROCESS_ID + " with key " + jobModel.getId().toString() + " failed to start. Process ID does not exist.");
        }
    }

    @Override
    public List<String> getTasks( String key) {
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKeyLike(JOB_PROCESS_ID)
                .processInstanceBusinessKey(key)
                .active()
                .list();

        return tasks.stream()
                .map(task -> task.getName())
                .collect(Collectors.toList());
    }

    @Override
    public boolean processTask(String key, String taskName) {
        Task task = taskService.createTaskQuery()
                .processDefinitionKeyLike(JOB_PROCESS_ID)
                .processInstanceBusinessKey(key)
                .taskName(taskName)
                .singleResult();

        if(task == null) {
            return false;
        } else {
            taskService.complete(task.getId());
        }

        return true;
    }
}
