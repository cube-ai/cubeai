package com.wyy.web.rest;

import com.wyy.UmmApp;

import com.wyy.config.SecurityBeanOverrideConfiguration;

import com.wyy.domain.CompositeSolutionMap;
import com.wyy.repository.CompositeSolutionMapRepository;
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
import java.util.List;

import static com.wyy.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CompositeSolutionMapResource REST controller.
 *
 * @see CompositeSolutionMapResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UmmApp.class, SecurityBeanOverrideConfiguration.class})
public class CompositeSolutionMapResourceIntTest {

    private static final String DEFAULT_PARENT_UUID = "AAAAAAAAAA";
    private static final String UPDATED_PARENT_UUID = "BBBBBBBBBB";

    private static final String DEFAULT_CHILD_UUID = "AAAAAAAAAA";
    private static final String UPDATED_CHILD_UUID = "BBBBBBBBBB";

    @Autowired
    private CompositeSolutionMapRepository compositeSolutionMapRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restCompositeSolutionMapMockMvc;

    private CompositeSolutionMap compositeSolutionMap;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CompositeSolutionMapResource compositeSolutionMapResource = new CompositeSolutionMapResource(compositeSolutionMapRepository);
        this.restCompositeSolutionMapMockMvc = MockMvcBuilders.standaloneSetup(compositeSolutionMapResource)
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
    public static CompositeSolutionMap createEntity(EntityManager em) {
        CompositeSolutionMap compositeSolutionMap = new CompositeSolutionMap()
            .parentUuid(DEFAULT_PARENT_UUID)
            .childUuid(DEFAULT_CHILD_UUID);
        return compositeSolutionMap;
    }

    @Before
    public void initTest() {
        compositeSolutionMap = createEntity(em);
    }

    @Test
    @Transactional
    public void createCompositeSolutionMap() throws Exception {
        int databaseSizeBeforeCreate = compositeSolutionMapRepository.findAll().size();

        // Create the CompositeSolutionMap
        restCompositeSolutionMapMockMvc.perform(post("/api/composite-solution-maps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(compositeSolutionMap)))
            .andExpect(status().isCreated());

        // Validate the CompositeSolutionMap in the database
        List<CompositeSolutionMap> compositeSolutionMapList = compositeSolutionMapRepository.findAll();
        assertThat(compositeSolutionMapList).hasSize(databaseSizeBeforeCreate + 1);
        CompositeSolutionMap testCompositeSolutionMap = compositeSolutionMapList.get(compositeSolutionMapList.size() - 1);
        assertThat(testCompositeSolutionMap.getParentUuid()).isEqualTo(DEFAULT_PARENT_UUID);
        assertThat(testCompositeSolutionMap.getChildUuid()).isEqualTo(DEFAULT_CHILD_UUID);
    }

    @Test
    @Transactional
    public void createCompositeSolutionMapWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = compositeSolutionMapRepository.findAll().size();

        // Create the CompositeSolutionMap with an existing ID
        compositeSolutionMap.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCompositeSolutionMapMockMvc.perform(post("/api/composite-solution-maps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(compositeSolutionMap)))
            .andExpect(status().isBadRequest());

        // Validate the CompositeSolutionMap in the database
        List<CompositeSolutionMap> compositeSolutionMapList = compositeSolutionMapRepository.findAll();
        assertThat(compositeSolutionMapList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkParentUuidIsRequired() throws Exception {
        int databaseSizeBeforeTest = compositeSolutionMapRepository.findAll().size();
        // set the field null
        compositeSolutionMap.setParentUuid(null);

        // Create the CompositeSolutionMap, which fails.

        restCompositeSolutionMapMockMvc.perform(post("/api/composite-solution-maps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(compositeSolutionMap)))
            .andExpect(status().isBadRequest());

        List<CompositeSolutionMap> compositeSolutionMapList = compositeSolutionMapRepository.findAll();
        assertThat(compositeSolutionMapList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkChildUuidIsRequired() throws Exception {
        int databaseSizeBeforeTest = compositeSolutionMapRepository.findAll().size();
        // set the field null
        compositeSolutionMap.setChildUuid(null);

        // Create the CompositeSolutionMap, which fails.

        restCompositeSolutionMapMockMvc.perform(post("/api/composite-solution-maps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(compositeSolutionMap)))
            .andExpect(status().isBadRequest());

        List<CompositeSolutionMap> compositeSolutionMapList = compositeSolutionMapRepository.findAll();
        assertThat(compositeSolutionMapList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCompositeSolutionMaps() throws Exception {
        // Initialize the database
        compositeSolutionMapRepository.saveAndFlush(compositeSolutionMap);

        // Get all the compositeSolutionMapList
        restCompositeSolutionMapMockMvc.perform(get("/api/composite-solution-maps?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(compositeSolutionMap.getId().intValue())))
            .andExpect(jsonPath("$.[*].parentUuid").value(hasItem(DEFAULT_PARENT_UUID.toString())))
            .andExpect(jsonPath("$.[*].childUuid").value(hasItem(DEFAULT_CHILD_UUID.toString())));
    }

    @Test
    @Transactional
    public void getCompositeSolutionMap() throws Exception {
        // Initialize the database
        compositeSolutionMapRepository.saveAndFlush(compositeSolutionMap);

        // Get the compositeSolutionMap
        restCompositeSolutionMapMockMvc.perform(get("/api/composite-solution-maps/{id}", compositeSolutionMap.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(compositeSolutionMap.getId().intValue()))
            .andExpect(jsonPath("$.parentUuid").value(DEFAULT_PARENT_UUID.toString()))
            .andExpect(jsonPath("$.childUuid").value(DEFAULT_CHILD_UUID.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCompositeSolutionMap() throws Exception {
        // Get the compositeSolutionMap
        restCompositeSolutionMapMockMvc.perform(get("/api/composite-solution-maps/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCompositeSolutionMap() throws Exception {
        // Initialize the database
        compositeSolutionMapRepository.saveAndFlush(compositeSolutionMap);
        int databaseSizeBeforeUpdate = compositeSolutionMapRepository.findAll().size();

        // Update the compositeSolutionMap
        CompositeSolutionMap updatedCompositeSolutionMap = compositeSolutionMapRepository.findOne(compositeSolutionMap.getId());
        // Disconnect from session so that the updates on updatedCompositeSolutionMap are not directly saved in db
        em.detach(updatedCompositeSolutionMap);
        updatedCompositeSolutionMap
            .parentUuid(UPDATED_PARENT_UUID)
            .childUuid(UPDATED_CHILD_UUID);

        restCompositeSolutionMapMockMvc.perform(put("/api/composite-solution-maps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCompositeSolutionMap)))
            .andExpect(status().isOk());

        // Validate the CompositeSolutionMap in the database
        List<CompositeSolutionMap> compositeSolutionMapList = compositeSolutionMapRepository.findAll();
        assertThat(compositeSolutionMapList).hasSize(databaseSizeBeforeUpdate);
        CompositeSolutionMap testCompositeSolutionMap = compositeSolutionMapList.get(compositeSolutionMapList.size() - 1);
        assertThat(testCompositeSolutionMap.getParentUuid()).isEqualTo(UPDATED_PARENT_UUID);
        assertThat(testCompositeSolutionMap.getChildUuid()).isEqualTo(UPDATED_CHILD_UUID);
    }

    @Test
    @Transactional
    public void updateNonExistingCompositeSolutionMap() throws Exception {
        int databaseSizeBeforeUpdate = compositeSolutionMapRepository.findAll().size();

        // Create the CompositeSolutionMap

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restCompositeSolutionMapMockMvc.perform(put("/api/composite-solution-maps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(compositeSolutionMap)))
            .andExpect(status().isCreated());

        // Validate the CompositeSolutionMap in the database
        List<CompositeSolutionMap> compositeSolutionMapList = compositeSolutionMapRepository.findAll();
        assertThat(compositeSolutionMapList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteCompositeSolutionMap() throws Exception {
        // Initialize the database
        compositeSolutionMapRepository.saveAndFlush(compositeSolutionMap);
        int databaseSizeBeforeDelete = compositeSolutionMapRepository.findAll().size();

        // Get the compositeSolutionMap
        restCompositeSolutionMapMockMvc.perform(delete("/api/composite-solution-maps/{id}", compositeSolutionMap.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<CompositeSolutionMap> compositeSolutionMapList = compositeSolutionMapRepository.findAll();
        assertThat(compositeSolutionMapList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CompositeSolutionMap.class);
        CompositeSolutionMap compositeSolutionMap1 = new CompositeSolutionMap();
        compositeSolutionMap1.setId(1L);
        CompositeSolutionMap compositeSolutionMap2 = new CompositeSolutionMap();
        compositeSolutionMap2.setId(compositeSolutionMap1.getId());
        assertThat(compositeSolutionMap1).isEqualTo(compositeSolutionMap2);
        compositeSolutionMap2.setId(2L);
        assertThat(compositeSolutionMap1).isNotEqualTo(compositeSolutionMap2);
        compositeSolutionMap1.setId(null);
        assertThat(compositeSolutionMap1).isNotEqualTo(compositeSolutionMap2);
    }
}
