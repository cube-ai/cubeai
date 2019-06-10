package com.wyy.web.rest;

import com.wyy.UmmApp;

import com.wyy.config.SecurityBeanOverrideConfiguration;

import com.wyy.domain.Description;
import com.wyy.repository.DescriptionRepository;
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
import org.springframework.util.Base64Utils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.wyy.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the DescriptionResource REST controller.
 *
 * @see DescriptionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UmmApp.class, SecurityBeanOverrideConfiguration.class})
public class DescriptionResourceIntTest {

    private static final String DEFAULT_SOLUTION_UUID = "AAAAAAAAAA";
    private static final String UPDATED_SOLUTION_UUID = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHOR_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    @Autowired
    private DescriptionRepository descriptionRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restDescriptionMockMvc;

    private Description description;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DescriptionResource descriptionResource = new DescriptionResource(descriptionRepository);
        this.restDescriptionMockMvc = MockMvcBuilders.standaloneSetup(descriptionResource)
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
    public static Description createEntity(EntityManager em) {
        Description description = new Description()
            .solutionUuid(DEFAULT_SOLUTION_UUID)
            .authorLogin(DEFAULT_AUTHOR_LOGIN)
            .content(DEFAULT_CONTENT);
        return description;
    }

    @Before
    public void initTest() {
        description = createEntity(em);
    }

    @Test
    @Transactional
    public void createDescription() throws Exception {
        int databaseSizeBeforeCreate = descriptionRepository.findAll().size();

        // Create the Description
        restDescriptionMockMvc.perform(post("/api/descriptions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(description)))
            .andExpect(status().isCreated());

        // Validate the Description in the database
        List<Description> descriptionList = descriptionRepository.findAll();
        assertThat(descriptionList).hasSize(databaseSizeBeforeCreate + 1);
        Description testDescription = descriptionList.get(descriptionList.size() - 1);
        assertThat(testDescription.getSolutionUuid()).isEqualTo(DEFAULT_SOLUTION_UUID);
        assertThat(testDescription.getAuthorLogin()).isEqualTo(DEFAULT_AUTHOR_LOGIN);
        assertThat(testDescription.getContent()).isEqualTo(DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    public void createDescriptionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = descriptionRepository.findAll().size();

        // Create the Description with an existing ID
        description.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDescriptionMockMvc.perform(post("/api/descriptions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(description)))
            .andExpect(status().isBadRequest());

        // Validate the Description in the database
        List<Description> descriptionList = descriptionRepository.findAll();
        assertThat(descriptionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllDescriptions() throws Exception {
        // Initialize the database
        descriptionRepository.saveAndFlush(description);

        // Get all the descriptionList
        restDescriptionMockMvc.perform(get("/api/descriptions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(description.getId().intValue())))
            .andExpect(jsonPath("$.[*].solutionUuid").value(hasItem(DEFAULT_SOLUTION_UUID.toString())))
            .andExpect(jsonPath("$.[*].authorLogin").value(hasItem(DEFAULT_AUTHOR_LOGIN.toString())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())));
    }

    @Test
    @Transactional
    public void getDescription() throws Exception {
        // Initialize the database
        descriptionRepository.saveAndFlush(description);

        // Get the description
        restDescriptionMockMvc.perform(get("/api/descriptions/{id}", description.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(description.getId().intValue()))
            .andExpect(jsonPath("$.solutionUuid").value(DEFAULT_SOLUTION_UUID.toString()))
            .andExpect(jsonPath("$.authorLogin").value(DEFAULT_AUTHOR_LOGIN.toString()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDescription() throws Exception {
        // Get the description
        restDescriptionMockMvc.perform(get("/api/descriptions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDescription() throws Exception {
        // Initialize the database
        descriptionRepository.saveAndFlush(description);
        int databaseSizeBeforeUpdate = descriptionRepository.findAll().size();

        // Update the description
        Description updatedDescription = descriptionRepository.findOne(description.getId());
        // Disconnect from session so that the updates on updatedDescription are not directly saved in db
        em.detach(updatedDescription);
        updatedDescription
            .solutionUuid(UPDATED_SOLUTION_UUID)
            .authorLogin(UPDATED_AUTHOR_LOGIN)
            .content(UPDATED_CONTENT);

        restDescriptionMockMvc.perform(put("/api/descriptions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedDescription)))
            .andExpect(status().isOk());

        // Validate the Description in the database
        List<Description> descriptionList = descriptionRepository.findAll();
        assertThat(descriptionList).hasSize(databaseSizeBeforeUpdate);
        Description testDescription = descriptionList.get(descriptionList.size() - 1);
        assertThat(testDescription.getSolutionUuid()).isEqualTo(UPDATED_SOLUTION_UUID);
        assertThat(testDescription.getAuthorLogin()).isEqualTo(UPDATED_AUTHOR_LOGIN);
        assertThat(testDescription.getContent()).isEqualTo(UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void updateNonExistingDescription() throws Exception {
        int databaseSizeBeforeUpdate = descriptionRepository.findAll().size();

        // Create the Description

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restDescriptionMockMvc.perform(put("/api/descriptions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(description)))
            .andExpect(status().isCreated());

        // Validate the Description in the database
        List<Description> descriptionList = descriptionRepository.findAll();
        assertThat(descriptionList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteDescription() throws Exception {
        // Initialize the database
        descriptionRepository.saveAndFlush(description);
        int databaseSizeBeforeDelete = descriptionRepository.findAll().size();

        // Get the description
        restDescriptionMockMvc.perform(delete("/api/descriptions/{id}", description.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Description> descriptionList = descriptionRepository.findAll();
        assertThat(descriptionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Description.class);
        Description description1 = new Description();
        description1.setId(1L);
        Description description2 = new Description();
        description2.setId(description1.getId());
        assertThat(description1).isEqualTo(description2);
        description2.setId(2L);
        assertThat(description1).isNotEqualTo(description2);
        description1.setId(null);
        assertThat(description1).isNotEqualTo(description2);
    }
}
