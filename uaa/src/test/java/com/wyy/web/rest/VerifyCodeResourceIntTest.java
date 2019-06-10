package com.wyy.web.rest;

import com.wyy.UaaApp;

import com.wyy.config.SecurityBeanOverrideConfiguration;

import com.wyy.domain.VerifyCode;
import com.wyy.repository.VerifyCodeRepository;
import com.wyy.service.VerifyCodeService;
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
 * Test class for the VerifyCodeResource REST controller.
 *
 * @see VerifyCodeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UaaApp.class)
public class VerifyCodeResourceIntTest {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_EXPIRE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private VerifyCodeRepository verifyCodeRepository;

    @Autowired
    private VerifyCodeService verifyCodeService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restVerifyCodeMockMvc;

    private VerifyCode verifyCode;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final VerifyCodeResource verifyCodeResource = new VerifyCodeResource(verifyCodeRepository, verifyCodeService);
        this.restVerifyCodeMockMvc = MockMvcBuilders.standaloneSetup(verifyCodeResource)
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
    public static VerifyCode createEntity(EntityManager em) {
        VerifyCode verifyCode = new VerifyCode()
            .code(DEFAULT_CODE)
            .expire(DEFAULT_EXPIRE);
        return verifyCode;
    }

    @Before
    public void initTest() {
        verifyCode = createEntity(em);
    }

    @Test
    @Transactional
    public void createVerifyCode() throws Exception {
        int databaseSizeBeforeCreate = verifyCodeRepository.findAll().size();

        // Create the VerifyCode
        restVerifyCodeMockMvc.perform(post("/api/verify-codes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(verifyCode)))
            .andExpect(status().isCreated());

        // Validate the VerifyCode in the database
        List<VerifyCode> verifyCodeList = verifyCodeRepository.findAll();
        assertThat(verifyCodeList).hasSize(databaseSizeBeforeCreate + 1);
        VerifyCode testVerifyCode = verifyCodeList.get(verifyCodeList.size() - 1);
        assertThat(testVerifyCode.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testVerifyCode.getExpire()).isEqualTo(DEFAULT_EXPIRE);
    }

    @Test
    @Transactional
    public void createVerifyCodeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = verifyCodeRepository.findAll().size();

        // Create the VerifyCode with an existing ID
        verifyCode.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restVerifyCodeMockMvc.perform(post("/api/verify-codes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(verifyCode)))
            .andExpect(status().isBadRequest());

        // Validate the VerifyCode in the database
        List<VerifyCode> verifyCodeList = verifyCodeRepository.findAll();
        assertThat(verifyCodeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllVerifyCodes() throws Exception {
        // Initialize the database
        verifyCodeRepository.saveAndFlush(verifyCode);

        // Get all the verifyCodeList
        restVerifyCodeMockMvc.perform(get("/api/verify-codes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(verifyCode.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].expire").value(hasItem(DEFAULT_EXPIRE.toString())));
    }

    @Test
    @Transactional
    public void getVerifyCode() throws Exception {
        // Initialize the database
        verifyCodeRepository.saveAndFlush(verifyCode);

        // Get the verifyCode
        restVerifyCodeMockMvc.perform(get("/api/verify-codes/{id}", verifyCode.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(verifyCode.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()))
            .andExpect(jsonPath("$.expire").value(DEFAULT_EXPIRE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingVerifyCode() throws Exception {
        // Get the verifyCode
        restVerifyCodeMockMvc.perform(get("/api/verify-codes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateVerifyCode() throws Exception {
        // Initialize the database
        verifyCodeRepository.saveAndFlush(verifyCode);
        int databaseSizeBeforeUpdate = verifyCodeRepository.findAll().size();

        // Update the verifyCode
        VerifyCode updatedVerifyCode = verifyCodeRepository.findOne(verifyCode.getId());
        // Disconnect from session so that the updates on updatedVerifyCode are not directly saved in db
        em.detach(updatedVerifyCode);
        updatedVerifyCode
            .code(UPDATED_CODE)
            .expire(UPDATED_EXPIRE);

        restVerifyCodeMockMvc.perform(put("/api/verify-codes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedVerifyCode)))
            .andExpect(status().isOk());

        // Validate the VerifyCode in the database
        List<VerifyCode> verifyCodeList = verifyCodeRepository.findAll();
        assertThat(verifyCodeList).hasSize(databaseSizeBeforeUpdate);
        VerifyCode testVerifyCode = verifyCodeList.get(verifyCodeList.size() - 1);
        assertThat(testVerifyCode.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testVerifyCode.getExpire()).isEqualTo(UPDATED_EXPIRE);
    }

    @Test
    @Transactional
    public void updateNonExistingVerifyCode() throws Exception {
        int databaseSizeBeforeUpdate = verifyCodeRepository.findAll().size();

        // Create the VerifyCode

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restVerifyCodeMockMvc.perform(put("/api/verify-codes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(verifyCode)))
            .andExpect(status().isCreated());

        // Validate the VerifyCode in the database
        List<VerifyCode> verifyCodeList = verifyCodeRepository.findAll();
        assertThat(verifyCodeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteVerifyCode() throws Exception {
        // Initialize the database
        verifyCodeRepository.saveAndFlush(verifyCode);
        int databaseSizeBeforeDelete = verifyCodeRepository.findAll().size();

        // Get the verifyCode
        restVerifyCodeMockMvc.perform(delete("/api/verify-codes/{id}", verifyCode.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<VerifyCode> verifyCodeList = verifyCodeRepository.findAll();
        assertThat(verifyCodeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VerifyCode.class);
        VerifyCode verifyCode1 = new VerifyCode();
        verifyCode1.setId(1L);
        VerifyCode verifyCode2 = new VerifyCode();
        verifyCode2.setId(verifyCode1.getId());
        assertThat(verifyCode1).isEqualTo(verifyCode2);
        verifyCode2.setId(2L);
        assertThat(verifyCode1).isNotEqualTo(verifyCode2);
        verifyCode1.setId(null);
        assertThat(verifyCode1).isNotEqualTo(verifyCode2);
    }
}
