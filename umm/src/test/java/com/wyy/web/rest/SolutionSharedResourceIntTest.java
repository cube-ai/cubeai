package com.wyy.web.rest;

import com.wyy.UmmApp;

import com.wyy.config.SecurityBeanOverrideConfiguration;

import com.wyy.domain.SolutionShared;
import com.wyy.repository.SolutionSharedRepository;
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
 * Test class for the SolutionSharedResource REST controller.
 *
 * @see SolutionSharedResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UmmApp.class, SecurityBeanOverrideConfiguration.class})
public class SolutionSharedResourceIntTest {

    private static final String DEFAULT_FROM_USER_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_FROM_USER_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_TO_USER_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_TO_USER_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_SOLUTION_UUID = "AAAAAAAAAA";
    private static final String UPDATED_SOLUTION_UUID = "BBBBBBBBBB";

    private static final String DEFAULT_SOLUTION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SOLUTION_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SOLUTION_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_SOLUTION_AUTHOR = "BBBBBBBBBB";

    private static final Instant DEFAULT_SOLUTION_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SOLUTION_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_SHARE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SHARE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private SolutionSharedRepository solutionSharedRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSolutionSharedMockMvc;

    private SolutionShared solutionShared;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SolutionSharedResource solutionSharedResource = new SolutionSharedResource(solutionSharedRepository);
        this.restSolutionSharedMockMvc = MockMvcBuilders.standaloneSetup(solutionSharedResource)
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
    public static SolutionShared createEntity(EntityManager em) {
        SolutionShared solutionShared = new SolutionShared()
            .fromUserLogin(DEFAULT_FROM_USER_LOGIN)
            .toUserLogin(DEFAULT_TO_USER_LOGIN)
            .solutionUuid(DEFAULT_SOLUTION_UUID)
            .solutionName(DEFAULT_SOLUTION_NAME)
            .solutionAuthor(DEFAULT_SOLUTION_AUTHOR)
            .solutionCreatedDate(DEFAULT_SOLUTION_CREATED_DATE)
            .shareDate(DEFAULT_SHARE_DATE);
        return solutionShared;
    }

    @Before
    public void initTest() {
        solutionShared = createEntity(em);
    }

    @Test
    @Transactional
    public void createSolutionShared() throws Exception {
        int databaseSizeBeforeCreate = solutionSharedRepository.findAll().size();

        // Create the SolutionShared
        restSolutionSharedMockMvc.perform(post("/api/solution-shareds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solutionShared)))
            .andExpect(status().isCreated());

        // Validate the SolutionShared in the database
        List<SolutionShared> solutionSharedList = solutionSharedRepository.findAll();
        assertThat(solutionSharedList).hasSize(databaseSizeBeforeCreate + 1);
        SolutionShared testSolutionShared = solutionSharedList.get(solutionSharedList.size() - 1);
        assertThat(testSolutionShared.getFromUserLogin()).isEqualTo(DEFAULT_FROM_USER_LOGIN);
        assertThat(testSolutionShared.getToUserLogin()).isEqualTo(DEFAULT_TO_USER_LOGIN);
        assertThat(testSolutionShared.getSolutionUuid()).isEqualTo(DEFAULT_SOLUTION_UUID);
        assertThat(testSolutionShared.getSolutionName()).isEqualTo(DEFAULT_SOLUTION_NAME);
        assertThat(testSolutionShared.getSolutionAuthor()).isEqualTo(DEFAULT_SOLUTION_AUTHOR);
        assertThat(testSolutionShared.getSolutionCreatedDate()).isEqualTo(DEFAULT_SOLUTION_CREATED_DATE);
        assertThat(testSolutionShared.getShareDate()).isEqualTo(DEFAULT_SHARE_DATE);
    }

    @Test
    @Transactional
    public void createSolutionSharedWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = solutionSharedRepository.findAll().size();

        // Create the SolutionShared with an existing ID
        solutionShared.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSolutionSharedMockMvc.perform(post("/api/solution-shareds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solutionShared)))
            .andExpect(status().isBadRequest());

        // Validate the SolutionShared in the database
        List<SolutionShared> solutionSharedList = solutionSharedRepository.findAll();
        assertThat(solutionSharedList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkFromUserLoginIsRequired() throws Exception {
        int databaseSizeBeforeTest = solutionSharedRepository.findAll().size();
        // set the field null
        solutionShared.setFromUserLogin(null);

        // Create the SolutionShared, which fails.

        restSolutionSharedMockMvc.perform(post("/api/solution-shareds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solutionShared)))
            .andExpect(status().isBadRequest());

        List<SolutionShared> solutionSharedList = solutionSharedRepository.findAll();
        assertThat(solutionSharedList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkToUserLoginIsRequired() throws Exception {
        int databaseSizeBeforeTest = solutionSharedRepository.findAll().size();
        // set the field null
        solutionShared.setToUserLogin(null);

        // Create the SolutionShared, which fails.

        restSolutionSharedMockMvc.perform(post("/api/solution-shareds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solutionShared)))
            .andExpect(status().isBadRequest());

        List<SolutionShared> solutionSharedList = solutionSharedRepository.findAll();
        assertThat(solutionSharedList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSolutionUuidIsRequired() throws Exception {
        int databaseSizeBeforeTest = solutionSharedRepository.findAll().size();
        // set the field null
        solutionShared.setSolutionUuid(null);

        // Create the SolutionShared, which fails.

        restSolutionSharedMockMvc.perform(post("/api/solution-shareds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solutionShared)))
            .andExpect(status().isBadRequest());

        List<SolutionShared> solutionSharedList = solutionSharedRepository.findAll();
        assertThat(solutionSharedList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSolutionShareds() throws Exception {
        // Initialize the database
        solutionSharedRepository.saveAndFlush(solutionShared);

        // Get all the solutionSharedList
        restSolutionSharedMockMvc.perform(get("/api/solution-shareds?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(solutionShared.getId().intValue())))
            .andExpect(jsonPath("$.[*].fromUserLogin").value(hasItem(DEFAULT_FROM_USER_LOGIN.toString())))
            .andExpect(jsonPath("$.[*].toUserLogin").value(hasItem(DEFAULT_TO_USER_LOGIN.toString())))
            .andExpect(jsonPath("$.[*].solutionUuid").value(hasItem(DEFAULT_SOLUTION_UUID.toString())))
            .andExpect(jsonPath("$.[*].solutionName").value(hasItem(DEFAULT_SOLUTION_NAME.toString())))
            .andExpect(jsonPath("$.[*].solutionAuthor").value(hasItem(DEFAULT_SOLUTION_AUTHOR.toString())))
            .andExpect(jsonPath("$.[*].solutionCreatedDate").value(hasItem(DEFAULT_SOLUTION_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].shareDate").value(hasItem(DEFAULT_SHARE_DATE.toString())));
    }

    @Test
    @Transactional
    public void getSolutionShared() throws Exception {
        // Initialize the database
        solutionSharedRepository.saveAndFlush(solutionShared);

        // Get the solutionShared
        restSolutionSharedMockMvc.perform(get("/api/solution-shareds/{id}", solutionShared.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(solutionShared.getId().intValue()))
            .andExpect(jsonPath("$.fromUserLogin").value(DEFAULT_FROM_USER_LOGIN.toString()))
            .andExpect(jsonPath("$.toUserLogin").value(DEFAULT_TO_USER_LOGIN.toString()))
            .andExpect(jsonPath("$.solutionUuid").value(DEFAULT_SOLUTION_UUID.toString()))
            .andExpect(jsonPath("$.solutionName").value(DEFAULT_SOLUTION_NAME.toString()))
            .andExpect(jsonPath("$.solutionAuthor").value(DEFAULT_SOLUTION_AUTHOR.toString()))
            .andExpect(jsonPath("$.solutionCreatedDate").value(DEFAULT_SOLUTION_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.shareDate").value(DEFAULT_SHARE_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSolutionShared() throws Exception {
        // Get the solutionShared
        restSolutionSharedMockMvc.perform(get("/api/solution-shareds/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSolutionShared() throws Exception {
        // Initialize the database
        solutionSharedRepository.saveAndFlush(solutionShared);
        int databaseSizeBeforeUpdate = solutionSharedRepository.findAll().size();

        // Update the solutionShared
        SolutionShared updatedSolutionShared = solutionSharedRepository.findOne(solutionShared.getId());
        // Disconnect from session so that the updates on updatedSolutionShared are not directly saved in db
        em.detach(updatedSolutionShared);
        updatedSolutionShared
            .fromUserLogin(UPDATED_FROM_USER_LOGIN)
            .toUserLogin(UPDATED_TO_USER_LOGIN)
            .solutionUuid(UPDATED_SOLUTION_UUID)
            .solutionName(UPDATED_SOLUTION_NAME)
            .solutionAuthor(UPDATED_SOLUTION_AUTHOR)
            .solutionCreatedDate(UPDATED_SOLUTION_CREATED_DATE)
            .shareDate(UPDATED_SHARE_DATE);

        restSolutionSharedMockMvc.perform(put("/api/solution-shareds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedSolutionShared)))
            .andExpect(status().isOk());

        // Validate the SolutionShared in the database
        List<SolutionShared> solutionSharedList = solutionSharedRepository.findAll();
        assertThat(solutionSharedList).hasSize(databaseSizeBeforeUpdate);
        SolutionShared testSolutionShared = solutionSharedList.get(solutionSharedList.size() - 1);
        assertThat(testSolutionShared.getFromUserLogin()).isEqualTo(UPDATED_FROM_USER_LOGIN);
        assertThat(testSolutionShared.getToUserLogin()).isEqualTo(UPDATED_TO_USER_LOGIN);
        assertThat(testSolutionShared.getSolutionUuid()).isEqualTo(UPDATED_SOLUTION_UUID);
        assertThat(testSolutionShared.getSolutionName()).isEqualTo(UPDATED_SOLUTION_NAME);
        assertThat(testSolutionShared.getSolutionAuthor()).isEqualTo(UPDATED_SOLUTION_AUTHOR);
        assertThat(testSolutionShared.getSolutionCreatedDate()).isEqualTo(UPDATED_SOLUTION_CREATED_DATE);
        assertThat(testSolutionShared.getShareDate()).isEqualTo(UPDATED_SHARE_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingSolutionShared() throws Exception {
        int databaseSizeBeforeUpdate = solutionSharedRepository.findAll().size();

        // Create the SolutionShared

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restSolutionSharedMockMvc.perform(put("/api/solution-shareds")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solutionShared)))
            .andExpect(status().isCreated());

        // Validate the SolutionShared in the database
        List<SolutionShared> solutionSharedList = solutionSharedRepository.findAll();
        assertThat(solutionSharedList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteSolutionShared() throws Exception {
        // Initialize the database
        solutionSharedRepository.saveAndFlush(solutionShared);
        int databaseSizeBeforeDelete = solutionSharedRepository.findAll().size();

        // Get the solutionShared
        restSolutionSharedMockMvc.perform(delete("/api/solution-shareds/{id}", solutionShared.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<SolutionShared> solutionSharedList = solutionSharedRepository.findAll();
        assertThat(solutionSharedList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SolutionShared.class);
        SolutionShared solutionShared1 = new SolutionShared();
        solutionShared1.setId(1L);
        SolutionShared solutionShared2 = new SolutionShared();
        solutionShared2.setId(solutionShared1.getId());
        assertThat(solutionShared1).isEqualTo(solutionShared2);
        solutionShared2.setId(2L);
        assertThat(solutionShared1).isNotEqualTo(solutionShared2);
        solutionShared1.setId(null);
        assertThat(solutionShared1).isNotEqualTo(solutionShared2);
    }
}
