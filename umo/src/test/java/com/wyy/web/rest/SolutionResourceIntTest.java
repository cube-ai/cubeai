package com.wyy.web.rest;


import com.google.gson.Gson;
import com.wyy.UmoApp;
import com.wyy.config.SecurityBeanOverrideConfiguration;
import com.wyy.domain.CompositeSolution;
import com.wyy.domain.Solution;

import com.wyy.domain.cdump.*;
import com.wyy.domain.protobuf.MessageargumentList;
import com.wyy.dto.SolutionNodes;
import com.wyy.service.CompositeSolutionService;
import com.wyy.service.SolutionService;
import com.wyy.util.ConfigurationProperties;

import com.wyy.web.rest.errors.ExceptionTranslator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.wyy.web.rest.TestUtil.createFormattingConversionService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the SolutionResource REST controller.
 *
 * @see SolutionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UmoApp.class, SecurityBeanOverrideConfiguration.class})
public class SolutionResourceIntTest {
    private static final String NODEID1 = "nodeid1";
    private static final String NODEID2 = "nodeid2";

    private static final String NODENAME1 = "nodename1";
    private static final String NODENAME2 = "nodename2";

    private static final String SOLUTIONUUID1 = "f98996a08f16490abb6845002c96804d";
    private static final String SOLUTIONUUID2 = "83a1770f840d40869d81557dc952b971";

    private static final String SOLUTIONUUIDCS2 = "889f112c-3c4f-4f3f-b60f-466e4761abc9";
    private static final String SOLUTIONUUID_BLUE = "bc41200d-943f-4d44-a13c-9e64abf2e08b";


    private static final String ADMIN_AUTHOR_LOGIN = "admin";

    private static final String DEFAULT_UUID = "AABBBCCCDD";
    private static final String UPDATED_UUID = "AAAAAAABBB";

    private static final String DEFAULT_AUTHOR_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHOR_NAME = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COMPANY = "AAAAAAAAAA";
    private static final String UPDATED_COMPANY = "BBBBBBBBBB";

    private static final String DEFAULT_CO_AUTHORS = "AAAAAAAAAA";
    private static final String UPDATED_CO_AUTHORS = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_SUMMARY = "AAAAAAAAAA";
    private static final String UPDATED_SUMMARY = "BBBBBBBBBB";

    private static final String DEFAULT_TAG_1 = "AAAAAAAAAA";
    private static final String UPDATED_TAG_1 = "BBBBBBBBBB";

    private static final String DEFAULT_TAG_2 = "AAAAAAAAAA";
    private static final String UPDATED_TAG_2 = "BBBBBBBBBB";

    private static final String DEFAULT_TAG_3 = "AAAAAAAAAA";
    private static final String UPDATED_TAG_3 = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT_1 = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT_1 = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT_2 = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT_2 = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT_3 = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT_3 = "BBBBBBBBBB";

    private static final Long DEFAULT_DISPLAY_ORDER = 1L;
    private static final Long UPDATED_DISPLAY_ORDER = 2L;

    private static final String DEFAULT_PICTURE_URL = "AAAAAAAAAA";
    private static final String UPDATED_PICTURE_URL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String DEFAULT_MODEL_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_MODEL_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_TOOLKIT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TOOLKIT_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_VALIDATION_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_VALIDATION_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_PUBLISH_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_PUBLISH_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_PUBLISH_REQUEST = "AAAAAAAAAA";
    private static final String UPDATED_PUBLISH_REQUEST = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_VIEW_COUNT = 1L;
    private static final Long UPDATED_VIEW_COUNT = 2L;

    private static final Long DEFAULT_DOWNLOAD_COUNT = 1L;
    private static final Long UPDATED_DOWNLOAD_COUNT = 2L;

    private static final Instant DEFAULT_LAST_DOWNLOAD = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_DOWNLOAD = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_COMMENT_COUNT = 1L;
    private static final Long UPDATED_COMMENT_COUNT = 2L;

    private static final Long DEFAULT_RATING_COUNT = 1L;
    private static final Long UPDATED_RATING_COUNT = 2L;

    private static final Double DEFAULT_RATING_AVERAGE = 1D;
    private static final Double UPDATED_RATING_AVERAGE = 2D;

    @Autowired
    private SolutionService solutionService;

    @Autowired
    private CompositeSolutionService compositeSolutionService;

    @Autowired
    private ConfigurationProperties configurationProperties;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restSolutionMockMvc;

    private CompositeSolution solution;

//    String cid = "f71cf2ad-9fcd-4da4-a979-2dc31e2d7f43";


//    String localpath = "./src/test/resources/";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        final SolutionResource solutionResource = new SolutionResource(
            solutionService,  compositeSolutionService, configurationProperties);
        this.restSolutionMockMvc = MockMvcBuilders.standaloneSetup(solutionResource)
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
    public static CompositeSolution createEntity() {
        Solution solution = new CompositeSolution()
            .uuid(DEFAULT_UUID)
            .authorLogin(DEFAULT_AUTHOR_LOGIN)
            .authorName(DEFAULT_AUTHOR_NAME)
            .company(DEFAULT_COMPANY)
            .coAuthors(DEFAULT_CO_AUTHORS)
            .name(DEFAULT_NAME)
            .version(DEFAULT_VERSION)
            .summary(DEFAULT_SUMMARY)
            .tag1(DEFAULT_TAG_1)
            .tag2(DEFAULT_TAG_2)
            .tag3(DEFAULT_TAG_3)
            .subject1(DEFAULT_SUBJECT_1)
            .subject2(DEFAULT_SUBJECT_2)
            .subject3(DEFAULT_SUBJECT_3)
            .displayOrder(DEFAULT_DISPLAY_ORDER)
            .pictureUrl(DEFAULT_PICTURE_URL)
            .active(DEFAULT_ACTIVE)
            .modelType(DEFAULT_MODEL_TYPE)
            .toolkitType(DEFAULT_TOOLKIT_TYPE)
            .validationStatus(DEFAULT_VALIDATION_STATUS)
            .publishStatus(DEFAULT_PUBLISH_STATUS)
            .publishRequest(DEFAULT_PUBLISH_REQUEST)
            .createdDate(DEFAULT_CREATED_DATE)
            .modifiedDate(DEFAULT_MODIFIED_DATE)
            .viewCount(DEFAULT_VIEW_COUNT)
            .downloadCount(DEFAULT_DOWNLOAD_COUNT)
            .lastDownload(DEFAULT_LAST_DOWNLOAD)
            .commentCount(DEFAULT_COMMENT_COUNT)
            .ratingCount(DEFAULT_RATING_COUNT)
            .ratingAverage(DEFAULT_RATING_AVERAGE);
        return (CompositeSolution) solution;
    }

    @Before
    public void initTest() {
        solution = createEntity();

    }

//    @Test
    public void createCompositeSolution() throws Exception {
        // Create the Solution
        restSolutionMockMvc.perform(post("/api/compositeSolutions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solution)))
            .andExpect(status().isCreated());

    }

//    @Test
//    public void getSolutions() throws Exception {
//
//        restSolutionMockMvc.perform(get("/api/solutions/?publishStatus="+"下架"+"&page=0&size=30"))
//            .andExpect(status().isOk());
//    }
    private Nodes createNodes(String uuid, String ntype, String px, String py, String radius, String typeName, String nodeName,
                              String nodeId, String nodeVersion){
        Ndata ndata = new Ndata();
        ndata.setFixed(false);
        ndata.setNtype(ntype);
        ndata.setPx(px);
        ndata.setPy(py);
        ndata.setRadius(radius);

        Capabilities capabilities = new Capabilities();
        Capabilities[] capabilities1 = new Capabilities[1];
        capabilities1[0] = capabilities;
        Requirements requirements = new Requirements();
        Requirements[] requirements1 = new Requirements[1];
        requirements1[0] = requirements;

        Nodes nodes = new Nodes();
        nodes.setName(nodeName);
        nodes.setNodeId(nodeId);
        nodes.setNdata(ndata);
        nodes.setNodeSolutionId(uuid);
        nodes.setNodeVersion(nodeVersion);

        nodes.setType(typeName);
        nodes.setCapabilities(capabilities1);
        nodes.setRequirements(requirements1);
        return nodes;
    }

//    @Test
    public void addNode() throws Exception {
        SolutionNodes solutionNodes  = new SolutionNodes();

        Nodes nodes = createNodes(SOLUTIONUUID1, "ntype1", "3", "3", "2", "type1"
            ,NODENAME1, NODEID1, "nodeversion1");
        solutionNodes.setNode(nodes);
        solutionNodes.setCdumpVersion("cdumpversion1");
        solutionNodes.setSolutionId(DEFAULT_UUID);
        solutionNodes.setUserId(DEFAULT_AUTHOR_LOGIN);


        restSolutionMockMvc.perform(post("/api/solutions/nodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solutionNodes)))
            .andExpect(status().is2xxSuccessful());
    }
    private void addOneNodes(Nodes nodes, String uuid, String userId) throws Exception {
        SolutionNodes solutionNodes  = new SolutionNodes();

        solutionNodes.setNode(nodes);
        solutionNodes.setCdumpVersion("cdumpversion1");
        solutionNodes.setSolutionId(uuid);
        solutionNodes.setUserId(userId);

        restSolutionMockMvc.perform(post("/api/solutions/nodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solutionNodes)))
            .andExpect(status().is2xxSuccessful());
    }

//    @Test
    public void modifyNode()throws Exception{
        ModifyNode modifyNode = new ModifyNode();
        modifyNode.setSolutionId(DEFAULT_UUID);
        modifyNode.setUserId(DEFAULT_AUTHOR_LOGIN);
        modifyNode.setNodeName(NODENAME1 + "-modify");
        modifyNode.setNodeId(NODEID1);

        Ndata ndata = new Ndata();
        ndata.setFixed(false);
        ndata.setNtype("ntype1-modify");
        ndata.setPx("101");
        ndata.setPy("201");
        ndata.setRadius("21");
        modifyNode.setNdata(ndata);

        restSolutionMockMvc.perform(put("/api/solutions/nodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(modifyNode)))
            .andExpect(status().is2xxSuccessful());
    }
//    @Test
    public void addLink()throws Exception{
//        Nodes nodes = createNodes(SOLUTIONUUID2, "ntype2", "20", "25", "35","type2",
//            "nodename2", NODEID2, "nodeversion2");
//        this.addOneNodes(nodes, DEFAULT_UUID, DEFAULT_AUTHOR_LOGIN);
        Link link = new Link();
        link.setSolutionId(DEFAULT_UUID);
        link.setLinkId("linkid1");
        link.setLinkName("linkname1");
        Property property = new Property();
        link.setProperty(property);
        link.setSourceNodeId("nodeid1");
        link.setSourceNodeName("nodename1-modify");
        link.setTargetNodeId("nodeid2");
        link.setTargetNodeName("nodename2");
        link.setUserId(DEFAULT_AUTHOR_LOGIN);
        link.setTargetNodeCapabilityName("targetNodeCapabilityName");

        restSolutionMockMvc.perform(post("/api/solutions/links")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(link)))
            .andExpect(status().is2xxSuccessful());
    }
//    @Test
    public void modifyLink()throws Exception{
        Link modifyLink = new Link();
        modifyLink.setSolutionId(DEFAULT_UUID);
        modifyLink.setLinkId("linkid1");
        modifyLink.setLinkName("linkname1-modify");
        modifyLink.setUserId(DEFAULT_AUTHOR_LOGIN);

        restSolutionMockMvc.perform(put("/api/solutions/links")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(modifyLink)))
            .andExpect(status().is2xxSuccessful());
    }
//    @Test
    public void saveCompositeSolutionCdump() throws Exception {

        restSolutionMockMvc.perform(post("/api/compositeSolutions/cdumps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solution)))
            .andExpect(status().is2xxSuccessful());
    }

//    @Test
    public void readCompositeSolutionGraph() throws Exception {
        restSolutionMockMvc.perform(get("/api/solutions/compositeSolutionGraphs?&solutionId="+DEFAULT_UUID))
            .andExpect(status().isOk());
    }
//    @Test
    public void deleteNode()throws Exception{
        ModifyNode modifyNode = new ModifyNode();
        modifyNode.setSolutionId(DEFAULT_UUID);
        modifyNode.setUserId(DEFAULT_AUTHOR_LOGIN);
        modifyNode.setNodeId(NODEID2);

        restSolutionMockMvc.perform(delete("/api/solutions/"+DEFAULT_UUID+"/nodes/"+NODEID2+"/"+DEFAULT_AUTHOR_LOGIN)
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().is2xxSuccessful());
    }

//    @Test
    public void deleteLink()throws Exception{
        this.addLink();
        restSolutionMockMvc.perform(delete("/api/solutions/"+DEFAULT_UUID+"/links/"+"linkid1/"+DEFAULT_AUTHOR_LOGIN)
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().is2xxSuccessful());
    }
//    @Test
    public void clearCdumpFile()throws Exception{
        CompositeSolution compositeSolution = new CompositeSolution();
        compositeSolution.setUuid(DEFAULT_UUID);
        compositeSolution.setAuthorLogin(DEFAULT_AUTHOR_LOGIN);
        restSolutionMockMvc.perform(put("/api/compositeSolutions/cdumps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(compositeSolution)))
            .andExpect(status().is2xxSuccessful());
    }

//    @Test
    public void closeCdumpFile()throws Exception{

        restSolutionMockMvc.perform(delete("/api/compositeSolutions/cdumps/" + DEFAULT_UUID +"/"+DEFAULT_AUTHOR_LOGIN)
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().is2xxSuccessful());
    }

//    @Test
    public void setProbeIndicator() throws Exception {
        CompositeSolution compositeSolution = new CompositeSolution();
        compositeSolution.setUuid(DEFAULT_UUID);
        compositeSolution.setAuthorLogin(DEFAULT_AUTHOR_LOGIN);
        compositeSolution.setProbeIndicator("true");

        restSolutionMockMvc.perform(put("/api/compositeSolutions/cdumps/probeIndicator")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(compositeSolution)))
            .andExpect(status().is2xxSuccessful());

    }

//    @Test
    public void deleteCompositeSolution() throws Exception {

        restSolutionMockMvc.perform(delete("/api/compositeSolutions/{id}", DEFAULT_UUID)
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

    }

//    @Test
    public void fetchJsonTOSCA() throws Exception {
        restSolutionMockMvc.perform(get("/api/solutions/tosca?&solutionId="+SOLUTIONUUID1))
            .andExpect(status().isOk());
    }

//    @Test
    public void fetchProtoBufJSON() throws Exception {
        restSolutionMockMvc.perform(get("/api/solutions/protobuf?&solutionId="+SOLUTIONUUID1))
            .andExpect(status().isOk());
    }

//    @Test
    public void getMatchingModels()throws Exception{
        String portType = "output";
//        String protobufJsonString = "[{\"role\":\"repeated\",\"tag\":\"1\",\"type\":\"string\"},{\"role\":\"repeated\",\"tag\":\"2\",\"type\":\"string\"}]";
        List<MessageargumentList> messageargumentListList= new ArrayList<>();
        MessageargumentList messageargumentList = new MessageargumentList();
        messageargumentList.setRole("repeated");
        messageargumentList.setTag("1");
        messageargumentList.setType("string");

        MessageargumentList messageargumentList2 = new MessageargumentList();
        messageargumentList2.setRole("repeated");
        messageargumentList2.setTag("2");
        messageargumentList2.setType("string");

        messageargumentListList.add(messageargumentList);
        messageargumentListList.add(messageargumentList2);
        String pp = new Gson().toJson(messageargumentListList);
//        System.out.println("****************** "+ pp);
//        JsonArray jsonArray = new JsonArray();
//        jsonArray.add(pp);
        String r = URLEncoder.encode(pp, "UTF-8");
//        System.out.println("******************after encode "+ r);
        restSolutionMockMvc.perform(get("/api/solutions/matchingModels?&userId="+DEFAULT_AUTHOR_LOGIN+"&portType="
            +configurationProperties.getMatchingInputPortType()+"&protobufJsonString="+r))
            .andExpect(status().isOk());
    }

    @Test
    public void validateCompositeSolution() throws Exception {
        CompositeSolution solution = new CompositeSolution();
        solution.setAuthorLogin(ADMIN_AUTHOR_LOGIN);
        solution.setName("start-end");
        solution.setUuid(SOLUTIONUUID_BLUE);

        restSolutionMockMvc.perform(post("/api/compositeSolutions/bluePrint")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(solution)))
            .andExpect(status().is2xxSuccessful());
    }
}
