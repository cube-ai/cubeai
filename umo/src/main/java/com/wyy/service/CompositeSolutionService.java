package com.wyy.service;


import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.wyy.domain.*;
import com.wyy.domain.blueprint.*;
import com.wyy.domain.cdump.*;
import com.wyy.domain.cdump.collator.CollatorInputField;
import com.wyy.domain.cdump.collator.CollatorMapInput;
import com.wyy.domain.cdump.collator.CollatorMapOutput;
import com.wyy.domain.cdump.collator.CollatorOutputField;
import com.wyy.domain.cdump.databroker.*;
import com.wyy.domain.cdump.splitter.SplitterInputField;
import com.wyy.domain.cdump.splitter.SplitterMapInput;
import com.wyy.domain.cdump.splitter.SplitterMapOutput;
import com.wyy.domain.cdump.splitter.SplitterOutputField;
import com.wyy.util.ConfigurationProperties;
import com.wyy.util.DEUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.time.Instant;
import java.util.*;

@Service
public class CompositeSolutionService {
    private static final Logger logger = LoggerFactory.getLogger(CompositeSolutionService.class);

    private static final String SPLITTER_TYPE = "Splitter";

    private static final String COLLATOR_TYPE = "Collator";

    private static final String MLMODEL_TYPE = "MLModel";

    private static final String DATAMAPPER_TYPE = "DataMapper";

    private static final String DATABROKER_TYPE = "DataBroker";

    private static final String OPERATION_EXTRACTOR = "%PLUS%";

    private static final String FIRST_NODE_POSITION = "first";

    private static final String LAST_NODE_POSITION = "last";

    private final UmmClient ummClient;
    private final ConfigurationProperties configurationProperties;
    private final NexusArtifactClient nexusArtifactClient;

    public CompositeSolutionService(UmmClient ummClient, ConfigurationProperties configurationProperties, NexusArtifactClient nexusArtifactClient) {
        this.ummClient = ummClient;
        this.configurationProperties = configurationProperties;
        this.nexusArtifactClient = nexusArtifactClient;
    }

    /**
     * create composite solution including create an empty cdump file and an solution
     * create an artifact for cdump and upload the cdump file in Nexus repository
     * @param solution
     * @return
     * @throws Exception
     */
    public String createCompositeSolution(Solution solution) throws Exception {
        String response;
        try {
            if (solution.getUuid() == null){
                solution.setUuid(UUID.randomUUID().toString());
            }
            if (solution.getModelType() == null){
                solution.setModelType(ModelType.Prediction.getTypeName());
            }
            solution.setToolkitType(configurationProperties.getToolKit());
            solution.setActive(true);
            logger.info("1. Successfully created solution,  ID :  {} ", solution.getUuid());

            ummClient.createSolution(solution);
            this.createEmptyCdumpFile(solution.getAuthorLogin(), solution.getUuid());
            logger.info("2. Successfully updated the Cdump file for solution ID :  {} ", solution.getUuid());

            // 3 upload the cdump file in Nexus repository. (file name should be the same as previous one) and create artifact in db
            String path = DEUtil.getCdumpPath(solution.getAuthorLogin(), configurationProperties.getToscaOutputFolder());
            String cdumpFileName = DEUtil.getCdumpFileName(solution.getUuid());
            File cdumpFile = new File(path.concat(cdumpFileName));
            this.createArtifactAdd2Nexus(solution, cdumpFile, configurationProperties.getCdumpArtifactType());

            logger.info( "Successfully Created the Solution {0} & generated Solution ID : {1}", solution.getName(), solution.getUuid());
            response = "{\"uuid\":\"" + solution.getUuid() + "\",\"success\":\"true\",\"errorMessage\":\"\"}";
            logger.info("*********response: {}", response);

        } catch (Exception e) {
            logger.error("Error :  Exception in createCompositeSolution() Failed to create the Solution ",e);
            throw new Exception("  Exception in createCompositeSolution , 222,Failed to create the Solution");
        }
        return response;
    }

    /**
     * This method used for to create the empty cdump file
     * @param userId
     * This method accepts userId as Parameter
     * @return
     * This method returns success or failure response in string format
     *
     * If in case exception occurs it will throw Exception
     */
    public boolean createEmptyCdumpFile(String userId, String solutionId){
        logger.debug(" createEmptyCdumpFile() : Begin ");
        try {

            if (userId != null) {
                Cdump cdump = new Cdump();
                cdump.setCtime(Instant.now().toString());
                cdump.setProbeIndicator("false");
                cdump.setValidSolution(false);
                cdump.setSolutionId(solutionId);
                String emptyCdumpJson = new Gson().toJson(cdump);
                String path = DEUtil.createCdumpPath(userId, configurationProperties.getToscaOutputFolder());
                DEUtil.writeDataToFile(path, DEUtil.getCdumpFileName(solutionId), emptyCdumpJson);
                logger.debug(emptyCdumpJson);
            }
        } catch (Exception e) {
            logger.error(" Exception occured in createEmptyCdumpFile() ", e);
            throw new NoSuchElementException("Failed to createEmptyCdumpFile");
        }
        logger.debug(" createEmptyCdumpFile() : End ");
        return true;
    }

    /**
     * 1. update solution's cdump file
     * 2. upload the cdump file in Nexus Repositry
     * 3. update the existing cdump artifact
     * 4. store members (parent-child relationships) of composite solutions into UMM
     * @param solution

     * @return
     * @throws Exception
     * @throws IOException
     */
    public String saveCompositeSolutionCdump(Solution solution) throws Exception{

        logger.debug( " saveCompositeSolutionCdump() Begin ");

        String path = DEUtil.getCdumpPath(solution.getAuthorLogin(), configurationProperties.getToscaOutputFolder());

        String cdumpFileName = DEUtil.getCdumpFileName(solution.getUuid());

        Instant cur = Instant.now();
        try {
            File cdumpFile = new File(path.concat(cdumpFileName));
            ObjectMapper mapper = new ObjectMapper();
            Cdump cdump = mapper.readValue(cdumpFile, Cdump.class);
            if (null == cdump) {
                logger.debug("Error : Cdump file {} not found for Solution ID :   {} ", cdumpFileName, solution.getUuid());
            } else {
                // 1 Update the cdump file with mtime, solution name
                cdump.setCname(solution.getName());
                cdump.setMtime(cur.toString());
                cdump.setValidSolution(false);
                cdump.setVersion(solution.getVersion());
                Gson gson = new Gson();
                String payload = gson.toJson(cdump);
                DEUtil.writeDataToFile(path, cdumpFileName, payload);
                logger.info("2. Successfully updated the Cdump file for solution ID :  {} ", solution.getUuid());

                // 2 upload the cdump file in Nexus repository. (file name should be the same as previous one).
                this.addArtifact2Nexus(solution, cdumpFile);
                // 3 update the existing artifact
                this.updateCdumpArtirfactSize(solution.getUuid(), cdumpFile.length());
                //4 store members (parent-child relationships) of composite solutions into UMM.
                boolean flag = storeCompositeSolutionMembers(cdump);
                logger.info("4. Store members (parent-child relationships) of composite solutions into UUM : Status : {}", flag);
                //5 save solution
//                ummClient.updateSolution(solution);
            }
        } catch (Exception e) {
            logger.error("Error : Exception in updateCompositeSolution() : ",e);
            throw new Exception("  Exception in updateCompositeSolution() , 222, Failed to update the cdump:" + e);
        }
        logger.debug( " saveCompositeSolutionCdump() End ");

        return "{\"solutionId\": \"" + solution.getUuid() + "\", \"version\" : \"" + solution.getVersion() + "\" }";
    }
    private void updateCdumpArtirfactSize(String solutionUuid, long fileSize){
        List<Artifact> artifacts = ummClient.getArtifacts(solutionUuid, configurationProperties.getCdumpArtifactType());
        if (null!= artifacts && artifacts.size()>0){
            Artifact artifact = artifacts.get(0);
            artifact.setFileSize(fileSize);
            ummClient.deleteArtifact(artifact.getId());
            ummClient.createArtifact(artifact);
        }
    }
//    public String updateExistingSolution(Solution solution, Cdump cdump, String cdumpFileName, String path) {
//        logger.debug( " updateExistingSolution() Start ");
//        String result = "";
//        Instant cur = Instant.now();
//        if (null == cdump) {
//            logger.debug("Error : Cdump file not found for Solution ID :   {} ", solution.getUuid());
//        } else {
//            // 1 Update the cdump file with mtime
//            cdump.setMtime(cur.toString());
//            cdump.setValidSolution(false);
//            cdump.setVersion(solution.getVersion());
//            Gson gson = new Gson();
//            String payload = gson.toJson(cdump);
//            DEUtil.writeDataToFile(path, cdumpFileName, payload);
//            logger.debug("4. Successfully updated the Cdump file for solution ID :  {} ", solution.getUuid());
//            File cdumpFile = new File(path.concat(cdumpFileName));
//            // 2 upload the cdump file in Nexus Repositry. (file name should be the same as previous one).
//            String longUrl = this.addArtifact2Nexus(solution, cdumpFile);
//            // 3 update the existing artifact
//            List<Artifact> artfactsList = ummClient.getArtifacts(solution.getUuid(), configurationProperties.getCdumpArtifactType()).getBody();
//            if (null!= artfactsList && artfactsList.size()>0){
//                Artifact artifact = artfactsList.get(0);
//                artifact.setUrl(longUrl);
//                artifact.setModifiedDate(cur);
//                artifact.setFileSize(cdumpFile.length());
//                ummClient.updateArtifact(artifact);
//
//            }
//            // 4 Update the solution (i.e., to update the modified date of the solution).
//            solution.setModifiedDate(cur);
//            ummClient.updateSolution(solution);
//        }
//
//        logger.debug( " updateExistingSolution() End ");
//        return result;
//    }
    private Boolean createArtifactAdd2Nexus(Solution solution, File file, String type) {
        String longUrl= this.addArtifact2Nexus(solution, file);
        if (null == longUrl) {
            return false;
        }
        createArtifact(solution.getUuid(), file.getName(), type, longUrl, file.length());
        return true;
    }

    private String addArtifact2Nexus(Solution solution, File file){
        String shortUrl = getArtifactShortUrl(solution.getAuthorLogin(), solution.getUuid(), file.getName());
        return this.nexusArtifactClient.addArtifact(shortUrl, file);
    }

    private String getArtifactShortUrl(String userId, String solutionUuid, String name){
        return  userId+ "/" + solutionUuid + "/artifact/" + name;
    }

    private void createArtifact(String uuid, String name, String type, String longUrl, long size){
        Artifact artifact = new Artifact();
        artifact.setSolutionUuid(uuid);
        artifact.setName(name);
        artifact.setType(type);
        artifact.setUrl(longUrl);
        artifact.setFileSize(size);
        this.ummClient.createArtifact(artifact).getBody();
    }

    private boolean storeCompositeSolutionMembers(Cdump cdump) throws Exception {
        boolean flag = false;
        try {
            List<Nodes> nodes = cdump.getNodes();
            List<CompositeSolutionMap> compositeSolutionMaps = ummClient.getAllCompositeSolutionMaps().getBody();
            List<Long> parentChildList = new ArrayList<>();
            for (CompositeSolutionMap compositeSolutionMap : compositeSolutionMaps){
                if (cdump.getSolutionId().equals(compositeSolutionMap.getParentUuid())){
                    parentChildList.add(compositeSolutionMap.getId());
                }
            }
            //delete all existing CompositeSolutionMap of composite solution
            if (!parentChildList.isEmpty() & parentChildList != null) {
                for (Long childNode : parentChildList) {
                    ummClient.deleteCompositeSolutionMap(childNode);
                }
            }
            //create CompositeSolutionMap
            if (nodes != null && !nodes.isEmpty() ) {
                for (Nodes node : nodes) {
                    CompositeSolutionMap compositeSolutionMap = new CompositeSolutionMap();
                    compositeSolutionMap.setParentUuid(cdump.getSolutionId());
                    compositeSolutionMap.setChildUuid(node.getNodeSolutionId());
                    ummClient.createCompositeSolutionMap(compositeSolutionMap);
                }
                flag = true;
            }
        } catch (Exception e) {
            logger.error(" Exception Occured in storeCompositeSolutionMembers() ", e);
            throw new Exception(" Exception in storeCompositeSolutionMembers() , 333,Failed to add CompositeSolution Member");
        }
        return flag;
    }

    /**
     * delete composite solution,
     * 1. delete artifacts from db,
     * 2. delete artifacts from nexus, including cdump and model image
     * 3. delete member CompositeSolutionMaps
     * 4. delete solution
     *
     * @param solutionId
     * @return
     * @throws Exception
     */
    public boolean deleteCompositeSolution(String solutionId) throws Exception{
        logger.debug( " deleteCompositeSolution() Begin ");
        boolean result = false;
        try {
            List<Solution> solutions = ummClient.getSolutionsByUuid(solutionId);
            System.out.println("********solution size " + solutions.size());
            if (null != solutions && solutions.size() > 0) {
                System.out.println("************** " + solutions.get(0).toString());
                List<Artifact> artifacts = ummClient.getArtifacts(solutionId, null);
                for (Artifact artifact : artifacts) {
                    ummClient.deleteArtifact(artifact.getId());
                    nexusArtifactClient.deleteArtifact(artifact.getUrl());
                }
                deleteMemberCompositeSolutionMaps(solutionId);
                ummClient.deleteSolution(solutions.get(0).getId());
                this.deleteCdumpFile(solutions.get(0).getAuthorLogin(), solutionId);
                result = true;

            } else {
                logger.debug("Solution Not found :  {} ", solutionId);
            }
        } catch (Exception e) {
            logger.error(" Exception in deleteCompositeSolution() ", e);
            throw new Exception("Exception in deleteCompositeSolution() , 201, Not able to delete the Solution Version");
        }
        logger.debug( " deleteCompositeSolution() End ");
        return result;
    }

    public String deleteCdumpFile(String userId, String solutionId) {
        logger.debug( " closeCompositeSolution() : Begin ");
        String result = "";
        String resultTemplate = "{\"success\":\"%s\", \"errorMessage\":\"%s\"}";

        try {
            if (userId == null) {
                result = String.format(resultTemplate, false, "Cannot perform requested operation – User Id missing");
            } else {
                String cdumpFileName = DEUtil.getCdumpFileName(solutionId);
                String path = DEUtil.getCdumpPath(userId, configurationProperties.getToscaOutputFolder());
                logger.debug( "Delete file :  {} ", path.concat(cdumpFileName));
                DEUtil.deleteFile(path.concat(cdumpFileName));
                logger.debug( "Delete User Dir :  {} ", path);
                DEUtil.rmUserdir(path);
                result = String.format(resultTemplate, true, "");
            }
        } catch (Exception e) {
            result = String.format(resultTemplate, false, "Cannot Close the Composite Solution");
            logger.error(" Exception in closeCompositeSolution() ", e);
        }
        logger.debug( " closeCompositeSolution() : End ");
        return result;
    }

    public String clearCdumpFile(String userId, String solutionId) {
        logger.debug( " clearCdumpFile() : Begin ");
        String result = "";
        String resultTemplate = "{\"success\":\"%s\", \"errorMessage\":\"%s\"}";

        ObjectMapper mapper = new ObjectMapper();
        try {
            String cdumpFileName = DEUtil.getCdumpFileName(solutionId);
            String path = DEUtil.getCdumpPath(userId, configurationProperties.getToscaOutputFolder());
            Cdump cdump = mapper.readValue(new File(path.concat(cdumpFileName)), Cdump.class);
            if (cdump.getNodes() == null && cdump.getRelations() == null) {
                result = String.format(resultTemplate, false, "No Nodes and Edges are there to Clear");
            }
            if (cdump.getNodes() != null) {
                cdump.getNodes().clear();
                result = String.format(resultTemplate, true, "");
            }
            if (cdump.getRelations() != null) {
                cdump.getRelations().clear();
                result = String.format(resultTemplate, true, "");
            }

            cdump.setMtime(Instant.now().toString());
            Gson gson = new Gson();
            String jsonInString = gson.toJson(cdump);
            DEUtil.writeDataToFile(path, cdumpFileName, jsonInString);
        } catch (Exception e) {
            logger.error(" Exception in clearCdumpFile() ", e);
            result = String.format(resultTemplate, false, "");
        }
        logger.debug( " clearCdumpFile() : End ");
        return result;
    }

    public DSResult validateCompositeSolution(String userId, String solutionName, String solutionId, String version) throws Exception {
        DSResult resultVo = new DSResult();
        logger.debug( "validateCompositeSolution() : Begin ");
        String path = DEUtil.getCdumpPath(userId, configurationProperties.getToscaOutputFolder());
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Instant currentDate = Instant.now();
        try {
            Cdump cdump = null;
            // 1. Read the cdump file
            logger.debug( "1. Read the cdump file");
            String cdumpFileName = DEUtil.getCdumpFileName(solutionId);
            cdump = mapper.readValue(new File(path.concat(cdumpFileName)), Cdump.class);
            // 2. get the Nodes from the cdump file and collect the nodeId's
            logger.debug("2. get the Nodes from the cdump file and collect the nodeId's");
            List<Nodes> nodes = cdump.getNodes();
            List<Relations> relationsList = cdump.getRelations();

            // Check for the Nodes and Relations in the CDUMP is empty or not
            if (null != nodes && null != relationsList && !relationsList.isEmpty()) {
                resultVo = validateComposition(cdump);
                if(resultVo.isSuccess()){
                    // On successful validation generate the BluePrint file
                    // Update the Cdump file with validaSolution as true after successful validation conditions and Modified Time also
                    logger.debug("On successful validation generate the BluePrint file.");
                    cdump.setValidSolution(true);
                    cdump.setMtime(currentDate.toString());

                    String firstNodeId = getNodeIdForPosition(cdump, FIRST_NODE_POSITION);
                    Nodes firstNode = getNodeForId(nodes, firstNodeId);
                    if(firstNode.getType().equals(DATABROKER_TYPE)){
                        for(Relations relation : relationsList){
                            if(relation.getSourceNodeId().equals(firstNodeId)){
                                String dataBrokerTargetNodeId = relation.getTargetNodeId();
                                Nodes dataBrokerTargetNode = getNodeForId(nodes, dataBrokerTargetNodeId);
                                //get the protobuf file string for the target model
                                String protoUri =	dataBrokerTargetNode.getProtoUri();
                                ByteArrayOutputStream bytes = nexusArtifactClient.getArtifact(protoUri);
                                logger.debug("ByteArrayOutputStream of ProtoFile :" + bytes);
                                if(null != bytes) {
                                    String protobufFile = bytes.toString();
                                    logger.debug("protobufFile in String Format :" + protobufFile);
                                    //put protobufFile String in databroker
                                    Property[] prop = firstNode.getProperties();
                                    if(null != prop[0].getData_broker_map()){
                                        prop[0].getData_broker_map().setProtobufFile(protobufFile);
                                    }
                                    firstNode.setProperties(prop);
                                } else {
                                    logger.error(" Exception Occured in ValidateCompositeSolution() : Failed to Set the ProtoBuf File in DataBrokerMap ");
                                    throw new Exception("Exception Occured in ValidateCompositeSolution() : Failed to Set the ProtoBuf File in DataBrokerMap");
                                }
                                break;
                            }
                        }
                    }
                    Gson gson = new Gson();
                    String emptyCdumpJson = gson.toJson(cdump);
                    path = DEUtil.createCdumpPath(userId, configurationProperties.getToscaOutputFolder());
                    // Write Data to File
                    DEUtil.writeDataToFile(path, cdumpFileName, emptyCdumpJson);
                    File cdumpFile = new File(path.concat(cdumpFileName));
                    List<Solution> solutions = ummClient.getSolutionsByUuid(solutionId);
                    this.addArtifact2Nexus(solutions.get(0), cdumpFile);
                    createAndUploadBluePrint(userId, solutionId, solutionName, version,cdump);
                }
            } else if (null != nodes && nodes.size() == 1 && (null == relationsList || relationsList.isEmpty())) {
                Nodes no = nodes.get(0);
                if (no.getType().equals(MLMODEL_TYPE)) {
                    cdump.setValidSolution(true);
                    cdump.setMtime(currentDate.toString());
                    Gson gson = new Gson();
                    String emptyCdumpJson = gson.toJson(cdump);
                    path = DEUtil.createCdumpPath(userId, configurationProperties.getToscaOutputFolder());
                    // Write Data to File
                    DEUtil.writeDataToFile(path, cdumpFileName, emptyCdumpJson);
                    File cdumpFile = new File(path.concat(cdumpFileName));
                    List<Solution> solutions = ummClient.getSolutionsByUuid(solutionId);
                    this.addArtifact2Nexus(solutions.get(0), cdumpFile);
                    createAndUploadBluePrint(userId, solutionId, solutionName, version, cdump);
                } else {
                    resultVo.setSuccess(false);
                    resultVo.setErrorDescription("Invalid Composite Solution : \"" + no.getName() + "\" should be ML Model only");
                }
            } else {
                resultVo.setSuccess(false);
                resultVo.setErrorDescription("Invalid Composite Solution : Composite Solution should contain at least two models to connect");
            }
        } catch (Exception e) {
            logger.error(" Exception in validateCompositeSolution() in Service ", e);
            throw new Exception("Exception in validateCompositeSolution() , 333, Failed to create the Solution TgifArtifact");
        }
        logger.debug( " validateCompositeSolution() in Service  : End ");
        return resultVo;
    }
    private DSResult checkModelNode(String firstNodeId, String lastNodeId, String nodeId, Nodes node, List<Relations> relationsList){
        DSResult resultVo = new DSResult();
        //check whether its first node
        if(nodeId.equals(firstNodeId)){ // Node--
            //Then it should be source of only one link.
            int connectedCnt = getSourceCountForNodeId(relationsList, nodeId);
            if(connectedCnt == 1) {
                resultVo.setSuccess(true);
            } else { //Indicates its connected to multiple nodes, and validation fails.
                resultVo.setSuccess(false);
                resultVo.setErrorDescription("Invalid Composite Solution : MLModel \"" + node.getName() + "\" is connected to multiple Nodes");
            }
        } else if(node.getNodeId().equals(lastNodeId)){ // --Node
            //Then it should be target of only one link.
            int connectedCnt = getTargetCountForNodeId(relationsList, nodeId);
            if(connectedCnt == 1 ) {
                //Check for correct port connected
                boolean isCorrectPortsConnected = correctPortsConnected(relationsList, nodeId);
                if(isCorrectPortsConnected){
                    resultVo.setSuccess(true);
                }else {
                    resultVo.setSuccess(false);
                    resultVo.setErrorDescription("Invalid Composite Solution : Incorrect ports are connected to MLModel \"" + node.getName() + "\"");
                }
            } else {
                //Indicates multiple nodes are connected to the Node and vlaidation fails.
                resultVo.setSuccess(false);
                resultVo.setErrorDescription("Invalid Composite Solution : Multiple Nodes are connected to MLModel \""+ node.getName() +"\"");
            }

        } else { // --Node--
            //It should be source of one link and target of another one link.
            int connectedCnt = getTargetCountForNodeId(relationsList, nodeId);
            if(connectedCnt == 1 ) {
                connectedCnt = getSourceCountForNodeId(relationsList, nodeId);
                if(connectedCnt == 1) {
                    //Check for correct port connected
                    boolean isCorrectPortsConnected = correctPortsConnected(relationsList, nodeId);
                    if(isCorrectPortsConnected){
                        resultVo.setSuccess(true);
                        resultVo.setErrorDescription("");
                    }else {
                        resultVo.setSuccess(false);
                        resultVo.setErrorDescription("Invalid Composite Solution : Incorrect ports are connected to MLModel \"" + node.getName() + "\"");
                    }
                } else { //Indicates its connected to multiple nodes, and validation fails.
                    resultVo.setSuccess(false);
                    resultVo.setErrorDescription("Invalid Composite Solution : MLModel \"" + node.getName() + "\" is connected to multiple Nodes");
                }
            } else {
                //Indicates multiple nodes are connected to the Node and validation fails.
                resultVo.setSuccess(false);
                resultVo.setErrorDescription("Invalid Composite Solution : Multiple Nodes are connected to MLModel \""+ node.getName() +"\"");
            }
        }
        return resultVo;
    }

    private DSResult checkDataBrokerNode(String firstNodeId, String nodeId, List<Nodes> nodes, Nodes node, List<Relations> relationsList) {
        DSResult resultVo = new DSResult();
        //DataBroker should be the first Node
        if (nodeId.equals(firstNodeId)) { // Node--
            // Then it should be source of only one link.
            int connectedCnt = getSourceCountForNodeId(relationsList, nodeId);
            if (connectedCnt == 1) {
                // It should not be connected to Splitter
                boolean connectedToSplitter = isConnectedToSplitter(nodes, relationsList, nodeId);
                if (!connectedToSplitter) {
                    // It should not be connected to Collator
                    boolean connectedToCollator = isConnectedToCollator(nodes, relationsList, nodeId);
                    if (!connectedToCollator) {
                        // Validate mapping Details
                        String check = null;
                        Property[] propArr = node.getProperties();
                        if (propArr.length == 1) {
                            DataBrokerMap dbMap = propArr[0].getData_broker_map();
                            if (null != dbMap) {
                                DBMapInput[] dbmInArr = dbMap.getMap_inputs();
                                if (null != dbmInArr) {
                                    for (DBMapInput dbMapIn : dbmInArr) {
                                        if (null != dbMapIn.getInput_field()) {
                                            check = dbMapIn.getInput_field().getChecked();
                                            // DataBroker Input Table Mapping Details must contain at least one check box checked
                                            if (check.equals("YES")) {
                                                resultVo.setSuccess(true);
                                                resultVo.setErrorDescription("");
                                                break;
                                            } else {
                                                resultVo.setSuccess(false);
                                                resultVo.setErrorDescription("Invalid Composite Solution : DataBroker \"" + node.getName() + "\" Mapping Details should contains at least one check box selected");
                                            }
                                        } else {
                                            resultVo.setSuccess(false);
                                            resultVo.setErrorDescription("Invalid Composite Solution : DataBroker \""+ node.getName()+ "\" Input Mapping Input Fields should not be empty");
                                        }
                                    }
                                } else {
                                    resultVo.setSuccess(false);
                                    resultVo.setErrorDescription("Invalid Composite Solution : DataBroker \""+ node.getName() + "\" Input Mapping Details should not be empty");
                                }
                            } else {
                                resultVo.setSuccess(false);
                                resultVo.setErrorDescription("Invalid Composite Solution : DataBroker \""+ node.getName() + "\" Mapping Details should not be empty");
                            }
                        } else {
                            resultVo.setSuccess(false);
                            resultVo.setErrorDescription("Invalid Composite Solution : DataBroker \""+ node.getName() + "\" Mapping Details are Incorrect");
                        }
                    } else {
                        resultVo.setSuccess(false);
                        resultVo.setErrorDescription("Invalid Composite Solution : DataBroker \""+ node.getName() + "\" cannot be connected to Collator");
                    }
                } else {
                    resultVo.setSuccess(false);
                    resultVo.setErrorDescription("Invalid Composite Solution : DataBroker \"" + node.getName()+ "\" cannot be connected to Splitter");
                }
            } else { // Indicates its connected to multiple nodes, and validation fails.
                resultVo.setSuccess(false);
                resultVo.setErrorDescription("Invalid Composite Solution : DataBroker \"" + node.getName() + "\" is connected to multiple Nodes");
            }

        } else {
            resultVo.setSuccess(false);
            resultVo.setErrorDescription("Invalid Composite Solution : DataBroker \"" + node.getName() + "\" should be the first Node");
        }
        return resultVo;
    }
    private DSResult checkDataMapperNode(String firstNodeId, String lastNodeId, String nodeId, List<Nodes> nodes, Nodes node, List<Relations> relationsList) {
        DSResult resultVo = new DSResult();
        // Datamapper should not be the first node
        if (node.getNodeId().equals(firstNodeId)) {
            resultVo.setSuccess(false);
            resultVo.setErrorDescription("Invalid Composite Solution : DataMapper \""
                + node.getName() + "\" should not be the first Node");
        } else if (node.getNodeId().equals(lastNodeId)) {
            resultVo.setSuccess(false);
            resultVo.setErrorDescription("Invalid Composite Solution : DataMapper \""
                + node.getName() + "\" should not be the Last Node");
        } else {
            // Then it should be Target of only one link.
            int connectedCnt = getTargetCountForNodeId(relationsList, nodeId);
            if(connectedCnt == 1 ) {
                // Then it should be source of only one link.
                connectedCnt = getSourceCountForNodeId(relationsList, nodeId);
                if(connectedCnt == 1){
                    // It should not be connected to Splitter
                    boolean connectedToSplitter = isConnectedToSplitter(nodes, relationsList, nodeId);
                    if (!connectedToSplitter) {
                        // It should not be connected to Collator
                        boolean connectedToCollator = isConnectedToCollator(nodes, relationsList, nodeId);
                        if (!connectedToCollator) {
                            // Validate mapping
                            Property[] p = node.getProperties();
                            if (p.length == 1) {
                                resultVo.setSuccess(true);
                                resultVo.setErrorDescription("");
                            } else {
                                resultVo.setSuccess(false);
                                resultVo.setErrorDescription("Invalid Composite Solution : DataMapper \"" + node.getName()
                                    + "\" Mapping Details are Incorrect");
                            }
                        } else {
                            resultVo.setSuccess(false);
                            resultVo.setErrorDescription("Invalid Composite Solution : DataMapper \""
                                + node.getName() + "\" cannot be connected to Collator");
                        }
                    } else {
                        resultVo.setSuccess(false);
                        resultVo.setErrorDescription("Invalid Composite Solution : DataMapper \""
                            + node.getName() + "\" cannot be connected to Splitter");
                    }
                } else {
                    resultVo.setSuccess(false);
                    resultVo.setErrorDescription("Invalid Composite Solution : Multiple Nodes are connected to output port of DataMapper \""
                        + node.getName() + "\"");
                }
            } else { // Indicates its connected to multiple nodes, and validation fails.
                resultVo.setSuccess(false);
                resultVo.setErrorDescription("Invalid Composite Solution : DataMapper \""
                    + node.getName() + "\" is connected to multiple Nodes");
            }
        }
        return resultVo;
    }

    private DSResult checkSplitterNode(String firstNodeId, String lastNodeId, String nodeId, List<Nodes> nodes, Nodes node, List<Relations> relationsList) {
        DSResult resultVo = new DSResult();
        // Should not be the first node
        if (node.getNodeId().equals(firstNodeId)) {
            resultVo.setSuccess(false);
            resultVo.setErrorDescription("Invalid Composite Solution : Splitter \""
                + node.getName() + "\" should not be the first Node");
        } else if (node.getNodeId().equals(lastNodeId)) { // It should not be last node i.e, should be
            // connected either one or more nodes.
            resultVo.setSuccess(false);
            resultVo.setErrorDescription("Invalid Composite Solution : Splitter \""
                + node.getName() + "\" should not be the Last Node");
        }else if(null != node.getProperties()[0].getSplitter_map().getMap_outputs() && node.getProperties()[0].getSplitter_map().getMap_outputs().length != 0){
            SplitterMapOutput mapOutput[] = node.getProperties()[0].getSplitter_map().getMap_outputs();
            for(SplitterMapOutput smo : mapOutput){
                String errorIndicator = smo.getOutput_field().getError_indicator();
                if(errorIndicator.equals("false")){
                    resultVo.setSuccess(false);
                    resultVo.setErrorDescription("Invalid Composite Solution : Splitter \""
                        + node.getName() + "\" Mapping Contains Error Indicator as False");
                    break;
                }
            }
        }else {
            // It should be target of only one link.
            int connectedCnt = getTargetCountForNodeId(relationsList, nodeId);
            Nodes sourceNode = null;
            Nodes targetNode = null;
            if(connectedCnt == 1 ) {
                // Source should not be Splitter/collator/DataBroker/DataMapper i.e, ML Model
                boolean connectedToTool = isSourceConnectedToTool(relationsList, nodes, sourceNode, nodeId);

                if(!connectedToTool){
                    // Target Should not be Splitter/collator/DataBroker/DataMapper i.e, ML Model
                    connectedToTool = isTargetConnectedToTool(relationsList, nodes, targetNode, nodeId);

                    if(!connectedToTool){
                        resultVo.setSuccess(true);
                        resultVo.setErrorDescription("");
                    } else {
                        resultVo.setSuccess(false);
                        resultVo.setErrorDescription("Invalid Composite Solution : Splitter \""
                            + node.getName() + "\" should not be connected to DS tool : \"" + targetNode.getName() + "\"");
                    }
                } else {
                    resultVo.setSuccess(false);
                    resultVo.setErrorDescription("Invalid Composite Solution : DS Tool \""
                        + sourceNode.getName() + "\" should not be connected to Splitter : \"" + node.getName() + "\"");
                }

            } else {
                resultVo.setSuccess(false);
                resultVo.setErrorDescription("Invalid Composite Solution : Multiple Nodes are connected to Input Port of Splitter \""
                    + node.getName() + "\"");
            }

        }
        return resultVo;
    }

    private boolean isTargetConnectedToTool(List<Relations> relationsList, List<Nodes> nodes, Nodes targetNode, String nodeId){
        boolean connectedToTool = false;
        for(Relations rel: relationsList){
            if(rel.getSourceNodeId().equals(nodeId)){
                targetNode = getNodeForId(nodes, rel.getTargetNodeId());
                if(!targetNode.getType().equals(MLMODEL_TYPE)){
                    connectedToTool = true;
                    break;
                }
            }
        }
        return connectedToTool;
    }
    private boolean isSourceConnectedToTool(List<Relations> relations, List<Nodes> nodes, Nodes sourceNode, String nodeId){
        boolean connectedToTool = false;

        for(Relations rel: relations){
            if(rel.getTargetNodeId().equals(nodeId)){
                sourceNode = getNodeForId(nodes, rel.getSourceNodeId());
                if(!sourceNode.getType().equals(MLMODEL_TYPE)){
                    connectedToTool = true;
                    break;
                }
            }
        }
        return connectedToTool;
    }

    private DSResult checkCollatorNode(String firstNodeId, String lastNodeId, String nodeId, List<Nodes> nodes, Nodes node, List<Relations> relationsList) {
        DSResult resultVo = new DSResult();
        // Should not be the first node
        if (node.getNodeId().equals(firstNodeId)) {
            resultVo.setSuccess(false);
            resultVo.setErrorDescription("Invalid Composite Solution : Collator \""
                + node.getName() + "\" should not be the first Node");
        } else if (node.getNodeId().equals(lastNodeId)) { // It should not be last node i.e, should be
            // connected either one or more nodes.
            resultVo.setSuccess(false);
            resultVo.setErrorDescription("Invalid Composite Solution : Collator \""
                + node.getName() + "\" should not be the Last Node");
        }else if(null != node.getProperties()[0].getCollator_map().getMap_inputs() && node.getProperties()[0].getCollator_map().getMap_inputs().length != 0){
            CollatorMapInput mapInput[] = node.getProperties()[0].getCollator_map().getMap_inputs();
            for(CollatorMapInput cmi : mapInput){
                String errorIndicator = cmi.getInput_field().getError_indicator();
                if(errorIndicator.equals("false")){
                    resultVo.setSuccess(false);
                    resultVo.setErrorDescription("Invalid Composite Solution : Collator \""
                        + node.getName() + "\" Mapping Contains Error Indicator as False");
                    break;
                }
            }
        }else {
            // It should be source of only one link.
            int connectedCnt = getSourceCountForNodeId(relationsList, nodeId);
            Nodes sourceNode = null;
            Nodes targetNode = null;
            if(connectedCnt == 1){
                // Source should not be Splitter/collator/DataBroker/DataMapper i.e, ML Model
                boolean connectedToTool = isSourceConnectedToTool(relationsList, nodes, sourceNode, nodeId);
                if(!connectedToTool){
                    // Target Should not be Splitter/collator/DataBroker/DataMapper i.e, ML Model
                    connectedToTool = isTargetConnectedToTool(relationsList, nodes, targetNode, nodeId);
                    if(!connectedToTool){
                        resultVo.setSuccess(true);
                        resultVo.setErrorDescription("");
                    } else {
                        resultVo.setSuccess(false);
                        resultVo.setErrorDescription("Invalid Composite Solution : Collator \""
                            + node.getName() + "\" should not be connected to DS tool : \"" + sourceNode.getName() + "\"");
                    }
                } else {
                    resultVo.setSuccess(false);
                    resultVo.setErrorDescription("Invalid Composite Solution : DS Tool \""
                        + sourceNode.getName() + "\" should not be connected to Collator : \"" + node.getName() + "\"");
                }

            } else {
                resultVo.setSuccess(false);
                resultVo.setErrorDescription("Invalid Composite Solution : Multiple Nodes are connected to output port of Collator \""
                    + node.getName() + "\"");
            }
        }
        return resultVo;
    }
    private DSResult validateEachNode(Cdump cdump) {
        DSResult resultVo = new DSResult();
        List<Nodes> nodes = cdump.getNodes();
        List<Relations> relationsList = cdump.getRelations();
        //Get the First and Last Model
        String firstNodeId = getNodeIdForPosition(cdump, FIRST_NODE_POSITION);
        String lastNodeId = getNodeIdForPosition(cdump,LAST_NODE_POSITION);
        //For each Node :
        for(Nodes node : nodes){
            //get the node type
            String nodeType = node.getType();
            String nodeId = node.getNodeId();
            switch (nodeType) {
                case MLMODEL_TYPE :
                    resultVo = checkModelNode(firstNodeId, lastNodeId, nodeId, node, relationsList);
                    break;
                case DATABROKER_TYPE :
                    resultVo = checkDataBrokerNode(firstNodeId, nodeId, nodes, node, relationsList);
                    break;
                case DATAMAPPER_TYPE :
                    resultVo = checkDataMapperNode(firstNodeId, lastNodeId, nodeId, nodes, node, relationsList);
                    break;
                case SPLITTER_TYPE:
                    resultVo = checkSplitterNode(firstNodeId, lastNodeId, nodeId, nodes, node, relationsList);
                    break;
                case COLLATOR_TYPE :
                    resultVo = checkCollatorNode(firstNodeId, lastNodeId, nodeId, nodes, node, relationsList);
                    break;
                default:
            }
            if (!resultVo.isSuccess()) {
                break;
            }
        }

        return resultVo;
    }
    private boolean isConnectedToNode(List<Nodes> nodes, List<Relations> relationsList, String nodeId, String nodeType) {
        boolean isConnected = false;
        List<String> collatorNodeIds = new ArrayList<String>();
        //1. Check if composite solution have splitter
        for(Nodes n : nodes){
            if(n.getType().equals(nodeType)){
                collatorNodeIds.add(n.getNodeId());
            }
        }

        if(!collatorNodeIds.isEmpty()){ //Collator exists in the composite solution
            //From relationsList get the links where source Node id is databroker
            for(Relations link : relationsList){
                if(link.getSourceNodeId().equals(nodeId)){
                    //check if target node id is splitter id
                    for(String targetId : collatorNodeIds){
                        if(link.getTargetNodeId().equals(targetId)){
                            isConnected = true;
                        }
                    }
                }
            }
        }
        return isConnected;
    }

    private boolean isConnectedToCollator(List<Nodes> nodes, List<Relations> relationsList, String nodeId) {
        return isConnectedToNode(nodes, relationsList, nodeId, COLLATOR_TYPE);
    }
    private boolean isConnectedToSplitter(List<Nodes> nodes, List<Relations> relationsList, String nodeId) {
        return isConnectedToNode(nodes, relationsList, nodeId, SPLITTER_TYPE);
    }

    private int getTargetCountForNodeId(List<Relations> relationsList, String nodeId) {
        int connectedCnt = 0;
        for(Relations rel : relationsList){
            String sourceNodeId = rel.getTargetNodeId();
            if(nodeId.equals(sourceNodeId)){
                connectedCnt++;
            }
        }
        return connectedCnt;
    }

    private int getSourceCountForNodeId(List<Relations> relationsList, String nodeId) {
        int connectedCnt = 0;
        for(Relations rel : relationsList){
            String sourceNodeId = rel.getSourceNodeId();
            if(nodeId.equals(sourceNodeId)){
                connectedCnt++;
            }
        }
        return connectedCnt;
    }

    /**
     * @param cdump
     * @return
     */
    private DSResult validateComposition(Cdump cdump) {
        DSResult result = new DSResult();
        List<Nodes> nodes = cdump.getNodes();
        List<Relations> relationsList = cdump.getRelations();
        //check if any isolated node
        List<String> isolatedNodesName = getIsolatedNodesName(nodes, relationsList);
        if(isolatedNodesName.isEmpty()){
            //Composite solution should have only one first Node
            List<String> firstNodeNames = getNodesForPosition(cdump, FIRST_NODE_POSITION);
            if(firstNodeNames.size() == 1){
                //Composite solution should have only one last Node
                List<String> lastNodeNames = getNodesForPosition(cdump, LAST_NODE_POSITION);
                if(lastNodeNames.size() == 1){
                    result = validateEachNode(cdump);
                } else {
                    result.setSuccess(false);
                    result.setErrorDescription("Invalid Composite Solution : Nodes " + lastNodeNames + " are not connected");
                }
            } else if (firstNodeNames.size() == 0) {
                result.setSuccess(false);
                result.setErrorDescription("Invalid Composite Solution : Cyclic Graph is not permitted");
            } else {
                result.setSuccess(false);
                result.setErrorDescription("Invalid Composite Solution : Nodes " + firstNodeNames + " are not connected");
            }
        } else {
            result.setSuccess(false);
            result.setErrorDescription("Invalid Composite Solution : " + isolatedNodesName + " are isolated nodes");
        }
        return result;
    }

    private List<String> getIsolatedNodesName(List<Nodes> nodes, List<Relations> relationsList) {
        //1. get the unique nodeids list from the relation
        HashSet<String> relNodeIdSet = new HashSet<>();
        for (Relations rhs : relationsList) {
            relNodeIdSet.add(rhs.getSourceNodeId());
            relNodeIdSet.add(rhs.getTargetNodeId());
        }
        //2. Check Isolated nodes
        List<String> isolatedNodesName = new ArrayList<>();
        for(Nodes node : nodes){
            if (!relNodeIdSet.contains(node.getNodeId())) {
                isolatedNodesName.add(node.getName());
            }
        }
        return isolatedNodesName;
    }

    private boolean correctPortsConnected(List<Relations> relationsList, String nodeId) {
        boolean isCorrectPortsConnected =  true;//ValidateCorrectPortsConnected(cdump);
        String srcOperation = null;
        String trgOperation = null;
        for (Relations rel : relationsList) {
            if(nodeId.equals(rel.getSourceNodeId())){
                srcOperation = rel.getSourceNodeRequirement().replace("+", OPERATION_EXTRACTOR);
                srcOperation = srcOperation.split(OPERATION_EXTRACTOR)[0];
                //Now check if same input port is connected or not and its not the first node.
                boolean isSameportConnected = false;
                for (Relations rel2 : relationsList) {
                    if(nodeId.equals(rel2.getTargetNodeId())){ //Node is target of some other link
                        trgOperation = rel2.getTargetNodeCapability().replace("+", OPERATION_EXTRACTOR);
                        trgOperation = trgOperation.split(OPERATION_EXTRACTOR)[0];
                        if(trgOperation.equals(srcOperation)){
                            isSameportConnected = true;
                            break;
                        }
                    }
                }
                if(!isSameportConnected){
                    isCorrectPortsConnected = false;
                    break;
                }
            }
        }
        return isCorrectPortsConnected;
    }

    private String createAndUploadBluePrint(String userId, String solutionId, String solutionName, String version, Cdump cdump) throws Exception {
        logger.debug("On successful validation generate the BluePrint file");
        BluePrint bluePrint = new BluePrint();
        bluePrint.setName(solutionName);
        bluePrint.setVersion(version);
        bluePrint.setProbeIndicator(getProbeIndicatorList(cdump.getProbeIndicator())); // In cdump probeIndicator is a string, in blueprint it should not be an array just a string
        bluePrint.setInputPorts(getContainerList(cdump));
        logger.debug("8. Get the nodes from Cdump file & set the required details in the blueprint nodes");
        bluePrint.setNodes(getBlueprintNodes(cdump));

        logger.debug("20. Create the BlueprintArtifact");
        try {
            this.createBluePrintArtifact(solutionId, bluePrint, DEUtil.getCdumpPath(userId, configurationProperties.getToscaOutputFolder()), userId);
        } catch (Exception e) {
            logger.error("Error : Exception in validateCompositeSolution() : Failed to create the Solution TgifArtifact ",e);
            throw new Exception("  Exception in validateCompositeSolution() , 333, Failed to create the Solution TgifArtifact");
        }
        return "{\"success\" : \"true\", \"errorDescription\" : \"\"}";
    }
    private List<ProbeIndicator> getProbeIndicatorList(String probeIndicator){
        ProbeIndicator pIndicator = new ProbeIndicator();
        pIndicator.setValue(probeIndicator);
        List<ProbeIndicator> probeLst = new ArrayList<>();
        probeLst.add(pIndicator);
        return probeLst;
    }
    private List<Container> getContainerList(Cdump cdump){
        List<Container> containerList = new ArrayList<>();
        Container container = new Container();
        BaseOperationSignature bos = new BaseOperationSignature();
        String opearion;
        List<Nodes> nodes = cdump.getNodes();
        List<Relations> relationsList = cdump.getRelations();
        Set<String> sourceNodeId = new HashSet<>();
        Set<String> targetNodeId = new HashSet<>();
        // if relations not equal to null i.e its contains more than one model in canvas
        if(null != relationsList && !relationsList.isEmpty()){
            for (Relations rlns : relationsList) {
                sourceNodeId.add(rlns.getSourceNodeId());
                targetNodeId.add(rlns.getTargetNodeId());
            }
            sourceNodeId.removeAll(targetNodeId);
            for (Relations rltn : relationsList) {
                if (sourceNodeId.contains(rltn.getSourceNodeId())) {
                    opearion = rltn.getSourceNodeRequirement().replace("+", OPERATION_EXTRACTOR);
                    opearion = opearion.split(OPERATION_EXTRACTOR)[0];
                    bos.setOperationName(opearion);
                    container.setBaseOperationSignature(bos);
                    String containerName = rltn.getSourceNodeName();
                    container.setContainerName(containerName);
                    containerList.add(container);
                }
            }
        }else{
            // canvas contains only one model which is ML Model only
            for(Nodes no : nodes){
                String reqOperationName = no.getRequirements()[0].getCapability().getId();
                bos.setOperationName(reqOperationName);
                container.setBaseOperationSignature(bos);
                container.setContainerName(no.getName());
                containerList.add(container);
            }
        }
        return containerList;
    }
    private List<BluePrintNode> getBlueprintNodes(Cdump cdump){
        List<BluePrintNode> bpnodes = new ArrayList<>();
        List<Nodes> cdumpNodes = cdump.getNodes();
        // 9. Extract NodeId, NodeName,NodeSolutionId,NodeVersion
        logger.debug("9. Extract NodeId, NodeName,NodeSolutionId");
        for (Nodes n : cdumpNodes) {
            // 13. Set the values in the bluePrint Node
            logger.debug("13. Set the values in the bluePrint Node");
            BluePrintNode bpnode = getBluePrintNode(n.getName(), dockerImageURL(n), n);
            //Set operation_signature_list
            bpnode.setOperationSignatureLists(getOperationSignatureList(cdump, n));
            // 14. Add the nodedetails to bluepring nodes list
            logger.debug("14. Add the nodedetails to blueprint nodes list");
            bpnodes.add(bpnode);
        }
        return bpnodes;
    }
    private String dockerImageURL(Nodes n){
        if (n.getType().equalsIgnoreCase(DATAMAPPER_TYPE)) {
            //TODO data mapper for future
//                logger.debug("GDM Found :  {} ", n.getNodeId());
//                // For Generic Data Mapper, get the dockerImageUrl by deploying the GDM Construct the image for the Generic Data mapper
//                logger.debug("For Generic Data Mapper, get the dockerImageUrl by deploying the GDM Construct the image for the Generic Data mapper");
//                dockerImageURL = gdmService.createDeployGDM(cdump, n.getNodeId(), userId);
//                if (null == dockerImageURL) {
//                    logger.debug("Error : Issue in createDeployGDM() : Failed to create the Solution TgifArtifact ");
//                    throw new Exception("  Issue in createDeployGDM() ", "333",
//                        "Issue while crearting and deploying GDM image");
//                }
        } else if(n.getType().equalsIgnoreCase(DATABROKER_TYPE)) {
            //TODO data broker for future
//                logger.debug("DataBroker Found :  {} ", n.getType());
//                dockerImageURL = dbService.getDataBrokerImageURI(n);
//                if (null == dockerImageURL) {
//                    logger.debug("Error : Issue in createDeployDataBroker() : Failed to create the Solution TgifArtifact ");
//                    throw new Exception("  Issue in createDeployGDM() ", "333",
//                        "Issue while crearting and deploying DataBroker image");
//                }
        } else {
            // Else for basic models, upload the image and get the uri
            // 12. Get the DockerImageUrl
            logger.debug("12. Get the DockerImageUrl");
            return getDockerImageURL(n.getNodeSolutionId());
        }
        return "";
    }
    private BluePrintNode getBluePrintNode(String nodeName, String dockerImageURL, Nodes n){
        BluePrintNode bpnode = new BluePrintNode();
        bpnode.setContainerName(nodeName);
        bpnode.setDockerImageURL(dockerImageURL);
        String node_type = (null != n.getType()? n.getType().trim() : "");
        bpnode.setNodeType(node_type);

        // Check for the Node type is DataBroker or not
        if (node_type.equals(DATABROKER_TYPE)) {
            // Need to set all the values of DataBrokerMap Get the Property[] from Nodes
            BPDataBrokerMap bpdbMap = getDataBrokerDetails(n);
            bpnode.setBpDataBrokerMap(bpdbMap);
        }

        // check for the Node type is Splitter or not
        if(node_type.equals(SPLITTER_TYPE)){
            // Need to set all the values of SplitterMap Get the Property[] from Nodes
            BPSplitterMap bpsMap = getSplitterDetails(n);
            bpnode.setBpSplitterMap(bpsMap);
            bpnode.setDockerImageURL("");
        }
        // check for the Node type is Collator or not
        if(node_type.equals(COLLATOR_TYPE)){
            // Need to set all the values of CollatorMap Get the Property[] from Nodes
            BPCollatorMap bpcMap = getCollatorDetails(n);
            bpnode.setBpCollatorMap(bpcMap);
            bpnode.setDockerImageURL("");
        }

        String protoUri = n.getProtoUri();
        switch (n.getType()){
            case COLLATOR_TYPE:
                bpnode.setProtoUri("");
                break;
            case  SPLITTER_TYPE:
                bpnode.setProtoUri("");
                break;
            default:
                bpnode.setProtoUri(protoUri);
        }
        return bpnode;
    }
    private List<OperationSignatureList> getOperationSignatureList(Cdump cdump, Nodes n){
        List<Capabilities> capabilities = Arrays.asList(n.getCapabilities());
        List<Relations> relationsList = cdump.getRelations();
        List<Container> containerLst;
        List<OperationSignatureList> oslList = new ArrayList<>();
        OperationSignatureList osll;
        NodeOperationSignature nos;
        List<Container> connectedToList;
        connectedToList = new ArrayList<>();
        // If the relations are null or empty i.e single ML model is there in Canvas
        if(null != relationsList && !relationsList.isEmpty()){
            //Get the connected port
            String connectedPort = getConnectedPort(cdump.getRelations(), n.getNodeId());
            for(Capabilities c : capabilities ){
                String targetNodeOperation = c.getTarget().getId();
                if(targetNodeOperation.equals(connectedPort)){
                    osll = new OperationSignatureList();
                    nos = new NodeOperationSignature();
                    nos.setOperationName(targetNodeOperation);
                    nos.setInputMessageName(c.getTarget().getName()[0].getMessageName());
                    //TODO NodeOperationSignature input_message_name should have been array, as operation can have multiple input messages.
                    //Its seems to be some gap
                    nos.setOutputMessageName(getOutputMessage(n.getRequirements(), targetNodeOperation));
                    osll.setNodeOperationSignature(nos);
                    containerLst = getRelations(cdump, n.getNodeId());
                    osll.setConnectedTo(containerLst);
                    oslList.add(osll);
                }
            }
        }else {
            // canvas contains only one Model which is ML Model only
            for(Capabilities c : capabilities ){
                String nodeOperationName = c.getTarget().getId();
                osll = new OperationSignatureList();
                nos = new NodeOperationSignature();
                nos.setOperationName(nodeOperationName);
                nos.setInputMessageName(c.getTarget().getName()[0].getMessageName());
                nos.setOutputMessageName(getOutputMessage(n.getRequirements(), nodeOperationName));
                osll.setNodeOperationSignature(nos);
                osll.setConnectedTo(connectedToList);
                oslList.add(osll);
            }
        }
        return oslList;
    }
    private void createBluePrintArtifact(String solutionId, BluePrint bluePrint, String path, String userId) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(Include.NON_NULL);
        // 15. Write Data to bluePrint file and construct the name of the file
        logger.debug("15. Write Data to bluePrint file and construct the name of the file");
        String bluePrintFileName = "BluePrint" + "-" + solutionId + ".json";
        // 16. Convert bluePrint to json
        logger.debug("16. Convert bluePrint to json");
        String bluePrintJson = mapper.writeValueAsString(bluePrint);
        // 17. Create and write details to file
        logger.debug("17. Create and write details to file");
        DEUtil.writeDataToFile(path, bluePrintFileName, bluePrintJson);
        File bluePrintFile = new File(path.concat(bluePrintFileName));
        // 18. Get the TgifArtifact Data
        logger.debug("18. Get the TgifArtifact Data");
        List<Solution> solutions = ummClient.getSolutionsByUuid(solutionId);
        Solution solution = solutions.get(0);
        String nexusUrl = this.addArtifact2Nexus(solution, bluePrintFile);

        List<Artifact> artifacts = ummClient.getArtifacts(solutionId, configurationProperties.getBlueprintArtifactType());

        if (artifacts!=null && artifacts.size()>0) {
            // 24. Update the TgifArtifact which is already exists as BP
            Artifact artifact = artifacts.get(0);
            artifact.setFileSize(bluePrintFile.length());
            artifact.setModifiedDate(Instant.now());
            artifact.setUrl(nexusUrl);
            ummClient.deleteArtifact(artifact.getId());
            ummClient.createArtifact(artifact);
            logger.debug("24. Updated the ArtifactTypeCode BP which is already exists");
        } else {
            this.createArtifact(solutionId, bluePrintFileName, configurationProperties.getBlueprintArtifactType(), nexusUrl, bluePrintFile.length());
            logger.debug("Successfully created the artifact for the BluePrint for the solution : " + solutionId);
        }
    }

    private BPCollatorMap getCollatorDetails(Nodes n) {
        Property[] prop = n.getProperties();
        ArrayList<Property> propsList = new ArrayList<>(Arrays.asList(prop));
        String collator_type = null;
        String output_message_signature = null;

        BPCollatorMap bpcMap = new BPCollatorMap();

        List<CollatorMapInput> collatorMapInputLst = new ArrayList<>();
        List<CollatorMapOutput> collatorMapOutputLst = new ArrayList<>();

        // Iterate over the PropertyList of the Node from Cdump
        if (null != propsList) {
            for (Property coprops : propsList) {
                if (null != coprops.getCollator_map()) {
                    // Set all the values from Property List of cdump to Blueprint CollatorMap object
                    collator_type = coprops.getCollator_map().getCollator_type();
                    output_message_signature = coprops.getCollator_map().getOutput_message_signature();

                    bpcMap.setCollator_type(collator_type);
                    bpcMap.setOutput_message_signature(output_message_signature);

                    // Get the CollatorMapInput[] from CollatorMap from the Cdump file
                    // Check if the collator_type is Param based then mapInputs
                    // and mapOutputs should be populated, else not
                    if (collator_type.equals(configurationProperties.getDefaultCollatorType())) {
                        bpcMap.setMap_inputs(null);
                        bpcMap.setMap_outputs(null);
                    } else {
                        if (null != coprops.getCollator_map().getMap_inputs() && coprops.getCollator_map().getMap_inputs().length != 0) {
                            CollatorMapInput[] coMapIn = coprops.getCollator_map().getMap_inputs();
                            // Convert CollatorMapInput[] to List
                            ArrayList<CollatorMapInput> cmiLst = new ArrayList<CollatorMapInput>(
                                Arrays.asList(coMapIn));
                            // Iterate over the CollatorMapInput List of the Cdump file
                            for (CollatorMapInput cmi : cmiLst) {

                                CollatorInputField coInField = new CollatorInputField();
                                CollatorMapInput coMapInput = new CollatorMapInput();

                                // set the all values into CollatorInputField of BluePrint File
                                coInField.setParameter_name(cmi.getInput_field().getParameter_name());
                                coInField.setParameter_tag(cmi.getInput_field().getParameter_tag());
                                coInField.setParameter_type(cmi.getInput_field().getParameter_type());
                                coInField.setParameter_role(cmi.getInput_field().getParameter_role());
                                coInField.setSource_name(cmi.getInput_field().getSource_name());
                                coInField.setMessage_signature(cmi.getInput_field().getMessage_signature());
                                coInField.setMapped_to_field(cmi.getInput_field().getMapped_to_field());
                                coInField.setError_indicator(cmi.getInput_field().getError_indicator());

                                coMapInput.setInput_field(coInField);
                                collatorMapInputLst.add(coMapInput);
                            }
                            // Convert the CollatorMapInput List to CollatorMapInput[]
                            CollatorMapInput[] coMapInArr = new CollatorMapInput[collatorMapInputLst.size()];
                            coMapInArr = collatorMapInputLst.toArray(coMapInArr);
                            bpcMap.setMap_inputs(coMapInArr);
                        }

                        // Get the CollatorMapOutput[] from CollatorMap from the Cdump file
                        if (null != coprops.getCollator_map().getMap_outputs() && coprops.getCollator_map().getMap_outputs().length != 0) {
                            CollatorMapOutput[] coMapOut = coprops.getCollator_map().getMap_outputs();
                            ArrayList<CollatorMapOutput> cmoLst = new ArrayList<CollatorMapOutput>(
                                Arrays.asList(coMapOut));

                            // Iterate over CollatorMapOutput List of Cdump File
                            for (CollatorMapOutput cmo : cmoLst) {

                                CollatorOutputField coOutField = new CollatorOutputField();
                                CollatorMapOutput coMapOutput = new CollatorMapOutput();

                                // set the all values into CollatorOutputField of BluePrint File
                                coOutField.setParameter_name(cmo.getOutput_field().getParameter_name());
                                coOutField.setParameter_tag(cmo.getOutput_field().getParameter_tag());
                                coOutField.setParameter_type(cmo.getOutput_field().getParameter_type());
                                coOutField.setParameter_role(cmo.getOutput_field().getParameter_role());

                                coMapOutput.setOutput_field(coOutField);
                                collatorMapOutputLst.add(coMapOutput);
                            }
                            // Convert the CollatorMapOutput List to CollatorMapOutput[]
                            CollatorMapOutput[] coMapOutArr = new CollatorMapOutput[collatorMapOutputLst.size()];
                            coMapOutArr = collatorMapOutputLst.toArray(coMapOutArr);
                            bpcMap.setMap_outputs(coMapOutArr);
                        }
                    }
                }
            }
        }
        return bpcMap;
    }

    private BPSplitterMap getSplitterDetails(Nodes n) {
        Property[] prop = n.getProperties();
        ArrayList<Property> propsList = new ArrayList<Property>(Arrays.asList(prop));
        String splitter_type = null;
        String input_message_signature = null;

        BPSplitterMap bpsMap = new BPSplitterMap();

        List<SplitterMapInput> splitterMapInputLst = new ArrayList<SplitterMapInput>();
        List<SplitterMapOutput> splitterMapOutputLst = new ArrayList<SplitterMapOutput>();

        // Iterate over the PropertyList of the Node from Cdump
        if (null != propsList) {
            for (Property splprops : propsList) {
                if (null != splprops.getSplitter_map()) {
                    // Set all the values from Property List of cdump to Blueprint SplitterMap object
                    splitter_type = splprops.getSplitter_map().getSplitter_type();
                    input_message_signature = splprops.getSplitter_map().getInput_message_signature();

                    bpsMap.setSplitter_type(splitter_type);
                    bpsMap.setInput_message_signature(input_message_signature);

                    // Get the SplitterMapInput[] from SplitterMap from the Cdump file
                    // Check if the splitter_type is Param based then mapInputs
                    // and mapOutputs should be populated, else not
                    if (splitter_type.equals(configurationProperties.getDefaultSplitterType())) {
                        bpsMap.setMap_inputs(null);
                        bpsMap.setMap_outputs(null);
                    } else {
                        if (null != splprops.getSplitter_map().getMap_inputs() && splprops.getSplitter_map().getMap_inputs().length !=0) {
                            SplitterMapInput[] spMapIn = splprops.getSplitter_map().getMap_inputs();
                            // Convert SplitterMapInput[] to List
                            ArrayList<SplitterMapInput> smiLst = new ArrayList<SplitterMapInput>(
                                Arrays.asList(spMapIn));
                            // Iterate over the SplitterMapInput List of the Cdump file
                            for (SplitterMapInput smi : smiLst) {
                                SplitterInputField spInField = new SplitterInputField();
                                SplitterMapInput spMapInput = new SplitterMapInput();
                                // set the all values into SplitterInputField of BluePrint File
                                spInField.setParameter_name(smi.getInput_field().getParameter_name());
                                spInField.setParameter_tag(smi.getInput_field().getParameter_tag());
                                spInField.setParameter_type(smi.getInput_field().getParameter_type());
                                spInField.setParameter_role(smi.getInput_field().getParameter_role());
                                spMapInput.setInput_field(spInField);
                                splitterMapInputLst.add(spMapInput);
                            }
                            // Convert the SplitterMapInput List to SplitterMapInput[]
                            SplitterMapInput[] spMapInArr = new SplitterMapInput[splitterMapInputLst.size()];
                            spMapInArr = splitterMapInputLst.toArray(spMapInArr);
                            bpsMap.setMap_inputs(spMapInArr);
                        }

                        // Get the SplitterMapOutput[] from SplitterMap from the Cdump file
                        if (null != splprops.getSplitter_map().getMap_outputs() && splprops.getSplitter_map().getMap_outputs().length != 0) {
                            SplitterMapOutput[] spMapOut = splprops.getSplitter_map().getMap_outputs();
                            ArrayList<SplitterMapOutput> smoLst = new ArrayList<SplitterMapOutput>(
                                Arrays.asList(spMapOut));

                            // Iterate over SplitterMapOutput of Cdump File
                            for (SplitterMapOutput smo : smoLst) {
                                SplitterOutputField spOutField = new SplitterOutputField();
                                SplitterMapOutput spMapOutput = new SplitterMapOutput();
                                spOutField.setParameter_name(smo.getOutput_field().getParameter_name());
                                spOutField.setParameter_tag(smo.getOutput_field().getParameter_tag());
                                spOutField.setParameter_type(smo.getOutput_field().getParameter_type());
                                spOutField.setParameter_role(smo.getOutput_field().getParameter_role());
                                spOutField.setTarget_name(smo.getOutput_field().getTarget_name());
                                spOutField.setMessage_signature(smo.getOutput_field().getMessage_signature());
                                spOutField.setError_indicator(smo.getOutput_field().getError_indicator());
                                spOutField.setMapped_to_field(smo.getOutput_field().getMapped_to_field());

                                spMapOutput.setOutput_field(spOutField);
                                splitterMapOutputLst.add(spMapOutput);
                            }
                            // Convert the SplitterMapOutput List to SplitterMapOutput[]
                            SplitterMapOutput[] spMapOutArr = new SplitterMapOutput[splitterMapOutputLst.size()];
                            spMapOutArr = splitterMapOutputLst.toArray(spMapOutArr);
                            bpsMap.setMap_outputs(spMapOutArr);
                        }
                    }
                }
            }
        }
        return bpsMap;
    }

    private BPDataBrokerMap getDataBrokerDetails(Nodes n) {
        Property[] prop = n.getProperties();
        // Convert the Property[] into List
        ArrayList<Property> propslst = new ArrayList<>(Arrays.asList(prop));
        String script = null;
        String data_broker_Type = null;
        String target_system_Url = null;
        String local_system_data_file_Path = null;
        String first_Row = null;
        String csv_file_field_Separator = null;
        String database_name = null;
        String table_name = null;
        String jdbc_driver_data_source_class_name = null;
        String user_id = null;
        String password = null;
        String protobufFile = null;

        // BluePrint DataBrokerMap object
        BPDataBrokerMap bpdbMap = new BPDataBrokerMap();
        List<DBMapInput> dataBrokerMapInputLst = new ArrayList<>();
        List<DBMapOutput> dataBrokerMapOutputLst = new ArrayList<>();

        // Iterate over the PropertyList of the Node from Cdump
        if (null != propslst) {
            for (Property dbprops : propslst) {
                if (null != dbprops.getData_broker_map()) {
                    // Set all the values from Property List of cdump to Blueprint DataBrokerMap object
                    script = dbprops.getData_broker_map().getScript();
                    data_broker_Type = dbprops.getData_broker_map().getData_broker_type();
                    target_system_Url = dbprops.getData_broker_map().getTarget_system_url();
                    local_system_data_file_Path = dbprops.getData_broker_map().getLocal_system_data_file_path();
                    first_Row = dbprops.getData_broker_map().getFirst_row();
                    csv_file_field_Separator = dbprops.getData_broker_map().getCsv_file_field_separator();
                    database_name = dbprops.getData_broker_map().getDatabase_name();
                    table_name = dbprops.getData_broker_map().getTable_name();
                    jdbc_driver_data_source_class_name = dbprops.getData_broker_map().getJdbc_driver_data_source_class_name();
                    user_id = dbprops.getData_broker_map().getUser_id();
                    password = dbprops.getData_broker_map().getPassword();
                    protobufFile = dbprops.getData_broker_map().getProtobufFile();

                    bpdbMap.setData_broker_type(data_broker_Type);
                    bpdbMap.setScript(script);
                    bpdbMap.setTarget_system_url(target_system_Url);
                    bpdbMap.setLocal_system_data_file_path(local_system_data_file_Path);
                    bpdbMap.setFirst_row(first_Row);
                    bpdbMap.setCsv_file_field_separator(csv_file_field_Separator);
                    bpdbMap.setDatabase_name(database_name);
                    bpdbMap.setTable_name(table_name);
                    bpdbMap.setJdbc_driver_data_source_class_name(jdbc_driver_data_source_class_name);
                    bpdbMap.setUser_id(user_id);
                    bpdbMap.setPassword(password);
                    bpdbMap.setProtobufFile(protobufFile);

                    // Get the DBMapInput[] from DataBrokerMap from the Cdump file
                    if (null != dbprops.getData_broker_map().getMap_inputs()) {
                        DBMapInput[] dmapIn = dbprops.getData_broker_map().getMap_inputs();
                        // Convert MapInputs[] to List
                        ArrayList<DBMapInput> dbmiLst = new ArrayList<DBMapInput>(Arrays.asList(dmapIn));
                        // Iterate over the DBMapInput List of the Cdump file
                        for (DBMapInput db : dbmiLst) {
                            DBInputField dbInField = new DBInputField();
                            DBMapInput dbMapInput = new DBMapInput();
                            // set the all values into DataBroker Input Field of BluePrint File
                            dbInField.setName(db.getInput_field().getName());
                            dbInField.setType(db.getInput_field().getType());
                            dbInField.setChecked(db.getInput_field().getChecked());
                            dbInField.setMapped_to_field(db.getInput_field().getMapped_to_field());
                            dbMapInput.setInput_field(dbInField);
                            dataBrokerMapInputLst.add(dbMapInput);
                        }
                        // Convert the DataBrokerMapInput Lsit to DBMapInput[]
                        DBMapInput[] dbMapInArr = new DBMapInput[dataBrokerMapInputLst.size()];
                        dbMapInArr = dataBrokerMapInputLst.toArray(dbMapInArr);
                        bpdbMap.setMap_inputs(dbMapInArr);
                    }

                    // Get the DBMapOutput[] from DataBrokerMap from the Cdump file
                    if (null != dbprops.getData_broker_map().getMap_outputs()) {
                        DBMapOutput[] dbMapOutArr = dbprops.getData_broker_map().getMap_outputs();
                        ArrayList<DBMapOutput> dbmoLst = new ArrayList<DBMapOutput>(Arrays.asList(dbMapOutArr));

                        DBOTypeAndRoleHierarchy[] dboTypeAndRoleHierarchyArr = null;

                        // Iterate over DBMapOutput List of Cdump File
                        for (DBMapOutput dbOut : dbmoLst) {
                            // Set DBMapOutput values of Cdump into DBOutputField values of BluePrint File
                            DBMapOutput dbMapOutput = new DBMapOutput();
                            DBOutputField dbOutField = new DBOutputField();
                            dbOutField.setName(dbOut.getOutput_field().getName());
                            dbOutField.setTag(dbOut.getOutput_field().getTag());

                            List<DBOTypeAndRoleHierarchy> dboList = new ArrayList<DBOTypeAndRoleHierarchy>();
                            // Iterate over DBOTypeAndRoleHierarchy List of Cdump File
                            for (DBOTypeAndRoleHierarchy dboTypeAndRoleHierarchy : dbOut.getOutput_field()
                                .getType_and_role_hierarchy_list()) {

                                DBOTypeAndRoleHierarchy dboTypeAndRole = new DBOTypeAndRoleHierarchy();
                                dboTypeAndRole.setName(dboTypeAndRoleHierarchy.getName());
                                dboTypeAndRole.setRole(dboTypeAndRoleHierarchy.getRole());
                                dboList.add(dboTypeAndRole);
                            }
                            dboTypeAndRoleHierarchyArr = new DBOTypeAndRoleHierarchy[dboList.size()];
                            dboTypeAndRoleHierarchyArr = dboList.toArray(dboTypeAndRoleHierarchyArr);
                            dbOutField.setType_and_role_hierarchy_list(dboTypeAndRoleHierarchyArr);

                            dbMapOutput.setOutput_field(dbOutField);
                            dataBrokerMapOutputLst.add(dbMapOutput);
                        }

                        // Convert DBMapOutPutList to DBMapOutput[]
                        DBMapOutput[] dbMapOutputArr = new DBMapOutput[dataBrokerMapOutputLst.size()];
                        dbMapOutputArr = dataBrokerMapOutputLst.toArray(dbMapOutputArr);
                        bpdbMap.setMap_outputs(dbMapOutputArr);
                    }
                }
            }
        }
        return bpdbMap;
    }

    /**
     * @param requirements
     * @param nodeOperationName
     * @return
     */
    private String getOutputMessage(Requirements[] requirements, String nodeOperationName) {
        String result = null;
        List<Requirements> requirementLst = Arrays.asList(requirements);
        if(null != nodeOperationName && nodeOperationName.trim() != ""){
            ReqCapability capability = null;
            for(Requirements r : requirementLst) {
                capability = r.getCapability();
                if(capability.getId().equals(nodeOperationName)){
                    result = capability.getName()[0].getMessageName();
                    //NodeOperationSignature output_message_name should have been array, as operation can have multiple output messages.
                    //Its seems to be some gap

                }
            }
        }

        return result;
    }

    private String getConnectedPort(List<Relations> requirements, String nodeId){
        String result = null;
        if(null != nodeId && nodeId.trim() != ""){
            for(Relations r : requirements) {
                if(nodeId.equals(r.getSourceNodeId())){
                    result = r.getSourceNodeRequirement().replace("+", OPERATION_EXTRACTOR);
                    result = result.split(OPERATION_EXTRACTOR)[0];
                    break;
                } else if(nodeId.equals(r.getTargetNodeId())){
                    result = r.getTargetNodeCapability().replace("+", OPERATION_EXTRACTOR);
                    result = result.split(OPERATION_EXTRACTOR)[0];
                    break;
                }
            }
        }
        return result;
    }

    private String getDockerImageURL(String nodeSolutionId) {
        List<Artifact> artifacts = ummClient.getArtifacts(nodeSolutionId, configurationProperties.getModelImageArtifactType());

        String dockerImageURL = "";
        if (artifacts != null && artifacts.size()>0){
            dockerImageURL = artifacts.get(0).getUrl();
        }
        return dockerImageURL;
    }

    /**
     * @param cdump
     * @param nodeId
     * @return
     */
    private List<Container> getRelations(Cdump cdump, String nodeId) {
        List<Container> connectedToList;
        Container connectedTo;
        connectedToList = new ArrayList<>();
        // Get the Relations from the Cdump File
        List<Relations> cRelations = cdump.getRelations();
        String operation = null;
        BaseOperationSignature bos;

        // Get the Relations with sourceNodeId as node id
        for (Relations cr : cRelations) {
            if (cr.getSourceNodeId().equals(nodeId)) {
                // Get the targeNodeName and set it to depends_on object
                connectedTo = new Container();
                connectedTo.setContainerName(cr.getTargetNodeName());
                bos = new BaseOperationSignature();
                operation = cr.getTargetNodeCapability().replace("+", OPERATION_EXTRACTOR);
                operation = operation.split(OPERATION_EXTRACTOR)[0];
                bos.setOperationName(operation);
                connectedTo.setBaseOperationSignature(bos);
                connectedToList.add(connectedTo);
            }
        }
        return connectedToList;
    }

    public DSResult setProbeIndicator(String userId,  String solutionId, String probeIndicator) {
        logger.debug( " setProbeIndicator() : Begin ");
        DSResult result = new DSResult();

        Gson gson = new Gson();
        ObjectMapper mapper = new ObjectMapper();
        try {

            String path = DEUtil.getCdumpPath(userId, configurationProperties.getToscaOutputFolder());
            String cdumpFileName = DEUtil.getCdumpFileName(solutionId);
            Cdump cdump = mapper.readValue(new File(path.concat(cdumpFileName)), Cdump.class);
            cdump.setProbeIndicator(probeIndicator);
            try {
                String jsonInString = gson.toJson(cdump);
                DEUtil.writeDataToFile(path, cdumpFileName, jsonInString);
                result.setSuccess(true);
                result.setErrorDescription("");
            }catch (JsonIOException e) {
                result.setSuccess(false);
                result.setErrorDescription(e.getMessage());
                logger.error("Exception in setProbeIndicator() ", e);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.setSuccess(false);
            result.setErrorDescription(e.getMessage());
        }
        logger.debug( " setProbeIndicator() : End ");
        return result;
    }

    private Nodes getNodeForId(List<Nodes> nodes, String nodeId){
        Nodes node = null;
        for(Nodes n : nodes){
            if(n.getNodeId().equals(nodeId)){
                node = n;
                break;
            }
        }
        return node;
    }

    private List<String> getNodesForPosition(Cdump cdump, String position) {
        List<String> nodeNames = new ArrayList<>();
        Set<String> sourceNodeId = new HashSet<>();
        Set<String> targetNodeId = new HashSet<>();
        List<Relations> relationsList = cdump.getRelations();
        List<Nodes> nodes = cdump.getNodes();
        Nodes node;
        for (Relations rlns : relationsList) {
            sourceNodeId.add(rlns.getSourceNodeId());
            targetNodeId.add(rlns.getTargetNodeId());
        }

        if (position.equals(FIRST_NODE_POSITION)) {
            sourceNodeId.removeAll(targetNodeId);
            for (String nodeId : sourceNodeId) {
                node = getNodeForId(nodes, nodeId);
                nodeNames.add(node.getName());
            }
        } else if(position.equals(LAST_NODE_POSITION)){
            targetNodeId.removeAll(sourceNodeId);
            for (String nodeId : targetNodeId) {
                node = getNodeForId(nodes, nodeId);
                nodeNames.add(node.getName());
            }
        }
        return nodeNames;
    }

    private String getNodeIdForPosition(Cdump cdump, String position) {
        String nodeId = null;
        Set<String> sourceNodeId = new HashSet<>();
        Set<String> targetNodeId = new HashSet<>();
        List<Relations> relationsList = cdump.getRelations();
        for (Relations rlns : relationsList) {
            sourceNodeId.add(rlns.getSourceNodeId());
            targetNodeId.add(rlns.getTargetNodeId());
        }
        if (position.equals(FIRST_NODE_POSITION)) {
            sourceNodeId.removeAll(targetNodeId);
            nodeId = (sourceNodeId.iterator().hasNext() ? sourceNodeId.iterator().next() : nodeId);
        } else if(position.equals(LAST_NODE_POSITION)){
            targetNodeId.removeAll(sourceNodeId);
            nodeId = (targetNodeId.iterator().hasNext() ? targetNodeId.iterator().next() : nodeId);
        }
        return nodeId;
    }

    private void deleteMemberCompositeSolutionMaps(String solutionId) throws Exception {
        try {
            List<CompositeSolutionMap> parentChildList = ummClient.getAllCompositeSolutionMaps().getBody(); ;
            if(!parentChildList.isEmpty() & parentChildList != null){
                for(CompositeSolutionMap childNode: parentChildList){
                    if (childNode.getParentUuid().equals(solutionId)){
                        ummClient.deleteCompositeSolutionMap(childNode.getId());
                    }
                }
            }
        } catch (Exception e) {
            logger.error(" Exception Occured in deleteMemberCompositeSolutionMaps() ", e);
            throw new Exception("Exception in updateCompositeSolution(), 333, Failed to drop CompositeSolution Member");
        }

    }

    public String updateCompositeSolution(Solution solution) throws Exception {
        String response;
        try {
            if (solution.getUuid() == null){
                logger.error("Error :  Exception in updateCompositeSolution() Failed to update the Solution for uuid is null");
                throw new Exception("  Exception in updateCompositeSolution , 223,Failed to update the Solution for uuid is null");
            }

            List<Solution> olds = ummClient.getSolutionsByUuid(solution.getUuid());
            if (olds.size() == 0) {
                logger.error("Error :  Exception in updateCompositeSolution() Failed to update the Solution for uuid is wrong");
                throw new Exception("  Exception in updateCompositeSolution , 224,Failed to update the Solution for uuid is wrong");
            }
            Solution old = olds.get(0);
            old.setName(solution.getName());
            old.setVersion(solution.getVersion());
            old.setSummary(solution.getSummary());
            ummClient.updateSolutionName(old);
            ummClient.updateSolutionBaseinfo(old);

            response = "{\"uuid\":\"" + solution.getUuid() + "\",\"success\":\"true\",\"errorMessage\":\"\"}";
            logger.info("*********response: {}", response);

        } catch (Exception e) {
            logger.error("Error :  Exception in updateCompositeSolution() Failed to update the Solution ",e);
            throw new Exception("  Exception in updateCompositeSolution , 222,Failed to update the Solution");
        }
        return response;
    }
}
