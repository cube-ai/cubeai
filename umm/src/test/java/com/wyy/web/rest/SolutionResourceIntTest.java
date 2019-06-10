package com.wyy.web.rest;

import com.wyy.UmmApp;

import com.wyy.config.SecurityBeanOverrideConfiguration;

import com.wyy.domain.Solution;
import com.wyy.repository.*;
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
 * Test class for the SolutionResource REST controller.
 *
 * @see SolutionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UmmApp.class, SecurityBeanOverrideConfiguration.class})
public class SolutionResourceIntTest {

    private static final String DEFAULT_UUID = "AAAAAAAAAA";
    private static final String UPDATED_UUID = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHOR_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHOR_NAME = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COMPANY = "AAAAAAAAAA";
    private static final String UPDATED_COMPANY = "BBBBBBBBBB";

    private static final String DEFAULT_CO_AUTHORS = "AAAAAAAAAA";
    private static final String UPDATED_CO_AUTHORS = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_SUMMARY = "AAAAAAAAAA";
    private static final String UPDATED_SUMMARY = "BBBBBBBBBB";

    private static final String DEFAULT_TAG_1 = "AAAAAAAAAA";
    private static final String UPDATED_TAG_1 = "BBBBBBBBBB";

    private static final String DEFAULT_TAG_2 = "AAAAAAAAAA";
    private static final String UPDATED_TAG_2 = "BBBBBBBBBB";

    private static final String DEFAULT_TAG_3 = "AAAAAAAAAA";
    private static final String UPDATED_TAG_3 = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT_1 = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT_1 = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT_2 = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT_2 = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT_3 = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT_3 = "BBBBBBBBBB";

    private static final Long DEFAULT_DISPLAY_ORDER = 1L;
    private static final Long UPDATED_DISPLAY_ORDER = 2L;

    private static final String DEFAULT_PICTURE_URL = "AAAAAAAAAA";
    private static final String UPDATED_PICTURE_URL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String DEFAULT_MODEL_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_MODEL_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_TOOLKIT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TOOLKIT_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_VALIDATION_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_VALIDATION_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_PUBLISH_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_PUBLISH_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_PUBLISH_REQUEST = "AAAAAAAAAA";
    private static final String UPDATED_PUBLISH_REQUEST = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_VIEW_COUNT = 1L;
    private static final Long UPDATED_VIEW_COUNT = 2L;

    private static final Long DEFAULT_DOWNLOAD_COUNT = 1L;
    private static final Long UPDATED_DOWNLOAD_COUNT = 2L;

    private static final Instant DEFAULT_LAST_DOWNLOAD = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_DOWNLOAD = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_COMMENT_COUNT = 1L;
    private static final Long UPDATED_COMMENT_COUNT = 2L;

    private static final Long DEFAULT_RATING_COUNT = 1L;
    private static final Long UPDATED_RATING_COUNT = 2L;

    private static final Double DEFAULT_RATING_AVERAGE = 1D;
    private static final Double UPDATED_RATING_AVERAGE = 2D;

    @Autowired
    private SolutionRepository solutionRepository;

    @Autowired
    private DescriptionRepository descriptionRepository;

    @Autowired
    private SolutionRatingRepository solutionRatingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PublishRequestRepository publishRequestRepository;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSolutionMockMvc;

    private Solution solution;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SolutionResource solutionResource = new SolutionResource(
            solutionRepository,
            descriptionRepository,
            solutionRatingRepository,
            commentRepository,
            publishRequestRepository,
            messageService);
        this.restSolutionMockMvc = MockMvcBuilders.standaloneSetup(solutionResource)
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
    public static Solution createEntity(EntityManager em) {
        Solution solution = new Solution()
            .uuid(DEFAULT_UUID)
            .authorLogin(DEFAULT_AUTHOR_LOGIN)
            .authorName(DEFAULT_AUTHOR_NAME)
            .company(DEFAULT_COMPANY)
            .coAuthors(DEFAULT_CO_AUTHORS)
            .name(DEFAULT_NAME)
            .version(DEFAULT_VERSION)
            .summary(DEFAULT_SUMMARY)
            .tag1(DEFAULT_TAG_1)
            .tag2(DEFAULT_TAG_2)
            .tag3(DEFAULT_TAG_3)
            .subject1(DEFAULT_SUBJECT_1)
            .subject2(DEFAULT_SUBJECT_2)
            .subject3(DEFAULT_SUBJECT_3)
            .displayOrder(DEFAULT_DISPLAY_ORDER)
            .pictureUrl(DEFAULT_PICTURE_URL)
            .active(DEFAULT_ACTIVE)
            .modelType(DEFAULT_MODEL_TYPE)
            .toolkitType(DEFAULT_TOOLKIT_TYPE)
            .validationStatus(DEFAULT_VALIDATION_STATUS)
            .publishStatus(DEFAULT_PUBLISH_STATUS)
            .publishRequest(DEFAULT_PUBLISH_REQUEST)
            .createdDate(DEFAULT_CREATED_DATE)
            .modifiedDate(DEFAULT_MODIFIED_DATE)
            .viewCount(DEFAULT_VIEW_COUNT)
            .downloadCount(DEFAULT_DOWNLOAD_COUNT)
            .lastDownload(DEFAULT_LAST_DOWNLOAD)
            .commentCount(DEFAULT_COMMENT_COUNT)
            .ratingCount(DEFAULT_RATING_COUNT)
            .ratingAverage(DEFAULT_RATING_AVERAGE);
        return solution;
    }

    @Before
    public void initTest() {
        solution = createEntity(em);
    }

    @Test
    @Transactional
    public void createSolution() throws Exception {
        int databaseSizeBeforeCreate = solutionRepository.findAll().size();

        // Create the Solution
        restSolutionMockMvc.perform(post("/api/solutions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solution)))
            .andExpect(status().isCreated());

        // Validate the Solution in the database
        List<Solution> solutionList = solutionRepository.findAll();
        assertThat(solutionList).hasSize(databaseSizeBeforeCreate + 1);
        Solution testSolution = solutionList.get(solutionList.size() - 1);
        assertThat(testSolution.getUuid()).isEqualTo(DEFAULT_UUID);
        assertThat(testSolution.getAuthorLogin()).isEqualTo(DEFAULT_AUTHOR_LOGIN);
        assertThat(testSolution.getAuthorName()).isEqualTo(DEFAULT_AUTHOR_NAME);
        assertThat(testSolution.getCompany()).isEqualTo(DEFAULT_COMPANY);
        assertThat(testSolution.getCoAuthors()).isEqualTo(DEFAULT_CO_AUTHORS);
        assertThat(testSolution.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSolution.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testSolution.getSummary()).isEqualTo(DEFAULT_SUMMARY);
        assertThat(testSolution.getTag1()).isEqualTo(DEFAULT_TAG_1);
        assertThat(testSolution.getTag2()).isEqualTo(DEFAULT_TAG_2);
        assertThat(testSolution.getTag3()).isEqualTo(DEFAULT_TAG_3);
        assertThat(testSolution.getSubject1()).isEqualTo(DEFAULT_SUBJECT_1);
        assertThat(testSolution.getSubject2()).isEqualTo(DEFAULT_SUBJECT_2);
        assertThat(testSolution.getSubject3()).isEqualTo(DEFAULT_SUBJECT_3);
        assertThat(testSolution.getDisplayOrder()).isEqualTo(DEFAULT_DISPLAY_ORDER);
        assertThat(testSolution.getPictureUrl()).isEqualTo(DEFAULT_PICTURE_URL);
        assertThat(testSolution.isActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testSolution.getModelType()).isEqualTo(DEFAULT_MODEL_TYPE);
        assertThat(testSolution.getToolkitType()).isEqualTo(DEFAULT_TOOLKIT_TYPE);
        assertThat(testSolution.getValidationStatus()).isEqualTo(DEFAULT_VALIDATION_STATUS);
        assertThat(testSolution.getPublishStatus()).isEqualTo(DEFAULT_PUBLISH_STATUS);
        assertThat(testSolution.getPublishRequest()).isEqualTo(DEFAULT_PUBLISH_REQUEST);
        assertThat(testSolution.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testSolution.getModifiedDate()).isEqualTo(DEFAULT_MODIFIED_DATE);
        assertThat(testSolution.getViewCount()).isEqualTo(DEFAULT_VIEW_COUNT);
        assertThat(testSolution.getDownloadCount()).isEqualTo(DEFAULT_DOWNLOAD_COUNT);
        assertThat(testSolution.getLastDownload()).isEqualTo(DEFAULT_LAST_DOWNLOAD);
        assertThat(testSolution.getCommentCount()).isEqualTo(DEFAULT_COMMENT_COUNT);
        assertThat(testSolution.getRatingCount()).isEqualTo(DEFAULT_RATING_COUNT);
        assertThat(testSolution.getRatingAverage()).isEqualTo(DEFAULT_RATING_AVERAGE);
    }

    @Test
    @Transactional
    public void createSolutionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = solutionRepository.findAll().size();

        // Create the Solution with an existing ID
        solution.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSolutionMockMvc.perform(post("/api/solutions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solution)))
            .andExpect(status().isBadRequest());

        // Validate the Solution in the database
        List<Solution> solutionList = solutionRepository.findAll();
        assertThat(solutionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkUuidIsRequired() throws Exception {
        int databaseSizeBeforeTest = solutionRepository.findAll().size();
        // set the field null
        solution.setUuid(null);

        // Create the Solution, which fails.

        restSolutionMockMvc.perform(post("/api/solutions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solution)))
            .andExpect(status().isBadRequest());

        List<Solution> solutionList = solutionRepository.findAll();
        assertThat(solutionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSolutions() throws Exception {
        // Initialize the database
        solutionRepository.saveAndFlush(solution);

        // Get all the solutionList
        restSolutionMockMvc.perform(get("/api/solutions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(solution.getId().intValue())))
            .andExpect(jsonPath("$.[*].uuid").value(hasItem(DEFAULT_UUID.toString())))
            .andExpect(jsonPath("$.[*].authorLogin").value(hasItem(DEFAULT_AUTHOR_LOGIN.toString())))
            .andExpect(jsonPath("$.[*].authorName").value(hasItem(DEFAULT_AUTHOR_NAME.toString())))
            .andExpect(jsonPath("$.[*].company").value(hasItem(DEFAULT_COMPANY.toString())))
            .andExpect(jsonPath("$.[*].coAuthors").value(hasItem(DEFAULT_CO_AUTHORS.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.toString())))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY.toString())))
            .andExpect(jsonPath("$.[*].tag1").value(hasItem(DEFAULT_TAG_1.toString())))
            .andExpect(jsonPath("$.[*].tag2").value(hasItem(DEFAULT_TAG_2.toString())))
            .andExpect(jsonPath("$.[*].tag3").value(hasItem(DEFAULT_TAG_3.toString())))
            .andExpect(jsonPath("$.[*].subject1").value(hasItem(DEFAULT_SUBJECT_1.toString())))
            .andExpect(jsonPath("$.[*].subject2").value(hasItem(DEFAULT_SUBJECT_2.toString())))
            .andExpect(jsonPath("$.[*].subject3").value(hasItem(DEFAULT_SUBJECT_3.toString())))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER.intValue())))
            .andExpect(jsonPath("$.[*].pictureUrl").value(hasItem(DEFAULT_PICTURE_URL.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].modelType").value(hasItem(DEFAULT_MODEL_TYPE.toString())))
            .andExpect(jsonPath("$.[*].toolkitType").value(hasItem(DEFAULT_TOOLKIT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].validationStatus").value(hasItem(DEFAULT_VALIDATION_STATUS.toString())))
            .andExpect(jsonPath("$.[*].publishStatus").value(hasItem(DEFAULT_PUBLISH_STATUS.toString())))
            .andExpect(jsonPath("$.[*].publishRequest").value(hasItem(DEFAULT_PUBLISH_REQUEST.toString())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].modifiedDate").value(hasItem(DEFAULT_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].viewCount").value(hasItem(DEFAULT_VIEW_COUNT.intValue())))
            .andExpect(jsonPath("$.[*].downloadCount").value(hasItem(DEFAULT_DOWNLOAD_COUNT.intValue())))
            .andExpect(jsonPath("$.[*].lastDownload").value(hasItem(DEFAULT_LAST_DOWNLOAD.toString())))
            .andExpect(jsonPath("$.[*].commentCount").value(hasItem(DEFAULT_COMMENT_COUNT.intValue())))
            .andExpect(jsonPath("$.[*].ratingCount").value(hasItem(DEFAULT_RATING_COUNT.intValue())))
            .andExpect(jsonPath("$.[*].ratingAverage").value(hasItem(DEFAULT_RATING_AVERAGE.doubleValue())));
    }

    @Test
    @Transactional
    public void getSolution() throws Exception {
        // Initialize the database
        solutionRepository.saveAndFlush(solution);

        // Get the solution
        restSolutionMockMvc.perform(get("/api/solutions/{id}", solution.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(solution.getId().intValue()))
            .andExpect(jsonPath("$.uuid").value(DEFAULT_UUID.toString()))
            .andExpect(jsonPath("$.authorLogin").value(DEFAULT_AUTHOR_LOGIN.toString()))
            .andExpect(jsonPath("$.authorName").value(DEFAULT_AUTHOR_NAME.toString()))
            .andExpect(jsonPath("$.company").value(DEFAULT_COMPANY.toString()))
            .andExpect(jsonPath("$.coAuthors").value(DEFAULT_CO_AUTHORS.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION.toString()))
            .andExpect(jsonPath("$.summary").value(DEFAULT_SUMMARY.toString()))
            .andExpect(jsonPath("$.tag1").value(DEFAULT_TAG_1.toString()))
            .andExpect(jsonPath("$.tag2").value(DEFAULT_TAG_2.toString()))
            .andExpect(jsonPath("$.tag3").value(DEFAULT_TAG_3.toString()))
            .andExpect(jsonPath("$.subject1").value(DEFAULT_SUBJECT_1.toString()))
            .andExpect(jsonPath("$.subject2").value(DEFAULT_SUBJECT_2.toString()))
            .andExpect(jsonPath("$.subject3").value(DEFAULT_SUBJECT_3.toString()))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER.intValue()))
            .andExpect(jsonPath("$.pictureUrl").value(DEFAULT_PICTURE_URL.toString()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.modelType").value(DEFAULT_MODEL_TYPE.toString()))
            .andExpect(jsonPath("$.toolkitType").value(DEFAULT_TOOLKIT_TYPE.toString()))
            .andExpect(jsonPath("$.validationStatus").value(DEFAULT_VALIDATION_STATUS.toString()))
            .andExpect(jsonPath("$.publishStatus").value(DEFAULT_PUBLISH_STATUS.toString()))
            .andExpect(jsonPath("$.publishRequest").value(DEFAULT_PUBLISH_REQUEST.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.modifiedDate").value(DEFAULT_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.viewCount").value(DEFAULT_VIEW_COUNT.intValue()))
            .andExpect(jsonPath("$.downloadCount").value(DEFAULT_DOWNLOAD_COUNT.intValue()))
            .andExpect(jsonPath("$.lastDownload").value(DEFAULT_LAST_DOWNLOAD.toString()))
            .andExpect(jsonPath("$.commentCount").value(DEFAULT_COMMENT_COUNT.intValue()))
            .andExpect(jsonPath("$.ratingCount").value(DEFAULT_RATING_COUNT.intValue()))
            .andExpect(jsonPath("$.ratingAverage").value(DEFAULT_RATING_AVERAGE.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingSolution() throws Exception {
        // Get the solution
        restSolutionMockMvc.perform(get("/api/solutions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSolution() throws Exception {
        // Initialize the database
        solutionRepository.saveAndFlush(solution);
        int databaseSizeBeforeUpdate = solutionRepository.findAll().size();

        // Update the solution
        Solution updatedSolution = solutionRepository.findOne(solution.getId());
        // Disconnect from session so that the updates on updatedSolution are not directly saved in db
        em.detach(updatedSolution);
        updatedSolution
            .uuid(UPDATED_UUID)
            .authorLogin(UPDATED_AUTHOR_LOGIN)
            .authorName(UPDATED_AUTHOR_NAME)
            .company(UPDATED_COMPANY)
            .coAuthors(UPDATED_CO_AUTHORS)
            .name(UPDATED_NAME)
            .version(UPDATED_VERSION)
            .summary(UPDATED_SUMMARY)
            .tag1(UPDATED_TAG_1)
            .tag2(UPDATED_TAG_2)
            .tag3(UPDATED_TAG_3)
            .subject1(UPDATED_SUBJECT_1)
            .subject2(UPDATED_SUBJECT_2)
            .subject3(UPDATED_SUBJECT_3)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .pictureUrl(UPDATED_PICTURE_URL)
            .active(UPDATED_ACTIVE)
            .modelType(UPDATED_MODEL_TYPE)
            .toolkitType(UPDATED_TOOLKIT_TYPE)
            .validationStatus(UPDATED_VALIDATION_STATUS)
            .publishStatus(UPDATED_PUBLISH_STATUS)
            .publishRequest(UPDATED_PUBLISH_REQUEST)
            .createdDate(UPDATED_CREATED_DATE)
            .modifiedDate(UPDATED_MODIFIED_DATE)
            .viewCount(UPDATED_VIEW_COUNT)
            .downloadCount(UPDATED_DOWNLOAD_COUNT)
            .lastDownload(UPDATED_LAST_DOWNLOAD)
            .commentCount(UPDATED_COMMENT_COUNT)
            .ratingCount(UPDATED_RATING_COUNT)
            .ratingAverage(UPDATED_RATING_AVERAGE);

        restSolutionMockMvc.perform(put("/api/solutions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedSolution)))
            .andExpect(status().isOk());

        // Validate the Solution in the database
        List<Solution> solutionList = solutionRepository.findAll();
        assertThat(solutionList).hasSize(databaseSizeBeforeUpdate);
        Solution testSolution = solutionList.get(solutionList.size() - 1);
        assertThat(testSolution.getUuid()).isEqualTo(UPDATED_UUID);
        assertThat(testSolution.getAuthorLogin()).isEqualTo(UPDATED_AUTHOR_LOGIN);
        assertThat(testSolution.getAuthorName()).isEqualTo(UPDATED_AUTHOR_NAME);
        assertThat(testSolution.getCompany()).isEqualTo(UPDATED_COMPANY);
        assertThat(testSolution.getCoAuthors()).isEqualTo(UPDATED_CO_AUTHORS);
        assertThat(testSolution.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSolution.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testSolution.getSummary()).isEqualTo(UPDATED_SUMMARY);
        assertThat(testSolution.getTag1()).isEqualTo(UPDATED_TAG_1);
        assertThat(testSolution.getTag2()).isEqualTo(UPDATED_TAG_2);
        assertThat(testSolution.getTag3()).isEqualTo(UPDATED_TAG_3);
        assertThat(testSolution.getSubject1()).isEqualTo(UPDATED_SUBJECT_1);
        assertThat(testSolution.getSubject2()).isEqualTo(UPDATED_SUBJECT_2);
        assertThat(testSolution.getSubject3()).isEqualTo(UPDATED_SUBJECT_3);
        assertThat(testSolution.getDisplayOrder()).isEqualTo(UPDATED_DISPLAY_ORDER);
        assertThat(testSolution.getPictureUrl()).isEqualTo(UPDATED_PICTURE_URL);
        assertThat(testSolution.isActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testSolution.getModelType()).isEqualTo(UPDATED_MODEL_TYPE);
        assertThat(testSolution.getToolkitType()).isEqualTo(UPDATED_TOOLKIT_TYPE);
        assertThat(testSolution.getValidationStatus()).isEqualTo(UPDATED_VALIDATION_STATUS);
        assertThat(testSolution.getPublishStatus()).isEqualTo(UPDATED_PUBLISH_STATUS);
        assertThat(testSolution.getPublishRequest()).isEqualTo(UPDATED_PUBLISH_REQUEST);
        assertThat(testSolution.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testSolution.getModifiedDate()).isEqualTo(UPDATED_MODIFIED_DATE);
        assertThat(testSolution.getViewCount()).isEqualTo(UPDATED_VIEW_COUNT);
        assertThat(testSolution.getDownloadCount()).isEqualTo(UPDATED_DOWNLOAD_COUNT);
        assertThat(testSolution.getLastDownload()).isEqualTo(UPDATED_LAST_DOWNLOAD);
        assertThat(testSolution.getCommentCount()).isEqualTo(UPDATED_COMMENT_COUNT);
        assertThat(testSolution.getRatingCount()).isEqualTo(UPDATED_RATING_COUNT);
        assertThat(testSolution.getRatingAverage()).isEqualTo(UPDATED_RATING_AVERAGE);
    }

    @Test
    @Transactional
    public void updateNonExistingSolution() throws Exception {
        int databaseSizeBeforeUpdate = solutionRepository.findAll().size();

        // Create the Solution

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restSolutionMockMvc.perform(put("/api/solutions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solution)))
            .andExpect(status().isCreated());

        // Validate the Solution in the database
        List<Solution> solutionList = solutionRepository.findAll();
        assertThat(solutionList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteSolution() throws Exception {
        // Initialize the database
        solutionRepository.saveAndFlush(solution);
        int databaseSizeBeforeDelete = solutionRepository.findAll().size();

        // Get the solution
        restSolutionMockMvc.perform(delete("/api/solutions/{id}", solution.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Solution> solutionList = solutionRepository.findAll();
        assertThat(solutionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Solution.class);
        Solution solution1 = new Solution();
        solution1.setId(1L);
        Solution solution2 = new Solution();
        solution2.setId(solution1.getId());
        assertThat(solution1).isEqualTo(solution2);
        solution2.setId(2L);
        assertThat(solution1).isNotEqualTo(solution2);
        solution1.setId(null);
        assertThat(solution1).isNotEqualTo(solution2);
    }
}
