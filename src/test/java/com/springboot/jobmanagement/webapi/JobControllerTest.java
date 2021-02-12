package com.springboot.jobmanagement.webapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.jobmanagement.domain.JobState;
import com.springboot.jobmanagement.domain.JobType;
import com.springboot.jobmanagement.domain.entity.JobEntity;
import com.springboot.jobmanagement.service.JobProcessService;
import com.springboot.jobmanagement.service.JobService;
import com.springboot.jobmanagement.webapi.model.JobAssembler;
import com.springboot.jobmanagement.webapi.model.JobModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class JobControllerTest {

    private static String BASE_PATH = "http://localhost/api/jobs";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JobAssembler jobAssembler;

    @MockBean
    private JobService jobService;

    @MockBean
    private JobProcessService jobProcessService;

    private JobEntity unallocatedA, unallocatedB;

    @BeforeEach
    public void setUp() {
        unallocatedA = new JobEntity();
        unallocatedA.setId(1l);
        unallocatedA.setType(JobType.TYPE_A);
        unallocatedA.setState(JobState.UNALLOCATED);

        unallocatedB = new JobEntity();
        unallocatedB.setId(2L);
        unallocatedB.setType(JobType.TYPE_B);
        unallocatedB.setState(JobState.UNALLOCATED);
    }

    @Test
    public void createJobTest_ShouldReturnCreatedJobModelWithLinks() throws Exception {
        when(jobService.create(any(JobModel.class))).thenReturn(unallocatedA);
        when(jobService.get(1l)).thenReturn(unallocatedA);
        when(jobProcessService.getTasks("1")).thenReturn(Arrays.asList("allocateJob", "deleteJob"));

        mockMvc.perform(post(BASE_PATH)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(unallocatedA)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id", is(1)))
                .andExpect(jsonPath("state", is(JobState.UNALLOCATED.toString())))
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/1")))
                .andExpect(jsonPath("_links.next.href", is(BASE_PATH + "/1/process/allocateJob")))
                .andExpect(jsonPath("_links.last.href", is(BASE_PATH + "/1/process/deleteJob")));
    }

    @Test
    public void getJobTest_ShouldReturnJobModelWithLinks() throws Exception {
        when(jobService.get(1l)).thenReturn(unallocatedA);
        when(jobProcessService.getTasks("1")).thenReturn(Arrays.asList("allocateJob", "deleteJob"));

        mockMvc.perform(get(BASE_PATH + "/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(unallocatedA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(1)))
                .andExpect(jsonPath("state", is(JobState.UNALLOCATED.toString())))
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/1")))
                .andExpect(jsonPath("_links.next.href", is(BASE_PATH + "/1/process/allocateJob")))
                .andExpect(jsonPath("_links.last.href", is(BASE_PATH + "/1/process/deleteJob")));
    }

    @Test
    public void getJobTest_ShouldReturnJobNotFoundException() throws Exception {
        when(jobService.get(1l)).thenReturn(null);
        mockMvc.perform(get(BASE_PATH + "/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("httpStatus", is(404)))
                .andExpect(jsonPath("exception", is("JobNotFoundException")));
    }

    @Test
    public void processJobTest_ShouldReturnJobWithUpdatedStateAndLinks() throws Exception {
        unallocatedA.setState(JobState.ALLOCATED);
        when(jobService.get(1l)).thenReturn(unallocatedA);
        when(jobProcessService.processTask("1", "allocateJob")).thenReturn(true);
        when(jobProcessService.getTasks("1")).thenReturn(Arrays.asList("toStateA", "deleteJob"));

        mockMvc.perform(post(BASE_PATH + "/1/process/allocateJob"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(1)))
                .andExpect(jsonPath("state", is(JobState.ALLOCATED.toString())))
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/1")))
                .andExpect(jsonPath("_links.next.href", is(BASE_PATH + "/1/process/toStateA")))
                .andExpect(jsonPath("_links.last.href", is(BASE_PATH + "/1/process/deleteJob")));
    }

    @Test
    public void processJobTest_ShouldReturnInvalidJobTransitionException() throws Exception {
        when(jobProcessService.processTask("1", "allocateJob")).thenReturn(false);

        mockMvc.perform(post(BASE_PATH + "/1/process/allocateJob"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("httpStatus", is(400)))
                .andExpect(jsonPath("exception", is("InvalidJobTransitionException")));
    }

    @Test
    public void searchJobs() {
    }

    @Test
    public void getAllJobsTest() {
    }
}