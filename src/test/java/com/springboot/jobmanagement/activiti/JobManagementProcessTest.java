package com.springboot.jobmanagement.activiti;

import com.springboot.jobmanagement.domain.JobState;
import com.springboot.jobmanagement.domain.JobType;
import com.springboot.jobmanagement.webapi.model.JobModel;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isNull;

public class JobManagementProcessTest extends ActivitiAbstractSpringTest {

    private final static String JOB_PROCESS_ID = "jobManagementProcess";

    private RuntimeService runtimeService;

    private TaskService taskService;

    private JobModel typeA, typeB;

    @Before
    public void setUp() {
        runtimeService = activitiRule.getRuntimeService();

        taskService = activitiRule.getTaskService();

        typeA = JobModel.builder()
                .id(1l)
                .type(JobType.TYPE_A)
                .state(JobState.UNALLOCATED)
                .build();

        typeB = JobModel.builder()
                .id(2l)
                .type(JobType.TYPE_B)
                .state(JobState.UNALLOCATED)
                .build();
    }

    @Test
    @Deployment(resources = {"processes/JobManagementProcess.bpmn"})
    public void jobProcess_JobTypeANormalProcess_ShouldFinishWholeWorkflow() {
        testCompleteJobWorkFlow(typeA);
    }

    @Test
    @Deployment(resources = {"processes/JobManagementProcess.bpmn"})
    public void jobProcess_JobTypeABackToStateA_ShouldNoptReturnStateATask() {
        ProcessInstance processInstance = testJobWorkFlowUntilStateBOnly(typeA);

        List<Task> currentTasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).includeProcessVariables().list();
        assertEquals(2, currentTasks.size());

        Task task1 = currentTasks.get(0);
        assertNotEquals(task1.getName(), "toStateA");

        Task task2 = currentTasks.get(1);
        assertNotEquals(task2.getName(), "toStateA");
    }

    @Test
    @Deployment(resources = {"processes/JobManagementProcess.bpmn"})
    public void jobProcess_JobTypeBNormalProcess_ShouldFinishWholeWorkflow() {
        testCompleteJobWorkFlow(typeB);
    }

    @Test
    @Deployment(resources = {"processes/JobManagementProcess.bpmn"})
    public void jobProcess_JobTypeBBackToStateA_ShouldAllowGoingBack() {
        ProcessInstance processInstance = testJobWorkFlowUntilStateBOnly(typeB);

        List<Task> currentTasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).includeProcessVariables().list();
        assertEquals(3, currentTasks.size());

        Task completeTask = currentTasks.get(1);
        assertEquals(JobState.STATE_B.toString(), completeTask.getProcessVariables().get("currentState"));

        Task backToStateATask = currentTasks.get(2);
        assertEquals(JobState.STATE_B.toString(), backToStateATask.getProcessVariables().get("currentState"));

        // back to state A job
        taskService.complete(backToStateATask.getId());

        currentTasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).includeProcessVariables().list();
        assertEquals(2, currentTasks.size());

        Task backToStateBTask = currentTasks.get(1);
        assertEquals(JobState.STATE_A.toString(), backToStateBTask.getProcessVariables().get("currentState"));

        // back to State B
        taskService.complete(backToStateBTask.getId());

        currentTasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).includeProcessVariables().list();
        assertEquals(3, currentTasks.size());
    }

    /**
     * Test complete job workflow
     * UNALLOCATED -> ALLOCATED -> STATE_A -> STATE_B -> COMPLETED -> DELETED
     * @param jobModel The jobModel
     */
    private void testCompleteJobWorkFlow(JobModel jobModel) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("jobType", jobModel.getType().toString());

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(JOB_PROCESS_ID, Long.toString(jobModel.getId()), variables);
        assertNotNull(processInstance);
        List<Task> currentTasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).includeProcessVariables().list();
        Task deleteTask = currentTasks.get(0);
        Task allocateTask = currentTasks.get(1);
        assertEquals("deleteJob", deleteTask.getName());
        assertEquals("allocateJob", allocateTask.getName());
        assertEquals(jobModel.getType().toString(), allocateTask.getProcessVariables().get("jobType"));

        // allocate job
        taskService.complete(allocateTask.getId());

        currentTasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).includeProcessVariables().list();
        assertEquals(2, currentTasks.size());

        Task toStateATask = currentTasks.get(1);
        assertEquals(JobState.ALLOCATED.toString(), toStateATask.getProcessVariables().get("currentState"));

        // toStateA
        taskService.complete(toStateATask.getId());

        currentTasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).includeProcessVariables().list();
        assertEquals(2, currentTasks.size());

        Task toStateBTask = currentTasks.get(1);
        assertEquals(JobState.STATE_A.toString(), toStateBTask.getProcessVariables().get("currentState"));

        // toStateB
        taskService.complete(toStateBTask.getId());

        currentTasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).includeProcessVariables().list();
        assertEquals(jobModel.getType().equals(JobType.TYPE_A) ? 2 : 3, currentTasks.size());

        Task completeTask = currentTasks.get(1);
        assertEquals(JobState.STATE_B.toString(), completeTask.getProcessVariables().get("currentState"));

        // complete job
        taskService.complete(completeTask.getId());

        currentTasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).includeProcessVariables().list();
        assertEquals(1, currentTasks.size());

        Task deleteJob = currentTasks.get(0);
        assertEquals(JobState.COMPLETED.toString(), deleteJob.getProcessVariables().get("currentState"));

        // delete job
        taskService.complete(deleteJob.getId());
        Task noExpectedTask = taskService.createTaskQuery().processInstanceId(processInstance.getId()).includeProcessVariables().singleResult();
        assertEquals(isNull(), noExpectedTask);
    }

    /**
     * Test complete job workflow
     * UNALLOCATED -> ALLOCATED -> STATE_A -> STATE_B
     * @param jobModel The jobModel
     * @return the current process instance
     */
    private ProcessInstance testJobWorkFlowUntilStateBOnly(JobModel jobModel) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("jobType", jobModel.getType().toString());

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(JOB_PROCESS_ID, Long.toString(jobModel.getId()), variables);
        assertNotNull(processInstance);
        List<Task> currentTasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).includeProcessVariables().list();
        Task deleteTask = currentTasks.get(0);
        Task allocateTask = currentTasks.get(1);
        assertEquals("deleteJob", deleteTask.getName());
        assertEquals("allocateJob", allocateTask.getName());
        assertEquals(jobModel.getType().toString(), allocateTask.getProcessVariables().get("jobType"));

        // allocate job
        taskService.complete(allocateTask.getId());

        currentTasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).includeProcessVariables().list();
        assertEquals(2, currentTasks.size());

        Task toStateATask = currentTasks.get(1);
        assertEquals(JobState.ALLOCATED.toString(), toStateATask.getProcessVariables().get("currentState"));

        // toStateA
        taskService.complete(toStateATask.getId());

        currentTasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).includeProcessVariables().list();
        assertEquals(2, currentTasks.size());

        Task toStateBTask = currentTasks.get(1);
        assertEquals(JobState.STATE_A.toString(), toStateBTask.getProcessVariables().get("currentState"));

        // toStateB
        taskService.complete(toStateBTask.getId());

        return processInstance;
    }
}
