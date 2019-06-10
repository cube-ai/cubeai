package com.wyy.web.rest;

import com.wyy.UmmApp;

import com.wyy.config.SecurityBeanOverrideConfiguration;

import com.wyy.domain.PublishRequest;
import com.wyy.repository.PublishRequestRepository;
import com.wyy.repository.SolutionRepository;
import com.wyy.service.MessageService;
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
 * Test class for the PublishRequestResource REST controller.
 *
 * @see PublishRequestResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UmmApp.class, SecurityBeanOverrideConfiguration.class})
public class PublishRequestResourceIntTest {

    private static final String DEFAULT_SOLUTION_UUID = "AAAAAAAAAA";
    private static final String UPDATED_SOLUTION_UUID = "BBBBBBBBBB";

    private static final String DEFAULT_SOLUTION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SOLUTION_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_REQUEST_USER_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_REQUEST_USER_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_REQUEST_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_REQUEST_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_REQUEST_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REQUEST_REASON = "BBBBBBBBBB";

    private static final Instant DEFAULT_REQUEST_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REQUEST_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_REVIEWED = false;
    private static final Boolean UPDATED_REVIEWED = true;

    private static final String DEFAULT_REVIEW_USER_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_REVIEW_USER_LOGIN = "BBBBBBBBBB";

    private static final Instant DEFAULT_REVIEW_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REVIEW_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_REVIEW_RESULT = "AAAAAAAAAA";
    private static final String UPDATED_REVIEW_RESULT = "BBBBBBBBBB";

    private static final String DEFAULT_REVIEW_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_REVIEW_COMMENT = "BBBBBBBBBB";

    @Autowired
    private PublishRequestRepository publishRequestRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPublishRequestMockMvc;

    private PublishRequest publishRequest;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PublishRequestResource publishRequestResource = new PublishRequestResource(publishRequestRepository);
        this.restPublishRequestMockMvc = MockMvcBuilders.standaloneSetup(publishRequestResource)
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
    public static PublishRequest createEntity(EntityManager em) {
        PublishRequest publishRequest = new PublishRequest()
            .solutionUuid(DEFAULT_SOLUTION_UUID)
            .solutionName(DEFAULT_SOLUTION_NAME)
            .requestUserLogin(DEFAULT_REQUEST_USER_LOGIN)
            .requestType(DEFAULT_REQUEST_TYPE)
            .requestReason(DEFAULT_REQUEST_REASON)
            .requestDate(DEFAULT_REQUEST_DATE)
            .reviewed(DEFAULT_REVIEWED)
            .reviewUserLogin(DEFAULT_REVIEW_USER_LOGIN)
            .reviewDate(DEFAULT_REVIEW_DATE)
            .reviewResult(DEFAULT_REVIEW_RESULT)
            .reviewComment(DEFAULT_REVIEW_COMMENT);
        return publishRequest;
    }

    @Before
    public void initTest() {
        publishRequest = createEntity(em);
    }

    @Test
    @Transactional
    public void createPublishRequest() throws Exception {
        int databaseSizeBeforeCreate = publishRequestRepository.findAll().size();

        // Create the PublishRequest
        restPublishRequestMockMvc.perform(post("/api/publish-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(publishRequest)))
            .andExpect(status().isCreated());

        // Validate the PublishRequest in the database
        List<PublishRequest> publishRequestList = publishRequestRepository.findAll();
        assertThat(publishRequestList).hasSize(databaseSizeBeforeCreate + 1);
        PublishRequest testPublishRequest = publishRequestList.get(publishRequestList.size() - 1);
        assertThat(testPublishRequest.getSolutionUuid()).isEqualTo(DEFAULT_SOLUTION_UUID);
        assertThat(testPublishRequest.getSolutionName()).isEqualTo(DEFAULT_SOLUTION_NAME);
        assertThat(testPublishRequest.getRequestUserLogin()).isEqualTo(DEFAULT_REQUEST_USER_LOGIN);
        assertThat(testPublishRequest.getRequestType()).isEqualTo(DEFAULT_REQUEST_TYPE);
        assertThat(testPublishRequest.getRequestReason()).isEqualTo(DEFAULT_REQUEST_REASON);
        assertThat(testPublishRequest.getRequestDate()).isEqualTo(DEFAULT_REQUEST_DATE);
        assertThat(testPublishRequest.isReviewed()).isEqualTo(DEFAULT_REVIEWED);
        assertThat(testPublishRequest.getReviewUserLogin()).isEqualTo(DEFAULT_REVIEW_USER_LOGIN);
        assertThat(testPublishRequest.getReviewDate()).isEqualTo(DEFAULT_REVIEW_DATE);
        assertThat(testPublishRequest.getReviewResult()).isEqualTo(DEFAULT_REVIEW_RESULT);
        assertThat(testPublishRequest.getReviewComment()).isEqualTo(DEFAULT_REVIEW_COMMENT);
    }

    @Test
    @Transactional
    public void createPublishRequestWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = publishRequestRepository.findAll().size();

        // Create the PublishRequest with an existing ID
        publishRequest.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPublishRequestMockMvc.perform(post("/api/publish-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(publishRequest)))
            .andExpect(status().isBadRequest());

        // Validate the PublishRequest in the database
        List<PublishRequest> publishRequestList = publishRequestRepository.findAll();
        assertThat(publishRequestList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllPublishRequests() throws Exception {
        // Initialize the database
        publishRequestRepository.saveAndFlush(publishRequest);

        // Get all the publishRequestList
        restPublishRequestMockMvc.perform(get("/api/publish-requests?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(publishRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].solutionUuid").value(hasItem(DEFAULT_SOLUTION_UUID.toString())))
            .andExpect(jsonPath("$.[*].solutionName").value(hasItem(DEFAULT_SOLUTION_NAME.toString())))
            .andExpect(jsonPath("$.[*].requestUserLogin").value(hasItem(DEFAULT_REQUEST_USER_LOGIN.toString())))
            .andExpect(jsonPath("$.[*].requestType").value(hasItem(DEFAULT_REQUEST_TYPE.toString())))
            .andExpect(jsonPath("$.[*].requestReason").value(hasItem(DEFAULT_REQUEST_REASON.toString())))
            .andExpect(jsonPath("$.[*].requestDate").value(hasItem(DEFAULT_REQUEST_DATE.toString())))
            .andExpect(jsonPath("$.[*].reviewed").value(hasItem(DEFAULT_REVIEWED.booleanValue())))
            .andExpect(jsonPath("$.[*].reviewUserLogin").value(hasItem(DEFAULT_REVIEW_USER_LOGIN.toString())))
            .andExpect(jsonPath("$.[*].reviewDate").value(hasItem(DEFAULT_REVIEW_DATE.toString())))
            .andExpect(jsonPath("$.[*].reviewResult").value(hasItem(DEFAULT_REVIEW_RESULT.toString())))
            .andExpect(jsonPath("$.[*].reviewComment").value(hasItem(DEFAULT_REVIEW_COMMENT.toString())));
    }

    @Test
    @Transactional
    public void getPublishRequest() throws Exception {
        // Initialize the database
        publishRequestRepository.saveAndFlush(publishRequest);

        // Get the publishRequest
        restPublishRequestMockMvc.perform(get("/api/publish-requests/{id}", publishRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(publishRequest.getId().intValue()))
            .andExpect(jsonPath("$.solutionUuid").value(DEFAULT_SOLUTION_UUID.toString()))
            .andExpect(jsonPath("$.solutionName").value(DEFAULT_SOLUTION_NAME.toString()))
            .andExpect(jsonPath("$.requestUserLogin").value(DEFAULT_REQUEST_USER_LOGIN.toString()))
            .andExpect(jsonPath("$.requestType").value(DEFAULT_REQUEST_TYPE.toString()))
            .andExpect(jsonPath("$.requestReason").value(DEFAULT_REQUEST_REASON.toString()))
            .andExpect(jsonPath("$.requestDate").value(DEFAULT_REQUEST_DATE.toString()))
            .andExpect(jsonPath("$.reviewed").value(DEFAULT_REVIEWED.booleanValue()))
            .andExpect(jsonPath("$.reviewUserLogin").value(DEFAULT_REVIEW_USER_LOGIN.toString()))
            .andExpect(jsonPath("$.reviewDate").value(DEFAULT_REVIEW_DATE.toString()))
            .andExpect(jsonPath("$.reviewResult").value(DEFAULT_REVIEW_RESULT.toString()))
            .andExpect(jsonPath("$.reviewComment").value(DEFAULT_REVIEW_COMMENT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPublishRequest() throws Exception {
        // Get the publishRequest
        restPublishRequestMockMvc.perform(get("/api/publish-requests/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePublishRequest() throws Exception {
        // Initialize the database
        publishRequestRepository.saveAndFlush(publishRequest);
        int databaseSizeBeforeUpdate = publishRequestRepository.findAll().size();

        // Update the publishRequest
        PublishRequest updatedPublishRequest = publishRequestRepository.findOne(publishRequest.getId());
        // Disconnect from session so that the updates on updatedPublishRequest are not directly saved in db
        em.detach(updatedPublishRequest);
        updatedPublishRequest
            .solutionUuid(UPDATED_SOLUTION_UUID)
            .solutionName(UPDATED_SOLUTION_NAME)
            .requestUserLogin(UPDATED_REQUEST_USER_LOGIN)
            .requestType(UPDATED_REQUEST_TYPE)
            .requestReason(UPDATED_REQUEST_REASON)
            .requestDate(UPDATED_REQUEST_DATE)
            .reviewed(UPDATED_REVIEWED)
            .reviewUserLogin(UPDATED_REVIEW_USER_LOGIN)
            .reviewDate(UPDATED_REVIEW_DATE)
            .reviewResult(UPDATED_REVIEW_RESULT)
            .reviewComment(UPDATED_REVIEW_COMMENT);

        restPublishRequestMockMvc.perform(put("/api/publish-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPublishRequest)))
            .andExpect(status().isOk());

        // Validate the PublishRequest in the database
        List<PublishRequest> publishRequestList = publishRequestRepository.findAll();
        assertThat(publishRequestList).hasSize(databaseSizeBeforeUpdate);
        PublishRequest testPublishRequest = publishRequestList.get(publishRequestList.size() - 1);
        assertThat(testPublishRequest.getSolutionUuid()).isEqualTo(UPDATED_SOLUTION_UUID);
        assertThat(testPublishRequest.getSolutionName()).isEqualTo(UPDATED_SOLUTION_NAME);
        assertThat(testPublishRequest.getRequestUserLogin()).isEqualTo(UPDATED_REQUEST_USER_LOGIN);
        assertThat(testPublishRequest.getRequestType()).isEqualTo(UPDATED_REQUEST_TYPE);
        assertThat(testPublishRequest.getRequestReason()).isEqualTo(UPDATED_REQUEST_REASON);
        assertThat(testPublishRequest.getRequestDate()).isEqualTo(UPDATED_REQUEST_DATE);
        assertThat(testPublishRequest.isReviewed()).isEqualTo(UPDATED_REVIEWED);
        assertThat(testPublishRequest.getReviewUserLogin()).isEqualTo(UPDATED_REVIEW_USER_LOGIN);
        assertThat(testPublishRequest.getReviewDate()).isEqualTo(UPDATED_REVIEW_DATE);
        assertThat(testPublishRequest.getReviewResult()).isEqualTo(UPDATED_REVIEW_RESULT);
        assertThat(testPublishRequest.getReviewComment()).isEqualTo(UPDATED_REVIEW_COMMENT);
    }

    @Test
    @Transactional
    public void updateNonExistingPublishRequest() throws Exception {
        int databaseSizeBeforeUpdate = publishRequestRepository.findAll().size();

        // Create the PublishRequest

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPublishRequestMockMvc.perform(put("/api/publish-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(publishRequest)))
            .andExpect(status().isCreated());

        // Validate the PublishRequest in the database
        List<PublishRequest> publishRequestList = publishRequestRepository.findAll();
        assertThat(publishRequestList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePublishRequest() throws Exception {
        // Initialize the database
        publishRequestRepository.saveAndFlush(publishRequest);
        int databaseSizeBeforeDelete = publishRequestRepository.findAll().size();

        // Get the publishRequest
        restPublishRequestMockMvc.perform(delete("/api/publish-requests/{id}", publishRequest.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<PublishRequest> publishRequestList = publishRequestRepository.findAll();
        assertThat(publishRequestList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PublishRequest.class);
        PublishRequest publishRequest1 = new PublishRequest();
        publishRequest1.setId(1L);
        PublishRequest publishRequest2 = new PublishRequest();
        publishRequest2.setId(publishRequest1.getId());
        assertThat(publishRequest1).isEqualTo(publishRequest2);
        publishRequest2.setId(2L);
        assertThat(publishRequest1).isNotEqualTo(publishRequest2);
        publishRequest1.setId(null);
        assertThat(publishRequest1).isNotEqualTo(publishRequest2);
    }
}
