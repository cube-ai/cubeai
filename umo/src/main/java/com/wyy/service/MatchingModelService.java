/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

package com.wyy.service;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wyy.domain.Artifact;
import com.wyy.domain.Solution;
import com.wyy.domain.matchingmodel.KeyVO;
import com.wyy.domain.matchingmodel.ModelDetailVO;
import com.wyy.domain.protobuf.MessageBody;
import com.wyy.domain.tgif.Call;
import com.wyy.domain.tgif.Provide;
import com.wyy.domain.tgif.Service;
import com.wyy.domain.tgif.Tgif;
import com.wyy.util.ConfigurationProperties;
import com.wyy.util.DEUtil;
import com.wyy.util.ModelCacheForMatching;
import org.codehaus.jackson.map.jsontype.impl.StdTypeResolverBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Component
public class MatchingModelService {
    private static final Logger logger = LoggerFactory.getLogger(MatchingModelService.class);

    private final ModelCacheForMatching modelCacheForMatching;
    private final ObjectMapper mapper = new ObjectMapper();
    private final UmmClient ummClient;
    private final ConfigurationProperties confprops;
    private final NexusArtifactClient nexusArtifactClient;

    public MatchingModelService(ModelCacheForMatching modelCacheForMatching, UmmClient ummClient, ConfigurationProperties confprops, NexusArtifactClient nexusArtifactClient) {
        this.modelCacheForMatching = modelCacheForMatching;
        this.ummClient = ummClient;
        this.confprops = confprops;
        this.nexusArtifactClient = nexusArtifactClient;
    }

    /**
     * This method will populate PublicModelCache For MatchingModels
     * @param models
     * 		models
     * @throws Exception
     * 		In Exception Case
     */
    public void populatePublicModelCacheForMatching(List<Solution> models) throws Exception{
        logger.debug(" populatePublicModelCacheForMatching() Begin ");
        HashMap<KeyVO, List<ModelDetailVO>> result;

        try {
            HashMap<KeyVO, List<ModelDetailVO>> modelCache = constructModelCache(models);
            result = (HashMap<KeyVO, List<ModelDetailVO>>) modelCacheForMatching.getPublicModelCache();
            if(null == result){
                result = new HashMap<>();
            }
            result.putAll(modelCache);
            modelCacheForMatching.setPublicModelCache(result);
        } catch (Exception e) {
            logger.error(" Exception in populatePublicModelCacheForMatching() ", e);
            throw new Exception("Failed to read the Model");
        }
        logger.debug(" populatePublicModelCacheForMatching() End ");
    }
    /**
     * This method will remove PublicModelCache For MatchingModels
     * @param models
     * 		models
     * @throws Exception
     * 		In Exception Case
     */
    public void removePublicModelCacheForMatching(List<Solution> models) throws Exception{
        logger.debug(" removePublicModelCacheForMatching() Begin ");
        HashMap<KeyVO, List<ModelDetailVO>> modelCache;
        try {
            modelCache = removeModelFromCache(models);
            modelCacheForMatching.setPublicModelCache(modelCache);
        } catch (Exception e) {
            logger.error(" Exception in populatePublicModelCacheForMatching() ", e);
            throw new Exception("Failed to read the Model");
        }
        logger.debug(" removePublicModelCacheForMatching() End ");
    }


    /**
     * This method will populate PrivateModelCache For MatchingModels
     * @param userId
     * 		UserId
     * @param models
     * 		models

     * 		In Exception Case
     */
    public void populatePrivateModelCacheForMatching(String userId, List<Solution> models){
        logger.debug(" populatePrivateModelCacheForMatching() Begin ");
        Map<KeyVO, List<ModelDetailVO>> privateModelCache = null;
        privateModelCache = modelCacheForMatching.getPrivateModelCache(userId);

        HashMap<KeyVO, List<ModelDetailVO>> modelCache = constructModelCache(models);
        if(null == privateModelCache){
            privateModelCache = new HashMap<>();
        }
        privateModelCache.putAll(modelCache);
        modelCacheForMatching.setUserPrivateModelCache(userId, privateModelCache);
        Date lastExceutionTime = modelCacheForMatching.getUserPrivateModelUpdateTime(userId);
        logger.debug(" lastExceutionTime : " + lastExceutionTime);
        logger.debug(" populatePrivateModelCacheForMatching() End ");
    }

    private  HashMap<KeyVO, List<ModelDetailVO>> removeModelFromCache(List<Solution> solutions) throws Exception {
        logger.debug(" removeModelFromCache() Begin ");
        HashMap<KeyVO, List<ModelDetailVO>> result = (HashMap<KeyVO, List<ModelDetailVO>>) modelCacheForMatching.getPublicModelCache();
        if(null != solutions && !solutions.isEmpty() && null != result && !result.isEmpty()){
            Tgif tgif = null;
            for(Solution solution : solutions ){
                //get TGIF file, TOSCA生成器输入文件;
                List<Artifact> artifacts = ummClient.getArtifacts(solution.getUuid(), confprops .getToscaInputArtifactType());
                    for (Artifact mlpArtifact : artifacts) {
                            try {
                                String tgifFileNexusURI = mlpArtifact.getUrl();
                                logger.debug( " TgifFileNexusURI  : " + tgifFileNexusURI );
                                ByteArrayOutputStream byteArrayOutputStream = nexusArtifactClient.getArtifact(tgifFileNexusURI);
                                if(null != byteArrayOutputStream && !byteArrayOutputStream.toString().isEmpty()){
                                    mapper.configure(Feature.AUTO_CLOSE_SOURCE, true);
                                    tgif = mapper.readValue(byteArrayOutputStream.toString(), Tgif.class);
                                }
                                if(null != tgif){
                                    Service service = tgif.getServices();
                                    if (service != null) {
                                        //1. process input messages
                                        if(service.getProvides() != null & service.getProvides().length != 0 ){
                                            for(Provide provide : service.getProvides()){
                                                //For every provide generate the keyVO
                                                try {
                                                    //Assuming that only one message as a input parameter.
                                                    //Currently multi input message parameter is not supported.
                                                    MessageBody[] messages = mapper.readValue(provide.getRequest().getFormat().toJSONString(),MessageBody[].class);
                                                    removeKey(messages, result);
                                                } catch (IOException e) {
                                                    logger.error(" exception occured in Provides part in removeModelFromCache()", e);
                                                    throw new Exception("Failed to read the Model");
                                                }
                                            }
                                        }
                                        //2. process output messages
                                        if (service.getCalls() != null && service.getCalls().length != 0) {
                                            for(Call call : service.getCalls()){
                                                //For every call generate the keyVO
                                                try {
                                                    //Assuming that only one message as a output parameter.
                                                    MessageBody[] messages = mapper.readValue(call.getRequest().getFormat().toJSONString(), MessageBody[].class);
                                                    removeKey(messages, result);
                                                } catch (IOException e) {
                                                    logger.error(" exception occured in Calls Part in removeModelFromCache()", e);
                                                    throw new Exception("Failed to read the Model");
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                logger.error(" exception occured in removeModelFromCache()", e);
                                throw new Exception("Failed to read the Model");
                            }
                    }
            }
        }
        logger.debug(" removeModelFromCache() End ");
        return result;
    }
    private void removeKey(MessageBody[] messages, HashMap<KeyVO, List<ModelDetailVO>> result){
        for(MessageBody msgBody : messages){
            if (null != msgBody.getMessageargumentList()) {
                KeyVO key = genKey(msgBody);
                //check if key is present in result
                if(result.containsKey(key)){
                    result.remove(key);
                }
            }
        }
    }
    private HashMap<KeyVO, List<ModelDetailVO>> constructModelCache(List<Solution> solutions) {
        logger.debug(" constructModelCache() Begin ");
        Tgif tgif = null;
        HashMap<KeyVO, List<ModelDetailVO>> result = new HashMap<>();
        for (Solution solution : solutions) {
            // get TGIF file
            List<Artifact> artifacts = ummClient.getArtifacts(solution.getUuid(), confprops.getToscaInputArtifactType());
                for (Artifact artifact : artifacts) {
                    String tgifFileNexusURI = "";
                        try {
                            tgifFileNexusURI = artifact.getUrl();
                            logger.debug(" TgifFileNexusURI 1  : " + tgifFileNexusURI);
                            ByteArrayOutputStream byteArrayOutputStream = nexusArtifactClient.getArtifact(tgifFileNexusURI);
                            if (null != byteArrayOutputStream && !byteArrayOutputStream.toString().isEmpty()) {
                                mapper.configure(Feature.AUTO_CLOSE_SOURCE, true);
                                tgif = mapper.readValue(byteArrayOutputStream.toString(), Tgif.class);
                            }
                            if (null != tgif) {
                                Service service = tgif.getServices();
                                if (service != null) {
                                    // 1. process input messages
                                    processInputMessage(service, solution, tgifFileNexusURI, result);
                                    // 2. process output messages
                                    if (service.getCalls() != null && service.getCalls().length != 0) {
                                        Call[] calls = service.getCalls();
                                        for (Call call : calls) {
                                            // For every call generate the keyVO
                                            genKeyVOPerCall(call, solution, tgifFileNexusURI, result);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            logger.debug(" Some exception so ignored record for Tgif FileNexus URI : {} ", tgifFileNexusURI);
                            logger.error(" exception occured in getModelCache()", e);
                        }

            }
        }
        logger.debug(" constructModelCache() End ");
        return result;
    }
    private KeyVO genKey(MessageBody msgBody ){
        int numberOfFields = msgBody.getMessageargumentList().size(); // Number of fields.
        // Check if nested message
        boolean isNestedMessage = DEUtil.isNested(msgBody.getMessageargumentList());
        // Construct KeyVO
        KeyVO key = new KeyVO();
        key.setNestedMessage(isNestedMessage);
        key.setNumberofFields(numberOfFields);
        key.setPortType(confprops.getMatchingInputPortType());
        return key;
    }
    private void processInputMessage(Service service, Solution solution, String tgifFileNexusURI, HashMap<KeyVO, List<ModelDetailVO>> result) throws IOException {
        if (service.getProvides() != null & service.getProvides().length != 0) {
            Provide[] inputs = service.getProvides();
            List<ModelDetailVO> modelDetailVOs;
            for (Provide provide : inputs) {
                // For every provide generate the keyVO
                if (null != provide.getRequest().getFormat()) {
                    // Assuming that only one message as a input parameter.
                    // Currently multi input message parameter is not supported.
                    MessageBody[] messages = mapper.readValue(provide.getRequest().getFormat().toJSONString(),MessageBody[].class);
                    genCatch(messages, provide.getRequest().getFormat().toJSONString(), solution, tgifFileNexusURI, result);
                }
            }
        }
    }
    private void genCatch(MessageBody[] messages, String protoBuf, Solution solution, String tgifFileNexusURI, HashMap<KeyVO, List<ModelDetailVO>> result ){
        List<ModelDetailVO> modelDetailVOs;
        for (MessageBody msgBody : messages) {// Assumption : only input message parameter
            if (null != msgBody.getMessageargumentList()) {
                KeyVO key = genKey(msgBody);
                // Construct ValueVO
                ModelDetailVO value = new ModelDetailVO();
                value.setModelId(solution.getUuid());
                value.setModelName(solution.getName());
                value.setProtobufJsonString(protoBuf);
                value.setTgifFileNexusURI(tgifFileNexusURI);
                value.setVersion(solution.getVersion());

                // check if key is present in result
                if (result.containsKey(key)) {
                    modelDetailVOs = result.get(key);
                } else {
                    modelDetailVOs = new ArrayList<>();
                }
                modelDetailVOs.add(value);
                result.put(key, modelDetailVOs);
            }
        }
    }
    private void genKeyVOPerCall(Call call, Solution solution, String tgifFileNexusURI, HashMap<KeyVO, List<ModelDetailVO>> result) throws IOException {
        if (null != call.getRequest().getFormat()) {
            // Assuming that only one message as a output parameter.
            MessageBody[] messages = mapper.readValue(call.getRequest().getFormat().toJSONString(),MessageBody[].class);
            genCatch(messages, call.getRequest().getFormat().toJSONString(), solution, tgifFileNexusURI, result);

        }
    }
}
