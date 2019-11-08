package com.wyy.web.rest;

import com.wyy.UcomposerApp;
import com.wyy.config.ConfigurationProperties;
import com.wyy.config.SecurityBeanOverrideConfiguration;
import com.wyy.service.ComposerService;

import com.wyy.web.rest.errors.ExceptionTranslator;
import io.github.jhipster.config.JHipsterProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

import static com.wyy.web.rest.TestUtil.createFormattingConversionService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the ProfileInfoResource REST controller.
 *
 * @see ProfileInfoResource
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UcomposerApp.class, SecurityBeanOverrideConfiguration.class})
public class ComposerResourceIntTest {
    private static final String solutonId = "bc41200d-943f-4d44-a13c-9e64abf2e08b";
    private static final String modelMethod = "start_add";
    @Autowired
    ComposerService composerService;

    @Autowired
    ConfigurationProperties configurationProperties;
    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;
    @Mock
    private Environment environment;

    @Mock
    private JHipsterProperties jHipsterProperties;

    private MockMvc restComposerMockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        final ComposerResource composerResource = new ComposerResource(composerService);
        this.restComposerMockMvc = MockMvcBuilders.standaloneSetup(composerResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Test
    public void orchestrate() throws Exception {
        String requestBody = "{" + "\"one_data\": 10" + "}";
        restComposerMockMvc.perform(post("/composer/" + solutonId + "/" + modelMethod)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(requestBody))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json"))
            .andExpect(status().is2xxSuccessful());
    }

//    @Test
//    public void runNode() throws Exception {
//        MultiValueMap<String,String> requestHeader = new HttpHeaders();
//        List<String> list = new ArrayList<>();
//        list.add("application/json");
//        requestHeader.put("Content-Type", list);
//        requestHeader.put("Accept", list);
//        String requestBody = "{" + "\"one_data\": 10" + "}";
//        composerService.orchestrate(solutonId, modelMethod, requestBody, requestHeader);
//    }


}
