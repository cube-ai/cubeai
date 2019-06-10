package com.wyy.web.rest;

import com.wyy.UaaApp;

import com.wyy.config.SecurityBeanOverrideConfiguration;

import com.wyy.domain.Article;
import com.wyy.repository.ArticleRepository;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.wyy.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ArticleResource REST controller.
 *
 * @see ArticleResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UaaApp.class)
public class ArticleResourceIntTest {

    private static final String DEFAULT_UUID = "AAAAAAAAAA";
    private static final String UPDATED_UUID = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHOR_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHOR_NAME = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT_1 = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT_1 = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT_2 = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT_2 = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT_3 = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT_3 = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_SUMMARY = "AAAAAAAAAA";
    private static final String UPDATED_SUMMARY = "BBBBBBBBBB";

    private static final String DEFAULT_TAG_1 = "AAAAAAAAAA";
    private static final String UPDATED_TAG_1 = "BBBBBBBBBB";

    private static final String DEFAULT_TAG_2 = "AAAAAAAAAA";
    private static final String UPDATED_TAG_2 = "BBBBBBBBBB";

    private static final String DEFAULT_TAG_3 = "AAAAAAAAAA";
    private static final String UPDATED_TAG_3 = "BBBBBBBBBB";

    private static final String DEFAULT_PICTURE_URL = "AAAAAAAAAA";
    private static final String UPDATED_PICTURE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Long DEFAULT_DISPLAY_ORDER = 1L;
    private static final Long UPDATED_DISPLAY_ORDER = 2L;

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restArticleMockMvc;

    private Article article;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ArticleResource articleResource = new ArticleResource(articleRepository);
        this.restArticleMockMvc = MockMvcBuilders.standaloneSetup(articleResource)
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
    public static Article createEntity(EntityManager em) {
        Article article = new Article()
            .uuid(DEFAULT_UUID)
            .authorLogin(DEFAULT_AUTHOR_LOGIN)
            .authorName(DEFAULT_AUTHOR_NAME)
            .subject1(DEFAULT_SUBJECT_1)
            .subject2(DEFAULT_SUBJECT_2)
            .subject3(DEFAULT_SUBJECT_3)
            .title(DEFAULT_TITLE)
            .summary(DEFAULT_SUMMARY)
            .tag1(DEFAULT_TAG_1)
            .tag2(DEFAULT_TAG_2)
            .tag3(DEFAULT_TAG_3)
            .pictureUrl(DEFAULT_PICTURE_URL)
            .content(DEFAULT_CONTENT)
            .displayOrder(DEFAULT_DISPLAY_ORDER)
            .createdDate(DEFAULT_CREATED_DATE)
            .modifiedDate(DEFAULT_MODIFIED_DATE);
        return article;
    }

    @Before
    public void initTest() {
        article = createEntity(em);
    }

    @Test
    @Transactional
    public void createArticle() throws Exception {
        int databaseSizeBeforeCreate = articleRepository.findAll().size();

        // Create the Article
        restArticleMockMvc.perform(post("/api/articles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(article)))
            .andExpect(status().isCreated());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeCreate + 1);
        Article testArticle = articleList.get(articleList.size() - 1);
        assertThat(testArticle.getUuid()).isEqualTo(DEFAULT_UUID);
        assertThat(testArticle.getAuthorLogin()).isEqualTo(DEFAULT_AUTHOR_LOGIN);
        assertThat(testArticle.getAuthorName()).isEqualTo(DEFAULT_AUTHOR_NAME);
        assertThat(testArticle.getSubject1()).isEqualTo(DEFAULT_SUBJECT_1);
        assertThat(testArticle.getSubject2()).isEqualTo(DEFAULT_SUBJECT_2);
        assertThat(testArticle.getSubject3()).isEqualTo(DEFAULT_SUBJECT_3);
        assertThat(testArticle.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testArticle.getSummary()).isEqualTo(DEFAULT_SUMMARY);
        assertThat(testArticle.getTag1()).isEqualTo(DEFAULT_TAG_1);
        assertThat(testArticle.getTag2()).isEqualTo(DEFAULT_TAG_2);
        assertThat(testArticle.getTag3()).isEqualTo(DEFAULT_TAG_3);
        assertThat(testArticle.getPictureUrl()).isEqualTo(DEFAULT_PICTURE_URL);
        assertThat(testArticle.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testArticle.getDisplayOrder()).isEqualTo(DEFAULT_DISPLAY_ORDER);
        assertThat(testArticle.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testArticle.getModifiedDate()).isEqualTo(DEFAULT_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void createArticleWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = articleRepository.findAll().size();

        // Create the Article with an existing ID
        article.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restArticleMockMvc.perform(post("/api/articles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(article)))
            .andExpect(status().isBadRequest());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllArticles() throws Exception {
        // Initialize the database
        articleRepository.saveAndFlush(article);

        // Get all the articleList
        restArticleMockMvc.perform(get("/api/articles?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(article.getId().intValue())))
            .andExpect(jsonPath("$.[*].uuid").value(hasItem(DEFAULT_UUID.toString())))
            .andExpect(jsonPath("$.[*].authorLogin").value(hasItem(DEFAULT_AUTHOR_LOGIN.toString())))
            .andExpect(jsonPath("$.[*].authorName").value(hasItem(DEFAULT_AUTHOR_NAME.toString())))
            .andExpect(jsonPath("$.[*].subject1").value(hasItem(DEFAULT_SUBJECT_1.toString())))
            .andExpect(jsonPath("$.[*].subject2").value(hasItem(DEFAULT_SUBJECT_2.toString())))
            .andExpect(jsonPath("$.[*].subject3").value(hasItem(DEFAULT_SUBJECT_3.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY.toString())))
            .andExpect(jsonPath("$.[*].tag1").value(hasItem(DEFAULT_TAG_1.toString())))
            .andExpect(jsonPath("$.[*].tag2").value(hasItem(DEFAULT_TAG_2.toString())))
            .andExpect(jsonPath("$.[*].tag3").value(hasItem(DEFAULT_TAG_3.toString())))
            .andExpect(jsonPath("$.[*].pictureUrl").value(hasItem(DEFAULT_PICTURE_URL.toString())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER.intValue())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].modifiedDate").value(hasItem(DEFAULT_MODIFIED_DATE.toString())));
    }

    @Test
    @Transactional
    public void getArticle() throws Exception {
        // Initialize the database
        articleRepository.saveAndFlush(article);

        // Get the article
        restArticleMockMvc.perform(get("/api/articles/{id}", article.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(article.getId().intValue()))
            .andExpect(jsonPath("$.uuid").value(DEFAULT_UUID.toString()))
            .andExpect(jsonPath("$.authorLogin").value(DEFAULT_AUTHOR_LOGIN.toString()))
            .andExpect(jsonPath("$.authorName").value(DEFAULT_AUTHOR_NAME.toString()))
            .andExpect(jsonPath("$.subject1").value(DEFAULT_SUBJECT_1.toString()))
            .andExpect(jsonPath("$.subject2").value(DEFAULT_SUBJECT_2.toString()))
            .andExpect(jsonPath("$.subject3").value(DEFAULT_SUBJECT_3.toString()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.summary").value(DEFAULT_SUMMARY.toString()))
            .andExpect(jsonPath("$.tag1").value(DEFAULT_TAG_1.toString()))
            .andExpect(jsonPath("$.tag2").value(DEFAULT_TAG_2.toString()))
            .andExpect(jsonPath("$.tag3").value(DEFAULT_TAG_3.toString()))
            .andExpect(jsonPath("$.pictureUrl").value(DEFAULT_PICTURE_URL.toString()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER.intValue()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.modifiedDate").value(DEFAULT_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingArticle() throws Exception {
        // Get the article
        restArticleMockMvc.perform(get("/api/articles/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateArticle() throws Exception {
        // Initialize the database
        articleRepository.saveAndFlush(article);
        int databaseSizeBeforeUpdate = articleRepository.findAll().size();

        // Update the article
        Article updatedArticle = articleRepository.findOne(article.getId());
        // Disconnect from session so that the updates on updatedArticle are not directly saved in db
        em.detach(updatedArticle);
        updatedArticle
            .uuid(UPDATED_UUID)
            .authorLogin(UPDATED_AUTHOR_LOGIN)
            .authorName(UPDATED_AUTHOR_NAME)
            .subject1(UPDATED_SUBJECT_1)
            .subject2(UPDATED_SUBJECT_2)
            .subject3(UPDATED_SUBJECT_3)
            .title(UPDATED_TITLE)
            .summary(UPDATED_SUMMARY)
            .tag1(UPDATED_TAG_1)
            .tag2(UPDATED_TAG_2)
            .tag3(UPDATED_TAG_3)
            .pictureUrl(UPDATED_PICTURE_URL)
            .content(UPDATED_CONTENT)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .createdDate(UPDATED_CREATED_DATE)
            .modifiedDate(UPDATED_MODIFIED_DATE);

        restArticleMockMvc.perform(put("/api/articles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedArticle)))
            .andExpect(status().isOk());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
        Article testArticle = articleList.get(articleList.size() - 1);
        assertThat(testArticle.getUuid()).isEqualTo(UPDATED_UUID);
        assertThat(testArticle.getAuthorLogin()).isEqualTo(UPDATED_AUTHOR_LOGIN);
        assertThat(testArticle.getAuthorName()).isEqualTo(UPDATED_AUTHOR_NAME);
        assertThat(testArticle.getSubject1()).isEqualTo(UPDATED_SUBJECT_1);
        assertThat(testArticle.getSubject2()).isEqualTo(UPDATED_SUBJECT_2);
        assertThat(testArticle.getSubject3()).isEqualTo(UPDATED_SUBJECT_3);
        assertThat(testArticle.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testArticle.getSummary()).isEqualTo(UPDATED_SUMMARY);
        assertThat(testArticle.getTag1()).isEqualTo(UPDATED_TAG_1);
        assertThat(testArticle.getTag2()).isEqualTo(UPDATED_TAG_2);
        assertThat(testArticle.getTag3()).isEqualTo(UPDATED_TAG_3);
        assertThat(testArticle.getPictureUrl()).isEqualTo(UPDATED_PICTURE_URL);
        assertThat(testArticle.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testArticle.getDisplayOrder()).isEqualTo(UPDATED_DISPLAY_ORDER);
        assertThat(testArticle.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testArticle.getModifiedDate()).isEqualTo(UPDATED_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingArticle() throws Exception {
        int databaseSizeBeforeUpdate = articleRepository.findAll().size();

        // Create the Article

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restArticleMockMvc.perform(put("/api/articles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(article)))
            .andExpect(status().isCreated());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteArticle() throws Exception {
        // Initialize the database
        articleRepository.saveAndFlush(article);
        int databaseSizeBeforeDelete = articleRepository.findAll().size();

        // Get the article
        restArticleMockMvc.perform(delete("/api/articles/{id}", article.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Article.class);
        Article article1 = new Article();
        article1.setId(1L);
        Article article2 = new Article();
        article2.setId(article1.getId());
        assertThat(article1).isEqualTo(article2);
        article2.setId(2L);
        assertThat(article1).isNotEqualTo(article2);
        article1.setId(null);
        assertThat(article1).isNotEqualTo(article2);
    }
}
