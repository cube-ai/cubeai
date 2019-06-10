package com.wyy.web.rest;

import com.wyy.UmmApp;

import com.wyy.config.SecurityBeanOverrideConfiguration;

import com.wyy.domain.Artifact;
import com.wyy.repository.ArtifactRepository;
import com.wyy.repository.SolutionRepository;
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
 * Test class for the ArtifactResource REST controller.
 *
 * @see ArtifactResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UmmApp.class, SecurityBeanOverrideConfiguration.class})
public class ArtifactResourceIntTest {

    private static final String DEFAULT_SOLUTION_UUID = "AAAAAAAAAA";
    private static final String UPDATED_SOLUTION_UUID = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final Long DEFAULT_FILE_SIZE = 1L;
    private static final Long UPDATED_FILE_SIZE = 2L;

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private SolutionRepository solutionRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restArtifactMockMvc;

    private Artifact artifact;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ArtifactResource artifactResource = new ArtifactResource(artifactRepository, solutionRepository);
        this.restArtifactMockMvc = MockMvcBuilders.standaloneSetup(artifactResource)
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
    public static Artifact createEntity(EntityManager em) {
        Artifact artifact = new Artifact()
            .solutionUuid(DEFAULT_SOLUTION_UUID)
            .name(DEFAULT_NAME)
            .type(DEFAULT_TYPE)
            .url(DEFAULT_URL)
            .fileSize(DEFAULT_FILE_SIZE)
            .createdDate(DEFAULT_CREATED_DATE)
            .modifiedDate(DEFAULT_MODIFIED_DATE);
        return artifact;
    }

    @Before
    public void initTest() {
        artifact = createEntity(em);
    }

    @Test
    @Transactional
    public void createArtifact() throws Exception {
        int databaseSizeBeforeCreate = artifactRepository.findAll().size();

        // Create the Artifact
        restArtifactMockMvc.perform(post("/api/artifacts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(artifact)))
            .andExpect(status().isCreated());

        // Validate the Artifact in the database
        List<Artifact> artifactList = artifactRepository.findAll();
        assertThat(artifactList).hasSize(databaseSizeBeforeCreate + 1);
        Artifact testArtifact = artifactList.get(artifactList.size() - 1);
        assertThat(testArtifact.getSolutionUuid()).isEqualTo(DEFAULT_SOLUTION_UUID);
        assertThat(testArtifact.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testArtifact.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testArtifact.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testArtifact.getFileSize()).isEqualTo(DEFAULT_FILE_SIZE);
        assertThat(testArtifact.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testArtifact.getModifiedDate()).isEqualTo(DEFAULT_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void createArtifactWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = artifactRepository.findAll().size();

        // Create the Artifact with an existing ID
        artifact.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restArtifactMockMvc.perform(post("/api/artifacts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(artifact)))
            .andExpect(status().isBadRequest());

        // Validate the Artifact in the database
        List<Artifact> artifactList = artifactRepository.findAll();
        assertThat(artifactList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllArtifacts() throws Exception {
        // Initialize the database
        artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList
        restArtifactMockMvc.perform(get("/api/artifacts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(artifact.getId().intValue())))
            .andExpect(jsonPath("$.[*].solutionUuid").value(hasItem(DEFAULT_SOLUTION_UUID.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].fileSize").value(hasItem(DEFAULT_FILE_SIZE.intValue())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].modifiedDate").value(hasItem(DEFAULT_MODIFIED_DATE.toString())));
    }

    @Test
    @Transactional
    public void getArtifact() throws Exception {
        // Initialize the database
        artifactRepository.saveAndFlush(artifact);

        // Get the artifact
        restArtifactMockMvc.perform(get("/api/artifacts/{id}", artifact.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(artifact.getId().intValue()))
            .andExpect(jsonPath("$.solutionUuid").value(DEFAULT_SOLUTION_UUID.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()))
            .andExpect(jsonPath("$.fileSize").value(DEFAULT_FILE_SIZE.intValue()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.modifiedDate").value(DEFAULT_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingArtifact() throws Exception {
        // Get the artifact
        restArtifactMockMvc.perform(get("/api/artifacts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateArtifact() throws Exception {
        // Initialize the database
        artifactRepository.saveAndFlush(artifact);
        int databaseSizeBeforeUpdate = artifactRepository.findAll().size();

        // Update the artifact
        Artifact updatedArtifact = artifactRepository.findOne(artifact.getId());
        // Disconnect from session so that the updates on updatedArtifact are not directly saved in db
        em.detach(updatedArtifact);
        updatedArtifact
            .solutionUuid(UPDATED_SOLUTION_UUID)
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .url(UPDATED_URL)
            .fileSize(UPDATED_FILE_SIZE)
            .createdDate(UPDATED_CREATED_DATE)
            .modifiedDate(UPDATED_MODIFIED_DATE);

        restArtifactMockMvc.perform(put("/api/artifacts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedArtifact)))
            .andExpect(status().isOk());

        // Validate the Artifact in the database
        List<Artifact> artifactList = artifactRepository.findAll();
        assertThat(artifactList).hasSize(databaseSizeBeforeUpdate);
        Artifact testArtifact = artifactList.get(artifactList.size() - 1);
        assertThat(testArtifact.getSolutionUuid()).isEqualTo(UPDATED_SOLUTION_UUID);
        assertThat(testArtifact.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testArtifact.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testArtifact.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testArtifact.getFileSize()).isEqualTo(UPDATED_FILE_SIZE);
        assertThat(testArtifact.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testArtifact.getModifiedDate()).isEqualTo(UPDATED_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingArtifact() throws Exception {
        int databaseSizeBeforeUpdate = artifactRepository.findAll().size();

        // Create the Artifact

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restArtifactMockMvc.perform(put("/api/artifacts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(artifact)))
            .andExpect(status().isCreated());

        // Validate the Artifact in the database
        List<Artifact> artifactList = artifactRepository.findAll();
        assertThat(artifactList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteArtifact() throws Exception {
        // Initialize the database
        artifactRepository.saveAndFlush(artifact);
        int databaseSizeBeforeDelete = artifactRepository.findAll().size();

        // Get the artifact
        restArtifactMockMvc.perform(delete("/api/artifacts/{id}", artifact.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Artifact> artifactList = artifactRepository.findAll();
        assertThat(artifactList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Artifact.class);
        Artifact artifact1 = new Artifact();
        artifact1.setId(1L);
        Artifact artifact2 = new Artifact();
        artifact2.setId(artifact1.getId());
        assertThat(artifact1).isEqualTo(artifact2);
        artifact2.setId(2L);
        assertThat(artifact1).isNotEqualTo(artifact2);
        artifact1.setId(null);
        assertThat(artifact1).isNotEqualTo(artifact2);
    }
}
