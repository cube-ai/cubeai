package com.wyy.web.rest;

import com.wyy.UmmApp;

import com.wyy.config.SecurityBeanOverrideConfiguration;

import com.wyy.domain.SolutionFavorite;
import com.wyy.repository.SolutionFavoriteRepository;
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
 * Test class for the SolutionFavoriteResource REST controller.
 *
 * @see SolutionFavoriteResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UmmApp.class, SecurityBeanOverrideConfiguration.class})
public class SolutionFavoriteResourceIntTest {

    private static final String DEFAULT_USER_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_USER_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_SOLUTION_UUID = "AAAAAAAAAA";
    private static final String UPDATED_SOLUTION_UUID = "BBBBBBBBBB";

    private static final String DEFAULT_SOLUTION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SOLUTION_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SOLUTION_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_SOLUTION_AUTHOR = "BBBBBBBBBB";

    private static final Instant DEFAULT_SOLUTION_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SOLUTION_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FAVORITE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FAVORITE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private SolutionFavoriteRepository solutionFavoriteRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSolutionFavoriteMockMvc;

    private SolutionFavorite solutionFavorite;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SolutionFavoriteResource solutionFavoriteResource = new SolutionFavoriteResource(solutionFavoriteRepository);
        this.restSolutionFavoriteMockMvc = MockMvcBuilders.standaloneSetup(solutionFavoriteResource)
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
    public static SolutionFavorite createEntity(EntityManager em) {
        SolutionFavorite solutionFavorite = new SolutionFavorite()
            .userLogin(DEFAULT_USER_LOGIN)
            .solutionUuid(DEFAULT_SOLUTION_UUID)
            .solutionName(DEFAULT_SOLUTION_NAME)
            .solutionAuthor(DEFAULT_SOLUTION_AUTHOR)
            .solutionCreatedDate(DEFAULT_SOLUTION_CREATED_DATE)
            .favoriteDate(DEFAULT_FAVORITE_DATE);
        return solutionFavorite;
    }

    @Before
    public void initTest() {
        solutionFavorite = createEntity(em);
    }

    @Test
    @Transactional
    public void createSolutionFavorite() throws Exception {
        int databaseSizeBeforeCreate = solutionFavoriteRepository.findAll().size();

        // Create the SolutionFavorite
        restSolutionFavoriteMockMvc.perform(post("/api/solution-favorites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solutionFavorite)))
            .andExpect(status().isCreated());

        // Validate the SolutionFavorite in the database
        List<SolutionFavorite> solutionFavoriteList = solutionFavoriteRepository.findAll();
        assertThat(solutionFavoriteList).hasSize(databaseSizeBeforeCreate + 1);
        SolutionFavorite testSolutionFavorite = solutionFavoriteList.get(solutionFavoriteList.size() - 1);
        assertThat(testSolutionFavorite.getUserLogin()).isEqualTo(DEFAULT_USER_LOGIN);
        assertThat(testSolutionFavorite.getSolutionUuid()).isEqualTo(DEFAULT_SOLUTION_UUID);
        assertThat(testSolutionFavorite.getSolutionName()).isEqualTo(DEFAULT_SOLUTION_NAME);
        assertThat(testSolutionFavorite.getSolutionAuthor()).isEqualTo(DEFAULT_SOLUTION_AUTHOR);
        assertThat(testSolutionFavorite.getSolutionCreatedDate()).isEqualTo(DEFAULT_SOLUTION_CREATED_DATE);
        assertThat(testSolutionFavorite.getFavoriteDate()).isEqualTo(DEFAULT_FAVORITE_DATE);
    }

    @Test
    @Transactional
    public void createSolutionFavoriteWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = solutionFavoriteRepository.findAll().size();

        // Create the SolutionFavorite with an existing ID
        solutionFavorite.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSolutionFavoriteMockMvc.perform(post("/api/solution-favorites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solutionFavorite)))
            .andExpect(status().isBadRequest());

        // Validate the SolutionFavorite in the database
        List<SolutionFavorite> solutionFavoriteList = solutionFavoriteRepository.findAll();
        assertThat(solutionFavoriteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkUserLoginIsRequired() throws Exception {
        int databaseSizeBeforeTest = solutionFavoriteRepository.findAll().size();
        // set the field null
        solutionFavorite.setUserLogin(null);

        // Create the SolutionFavorite, which fails.

        restSolutionFavoriteMockMvc.perform(post("/api/solution-favorites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solutionFavorite)))
            .andExpect(status().isBadRequest());

        List<SolutionFavorite> solutionFavoriteList = solutionFavoriteRepository.findAll();
        assertThat(solutionFavoriteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSolutionUuidIsRequired() throws Exception {
        int databaseSizeBeforeTest = solutionFavoriteRepository.findAll().size();
        // set the field null
        solutionFavorite.setSolutionUuid(null);

        // Create the SolutionFavorite, which fails.

        restSolutionFavoriteMockMvc.perform(post("/api/solution-favorites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solutionFavorite)))
            .andExpect(status().isBadRequest());

        List<SolutionFavorite> solutionFavoriteList = solutionFavoriteRepository.findAll();
        assertThat(solutionFavoriteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSolutionFavorites() throws Exception {
        // Initialize the database
        solutionFavoriteRepository.saveAndFlush(solutionFavorite);

        // Get all the solutionFavoriteList
        restSolutionFavoriteMockMvc.perform(get("/api/solution-favorites?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(solutionFavorite.getId().intValue())))
            .andExpect(jsonPath("$.[*].userLogin").value(hasItem(DEFAULT_USER_LOGIN.toString())))
            .andExpect(jsonPath("$.[*].solutionUuid").value(hasItem(DEFAULT_SOLUTION_UUID.toString())))
            .andExpect(jsonPath("$.[*].solutionName").value(hasItem(DEFAULT_SOLUTION_NAME.toString())))
            .andExpect(jsonPath("$.[*].solutionAuthor").value(hasItem(DEFAULT_SOLUTION_AUTHOR.toString())))
            .andExpect(jsonPath("$.[*].solutionCreatedDate").value(hasItem(DEFAULT_SOLUTION_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].favoriteDate").value(hasItem(DEFAULT_FAVORITE_DATE.toString())));
    }

    @Test
    @Transactional
    public void getSolutionFavorite() throws Exception {
        // Initialize the database
        solutionFavoriteRepository.saveAndFlush(solutionFavorite);

        // Get the solutionFavorite
        restSolutionFavoriteMockMvc.perform(get("/api/solution-favorites/{id}", solutionFavorite.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(solutionFavorite.getId().intValue()))
            .andExpect(jsonPath("$.userLogin").value(DEFAULT_USER_LOGIN.toString()))
            .andExpect(jsonPath("$.solutionUuid").value(DEFAULT_SOLUTION_UUID.toString()))
            .andExpect(jsonPath("$.solutionName").value(DEFAULT_SOLUTION_NAME.toString()))
            .andExpect(jsonPath("$.solutionAuthor").value(DEFAULT_SOLUTION_AUTHOR.toString()))
            .andExpect(jsonPath("$.solutionCreatedDate").value(DEFAULT_SOLUTION_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.favoriteDate").value(DEFAULT_FAVORITE_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSolutionFavorite() throws Exception {
        // Get the solutionFavorite
        restSolutionFavoriteMockMvc.perform(get("/api/solution-favorites/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSolutionFavorite() throws Exception {
        // Initialize the database
        solutionFavoriteRepository.saveAndFlush(solutionFavorite);
        int databaseSizeBeforeUpdate = solutionFavoriteRepository.findAll().size();

        // Update the solutionFavorite
        SolutionFavorite updatedSolutionFavorite = solutionFavoriteRepository.findOne(solutionFavorite.getId());
        // Disconnect from session so that the updates on updatedSolutionFavorite are not directly saved in db
        em.detach(updatedSolutionFavorite);
        updatedSolutionFavorite
            .userLogin(UPDATED_USER_LOGIN)
            .solutionUuid(UPDATED_SOLUTION_UUID)
            .solutionName(UPDATED_SOLUTION_NAME)
            .solutionAuthor(UPDATED_SOLUTION_AUTHOR)
            .solutionCreatedDate(UPDATED_SOLUTION_CREATED_DATE)
            .favoriteDate(UPDATED_FAVORITE_DATE);

        restSolutionFavoriteMockMvc.perform(put("/api/solution-favorites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedSolutionFavorite)))
            .andExpect(status().isOk());

        // Validate the SolutionFavorite in the database
        List<SolutionFavorite> solutionFavoriteList = solutionFavoriteRepository.findAll();
        assertThat(solutionFavoriteList).hasSize(databaseSizeBeforeUpdate);
        SolutionFavorite testSolutionFavorite = solutionFavoriteList.get(solutionFavoriteList.size() - 1);
        assertThat(testSolutionFavorite.getUserLogin()).isEqualTo(UPDATED_USER_LOGIN);
        assertThat(testSolutionFavorite.getSolutionUuid()).isEqualTo(UPDATED_SOLUTION_UUID);
        assertThat(testSolutionFavorite.getSolutionName()).isEqualTo(UPDATED_SOLUTION_NAME);
        assertThat(testSolutionFavorite.getSolutionAuthor()).isEqualTo(UPDATED_SOLUTION_AUTHOR);
        assertThat(testSolutionFavorite.getSolutionCreatedDate()).isEqualTo(UPDATED_SOLUTION_CREATED_DATE);
        assertThat(testSolutionFavorite.getFavoriteDate()).isEqualTo(UPDATED_FAVORITE_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingSolutionFavorite() throws Exception {
        int databaseSizeBeforeUpdate = solutionFavoriteRepository.findAll().size();

        // Create the SolutionFavorite

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restSolutionFavoriteMockMvc.perform(put("/api/solution-favorites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solutionFavorite)))
            .andExpect(status().isCreated());

        // Validate the SolutionFavorite in the database
        List<SolutionFavorite> solutionFavoriteList = solutionFavoriteRepository.findAll();
        assertThat(solutionFavoriteList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteSolutionFavorite() throws Exception {
        // Initialize the database
        solutionFavoriteRepository.saveAndFlush(solutionFavorite);
        int databaseSizeBeforeDelete = solutionFavoriteRepository.findAll().size();

        // Get the solutionFavorite
        restSolutionFavoriteMockMvc.perform(delete("/api/solution-favorites/{id}", solutionFavorite.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<SolutionFavorite> solutionFavoriteList = solutionFavoriteRepository.findAll();
        assertThat(solutionFavoriteList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SolutionFavorite.class);
        SolutionFavorite solutionFavorite1 = new SolutionFavorite();
        solutionFavorite1.setId(1L);
        SolutionFavorite solutionFavorite2 = new SolutionFavorite();
        solutionFavorite2.setId(solutionFavorite1.getId());
        assertThat(solutionFavorite1).isEqualTo(solutionFavorite2);
        solutionFavorite2.setId(2L);
        assertThat(solutionFavorite1).isNotEqualTo(solutionFavorite2);
        solutionFavorite1.setId(null);
        assertThat(solutionFavorite1).isNotEqualTo(solutionFavorite2);
    }
}
