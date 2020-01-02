package com.wyy.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wyy.config.ConfigurationProperties;
import com.wyy.domain.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Service
public class ComposerService {
    private static final Logger logger = LoggerFactory.getLogger(ComposerService.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final NexusArtifactClient nexusArtifactClient;
    private final UmmClient ummClient;
    private final UmuClient umuClient;
    private final ConfigurationProperties configurationProperties;
    private final String apiExample = "api-example.txt";

//    @Value("${kubernetes.ability.internalIP}")
    private String internalIP = "k8s.unicom.gq";

    public ComposerService(NexusArtifactClient nexusArtifactClient, UmmClient ummClient, UmuClient umuClient, ConfigurationProperties configurationProperties) {
        this.nexusArtifactClient = nexusArtifactClient;
        this.ummClient = ummClient;
        this.umuClient = umuClient;
        this.configurationProperties = configurationProperties;
    }

    private ResponseEntity<String> apiGateway(String url, String requestBody, MultiValueMap<String,String> requestHeader) {
        logger.debug("Start API forwarding");

        try {
            HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, requestHeader);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8"))); // 直接使用RestTemplate的POST方法时，字符串默认使用“ISO-8859-1”编码，需要转换
            ResponseEntity<String> response = restTemplate.postForEntity(url, httpEntity, String.class);
            return ResponseEntity.status(response.getStatusCodeValue()).body(response.getBody());
        } catch(HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    private ResponseEntity<String> getGateway(String url, MultiValueMap<String,String> requestHeader) {
        logger.debug("Start API get forwarding");
        try {

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, requestHeader);
            return ResponseEntity.status(response.getStatusCodeValue()).body(response.getBody());
        } catch(HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    private String readArtifact(String solutionId, String artifactType){
        String result = "";
        String nexusURI = getArtifactNexusUrl(solutionId, artifactType);
        if (null != nexusURI && !"".equals(nexusURI)) {
            ByteArrayOutputStream byteArrayOutputStream = nexusArtifactClient.getArtifact(nexusURI);
            result = byteArrayOutputStream.toString();
        }
        return result;
    }

    private String getArtifactNexusUrl(String solutionId, String artifactType) {
        logger.debug("getArtifactNexusUrl() : Begin");
        String nexusURI = "";
        // Get the list of TgifArtifact for the SolutionId.
        List<Artifact> artifactList = ummClient.getArtifacts(solutionId, artifactType);
        if (null != artifactList && !artifactList.isEmpty()) {
            nexusURI = artifactList.get(0).getUrl();
            logger.debug(" Nexus URI :  {} ", nexusURI);
        }
        logger.debug("getArtifactNexusUrl() : End");
        return nexusURI;
    }

    private String readArtifactBlueprint(String solutionUuid) {
        return readArtifact(solutionUuid, configurationProperties.getBlueprintArtifactType());
    }

    private Example getExample(String solutionUuid, MultiValueMap<String,String> requestHeader) throws IOException {
        List<Document> documents = ummClient.getApiExamples(solutionUuid, apiExample);
        if (documents != null && documents.size() >0) {
            String url = documents.get(0).getUrl();
            ResponseEntity<String> res = this.getGateway(url, requestHeader);
            Examples examples = mapper.readValue(res.getBody(), new TypeReference<Examples>() {});
            if (examples != null && examples.getExamples() != null && examples.getExamples().size() >0 ) {
                return examples.getExamples().get(0);
            }
        }
        return null;
    }

    /**
     * set value to body, assume that body only has one field
     * @param example
     * @param value
     * @return
     * @throws JSONException
     */
    private String getUpdatedBody(Example example, Object value) throws JSONException {
        Map body = (Map) example.getBody();
        JSONObject jsonObject = new JSONObject(body);
        Iterator keys = jsonObject.keys();
        while (keys.hasNext()){
            String key = (String) keys.next();
            logger.info("getUpdatedBody key {}", key);
            jsonObject.putOpt(key, value);
            return jsonObject.toString();
        }
        return "";
    }
    private String parseRequestBody(String requestBody, String solutionName, MultiValueMap<String,String> requestHeader) throws Exception {
        JSONObject jsonObject = new JSONObject(requestBody);
        Object value = jsonObject.get("value");
        if (value == null) {
            Iterator keys = jsonObject.keys();
            while (keys.hasNext()){
                String key = (String) keys.next();
                value = jsonObject.get(key);
                break;
            }
        }

        Solution solution = this.getBaseSolutionByName(solutionName);
        if (solution == null) {
            throw new Exception("Solution is null for name " + solutionName);
        }
        Example example = this.getExample(solution.getUuid(), requestHeader);
        if (example == null) {
            throw new Exception("Example is null for solution " + solution.getUuid());
        }

        return this.getUpdatedBody(example, value);
    }
    private ResponseEntity<String> callNode(Node node, Blueprint blueprint, String operation, String requestBody, MultiValueMap<String,String> requestHeader) throws Exception {
        String url = constructURL(node, operation);
        String body = this.parseRequestBody(requestBody, node.getContainerName(), requestHeader);
        logger.info("callNode  {} on operation {} with value {}", node.getContainerName(), operation, body);
        ResponseEntity<String> res = apiGateway(url, body, requestHeader);
        logger.info("callNode  {} on operation {} result {}", node.getContainerName(), operation, res.getBody());
        node.setBeingProcessedByAThread(true);
        ArrayList<OperationSignatureList> operationSignatureListArrayList = node.getOperationSignatureList();
        for (OperationSignatureList operationSignatureList: operationSignatureListArrayList) {
            ArrayList<ConnectedTo> connectedTos = operationSignatureList.getConnectedTo();
            for (ConnectedTo connectedTo: connectedTos) {
                Node curNode = blueprint.getNodebyContainer(connectedTo.getContainerName());
                if (curNode != null && !curNode.isBeingProcessedByAThread()){
                    return callNode(curNode, blueprint, connectedTo.getOperationSignature().getOperationName(), res.getBody(), requestHeader);
                }
            }
        }
        return new ResponseEntity<>(res, HttpStatus.OK).getBody();
    }
    public ResponseEntity<String> orchestrate(String solutionUuid, String modelMethod, String requestBody, MultiValueMap<String,String> requestHeader) throws Exception {
        String blueprintJson = readArtifactBlueprint(solutionUuid);
        if (StringUtils.isEmpty(blueprintJson)) {
            logger.error("notify: Empty blueprint JSON");
            return new ResponseEntity<>("Empty blueprint JSON", HttpStatus.PARTIAL_CONTENT);
        }
        logger.info("orchestrate solution {} on method {}", solutionUuid, modelMethod );
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Blueprint blueprint = mapper.readValue(blueprintJson, new TypeReference<Blueprint>() {});
        List<Node> nodeList = blueprint.getNodes();

        for (Node n : nodeList) {
            n.setNodeOutput(null);
            n.setOutputAvailable(false);
            n.setBeingProcessedByAThread(false);
        }
        // Input node related
        String inpContainer = null;
        boolean singleModel = false;
        List<InputPort> inps = blueprint.getInputPorts();
        // Now call the input node and its operation signature.
        for (InputPort inport : inps) {
            if (inport.getOperationSignature().getOperationName().equals(modelMethod)) {
                inpContainer = inport.getContainerName();
                break;
            }
        }

        Node inpNode = blueprint.getNodebyContainer(inpContainer);
        // Check if the input node has any dependencies? if no, then set
        // singlemodel to true.
        ArrayList<ConnectedTo> inpNodeDeps = findConnectedTo(inpNode, modelMethod);
        if (inpNodeDeps == null || inpNodeDeps.isEmpty()) {
            singleModel = true;
        }
        String url = constructURL(inpNode, modelMethod);
        ResponseEntity<String> res = apiGateway(url, requestBody, requestHeader);
        logger.info("orchestrate solution {} on method {} result {}", solutionUuid, modelMethod, res.getBody());
        if (singleModel) {
            return res;
        }else {
            // set the output for input node
            inpNode.setNodeOutput(res.getBody());
            // set outputAvailable for input node
            inpNode.setOutputAvailable(true);
            inpNode.setBeingProcessedByAThread(true);
            //call connected node one by one
            for (ConnectedTo connectedTo:inpNodeDeps) {
                Node node = blueprint.getNodebyContainer(connectedTo.getContainerName());
                if (node != null && !node.isBeingProcessedByAThread()){
                    return callNode(node, blueprint, connectedTo.getOperationSignature().getOperationName(), res.getBody(), requestHeader);
                }
            }

            return res;
        }
    }

    private ArrayList<ConnectedTo> findConnectedTo(Node n, String sent_ops) {
        ArrayList<OperationSignatureList> listOfOperationSigList = n.getOperationSignatureList();
        for (OperationSignatureList signatureList : listOfOperationSigList) {
            if ((signatureList.getOperationSignature().getOperationName()).equals(sent_ops)) {
                return signatureList.getConnectedTo();
            }
        }
        return null;
    }

    private String constructURL(Node n, String modelMethod) throws Exception {
        String finalUrl;
        Solution solution = this.getBaseSolutionByName(n.getContainerName());
        if (solution == null) {
            throw new Exception("No solution for node container:" + n.getContainerName());
        }
        List<Deployment> deployments1 = this.ummClient.getDeploymentsBySolutionUuid(solution.getUuid(), Boolean.FALSE, "运行");
        List<Deployment> deployments2 = this.ummClient.getDeploymentsBySolutionUuid(solution.getUuid(), Boolean.TRUE, "运行");
        if (deployments1.isEmpty() && deployments2.isEmpty()){
            throw new Exception("No deployment for solution:" + solution.getUuid());
        }
        List<Deployment> deployments = deployments1.size()>0 ? deployments1 : deployments2;
        Deployment deployment = deployments.get(0);
        Integer k8sPort = deployment.getk8sPort();

        if(k8sPort == null) {
            throw new Exception("Deployment: " + deployment.getId() + " is not running");
        }
        finalUrl = "http://" + internalIP + ":" + k8sPort + "/model/methods/" + modelMethod;
        return finalUrl;
    }

    private Solution getBaseSolutionByName(String name){
        String status = "上架";
        String subject3 = "base";
        List<Solution> solutions = this.ummClient.getSolutionsByNameSubject3(name, status, subject3);
        if (solutions.isEmpty()){
            return null;
        }else {
            return solutions.get(0);
        }
    }

}
