package com.wyy.web.rest;

import com.wyy.UmmApp;

import com.wyy.config.SecurityBeanOverrideConfiguration;

import com.wyy.domain.Deployment;
import com.wyy.repository.DeploymentRepository;
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
 * Test class for the DeploymentResource REST controller.
 *
 * @see DeploymentResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UmmApp.class, SecurityBeanOverrideConfiguration.class})
public class DeploymentResourceIntTest {

    private static final String DEFAULT_UUID = "AAAAAAAAAA";
    private static final String UPDATED_UUID = "BBBBBBBBBB";

    private static final String DEFAULT_DEPLOYER = "AAAAAAAAAA";
    private static final String UPDATED_DEPLOYER = "BBBBBBBBBB";

    private static final String DEFAULT_SOLUTION_UUID = "AAAAAAAAAA";
    private static final String UPDATED_SOLUTION_UUID = "BBBBBBBBBB";

    private static final String DEFAULT_SOLUTION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SOLUTION_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SOLUTION_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_SOLUTION_AUTHOR = "BBBBBBBBBB";

    private static final Integer DEFAULT_K_8_S_PORT = 1;
    private static final Integer UPDATED_K_8_S_PORT = 2;

    private static final Boolean DEFAULT_IS_PUBLIC = false;
    private static final Boolean UPDATED_IS_PUBLIC = true;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_PICTURE_URL = "AAAAAAAAAA";
    private static final String UPDATED_PICTURE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_MODEL_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_MODEL_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_TOOLKIT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TOOLKIT_TYPE = "BBBBBBBBBB";

    private static final Long DEFAULT_CALL_COUNT = 1L;
    private static final Long UPDATED_CALL_COUNT = 2L;

    private static final String DEFAULT_DEMO_URL = "AAAAAAAAAA";
    private static final String UPDATED_DEMO_URL = "BBBBBBBBBB";

    @Autowired
    private DeploymentRepository deploymentRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restDeploymentMockMvc;

    private Deployment deployment;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DeploymentResource deploymentResource = new DeploymentResource(deploymentRepository);
        this.restDeploymentMockMvc = MockMvcBuilders.standaloneSetup(deploymentResource)
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
    public static Deployment createEntity(EntityManager em) {
        Deployment deployment = new Deployment()
            .uuid(DEFAULT_UUID)
            .deployer(DEFAULT_DEPLOYER)
            .solutionUuid(DEFAULT_SOLUTION_UUID)
            .solutionName(DEFAULT_SOLUTION_NAME)
            .solutionAuthor(DEFAULT_SOLUTION_AUTHOR)
            .k8sPort(DEFAULT_K_8_S_PORT)
            .isPublic(DEFAULT_IS_PUBLIC)
            .status(DEFAULT_STATUS)
            .createdDate(DEFAULT_CREATED_DATE)
            .modifiedDate(DEFAULT_MODIFIED_DATE)
            .pictureUrl(DEFAULT_PICTURE_URL)
            .modelType(DEFAULT_MODEL_TYPE)
            .toolkitType(DEFAULT_TOOLKIT_TYPE)
            .callCount(DEFAULT_CALL_COUNT)
            .demoUrl(DEFAULT_DEMO_URL);
        return deployment;
    }

    @Before
    public void initTest() {
        deployment = createEntity(em);
    }

    @Test
    @Transactional
    public void createDeployment() throws Exception {
        int databaseSizeBeforeCreate = deploymentRepository.findAll().size();

        // Create the Deployment
        restDeploymentMockMvc.perform(post("/api/deployments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deployment)))
            .andExpect(status().isCreated());

        // Validate the Deployment in the database
        List<Deployment> deploymentList = deploymentRepository.findAll();
        assertThat(deploymentList).hasSize(databaseSizeBeforeCreate + 1);
        Deployment testDeployment = deploymentList.get(deploymentList.size() - 1);
        assertThat(testDeployment.getUuid()).isEqualTo(DEFAULT_UUID);
        assertThat(testDeployment.getDeployer()).isEqualTo(DEFAULT_DEPLOYER);
        assertThat(testDeployment.getSolutionUuid()).isEqualTo(DEFAULT_SOLUTION_UUID);
        assertThat(testDeployment.getSolutionName()).isEqualTo(DEFAULT_SOLUTION_NAME);
        assertThat(testDeployment.getSolutionAuthor()).isEqualTo(DEFAULT_SOLUTION_AUTHOR);
        assertThat(testDeployment.getk8sPort()).isEqualTo(DEFAULT_K_8_S_PORT);
        assertThat(testDeployment.isIsPublic()).isEqualTo(DEFAULT_IS_PUBLIC);
        assertThat(testDeployment.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testDeployment.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testDeployment.getModifiedDate()).isEqualTo(DEFAULT_MODIFIED_DATE);
        assertThat(testDeployment.getPictureUrl()).isEqualTo(DEFAULT_PICTURE_URL);
        assertThat(testDeployment.getModelType()).isEqualTo(DEFAULT_MODEL_TYPE);
        assertThat(testDeployment.getToolkitType()).isEqualTo(DEFAULT_TOOLKIT_TYPE);
        assertThat(testDeployment.getCallCount()).isEqualTo(DEFAULT_CALL_COUNT);
        assertThat(testDeployment.getDemoUrl()).isEqualTo(DEFAULT_DEMO_URL);
    }

    @Test
    @Transactional
    public void createDeploymentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = deploymentRepository.findAll().size();

        // Create the Deployment with an existing ID
        deployment.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDeploymentMockMvc.perform(post("/api/deployments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deployment)))
            .andExpect(status().isBadRequest());

        // Validate the Deployment in the database
        List<Deployment> deploymentList = deploymentRepository.findAll();
        assertThat(deploymentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllDeployments() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get all the deploymentList
        restDeploymentMockMvc.perform(get("/api/deployments?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(deployment.getId().intValue())))
            .andExpect(jsonPath("$.[*].uuid").value(hasItem(DEFAULT_UUID.toString())))
            .andExpect(jsonPath("$.[*].deployer").value(hasItem(DEFAULT_DEPLOYER.toString())))
            .andExpect(jsonPath("$.[*].solutionUuid").value(hasItem(DEFAULT_SOLUTION_UUID.toString())))
            .andExpect(jsonPath("$.[*].solutionName").value(hasItem(DEFAULT_SOLUTION_NAME.toString())))
            .andExpect(jsonPath("$.[*].solutionAuthor").value(hasItem(DEFAULT_SOLUTION_AUTHOR.toString())))
            .andExpect(jsonPath("$.[*].k8sPort").value(hasItem(DEFAULT_K_8_S_PORT)))
            .andExpect(jsonPath("$.[*].isPublic").value(hasItem(DEFAULT_IS_PUBLIC.booleanValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].modifiedDate").value(hasItem(DEFAULT_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].pictureUrl").value(hasItem(DEFAULT_PICTURE_URL.toString())))
            .andExpect(jsonPath("$.[*].modelType").value(hasItem(DEFAULT_MODEL_TYPE.toString())))
            .andExpect(jsonPath("$.[*].toolkitType").value(hasItem(DEFAULT_TOOLKIT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].callCount").value(hasItem(DEFAULT_CALL_COUNT.intValue())))
            .andExpect(jsonPath("$.[*].demoUrl").value(hasItem(DEFAULT_DEMO_URL.toString())));
    }

    @Test
    @Transactional
    public void getDeployment() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);

        // Get the deployment
        restDeploymentMockMvc.perform(get("/api/deployments/{id}", deployment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(deployment.getId().intValue()))
            .andExpect(jsonPath("$.uuid").value(DEFAULT_UUID.toString()))
            .andExpect(jsonPath("$.deployer").value(DEFAULT_DEPLOYER.toString()))
            .andExpect(jsonPath("$.solutionUuid").value(DEFAULT_SOLUTION_UUID.toString()))
            .andExpect(jsonPath("$.solutionName").value(DEFAULT_SOLUTION_NAME.toString()))
            .andExpect(jsonPath("$.solutionAuthor").value(DEFAULT_SOLUTION_AUTHOR.toString()))
            .andExpect(jsonPath("$.k8sPort").value(DEFAULT_K_8_S_PORT))
            .andExpect(jsonPath("$.isPublic").value(DEFAULT_IS_PUBLIC.booleanValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.modifiedDate").value(DEFAULT_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.pictureUrl").value(DEFAULT_PICTURE_URL.toString()))
            .andExpect(jsonPath("$.modelType").value(DEFAULT_MODEL_TYPE.toString()))
            .andExpect(jsonPath("$.toolkitType").value(DEFAULT_TOOLKIT_TYPE.toString()))
            .andExpect(jsonPath("$.callCount").value(DEFAULT_CALL_COUNT.intValue()))
            .andExpect(jsonPath("$.demoUrl").value(DEFAULT_DEMO_URL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDeployment() throws Exception {
        // Get the deployment
        restDeploymentMockMvc.perform(get("/api/deployments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDeployment() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);
        int databaseSizeBeforeUpdate = deploymentRepository.findAll().size();

        // Update the deployment
        Deployment updatedDeployment = deploymentRepository.findOne(deployment.getId());
        // Disconnect from session so that the updates on updatedDeployment are not directly saved in db
        em.detach(updatedDeployment);
        updatedDeployment
            .uuid(UPDATED_UUID)
            .deployer(UPDATED_DEPLOYER)
            .solutionUuid(UPDATED_SOLUTION_UUID)
            .solutionName(UPDATED_SOLUTION_NAME)
            .solutionAuthor(UPDATED_SOLUTION_AUTHOR)
            .k8sPort(UPDATED_K_8_S_PORT)
            .isPublic(UPDATED_IS_PUBLIC)
            .status(UPDATED_STATUS)
            .createdDate(UPDATED_CREATED_DATE)
            .modifiedDate(UPDATED_MODIFIED_DATE)
            .pictureUrl(UPDATED_PICTURE_URL)
            .modelType(UPDATED_MODEL_TYPE)
            .toolkitType(UPDATED_TOOLKIT_TYPE)
            .callCount(UPDATED_CALL_COUNT)
            .demoUrl(UPDATED_DEMO_URL);

        restDeploymentMockMvc.perform(put("/api/deployments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedDeployment)))
            .andExpect(status().isOk());

        // Validate the Deployment in the database
        List<Deployment> deploymentList = deploymentRepository.findAll();
        assertThat(deploymentList).hasSize(databaseSizeBeforeUpdate);
        Deployment testDeployment = deploymentList.get(deploymentList.size() - 1);
        assertThat(testDeployment.getUuid()).isEqualTo(UPDATED_UUID);
        assertThat(testDeployment.getDeployer()).isEqualTo(UPDATED_DEPLOYER);
        assertThat(testDeployment.getSolutionUuid()).isEqualTo(UPDATED_SOLUTION_UUID);
        assertThat(testDeployment.getSolutionName()).isEqualTo(UPDATED_SOLUTION_NAME);
        assertThat(testDeployment.getSolutionAuthor()).isEqualTo(UPDATED_SOLUTION_AUTHOR);
        assertThat(testDeployment.getk8sPort()).isEqualTo(UPDATED_K_8_S_PORT);
        assertThat(testDeployment.isIsPublic()).isEqualTo(UPDATED_IS_PUBLIC);
        assertThat(testDeployment.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testDeployment.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testDeployment.getModifiedDate()).isEqualTo(UPDATED_MODIFIED_DATE);
        assertThat(testDeployment.getPictureUrl()).isEqualTo(UPDATED_PICTURE_URL);
        assertThat(testDeployment.getModelType()).isEqualTo(UPDATED_MODEL_TYPE);
        assertThat(testDeployment.getToolkitType()).isEqualTo(UPDATED_TOOLKIT_TYPE);
        assertThat(testDeployment.getCallCount()).isEqualTo(UPDATED_CALL_COUNT);
        assertThat(testDeployment.getDemoUrl()).isEqualTo(UPDATED_DEMO_URL);
    }

    @Test
    @Transactional
    public void updateNonExistingDeployment() throws Exception {
        int databaseSizeBeforeUpdate = deploymentRepository.findAll().size();

        // Create the Deployment

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restDeploymentMockMvc.perform(put("/api/deployments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deployment)))
            .andExpect(status().isCreated());

        // Validate the Deployment in the database
        List<Deployment> deploymentList = deploymentRepository.findAll();
        assertThat(deploymentList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteDeployment() throws Exception {
        // Initialize the database
        deploymentRepository.saveAndFlush(deployment);
        int databaseSizeBeforeDelete = deploymentRepository.findAll().size();

        // Get the deployment
        restDeploymentMockMvc.perform(delete("/api/deployments/{id}", deployment.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Deployment> deploymentList = deploymentRepository.findAll();
        assertThat(deploymentList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Deployment.class);
        Deployment deployment1 = new Deployment();
        deployment1.setId(1L);
        Deployment deployment2 = new Deployment();
        deployment2.setId(deployment1.getId());
        assertThat(deployment1).isEqualTo(deployment2);
        deployment2.setId(2L);
        assertThat(deployment1).isNotEqualTo(deployment2);
        deployment1.setId(null);
        assertThat(deployment1).isNotEqualTo(deployment2);
    }
}
