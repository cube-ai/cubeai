package com.wyy.service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.wyy.domain.Artifact;
import com.wyy.domain.MatchingModel;

import com.wyy.domain.Solution;
import com.wyy.domain.cdump.*;
import com.wyy.domain.cdump.collator.CollatorInputField;
import com.wyy.domain.cdump.collator.CollatorMap;
import com.wyy.domain.cdump.collator.CollatorMapInput;
import com.wyy.domain.cdump.collator.CollatorMapOutput;
import com.wyy.domain.cdump.databroker.DBInputField;
import com.wyy.domain.cdump.databroker.DBMapInput;
import com.wyy.domain.cdump.databroker.DBMapOutput;
import com.wyy.domain.cdump.databroker.DataBrokerMap;
import com.wyy.domain.cdump.datamapper.*;
import com.wyy.domain.cdump.splitter.SplitterMap;
import com.wyy.domain.cdump.splitter.SplitterMapInput;
import com.wyy.domain.cdump.splitter.SplitterMapOutput;
import com.wyy.domain.cdump.splitter.SplitterOutputField;
import com.wyy.domain.matchingmodel.KeyVO;
import com.wyy.domain.matchingmodel.ModelDetailVO;
import com.wyy.domain.protobuf.MessageBody;
import com.wyy.domain.protobuf.MessageargumentList;
import com.wyy.util.ConfigurationProperties;
import com.wyy.util.DEUtil;
import com.wyy.util.ModelCacheForMatching;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class SolutionService {
    private static final Logger logger = LoggerFactory.getLogger(SolutionService.class);

    private final ObjectMapper mapper = new ObjectMapper();

    private final UmmClient ummClient;

    private final ConfigurationProperties configurationProperties;
    private final NexusArtifactClient nexusArtifactClient;
    private final ModelCacheForMatching modelCacheForMatching;
    private final MatchingModelService matchingModelService;
//    private Nodes nodesData;
//    private String ndata;

    public SolutionService(UmmClient ummClient, ConfigurationProperties confprops, NexusArtifactClient nexusArtifactClient, ModelCacheForMatching modelCacheForMatching, MatchingModelService matchingModelService) {
        this.ummClient = ummClient;

        this.configurationProperties = confprops;
        this.nexusArtifactClient = nexusArtifactClient;

        this.modelCacheForMatching = modelCacheForMatching;
        this.matchingModelService = matchingModelService;
    }
    public List<Solution> getAllSolutions(String active, String uuid, String name, String authorLogin, String modelType, String toolkitType,
                               String publishStatus, String publishRequest, String subject1, String subject2, String subject3, String tag, String filter, Pageable pageable) throws Exception {

        List<Solution> solutionsList = new ArrayList<>();
        try {
            solutionsList = ummClient.getSolutions(active, uuid, name, authorLogin, modelType, toolkitType, publishStatus,
                publishRequest, subject1, subject2, subject3, tag, filter, pageable.getPageNumber(), pageable.getPageSize());

            if (null == solutionsList || solutionsList.isEmpty()) {
                logger.debug(" uum returned empty Solution list");
            } else {
                logger.debug(" uum returned Solution list of size :  {} ", solutionsList.size());
            }
        } catch (Exception e) {
            logger.error(" Exception in getSolutions() ", e);
            throw new Exception("Exception in getSolutions() " + e.getMessage());
        }
        logger.debug(" getSolutions() End ");
        return solutionsList;
    }
    /**
     * This method used for get the Solutions form UUM
     * This method accepts userId as Parameter
     * @return
     * This method returns solutions
     * @throws Exception
     * If in case exception occurs it will throw Exception
     */
    public List<Solution> getSolutions(String userId, String filter, Pageable pageable) throws Exception {
        List<Solution> solutionsList = new ArrayList<>();
        try {
            solutionsList = ummClient.getSolutions(userId, filter, pageable);
            if (null == solutionsList || solutionsList.isEmpty()) {
                logger.debug(" uum returned empty Solution list");
            } else {
                logger.debug(" uum returned Solution list of size :  {} ", solutionsList.size());
            }
        } catch (Exception e) {
            logger.error(" Exception in getSolutions() ", e);
            throw new Exception("Exception in getSolutions() " + e.getMessage());
        }
        logger.debug(" getSolutions() End ");
        return solutionsList;
    }

    public boolean validateNode(Nodes node) {
        boolean result = false;
        boolean nameFlag = false;
        boolean nodeIdFlag = false;
        boolean nodeSolutionIdFlag = false;
        boolean nodeVersionFlag = true;

// [Nodes{name='tf-model', nodeId='null', nodeSolutionId='56cbfb3a-58ff-4c8c-9773-779a183dd32a', nodeVersion='null',
// protoUri='null', requirements=null, properties=null, capabilities=null, ndata=null, type=null}]
        boolean ndataFlag = false;

        if (null != node.getName() && node.getName().trim().length() > 0) {
            nameFlag = true;
        }
        if (null != node.getNodeId() && node.getNodeId().trim().length() > 0) {
            nodeIdFlag = true;
        }
        if (null != node.getNodeSolutionId() && node.getNodeSolutionId().trim().length() > 0) {
            nodeSolutionIdFlag = true;
        }
        if (null != node.getNodeVersion() && node.getNodeVersion().trim().length() > 0) {
            nodeVersionFlag = true;
        }

        if (null != node.getNdata()) {
            ndataFlag = true;
        }

        if (nameFlag && nodeIdFlag && nodeSolutionIdFlag && nodeVersionFlag && ndataFlag) {
            result = true;
        }
        return result;
    }
    private void updateDataBrokerTypeNodeProtoUri(Nodes no, String sourceNodeId, String targetNodeId, List<Nodes> nodes){
        if (no.getNodeId().equals(sourceNodeId)) {
            for (Nodes n : nodes) {
                if (n.getNodeId().equals(targetNodeId)) {
                    no.setProtoUri(n.getProtoUri());
                    break;
                }
            }
        }
    }
    private void updateDataBrokerTypeNodesProtoUri(List<Nodes> nodes, String sourceNodeId, String targetNodeId){
        for (Nodes no : nodes) {
            if ( no.getType().equals(configurationProperties.getDatabrokerType())) {
                updateDataBrokerTypeNodeProtoUri(no, sourceNodeId, targetNodeId, nodes);
            }
        }
    }
    private Relations createRelationsObj(String linkName, String linkId, String sourceNodeName, String sourceNodeId, String targetNodeName,
                                         String targetNodeId, String sourceNodeRequirement, String targetNodeCapabilityName,
                                         String input, String output, String start, String end){
        Relations relationObj = new Relations();
        relationObj.setLinkName(linkName);
        relationObj.setLinkId(linkId);
        relationObj.setSourceNodeName(sourceNodeName);
        relationObj.setSourceNodeId(sourceNodeId);
        relationObj.setTargetNodeName(targetNodeName);
        relationObj.setTargetNodeId(targetNodeId);
        relationObj.setSourceNodeRequirement(sourceNodeRequirement);
        relationObj.setTargetNodeCapability(targetNodeCapabilityName);
        relationObj.setInput(input);
        relationObj.setOutput(output);
        relationObj.setStart(start);
        relationObj.setEnd(end);

        return relationObj;
    }

    private void updateLinkDetails(String linkName, String linkId, String sourceNodeName, String sourceNodeId, String targetNodeName,
                                   String targetNodeId, String sourceNodeRequirement, String targetNodeCapabilityName, Cdump cdump,
                                   String input, String output, String start, String end) {
        this.updateDataBrokerTypeNodesProtoUri(cdump.getNodes(), sourceNodeId, targetNodeId);
        logger.debug("updateLinkDetails begin");
        Relations relationObj = this.createRelationsObj(linkName, linkId, sourceNodeName, sourceNodeId, targetNodeName,
            targetNodeId, sourceNodeRequirement, targetNodeCapabilityName, input, output, start, end);

        if (cdump.getRelations() == null) {
            List<Relations> list = new ArrayList<>();
            list.add(relationObj);
            cdump.setRelations(list);
        } else {
            cdump.getRelations().add(relationObj);
        }
        logger.debug("updateLinkDetails end");
    }
    /**
     * This method used for to add the Link between two models
     *
     * @return
     * This method returns success as true or false
     *
     */
    public boolean addLink(String userId, String linkName, String linkId,
                           String sourceNodeName, String sourceNodeId, String targetNodeName, String targetNodeId,
                           String sourceNodeRequirement, String targetNodeCapabilityName, String solutionId, Property property,
                           String input, String output, String start, String end){
        logger.debug(" addLink() in SolutionService : Begin ");
        boolean addedLink = false;
        if (solutionId == null){
            logger.debug(" addLink() solutionId is null, return false ");
            return addedLink;
        }
        String nodeToUpdate;
        List<Nodes> nodesList;
        try {
            if (StringUtils.isEmpty(linkId)){
                linkId = UUID.randomUUID().toString();
            }
            String cdumpFileName = DEUtil.getCdumpFileName(solutionId);
            String path = DEUtil.getCdumpPath(userId, configurationProperties.getToscaOutputFolder());
            Cdump cdump = mapper.readValue(new File(path.concat(cdumpFileName)), Cdump.class);
            nodesList = cdump.getNodes();
            if (nodesList == null || nodesList.isEmpty()){
                logger.debug(" addLink() nodes in cdump is null, return false ");
                return addedLink;
            }
            // update relations list, if link is created between 2 models
            if (null == property || (null != property && null == property.getData_map() && null == property.getCollator_map() && null == property.getSplitter_map())) {
                updateLinkDetails(linkName, linkId, sourceNodeName, sourceNodeId, targetNodeName, targetNodeId, sourceNodeRequirement,
                    targetNodeCapabilityName, cdump, input, output, start, end);
                addedLink = true;
            }
            if (null != property) {
                // if has splitter_map, update splitter_map and link
                if (null != property.getSplitter_map()) {
                    nodeToUpdate = targetNodeId;
                    for (Nodes node : nodesList) {
                        if (node.getNodeId().equals(nodeToUpdate)) {
                            // update Splitter Input Message Signature
                            updateSplitterMap(property.getSplitter_map(), node);
                            updateLinkDetails(linkName, linkId, sourceNodeName, sourceNodeId, targetNodeName, targetNodeId, sourceNodeRequirement,
                                targetNodeCapabilityName, cdump, input, output, start, end);
                            addedLink = true;
                        }
                    }
                }
                // if has collator_map, update collator_map and link
                if (null != property.getCollator_map()) {
                    nodeToUpdate = sourceNodeId;
                    for (Nodes node : nodesList) {
                        if (node.getNodeId().equals(nodeToUpdate)) {
                            // update Collator Output Message Signature
                            updateCollatorMap(property.getCollator_map(), node);
                            updateLinkDetails(linkName, linkId, sourceNodeName, sourceNodeId, targetNodeName, targetNodeId, sourceNodeRequirement,
                                targetNodeCapabilityName, cdump, input, output, start, end);
                            addedLink = true;
                        }
                    }
                } else if (null != property.getData_map()) {
                    addedLink = updateDataMapAndRelations(
                        nodesList, cdump, linkName, linkId, sourceNodeName, sourceNodeId, targetNodeName, targetNodeId, sourceNodeRequirement,
                        targetNodeCapabilityName, property, input, output, start, end);
                }
            }
            try {
                Gson gson = new Gson();
                String jsonInString = gson.toJson(cdump);
                DEUtil.writeDataToFile(path, cdumpFileName, jsonInString);
            } catch (JsonIOException e) {
                logger.error("Exception in addLink() when writeDataToFile", e);
                addedLink = false;
            }
        } catch (Exception e) {
            logger.error(" Exception in addLink() ", e);
            addedLink = false;
        }

        logger.debug(" addLink() in SolutionService : End ");
        return addedLink;

    }

    // set properties field of Data Mapper + update relations list, if link is b/w Model & Data Mapper
    // Identify Data Mapper node to update, the relation direction is from Data Mapper to Model
    private boolean updateDataMapAndRelations(List<Nodes> nodesList, Cdump cdump, String linkName, String linkId,
                                              String sourceNodeName, String sourceNodeId, String targetNodeName, String targetNodeId,
                                              String sourceNodeRequirement, String targetNodeCapabilityName, Property property,
                                              String input, String output, String start, String end){
        boolean addedLink = false;
        String nodeToUpdate;

        if (null != property.getData_map() && property.getData_map().getMap_inputs().length == 0) {
            nodeToUpdate = sourceNodeId;
        } else {
            nodeToUpdate = targetNodeId;
        }
        // update the properties field of Data mapper node + update relations list with link details

        for (Nodes node : nodesList) {
            if (node.getNodeId().equals(nodeToUpdate)) {
                Property[] propertyArr = node.getProperties();
                if (null == propertyArr || propertyArr.length == 0) {
                    Property[] propertyArray = new Property[1];
                    propertyArray[0] = property;
                    node.setProperties(propertyArray);
                    updateLinkDetails(linkName, linkId, sourceNodeName, sourceNodeId, targetNodeName, targetNodeId, sourceNodeRequirement,
                        targetNodeCapabilityName, cdump, input, output, start, end);
                    addedLink = true;
                    break;
                } else {
                    // set map_outputs of data_map under properties field of DM
                    if (null != property.getData_map()
                        && property.getData_map().getMap_inputs().length == 0) {

                        propertyArr[0].getData_map()
                            .setMap_outputs(property.getData_map().getMap_outputs());

                        updateLinkDetails(linkName, linkId, sourceNodeName, sourceNodeId, targetNodeName, targetNodeId, sourceNodeRequirement,
                            targetNodeCapabilityName, cdump, input, output, start, end);
                        addedLink = true;
                        break;
                    }
                    // set map_inputs of data_map under properties field of DM
                    if (null != property.getData_map()
                        && property.getData_map().getMap_outputs().length == 0) {
                        propertyArr[0].getData_map().setMap_inputs(property.getData_map().getMap_inputs());
                        updateLinkDetails(linkName, linkId, sourceNodeName, sourceNodeId, targetNodeName, targetNodeId, sourceNodeRequirement,
                            targetNodeCapabilityName, cdump, input, output, start, end);
                        addedLink = true;
                        break;
                    }
                }
            }
        }

        return addedLink;
    }
    private void updateCollatorMap(CollatorMap collatorMap, Nodes nodesData) {
        logger.debug(" updateCollatorMap()  : Begin");
        Property properties[] = nodesData.getProperties();
        Property newProperty;
        ArrayList<Property> propertyList = new ArrayList<>(Arrays.asList(properties));
        // If In case of properties Contains CollatorMap
        if (null != properties && properties.length != 0) {
            for (Property p : propertyList) {
                if (p.getCollator_map() != null) {
                    CollatorMap cMap = p.getCollator_map();
                    if (null != collatorMap.getCollator_type()) {
                        cMap.setCollator_type(collatorMap.getCollator_type());
                    }
                    if (null != collatorMap.getMap_inputs()) {
                        cMap.setMap_inputs(collatorMap.getMap_inputs());
                    }
                    if (null != collatorMap.getMap_outputs()) {
                        cMap.setMap_outputs(collatorMap.getMap_outputs());
                    }
                    if(null != collatorMap.getOutput_message_signature()){
                        cMap.setOutput_message_signature(collatorMap.getOutput_message_signature());
                    }
                    p.setCollator_map(cMap);
                }
            }
            nodesData.setProperties(properties);
        } else {  //if in case properties for the node is empty.
            newProperty = new Property();
            newProperty.setCollator_map(collatorMap);
            Property newProperties[] = new Property[1];
            newProperties[0] = newProperty;
            nodesData.setProperties(newProperties);
            logger.debug(" updateCollatorMap() : End");
        }
    }
    private void updateSplitterMap(SplitterMap splitterMap, Nodes nodesData) {
        logger.debug(" updateSplitterMap()  : Begin");
        Property properties[] = nodesData.getProperties();
        Property newProperty;
        // If In case of properties Contains SplitterMap
        ArrayList<Property> propertyList = new ArrayList<>(Arrays.asList(properties));
        if (null != properties && properties.length != 0) {
            for (Property p : propertyList) {
                if (p.getSplitter_map() != null) {
                    SplitterMap sMap = p.getSplitter_map();
                    if (null != splitterMap.getSplitter_type()) {
                        sMap.setSplitter_type(splitterMap.getSplitter_type());
                    }
                    if (null != splitterMap.getMap_inputs()) {
                        sMap.setMap_inputs(splitterMap.getMap_inputs());
                    }
                    if (null != splitterMap.getMap_outputs()) {
                        sMap.setMap_outputs(splitterMap.getMap_outputs());
                    }
                    if(null != splitterMap.getInput_message_signature()){
                        sMap.setInput_message_signature(splitterMap.getInput_message_signature());
                    }
                    p.setSplitter_map(sMap);
                }
            }
            nodesData.setProperties(properties);
        } else {  //if in case properties for the node is empty.
            newProperty = new Property();
            newProperty.setSplitter_map(splitterMap);
            Property newProperties[] = new Property[1];
            newProperties[0] = newProperty;
            nodesData.setProperties(newProperties);
            logger.debug(" updateSplitterMap() : End");
        }
    }

    /**
     * This method used for to read the composite solution graph

     * @return
     * This method will return success or failure response in string format
     * @throws Exception
     * If in case exception occurs it will throw Exception
     */
    public String readCompositeSolutionGraph(String login, String solutionId) throws Exception{

        logger.debug(" readCompositeSolutionGraph()  : Begin ");
        String result = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            List<Solution> solutions = ummClient.getSolutionsByUuid(solutionId);
            if (solutions.size()<=0){
                return "{\"error\": \"Requested Solution Not Found\"}";
            }
            //get artifact of type : CDUMP文件
            String nexusURI = getArtifactNexusUrl(solutionId, configurationProperties.getCdumpArtifactType());
            if (null != nexusURI && !"".equals(nexusURI)) {
                byteArrayOutputStream = nexusArtifactClient.getArtifact(nexusURI);
                if (byteArrayOutputStream != null){
                    result = byteArrayOutputStream.toString();
                    logger.info(" Response in String Format :  {} ", result );
                    this.updateCdumpByPayload(result, login, solutionId);
                }

            } else {
                result = "{\"error\": \"CDUMP TgifArtifact Not Found for this solution\"}";
            }
        } catch (Exception e) {
            logger.error(" exception in readCompositeSolutionGraph()",
                e);
            throw new Exception("Failed to read the CompositeSolution");
        } finally {
            try {
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (IOException e) {
                logger.error(
                    "Error : Exception in readArtifact() : Failed to close the byteArrayOutputStream", e);
                throw new Exception("Failed to read the CompositeSolution");
            }
        }
        logger.debug(" readCompositeSolutionGraph()  : End ");
        return result;
    }

    private void updateCdumpByPayload(String payload, String login, String solutionId) throws Exception {
        String path = DEUtil.getCdumpPath(login, configurationProperties.getToscaOutputFolder());
        String cdumpFileName = DEUtil.getCdumpFileName(solutionId);
        try {
            DEUtil.writeDataToFile(path, cdumpFileName, payload);
        }catch (Exception e){
            logger.error("Error : Exception in updateCdumpFromRemote() : ",e);
            throw new Exception("  Exception in updateCdumpFromRemote() , 222, Failed to update the cdump:" + e);
        }
    }
    /**
     * This method used for to modify the Node
     * @return
     * This method will return success/failure response in string format
     */
    public String modifyNode(String userId, String solutionId, String nodeId, String nodeName, Ndata nData,
                             FieldMap fieldMap, DataBrokerMap databrokerMap, CollatorMap collatorMap, SplitterMap splitterMap) {
        logger.debug(" modifyNode() : Begin");

        String results = "";
        String resultTemplate = "{\"success\" : \"%s\", \"errorDescription\" : \"%s\"}";
        try {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.setSerializationInclusion(Include.NON_NULL);

            String cdumpFileName = DEUtil.getCdumpFileName(solutionId);
            String path = DEUtil.getCdumpPath(userId, configurationProperties.getToscaOutputFolder());
            Cdump cdump = mapper.readValue(new File(path.concat(cdumpFileName)), Cdump.class);

            List<Nodes> cdumpNodeList = cdump.getNodes();
            if (null == cdumpNodeList || cdumpNodeList.isEmpty()) {
                results = String.format(resultTemplate, false, "NodeList is empty");
            } else {
                Nodes nodesData = getNodesbyNodeId(nodeId, cdumpNodeList);

                if (nodesData!=null){
                        updateNode(nodesData, nData);
                        if (null != nodeName) {
                            nodesData.setName(nodeName);
                            // Code to modify the link if node names are changed
                            updateLinkNodeName(cdump, nodeId, nodeName);
                        }
                        Property properties[] = nodesData.getProperties();
                        // to update the DataMapper
                        if (null != fieldMap && fieldMap.toString().length() != 0) {
                            updateDataMapper(fieldMap, properties);
                        }
                        // to update the DataBroker
                        if (null != databrokerMap && databrokerMap.toString().length() != 0) {
                            updateDataBroker(databrokerMap, nodesData);
                        }
                        // to update the CollatorMap
                        if (null != collatorMap && collatorMap.toString().length() != 0) {
                            updateCollatorMap(collatorMap, nodesData);
                        }
                        // to update the SplitterMap
                        if (null != splitterMap && splitterMap.toString().length() != 0) {
                            updateSplitterMap(splitterMap, nodesData);
                        }
                        results = String.format(resultTemplate, true, "");
                } else {
                    results = String.format(resultTemplate, false, "Invalid Node Id – not found");
                }

                mapper.writeValue(new File(path.concat(cdumpFileName)), cdump);
                logger.debug(" Node Modified Successfully ");
            }
        } catch (Exception e) {
            logger.error("Exception in  modifyNode() ", e);
            results = String.format(resultTemplate, false, "Not able to modify the Node");
        }
        logger.debug(" modifyNode()   : End");
        return results;
    }

    private Nodes getNodesbyNodeId(String nodeId, List<Nodes> cdumpNodeList){
        for (Nodes nodesData : cdumpNodeList) {
            if (nodesData.getNodeId().equals(nodeId)) {
                return nodesData;
            }
        }
        return null;
    }
    private void updateLinkNodeName(Cdump cdump, String nodeId, String nodeName){
        List<Relations> relations = cdump.getRelations();
        if (null != relations && !relations.isEmpty()) {
            // iterate through relation
            for (Relations relation : relations) {
                // check if the relation contains the specified nodeId
                if (relation.getSourceNodeId().equals(nodeId)) {
                    relation.setSourceNodeName(nodeName);
                } else if (relation.getTargetNodeId().equals(nodeId)) {
                    relation.setTargetNodeName(nodeName);
                }
            }
        }
    }
    private void updateNode(Nodes nodesData, Ndata ndata){
        if (null != ndata ) {
            Ndata dat = nodesData.getNdata();
            dat.setPx(ndata.getPx());
            dat.setNtype(ndata.getNtype());
            dat.setFixed(ndata.isFixed());
            dat.setPy(ndata.getPy());
            dat.setRadius(ndata.getRadius());
            nodesData.setNdata(dat);
        }
    }
    private void updateDataBroker(DataBrokerMap databrokerMap, Nodes nodesData) {
        logger.debug(" updateDataBroker()  : Begin");
        Property properties[] = nodesData.getProperties();
        Property newProperty;
        if(null != properties){
            // For New Solution Create the Property[]
            newProperty = new Property();
            newProperty.setData_broker_map(databrokerMap);

            //check if the databrokerMap already exist.
            ArrayList<Property> propertyList = new ArrayList<>(Arrays.asList(properties));

            if(propertyList.size() == 0){ //if the properties is empty then add the new property with databroket map.
                propertyList.add(newProperty);
            } else {
                for(Property p : propertyList){
                    if(p.getData_broker_map() != null){ //else if data broker map exist and update the same.
                        DataBrokerMap dataBrokerMap = p.getData_broker_map();

                        if(null != databrokerMap.getCsv_file_field_separator()){
                            dataBrokerMap.setCsv_file_field_separator(databrokerMap.getCsv_file_field_separator());
                        }
                        if(null != databrokerMap.getData_broker_type()){
                            dataBrokerMap.setData_broker_type(databrokerMap.getData_broker_type());
                        }
                        if(null != databrokerMap.getFirst_row()){
                            dataBrokerMap.setFirst_row(databrokerMap.getFirst_row());
                        }
                        if(null != databrokerMap.getLocal_system_data_file_path()){
                            dataBrokerMap.setLocal_system_data_file_path(databrokerMap.getLocal_system_data_file_path());
                        }
                        if(null != databrokerMap.getScript()){
                            dataBrokerMap.setScript(databrokerMap.getScript());
                        }
                        if(null != databrokerMap.getTarget_system_url()){
                            dataBrokerMap.setTarget_system_url(databrokerMap.getTarget_system_url());
                        }
                        if(null != databrokerMap.getDatabase_name()){
                            dataBrokerMap.setDatabase_name(databrokerMap.getDatabase_name());
                        }
                        if(null != databrokerMap.getTable_name()){
                            dataBrokerMap.setTable_name(databrokerMap.getTable_name());
                        }
                        if(null != databrokerMap.getJdbc_driver_data_source_class_name()){
                            dataBrokerMap.setJdbc_driver_data_source_class_name(databrokerMap.getJdbc_driver_data_source_class_name());
                        }
                        if(null != databrokerMap.getUser_id()){
                            dataBrokerMap.setUser_id(databrokerMap.getUser_id());
                        }
                        if(null != databrokerMap.getPassword()){
                            dataBrokerMap.setPassword(databrokerMap.getPassword());
                        }
                        if(null != databrokerMap.getProtobufFile() && !databrokerMap.getProtobufFile().isEmpty()){
                            dataBrokerMap.setProtobufFile(databrokerMap.getProtobufFile());
                        }
                        if (null != databrokerMap.getMap_inputs() && null != databrokerMap.getMap_outputs()) {
                            dataBrokerMap.setMap_inputs(databrokerMap.getMap_inputs());
                            dataBrokerMap.setMap_outputs(databrokerMap.getMap_outputs());
                        }
                        p.setData_broker_map(dataBrokerMap);
                    } else {
                        propertyList.add(newProperty); //else add the new dataBroker map to non empty properties.
                    }
                }
            }
            properties = propertyList.toArray(new Property[propertyList.size()]);
            int cnt = 0;
            for(Property p : propertyList){
                properties[cnt] = p;
                cnt++;
            }
            logger.debug(" updateDataBroker()  : End");
            nodesData.setProperties(properties);
        }
    }
    private void updateDataMapper(FieldMap fieldmap, Property[] properties) {
        logger.debug(" updateDataMapper(): Begin");
        if (null != properties && properties.length != 0) {
            // iterate through each property
            for (Property props : properties) {
                DataMap datamap = props.getData_map();
                if (null != datamap && !datamap.toString().isEmpty()) {
                    MapInputs[] mapInputs = datamap.getMap_inputs();
                    if (null != mapInputs && mapInputs.length != 0) {
                        // iterate through mapInputs
                        for (MapInputs mapInput : mapInputs) {
                            // Check if the input message name matches with the provided input meassage name
                            if (mapInput.getMessage_name()
                                .equals(fieldmap.getInput_field_message_name())) {
                                DataMapInputField[] dataMapInputFieldList = mapInput
                                    .getInput_fields();
                                if (null != dataMapInputFieldList
                                    && dataMapInputFieldList.length != 0) {
                                    // iterate through dataMapInput fields
                                    for (DataMapInputField dataMapInputField : dataMapInputFieldList) {
                                        //check if the output/destination tag id is already linked to any source
                                        //node remove the destination node from the object
                                        if (dataMapInputField.getMapped_to_field()
                                            .equals(fieldmap.getOutput_field_tag_id())) {
                                            // delete the mapping if any
                                            dataMapInputField.setMapped_to_message("");
                                            dataMapInputField.setMapped_to_field("");
                                        }
                                        // check if the input source tagid matches with the provided input/source tagid
                                        if (dataMapInputField.getTag()
                                            .equals(fieldmap.getInput_field_tag_id())) {
                                            // update the object with the latest mapping
                                            dataMapInputField.setMapped_to_message(
                                                fieldmap.getOutput_field_message_name());
                                            dataMapInputField.setMapped_to_field(
                                                fieldmap.getOutput_field_tag_id());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        logger.debug(" updateDataMapper()  : End");
    }
    /**
     *
     * @return
     * This method will return success/failure response in string format
     */
    public String modifyLinkName(String userId, String solutionId, String linkId, String linkName){
        String results = "";
        String resultTemplate = "{\"success\" : \"%s\", \"errorDescription\" : \"%s\"}";
        if (solutionId == null){
            logger.debug(" modifyLinkName() both solutionId and solutionId is null");
            return results;
        }
        try {
            Cdump cdump;

            String cdumpFileName = DEUtil.getCdumpFileName(solutionId);
            String path = DEUtil.getCdumpPath(userId, configurationProperties.getToscaOutputFolder());
            cdump = mapper.readValue(new File(path.concat(cdumpFileName)), Cdump.class);
            List<Relations> relations = cdump.getRelations();
            if (null == relations || relations.isEmpty()) {
                results = String.format(resultTemplate, false, "Relations are empty.");
            } else {
                for (Relations relation : relations) {
                    if (relation.getLinkId().equals(linkId)) {
                        relation.setLinkName(linkName);
                        results = String.format(resultTemplate, true, "");
                        break;
                    } else {
                        results = String.format(resultTemplate, false, "Invalid Link Id – not found");
                    }
                }
                mapper.writeValue(new File(path.concat(cdumpFileName)), cdump);
                logger.debug(" Link Modified Successfully ");
            }
        } catch (Exception e) {
            logger.error("Exception in  modifyLinkName() ", e);
            results = String.format(resultTemplate, false, "Not able to modify the Link");
        }
        logger.debug(" modifyLinkName()  : End");
        return results;
    }

    /**

     * @return
     * This method will return success response as true/false
     * @throws Exception
     * If in case exception it will throw Exception
     */
    public boolean deleteNode(String userId, String solutionId, String nodeId) throws Exception{
        logger.debug(" deleteNode() in SolutionService : Begin ");
        boolean deletedNode = false;

        try {
            String cdumpFileName = DEUtil.getCdumpFileName(solutionId);
            String path = DEUtil.getCdumpPath(userId, configurationProperties.getToscaOutputFolder());
            File file = new File(path.concat(cdumpFileName));
            if (file.exists()) {
                try {
                    Cdump cdump = mapper.readValue(file, Cdump.class);
                    List<Nodes> nodesList = cdump.getNodes();
                    List<Relations> relationsList = cdump.getRelations();
                    if (nodesList == null || nodesList.isEmpty()) {
                        deletedNode = false;
                    } else {
                        // Deleting node if exists
                        Iterator<Nodes> nodesIterator = nodesList.iterator();
                        while (nodesIterator.hasNext()) {
                            Nodes node = nodesIterator.next();
                            if (node.getNodeId().equals(nodeId)) {
                                deletedNode = true;
                                nodesIterator.remove();
                                break;
                            }
                        }
                        if (null != relationsList) {
                            for (Relations relations : relationsList) {
                                if (relations.getTargetNodeId().equals(nodeId)) {
                                    // delete the LinkId related TargetNodeId
                                    deleteLinksTargetNode(nodeId, nodesList, relations);
                                }
                                if (relations.getSourceNodeId().equals(nodeId)) {
                                    // delete the LinkId related SourceNodeId
                                    deleteLinksSourceNode(nodeId, nodesList, relations);
                                }
                            }
                        }
                        // Deleting relationsList for given nodeId
                        if (relationsList == null || relationsList.isEmpty()) {
                        } else {
                            Iterator<Relations> relationsIterator = relationsList.iterator();
                            while (relationsIterator.hasNext()) {
                                Relations relation = relationsIterator.next();
                                if (relation.getSourceNodeId().equals(nodeId)
                                    || relation.getTargetNodeId().equals(nodeId)) {
                                    relationsIterator.remove();
                                }
                            }
                        }
                        Gson gson = new Gson();
                        String jsonInString = gson.toJson(cdump);
                        DEUtil.writeDataToFile(path, cdumpFileName, jsonInString);
                    }
                } catch (JsonParseException e) {
                    logger.error( " JsonParseException in deleteNode() ", e);
                    throw e;
                } catch (JsonMappingException e) {
                    logger.error( " JsonMappingException in deleteNode() ", e);
                    throw e;
                } catch (IOException e) {
                    logger.error( " IOException in deleteNode() ", e);
                    throw e;
                }
            }
        } catch (Exception e) {
            logger.error( " Exception in deleteNode() ", e);
            throw new Exception("Failed to Delete the Node");
        }
        logger.debug(" deleteNode() in SolutionService : Ends ");
        return deletedNode;
    }

    private void deleteLinksTargetNode(String nodeId, List<Nodes> nodesList, Relations relations) {
        String sourceNodeId = relations.getSourceNodeId();
        for (Nodes no : nodesList) {
            if (no.getNodeId().equals(sourceNodeId)) {
                String nodeType = no.getType();
                Property[] propArr = no.getProperties();
                if (propArr == null){
                    return;
                }
                ArrayList<Property> arrayList = new ArrayList<>(Arrays.asList(propArr));
                // check if its of DataBroker or not
                if (configurationProperties.getDatabrokerType().equals(nodeType)) {
                    Iterator<Property> propertyItr = arrayList.iterator();
                    while (propertyItr.hasNext()) {
                        Property prop = propertyItr.next();
                        DBMapOutput[] dbMapOutputArr = prop.getData_broker_map().getMap_outputs();
                        for (int i = 0; i < dbMapOutputArr.length; i++) {
                            dbMapOutputArr[i].setOutput_field(null);
                        }
                        DBMapInput[] dbMapInputArr = prop.getData_broker_map().getMap_inputs();
                        for (DBMapInput dbMapIp : dbMapInputArr) {
                            DBInputField dbInField = dbMapIp.getInput_field();
                            dbInField.setChecked("NO");
                            dbInField.setMapped_to_field("");
                        }
                    }
                }
                // check if its of Collator(Array-based/Param-based) or not if
                // yes need to delete the corresponding entries in the collator_map
                else if (configurationProperties.getCollatorType().equals(nodeType)) {
                    updatePropertyWithCollator(arrayList.iterator(), nodeId);
                }
                // check if its of Splitter(Copy-based/Param-based) or not if
                // yes need to delete the corresponding entries in the splitter_map
                else if (configurationProperties.getSplitterType().equals(nodeType)) {
                    updatePropertyWithSplitter(arrayList.iterator(), nodeId);
                }
            }
        }
    }
    private void updatePropertyWithSplitter(Iterator<Property> propertyItr, String nodeId){
        while (propertyItr.hasNext()) {
            Property prop = propertyItr.next();
            if (null != prop.getSplitter_map().getMap_outputs()) {
                SplitterMapOutput[] sMapOutArr = prop.getSplitter_map().getMap_outputs();
                for (int i = 0; i < sMapOutArr.length; i++) {
                    if (sMapOutArr[i].getOutput_field().getTarget_name().equals(nodeId)) {
                        sMapOutArr[i].setOutput_field(new SplitterOutputField());
                    }
                }
            }
        }
    }
    private void updatePropertyWithCollator(Iterator<Property> propertyItr, String nodeId){
        while (propertyItr.hasNext()) {
            Property prop = propertyItr.next();
            if (null != prop.getCollator_map().getMap_inputs()) {
                CollatorMapInput[] cMapInArr = prop.getCollator_map().getMap_inputs();
                for (int i = 0; i < cMapInArr.length; i++) {
                    if (cMapInArr[i].getInput_field().getSource_name().equals(nodeId)) {
                        cMapInArr[i].setInput_field(new CollatorInputField());
                    }

                }
            }
        }
    }
    private void deleteLinksSourceNode(String nodeId, List<Nodes> nodesList, Relations relations) {
        String targetNodeId = relations.getTargetNodeId();
        for (Nodes no : nodesList) {
            if (no.getNodeId().equals(targetNodeId)) {
                String nodeType = no.getType();
                Property[] propArr = no.getProperties();
                if (propArr == null){
                    return;
                }
                ArrayList<Property> arrayList = new ArrayList<>(Arrays.asList(propArr));
                // check if its of Collator(Array-based/Param-based) or not if
                // yes need to delete the corresponding entries in the collator_map
                if (configurationProperties.getCollatorType().equals(nodeType)) {
                    updatePropertyWithCollator(arrayList.iterator(), nodeId);
                }
                // check if its of Splitter(Copy-based/Param-based) or not if
                // yes need to delete the corresponding entries in the splitter_map
                else if (configurationProperties.getSplitterType().equals(nodeType)){
                    updatePropertyWithSplitter(arrayList.iterator(), nodeId);
                }
            }
        }

    }
    private void deletePropertiesFromDataMapperSourceNode(String sourceNodeId, Nodes node, String linkId, List<Relations> relationsList){
        logger.debug("1. For all NodeTypes input is SourceNodeId which is same as nodeId in Nodes ");
        if (node.getNodeId().equals(sourceNodeId) && node.getProperties()!=null &&node.getProperties().length != 0) {
            if (configurationProperties.getGdmType().equals(node.getType())) {
                node.getProperties()[0].getData_map().setMap_outputs(new MapOutput[0]);
            } else if (configurationProperties.getDatabrokerType().equals(node.getType())) {
                node.getProperties()[0].getData_broker_map().setMap_outputs(new DBMapOutput[0]);
                DBMapInput[] dbMapInArr = node.getProperties()[0].getData_broker_map()
                    .getMap_inputs();
                for (DBMapInput dbmInput : dbMapInArr) {
                    DBInputField dbiField = dbmInput.getInput_field();
                    dbiField.setChecked("NO");
                    dbiField.setMapped_to_field("");
                }
                // Collator map Output which have only one output link
                logger.debug(" Collator map Output which have only one output link ");
            } else if (configurationProperties.getCollatorType().equals(node.getType())) {
                logger.debug("Output Message Signature set as empty for Collator");
                node.getProperties()[0].getCollator_map().setOutput_message_signature("");
                node.getProperties()[0].getCollator_map()
                    .setMap_outputs(new CollatorMapOutput[0]);

                // Splitter Map Output which may have single or multiple link(s)

            } else if (configurationProperties.getSplitterType().equals(node.getType())) {
                logger.debug("splitterLink() : Begin  ");
                splitterLink(linkId, relationsList, node);
                logger.debug("splitterLink() : End ");
            }
        }
    }

    private void deletePropertiesFromDataMapperTargetNode(String targetNodeId, Nodes node, String linkId, List<Relations> relationsList){
        if (node.getNodeId().equals(targetNodeId) && node.getProperties()!=null && node.getProperties().length != 0) {
            logger.debug(" For all NodeTypes input is targetNodeId which is same as nodeId in Nodes");
            if (configurationProperties.getGdmType().equals(node.getType())) {
                node.getProperties()[0].getData_map().setMap_inputs(new MapInputs[0]);
            } else if (configurationProperties.getSplitterType().equals(node.getType())) {
                logger.debug("Input Message Signature set as empty for Splitter");
                node.getProperties()[0].getSplitter_map().setInput_message_signature("");
                node.getProperties()[0].getSplitter_map().setMap_inputs(new SplitterMapInput[0]);
            } else {
                if (null != node.getProperties()[0].getCollator_map().getMap_inputs()) {
                    List<String> targetNodeList = new ArrayList<>();
                    String source = null;
                    for (Relations rel : relationsList) {
                        if (rel.getLinkId().equals(linkId)) {
                            source = rel.getSourceNodeId();
                        }
                        if (rel.getTargetNodeId().equals(node.getNodeId())
                            && node.getType().equals(configurationProperties.getCollatorType())) {
                            targetNodeList.add(rel.getTargetNodeId());
                        }
                    }
                    // If the targetNodeId List size is having only one means collator contains one input and need to delete
                    // the entire mapInputs and Source table details
                    if (targetNodeList.size() == 0) {
                        logger.debug(" If the targetNodeId List size is having only one means collator contains one input.");
                        if (configurationProperties.getCollatorType().equals(node.getType())) {
                            node.getProperties()[0].getCollator_map().setMap_inputs(new CollatorMapInput[0]);
                        }
                        // If the targetNodeId List size is more than one means collator contains more than one inputs and need to
                        // delete the only deleted link related mapping details mapInputs and Source table details
                    } else {
                        logger.debug(" If the targetNodeId List size is more than one means collator contains more than one inputs.");
                        CollatorMapInput[] cmInput = node.getProperties()[0].getCollator_map()
                            .getMap_inputs();
                        List<CollatorMapInput> cim = new LinkedList<>(Arrays.asList(cmInput));
                        Iterator<CollatorMapInput> cmiItr = cim.iterator();
                        CollatorMapInput collatorMapInput;
                        while (cmiItr.hasNext()) {
                            collatorMapInput = cmiItr.next();
                            if (source.equals(collatorMapInput.getInput_field().getSource_name())) {
                                cmiItr.remove();
                                break;
                            }
                        }
                        CollatorMapInput newCmInput[] = cim.toArray(new CollatorMapInput[cim.size()]);
                        node.getProperties()[0].getCollator_map().setMap_inputs(newCmInput);
                    }
                }
            }
        }
    }
    /**

     * @return
     * This method will return success response as true/false
     */
    public boolean deleteLink(String userId, String solutionId, String linkId){
        logger.debug(" deleteLink() in SolutionService : Begin ");
        String cdumpFileName;
        String sourceNodeId;
        String targetNodeId;

        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<Nodes> nodesList = new ArrayList<>();
        String filePath = DEUtil.getCdumpPath(userId, configurationProperties.getToscaOutputFolder());
        try {

            cdumpFileName = DEUtil.getCdumpFileName(solutionId);
            File file = new File(filePath.concat(cdumpFileName));
            if (file.exists()) {
                Cdump cdump = mapper.readValue(file, Cdump.class);
                List<Relations> relationsList = cdump.getRelations();
                if (null == relationsList || relationsList.isEmpty()) {
                    logger.debug("relationsList is empty");
                    return true;
                } else {
                    Iterator<Relations> relationsItr = relationsList.iterator();
                    nodesList = cdump.getNodes();
                    // Identify link to delete + Data mapper node to delete it's properties field
                    while (relationsItr.hasNext()) {
                        Relations relation = relationsItr.next();
                        if (relation.getLinkId().equals(linkId)) {
                            sourceNodeId = relation.getSourceNodeId();
                            targetNodeId = relation.getTargetNodeId();
                            // delete properties field from DM
                            for (Nodes node : nodesList) {
                                // For all NodeTypes input is SourceNodeId which is same as nodeId in Nodes
                                deletePropertiesFromDataMapperSourceNode(sourceNodeId, node, linkId, relationsList);
                                // For all NodeTypes input is targetNodeId which is same as nodeId in Nodes
                                deletePropertiesFromDataMapperTargetNode(targetNodeId, node, linkId, relationsList);
                            }
                            // delete link details form relations list
                            relationsItr.remove();
                            break;
                        }
                    }
                    cdump.setNodes(nodesList);
                    String jsonInString = mapper.writeValueAsString(cdump);
                    DEUtil.writeDataToFile(filePath, cdumpFileName, jsonInString);
                }
            }
        } catch (Exception e) {
            logger.error(" Exception in deleteLink() in SolutionService", e);
        }
        logger.debug(" deleteLink() in SolutionService End ");
        return true;
    }
    private void splitterLink(String linkId, List<Relations> relationsList, Nodes node) {
        logger.debug( "splitterLink() : Begin  ");
        List<String> sourceNodeList = new ArrayList<>();
        String target = null;
        if (null != node.getProperties()[0].getSplitter_map().getMap_outputs()) {
            for (Relations rel : relationsList) {
                if (rel.getLinkId().equals(linkId)) {
                    target = rel.getTargetNodeId();
                }
                if (rel.getSourceNodeId().equals(node.getNodeId())
                    && node.getType().equals(configurationProperties.getSplitterType())) {
                    sourceNodeList.add(rel.getSourceNodeId());
                }
            }
            // If the sourceNodeId List size is having only one means splitter contains one output
            // and need to delete the entire mapOutput and target table details
            if (sourceNodeList.size() == 0) {
                if (configurationProperties.getSplitterType().equals(node.getType())) {
                    node.getProperties()[0].getSplitter_map().setMap_outputs(new SplitterMapOutput[0]);
                }
                // If the sourceNodeId List size is more than one means splitter  contains more than one output
                // and need to delete the only, deleted link related mapping details mapOutput and target table details
            } else {
                SplitterMapOutput[] spOutput = node.getProperties()[0].getSplitter_map().getMap_outputs();
                List<SplitterMapOutput> spMapOut = new LinkedList<>(Arrays.asList(spOutput));
                Iterator<SplitterMapOutput> spMapOutItr = spMapOut.iterator();
                SplitterMapOutput splitterMapOutput;
                while (spMapOutItr.hasNext()) {
                    splitterMapOutput = spMapOutItr.next();
                    if (target.equals(splitterMapOutput.getOutput_field().getTarget_name())) {
                        spMapOutItr.remove();
                        break;
                    }
                }
                SplitterMapOutput newSplOut[] = spMapOut.toArray(new SplitterMapOutput[spMapOut.size()]);
                node.getProperties()[0].getSplitter_map().setMap_outputs(newSplOut);
                logger.debug( "splitterLink() : Begin  ");
            }
        }
    }
    private boolean nodeExists(Nodes node, List<Nodes> nodes){
        ArrayList<String> idList = new ArrayList<>();
        for (Nodes n : nodes) {
            idList.add(n.getNodeId());
        }
        return idList.contains(node.getNodeId());
    }

    /**

     * @return
     * This method will return success/failure response in string format
     */
    public String addNode(String name, String userId, String solutionId, String cdumpVersion, Nodes node){
        String results = "";
        if (solutionId == null){
            logger.debug(" addNode() solutionId is null");
            return results;
        }

        String resultTemplate = "{\"success\" : \"%s\", \"errorDescription\" : \"%s\"}";
        try {
            if (StringUtils.isEmpty(node.getNodeId())){
                node.setNodeId(UUID.randomUUID().toString());
            }

            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
            String cdumpFileName = DEUtil.getCdumpFileName(solutionId);
            String path = DEUtil.getCdumpPath(userId, configurationProperties.getToscaOutputFolder());
            Cdump cdump = mapper.readValue(new File(path.concat(cdumpFileName)), Cdump.class);
            cdump.setCname(name);
            List<Nodes> nodes = cdump.getNodes();
            node.setProtoUri(getArtifactNexusUrl(node.getNodeSolutionId(), configurationProperties.getToscaInputArtifactType()));
            if (nodes != null) {
                if (nodeExists(node, nodes)) {
                    return String.format(resultTemplate, false, "Node Id already exists – cannot perform the requested operation");
                } else {
                    nodes.add(node);
                    cdump.setNodes(nodes);
                }
            } else {
                List<Nodes> nodeList = new ArrayList<>();
                nodeList.add(node);
                cdump.setNodes(nodeList);
            }
            results = String.format(resultTemplate, true, "");
            cdump.setSolutionId(solutionId);
            cdump.setVersion(cdumpVersion);
            mapper.writeValue(new File(path.concat(cdumpFileName)), cdump);
        } catch (Exception e) {
            logger.error( " Exception in addNode() ", e);
            results = String.format(resultTemplate, false, "Node not Added");
        }
        return results;
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

    public String getMatchingModels(String userId, String portType, String protoBufJsonString) throws Exception {
        logger.debug(" getMatchingModels() Begin ");
        String jsonInString;
        List<MatchingModel> matchingModelList = new ArrayList<>();
        List<MessageargumentList> inMsgArgList = mapper.readValue(protoBufJsonString, new TypeReference<ArrayList<MessageargumentList>>() {});
        matchingModelList = getPublicMatchingModels(portType,inMsgArgList);
        //Check if user private cache is old to recent.
//        Date lastExecutionTime = modelCacheForMatching.getUserPrivateModelUpdateTime(userId);
//        logger.debug(" lastExecutionTime : " + lastExecutionTime);
//        if(null != lastExecutionTime){
//            Long minutes = TimeUnit.MILLISECONDS.toMinutes((new Date()).getTime() - lastExecutionTime.getTime());
//            if (minutes > configurationProperties.getPrivateCacheRemovalTime()) { //If difference is more than configurable min then cache is too old.
//                modelCacheForMatching.removeUserPrivateModelCache(userId);
//            }
//        }
        //private models are 下架, should no be matched
        // get the private matching models
//        List<MatchingModel> privateMatchingModels = getPrivateMatchingModels(userId, portType,inMsgArgList);
//        for (MatchingModel matchingModel: privateMatchingModels) {
//            if (!matchingModelList.contains(matchingModel)) {
//                matchingModelList.add(matchingModel);
//            }
//        }
        jsonInString = mapper.writeValueAsString(matchingModelList);
        logger.debug(" getMatchingModels() End "+ mapper.writeValueAsString(matchingModelList));
        return jsonInString;
    }

    private List<MatchingModel> getPublicMatchingModels(String portType, List<MessageargumentList> inMsgArgList) throws Exception {
        logger.debug(" getPublicMatchingModels() Begin ");
        List<MatchingModel> matchingModelList;
        Map<KeyVO, List<ModelDetailVO>> publicModelCache = modelCacheForMatching.getPublicModelCache();
        if(publicModelCache.isEmpty()) {
            this.updateModelCache();
            publicModelCache = modelCacheForMatching.getPublicModelCache();
        }
        matchingModelList = getMatchingModels(portType, inMsgArgList, publicModelCache);
        logger.debug(" getPublicMatchingModels() End "+ mapper.writeValueAsString(matchingModelList));
        return matchingModelList;
    }

    private List<MatchingModel> getPrivateMatchingModels(String userId, String portType, List<MessageargumentList> inMsgArgList) throws Exception {
        logger.debug(" getPrivateMatchingModels() Begin ");
        List<MatchingModel> matchingModelList;

        Map<KeyVO, List<ModelDetailVO>> privateModelCache = modelCacheForMatching.getPrivateModelCache(userId);
        //for first time it will be null
        if(null == privateModelCache) {
            //populate user privateModelCache
            List<Solution> solutions = ummClient.getSolutionsByUser(userId, "下架");
            matchingModelService.populatePrivateModelCacheForMatching(userId,solutions);
            modelCacheForMatching.setUserPrivateModelUpdateTime(userId, new Date());
            privateModelCache = modelCacheForMatching.getPrivateModelCache(userId);
        }
        matchingModelList = getMatchingModels(portType, inMsgArgList, privateModelCache);

        logger.debug(" getPrivateMatchingModels() End "+ mapper.writeValueAsString(matchingModelList));
        return matchingModelList;
    }

    private void matchingModelList(Map<KeyVO, List<ModelDetailVO>> modelCache, KeyVO inKeyVO, List<MessageargumentList> inMsgArgList,
                                   List<MatchingModel> matchingModelList) throws IOException {
        List<ModelDetailVO> modelDetVOList = modelCache.get(inKeyVO);
        for(ModelDetailVO modeldetailVo : modelDetVOList){
            MessageBody[] messages = mapper.readValue(modeldetailVo.getProtobufJsonString(), MessageBody[].class);
            for(MessageBody messageBody	 : messages ){
                List<MessageargumentList> msgArgList = messageBody.getMessageargumentList();
                //e.g. [{"messageName":"ClassifyIn","messageargumentList":[{"role":"","name":"tok_corpus","tag":"1","type":"string"}]}]
                // every MessageargumentList in inMsgArgList must equals to models's MessageargumentList
                if(null != msgArgList && inMsgArgList.size() == msgArgList.size() && inMsgArgList.containsAll(msgArgList)) {
                    MatchingModel matchingModel = new MatchingModel();
                    matchingModel.setMatchingModelName(modeldetailVo.getModelName());
                    matchingModel.setTgifFileNexusURI(modeldetailVo.getTgifFileNexusURI());
                    if (!matchingModelList.contains(matchingModel)){
                        matchingModelList.add(matchingModel);
                    }
                }
            }
        }
    }

    private List<MatchingModel> getMatchingModels(String portType, List<MessageargumentList> inMsgArgList, Map<KeyVO,
        List<ModelDetailVO>> modelCache) throws IOException{
        logger.debug(" getMatchingModels() Begin ");
        logger.debug("inMsgArgList {}", new Gson().toJson(inMsgArgList) );
        List<MatchingModel> matchingModels = new ArrayList<>();
        if(null != inMsgArgList && !inMsgArgList.isEmpty()) {
            //Construct KeyVO
            KeyVO inKeyVO = new KeyVO();
            inKeyVO.setNestedMessage(DEUtil.isNested(inMsgArgList));
            inKeyVO.setNumberofFields(inMsgArgList.size());
            if(portType.equals(configurationProperties.getMatchingOutputPortType())) {
                inKeyVO.setPortType(configurationProperties.getMatchingInputPortType());
            }else if (portType.equals(configurationProperties.getMatchingInputPortType())) {
                inKeyVO.setPortType(configurationProperties.getMatchingOutputPortType());
            }
            //check if key is present in modelCacheForMatching
            if(modelCache.containsKey(inKeyVO)){
                matchingModelList(modelCache, inKeyVO, inMsgArgList, matchingModels);
            }
        }

        logger.debug(" getMatchingModels() End ");
        return matchingModels;
    }

    public String validateAddLinkInputs(String sourceNodeName, String linkId, String targetNodeName,
                                         String targetNodeCapabilityName, String sourceNodeId) {
        List<String> errList = new ArrayList<>();
        if (sourceNodeName == null) {
            errList.add("Source Node name missing" + " ");
        }
        if (linkId == null) {
            errList.add("Link missing" + " ");
        }
        if (targetNodeName == null) {
            errList.add("target Node name missing" + " ");
        }
        if (targetNodeCapabilityName == null) {
            errList.add("targetNodeCapabilityName mising" + " ");
            if (sourceNodeId == null) {
                errList.add("sourceNodeId mising" + " ");
            }
        }
        return errList.toString();
    }
    public boolean validateProperty(Property property) {
        logger.debug(" validateProperty()  : Begin");
        Gson gson = new Gson();
        boolean isValid;
        boolean map_inputsFlag = false;
        boolean map_outputsFlag = false;
        JsonParser parser = new JsonParser();
        try {
            // if link if b/w 2 models
            if (null == property || (null != property && null == property.getData_map())) {
                map_inputsFlag = true;
                map_outputsFlag = true;
            } else {
                DataMap dMap = property.getData_map();
                // validate JSON structure if link is b/w model & Data mapper
                parser.parse(gson.toJson(property));
                // validate map_inputs
                if (dMap.getMap_outputs().length == 0) {
                    MapInputs[] map_inputs = dMap.getMap_inputs();
                    if (map_inputs != null && map_inputs.length != 0) {
                        for (int i = 0; i < map_inputs.length; i++) {
                            if (map_inputs[i].getMessage_name() != null && map_inputs[i].getInput_fields() != null
                                && map_inputs[i].getInput_fields().length != 0) {
                                map_inputsFlag = true;
                                map_outputsFlag = true;
                            }
                        }
                    }
                }
                // validate map_outputs
                if (dMap.getMap_inputs().length == 0) {
                    MapOutput[] map_outputs = dMap.getMap_outputs();
                    if (map_outputs != null && map_outputs.length != 0) {
                        for (int i = 0; i < map_outputs.length; i++) {
                            if (map_outputs[i].getMessage_name() != null && map_outputs[i].getOutput_fields() != null
                                && map_outputs[i].getOutput_fields().length != 0) {
                                map_outputsFlag = true;
                                map_inputsFlag = true;
                            }
                        }
                    }
                }
            }
            isValid = map_outputsFlag && map_inputsFlag;
        } catch (Exception e) {
            logger.error(" Exception in validateProperty() ", e);
            isValid = false;
        }
        logger.debug(" validateProperty()  : End");
        return isValid;
    }

    public String fetchJsonTOSCA(String solutionId) {
        return readArtifact(solutionId, configurationProperties.getToscaInputArtifactType());
    }

    public String readArtifact(String solutionId, String artifactType){
        String result = "";
        String nexusURI = getArtifactNexusUrl(solutionId, artifactType);
        if (null != nexusURI && !"".equals(nexusURI)) {
            ByteArrayOutputStream byteArrayOutputStream = nexusArtifactClient.getArtifact(nexusURI);
            result = byteArrayOutputStream.toString();
        }
        return result;
    }

    public String fetchProtoBufJSON(String solutionId) {
        return readArtifact(solutionId, configurationProperties.getProtoArtifactType());
    }

    public void updateModelCache() throws Exception {
        logger.debug("updateModelCache() Begin ");
        Map<KeyVO, List<ModelDetailVO>> publicModelCache = new HashMap<>();
        modelCacheForMatching.setPublicModelCache(publicModelCache);
        List<Solution> dsModels = ummClient.getPublishedSolutions("上架");
        this.matchingModelService.populatePublicModelCacheForMatching(dsModels);
        logger.debug("updateModelCache() End ");
    }
}
