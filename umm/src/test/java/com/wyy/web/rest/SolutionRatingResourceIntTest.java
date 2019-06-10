package com.wyy.web.rest;

import com.wyy.UmmApp;

import com.wyy.config.SecurityBeanOverrideConfiguration;

import com.wyy.domain.SolutionRating;
import com.wyy.repository.SolutionRatingRepository;
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
 * Test class for the SolutionRatingResource REST controller.
 *
 * @see SolutionRatingResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UmmApp.class, SecurityBeanOverrideConfiguration.class})
public class SolutionRatingResourceIntTest {

    private static final String DEFAULT_USER_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_USER_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_SOLUTION_UUID = "AAAAAAAAAA";
    private static final String UPDATED_SOLUTION_UUID = "BBBBBBBBBB";

    private static final Integer DEFAULT_RATING_SCORE = 1;
    private static final Integer UPDATED_RATING_SCORE = 2;

    private static final String DEFAULT_RATING_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_RATING_TEXT = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private SolutionRatingRepository solutionRatingRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSolutionRatingMockMvc;

    private SolutionRating solutionRating;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SolutionRatingResource solutionRatingResource = new SolutionRatingResource(solutionRatingRepository);
        this.restSolutionRatingMockMvc = MockMvcBuilders.standaloneSetup(solutionRatingResource)
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
    public static SolutionRating createEntity(EntityManager em) {
        SolutionRating solutionRating = new SolutionRating()
            .userLogin(DEFAULT_USER_LOGIN)
            .solutionUuid(DEFAULT_SOLUTION_UUID)
            .ratingScore(DEFAULT_RATING_SCORE)
            .ratingText(DEFAULT_RATING_TEXT)
            .createdDate(DEFAULT_CREATED_DATE)
            .modifiedDate(DEFAULT_MODIFIED_DATE);
        return solutionRating;
    }

    @Before
    public void initTest() {
        solutionRating = createEntity(em);
    }

    @Test
    @Transactional
    public void createSolutionRating() throws Exception {
        int databaseSizeBeforeCreate = solutionRatingRepository.findAll().size();

        // Create the SolutionRating
        restSolutionRatingMockMvc.perform(post("/api/solution-ratings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solutionRating)))
            .andExpect(status().isCreated());

        // Validate the SolutionRating in the database
        List<SolutionRating> solutionRatingList = solutionRatingRepository.findAll();
        assertThat(solutionRatingList).hasSize(databaseSizeBeforeCreate + 1);
        SolutionRating testSolutionRating = solutionRatingList.get(solutionRatingList.size() - 1);
        assertThat(testSolutionRating.getUserLogin()).isEqualTo(DEFAULT_USER_LOGIN);
        assertThat(testSolutionRating.getSolutionUuid()).isEqualTo(DEFAULT_SOLUTION_UUID);
        assertThat(testSolutionRating.getRatingScore()).isEqualTo(DEFAULT_RATING_SCORE);
        assertThat(testSolutionRating.getRatingText()).isEqualTo(DEFAULT_RATING_TEXT);
        assertThat(testSolutionRating.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testSolutionRating.getModifiedDate()).isEqualTo(DEFAULT_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void createSolutionRatingWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = solutionRatingRepository.findAll().size();

        // Create the SolutionRating with an existing ID
        solutionRating.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSolutionRatingMockMvc.perform(post("/api/solution-ratings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solutionRating)))
            .andExpect(status().isBadRequest());

        // Validate the SolutionRating in the database
        List<SolutionRating> solutionRatingList = solutionRatingRepository.findAll();
        assertThat(solutionRatingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkUserLoginIsRequired() throws Exception {
        int databaseSizeBeforeTest = solutionRatingRepository.findAll().size();
        // set the field null
        solutionRating.setUserLogin(null);

        // Create the SolutionRating, which fails.

        restSolutionRatingMockMvc.perform(post("/api/solution-ratings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solutionRating)))
            .andExpect(status().isBadRequest());

        List<SolutionRating> solutionRatingList = solutionRatingRepository.findAll();
        assertThat(solutionRatingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSolutionUuidIsRequired() throws Exception {
        int databaseSizeBeforeTest = solutionRatingRepository.findAll().size();
        // set the field null
        solutionRating.setSolutionUuid(null);

        // Create the SolutionRating, which fails.

        restSolutionRatingMockMvc.perform(post("/api/solution-ratings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solutionRating)))
            .andExpect(status().isBadRequest());

        List<SolutionRating> solutionRatingList = solutionRatingRepository.findAll();
        assertThat(solutionRatingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSolutionRatings() throws Exception {
        // Initialize the database
        solutionRatingRepository.saveAndFlush(solutionRating);

        // Get all the solutionRatingList
        restSolutionRatingMockMvc.perform(get("/api/solution-ratings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(solutionRating.getId().intValue())))
            .andExpect(jsonPath("$.[*].userLogin").value(hasItem(DEFAULT_USER_LOGIN.toString())))
            .andExpect(jsonPath("$.[*].solutionUuid").value(hasItem(DEFAULT_SOLUTION_UUID.toString())))
            .andExpect(jsonPath("$.[*].ratingScore").value(hasItem(DEFAULT_RATING_SCORE)))
            .andExpect(jsonPath("$.[*].ratingText").value(hasItem(DEFAULT_RATING_TEXT.toString())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].modifiedDate").value(hasItem(DEFAULT_MODIFIED_DATE.toString())));
    }

    @Test
    @Transactional
    public void getSolutionRating() throws Exception {
        // Initialize the database
        solutionRatingRepository.saveAndFlush(solutionRating);

        // Get the solutionRating
        restSolutionRatingMockMvc.perform(get("/api/solution-ratings/{id}", solutionRating.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(solutionRating.getId().intValue()))
            .andExpect(jsonPath("$.userLogin").value(DEFAULT_USER_LOGIN.toString()))
            .andExpect(jsonPath("$.solutionUuid").value(DEFAULT_SOLUTION_UUID.toString()))
            .andExpect(jsonPath("$.ratingScore").value(DEFAULT_RATING_SCORE))
            .andExpect(jsonPath("$.ratingText").value(DEFAULT_RATING_TEXT.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.modifiedDate").value(DEFAULT_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSolutionRating() throws Exception {
        // Get the solutionRating
        restSolutionRatingMockMvc.perform(get("/api/solution-ratings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSolutionRating() throws Exception {
        // Initialize the database
        solutionRatingRepository.saveAndFlush(solutionRating);
        int databaseSizeBeforeUpdate = solutionRatingRepository.findAll().size();

        // Update the solutionRating
        SolutionRating updatedSolutionRating = solutionRatingRepository.findOne(solutionRating.getId());
        // Disconnect from session so that the updates on updatedSolutionRating are not directly saved in db
        em.detach(updatedSolutionRating);
        updatedSolutionRating
            .userLogin(UPDATED_USER_LOGIN)
            .solutionUuid(UPDATED_SOLUTION_UUID)
            .ratingScore(UPDATED_RATING_SCORE)
            .ratingText(UPDATED_RATING_TEXT)
            .createdDate(UPDATED_CREATED_DATE)
            .modifiedDate(UPDATED_MODIFIED_DATE);

        restSolutionRatingMockMvc.perform(put("/api/solution-ratings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedSolutionRating)))
            .andExpect(status().isOk());

        // Validate the SolutionRating in the database
        List<SolutionRating> solutionRatingList = solutionRatingRepository.findAll();
        assertThat(solutionRatingList).hasSize(databaseSizeBeforeUpdate);
        SolutionRating testSolutionRating = solutionRatingList.get(solutionRatingList.size() - 1);
        assertThat(testSolutionRating.getUserLogin()).isEqualTo(UPDATED_USER_LOGIN);
        assertThat(testSolutionRating.getSolutionUuid()).isEqualTo(UPDATED_SOLUTION_UUID);
        assertThat(testSolutionRating.getRatingScore()).isEqualTo(UPDATED_RATING_SCORE);
        assertThat(testSolutionRating.getRatingText()).isEqualTo(UPDATED_RATING_TEXT);
        assertThat(testSolutionRating.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testSolutionRating.getModifiedDate()).isEqualTo(UPDATED_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingSolutionRating() throws Exception {
        int databaseSizeBeforeUpdate = solutionRatingRepository.findAll().size();

        // Create the SolutionRating

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restSolutionRatingMockMvc.perform(put("/api/solution-ratings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solutionRating)))
            .andExpect(status().isCreated());

        // Validate the SolutionRating in the database
        List<SolutionRating> solutionRatingList = solutionRatingRepository.findAll();
        assertThat(solutionRatingList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteSolutionRating() throws Exception {
        // Initialize the database
        solutionRatingRepository.saveAndFlush(solutionRating);
        int databaseSizeBeforeDelete = solutionRatingRepository.findAll().size();

        // Get the solutionRating
        restSolutionRatingMockMvc.perform(delete("/api/solution-ratings/{id}", solutionRating.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<SolutionRating> solutionRatingList = solutionRatingRepository.findAll();
        assertThat(solutionRatingList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SolutionRating.class);
        SolutionRating solutionRating1 = new SolutionRating();
        solutionRating1.setId(1L);
        SolutionRating solutionRating2 = new SolutionRating();
        solutionRating2.setId(solutionRating1.getId());
        assertThat(solutionRating1).isEqualTo(solutionRating2);
        solutionRating2.setId(2L);
        assertThat(solutionRating1).isNotEqualTo(solutionRating2);
        solutionRating1.setId(null);
        assertThat(solutionRating1).isNotEqualTo(solutionRating2);
    }
}
