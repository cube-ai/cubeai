package com.wyy.web.rest;

import com.wyy.UmmApp;

import com.wyy.config.SecurityBeanOverrideConfiguration;

import com.wyy.domain.TaskStep;
import com.wyy.repository.TaskStepRepository;
import com.wyy.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.wyy.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TaskStepResource REST controller.
 *
 * @see TaskStepResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UmmApp.class, SecurityBeanOverrideConfiguration.class})
public class TaskStepResourceIntTest {

    private static final String DEFAULT_TASK_UUID = "AAAAAAAAAA";
    private static final String UPDATED_TASK_UUID = "BBBBBBBBBB";

    private static final String DEFAULT_STEP_NAME = "AAAAAAAAAA";
    private static final String UPDATED_STEP_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_STEP_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STEP_STATUS = "BBBBBBBBBB";

    private static final Integer DEFAULT_STEP_PROGRESS = 100;
    private static final Integer UPDATED_STEP_PROGRESS = 99;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_STEP_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STEP_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private TaskStepRepository taskStepRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTaskStepMockMvc;

    private TaskStep taskStep;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TaskStepResource taskStepResource = new TaskStepResource(taskStepRepository);
        this.restTaskStepMockMvc = MockMvcBuilders.standaloneSetup(taskStepResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskStep createEntity(EntityManager em) {
        TaskStep taskStep = new TaskStep()
            .taskUuid(DEFAULT_TASK_UUID)
            .stepName(DEFAULT_STEP_NAME)
            .stepStatus(DEFAULT_STEP_STATUS)
            .stepProgress(DEFAULT_STEP_PROGRESS)
            .description(DEFAULT_DESCRIPTION)
            .stepDate(DEFAULT_STEP_DATE);
        return taskStep;
    }

    @Before
    public void initTest() {
        taskStep = createEntity(em);
    }

    @Test
    @Transactional
    public void createTaskStep() throws Exception {
        int databaseSizeBeforeCreate = taskStepRepository.findAll().size();

        // Create the TaskStep
        restTaskStepMockMvc.perform(post("/api/task-steps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskStep)))
            .andExpect(status().isCreated());

        // Validate the TaskStep in the database
        List<TaskStep> taskStepList = taskStepRepository.findAll();
        assertThat(taskStepList).hasSize(databaseSizeBeforeCreate + 1);
        TaskStep testTaskStep = taskStepList.get(taskStepList.size() - 1);
        assertThat(testTaskStep.getTaskUuid()).isEqualTo(DEFAULT_TASK_UUID);
        assertThat(testTaskStep.getStepName()).isEqualTo(DEFAULT_STEP_NAME);
        assertThat(testTaskStep.getStepStatus()).isEqualTo(DEFAULT_STEP_STATUS);
        assertThat(testTaskStep.getStepProgress()).isEqualTo(DEFAULT_STEP_PROGRESS);
        assertThat(testTaskStep.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTaskStep.getStepDate()).isEqualTo(DEFAULT_STEP_DATE);
    }

    @Test
    @Transactional
    public void createTaskStepWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = taskStepRepository.findAll().size();

        // Create the TaskStep with an existing ID
        taskStep.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskStepMockMvc.perform(post("/api/task-steps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskStep)))
            .andExpect(status().isBadRequest());

        // Validate the TaskStep in the database
        List<TaskStep> taskStepList = taskStepRepository.findAll();
        assertThat(taskStepList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkTaskUuidIsRequired() throws Exception {
        int databaseSizeBeforeTest = taskStepRepository.findAll().size();
        // set the field null
        taskStep.setTaskUuid(null);

        // Create the TaskStep, which fails.

        restTaskStepMockMvc.perform(post("/api/task-steps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskStep)))
            .andExpect(status().isBadRequest());

        List<TaskStep> taskStepList = taskStepRepository.findAll();
        assertThat(taskStepList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTaskSteps() throws Exception {
        // Initialize the database
        taskStepRepository.saveAndFlush(taskStep);

        // Get all the taskStepList
        restTaskStepMockMvc.perform(get("/api/task-steps?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskStep.getId().intValue())))
            .andExpect(jsonPath("$.[*].taskUuid").value(hasItem(DEFAULT_TASK_UUID.toString())))
            .andExpect(jsonPath("$.[*].stepName").value(hasItem(DEFAULT_STEP_NAME.toString())))
            .andExpect(jsonPath("$.[*].stepStatus").value(hasItem(DEFAULT_STEP_STATUS.toString())))
            .andExpect(jsonPath("$.[*].stepProgress").value(hasItem(DEFAULT_STEP_PROGRESS)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].stepDate").value(hasItem(DEFAULT_STEP_DATE.toString())));
    }

    @Test
    @Transactional
    public void getTaskStep() throws Exception {
        // Initialize the database
        taskStepRepository.saveAndFlush(taskStep);

        // Get the taskStep
        restTaskStepMockMvc.perform(get("/api/task-steps/{id}", taskStep.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(taskStep.getId().intValue()))
            .andExpect(jsonPath("$.taskUuid").value(DEFAULT_TASK_UUID.toString()))
            .andExpect(jsonPath("$.stepName").value(DEFAULT_STEP_NAME.toString()))
            .andExpect(jsonPath("$.stepStatus").value(DEFAULT_STEP_STATUS.toString()))
            .andExpect(jsonPath("$.stepProgress").value(DEFAULT_STEP_PROGRESS))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.stepDate").value(DEFAULT_STEP_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTaskStep() throws Exception {
        // Get the taskStep
        restTaskStepMockMvc.perform(get("/api/task-steps/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTaskStep() throws Exception {
        // Initialize the database
        taskStepRepository.saveAndFlush(taskStep);
        int databaseSizeBeforeUpdate = taskStepRepository.findAll().size();

        // Update the taskStep
        TaskStep updatedTaskStep = taskStepRepository.findOne(taskStep.getId());
        // Disconnect from session so that the updates on updatedTaskStep are not directly saved in db
        em.detach(updatedTaskStep);
        updatedTaskStep
            .taskUuid(UPDATED_TASK_UUID)
            .stepName(UPDATED_STEP_NAME)
            .stepStatus(UPDATED_STEP_STATUS)
            .stepProgress(UPDATED_STEP_PROGRESS)
            .description(UPDATED_DESCRIPTION)
            .stepDate(UPDATED_STEP_DATE);

        restTaskStepMockMvc.perform(put("/api/task-steps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTaskStep)))
            .andExpect(status().isOk());

        // Validate the TaskStep in the database
        List<TaskStep> taskStepList = taskStepRepository.findAll();
        assertThat(taskStepList).hasSize(databaseSizeBeforeUpdate);
        TaskStep testTaskStep = taskStepList.get(taskStepList.size() - 1);
        assertThat(testTaskStep.getTaskUuid()).isEqualTo(UPDATED_TASK_UUID);
        assertThat(testTaskStep.getStepName()).isEqualTo(UPDATED_STEP_NAME);
        assertThat(testTaskStep.getStepStatus()).isEqualTo(UPDATED_STEP_STATUS);
        assertThat(testTaskStep.getStepProgress()).isEqualTo(UPDATED_STEP_PROGRESS);
        assertThat(testTaskStep.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTaskStep.getStepDate()).isEqualTo(UPDATED_STEP_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingTaskStep() throws Exception {
        int databaseSizeBeforeUpdate = taskStepRepository.findAll().size();

        // Create the TaskStep

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTaskStepMockMvc.perform(put("/api/task-steps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskStep)))
            .andExpect(status().isCreated());

        // Validate the TaskStep in the database
        List<TaskStep> taskStepList = taskStepRepository.findAll();
        assertThat(taskStepList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTaskStep() throws Exception {
        // Initialize the database
        taskStepRepository.saveAndFlush(taskStep);
        int databaseSizeBeforeDelete = taskStepRepository.findAll().size();

        // Get the taskStep
        restTaskStepMockMvc.perform(delete("/api/task-steps/{id}", taskStep.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<TaskStep> taskStepList = taskStepRepository.findAll();
        assertThat(taskStepList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskStep.class);
        TaskStep taskStep1 = new TaskStep();
        taskStep1.setId(1L);
        TaskStep taskStep2 = new TaskStep();
        taskStep2.setId(taskStep1.getId());
        assertThat(taskStep1).isEqualTo(taskStep2);
        taskStep2.setId(2L);
        assertThat(taskStep1).isNotEqualTo(taskStep2);
        taskStep1.setId(null);
        assertThat(taskStep1).isNotEqualTo(taskStep2);
    }
}
