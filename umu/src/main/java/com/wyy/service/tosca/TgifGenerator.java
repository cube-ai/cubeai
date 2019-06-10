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

package com.wyy.service.tosca;

import com.alibaba.fastjson.JSON;
import com.wyy.service.tosca.vo.tgif.*;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONArray;
import java.util.Iterator;


public class TgifGenerator {

    private static final Logger logger = LoggerFactory.getLogger(TgifGenerator.class);

    /*public Tgif populateTgif( String version, JSONObject metaDataJson, JSONObject protobufJson) {
        // TODO: 很多处理metadata和proto的方法
        Tgif result = new Tgif();
        return result;
    }*/

    /**
     *
     * @param version
     * @param metaDataJson
     * @param protobufJson
     * @return
     */
    public Tgif populateTgif(String version, JSONObject metaDataJson, JSONObject protobufJson) {
        logger.debug("--------  populateTgif() Begin ------------");

        @SuppressWarnings("unchecked")
        String solutionName = metaDataJson.getOrDefault("name", "Key not found").toString();
        @SuppressWarnings("unchecked")
        String description = metaDataJson.getOrDefault("description", "").toString();
        String COMPONENT_TYPE = "Docker";

        // Set Self
        Self self = new Self(version, solutionName, description, COMPONENT_TYPE);

        // Set empty Stream
        Stream streams = null;

        // Set services
        Call[] scalls = getCalls(protobufJson);
        Provide[] sprovides = getProvides(protobufJson);

        Service services = new Service(scalls, sprovides);

        // Set array of Parameters
        Parameter[] parameters = getParameters(protobufJson);

        // Set Auxilary
        Auxiliary auxiliary = null;

        // Set array of artifacts
        Artifact[] artifacts = getArtifacts(protobufJson);

        Tgif result = new Tgif(self, streams, services, parameters, auxiliary, artifacts);

        logger.debug("--------  populateTgif() End ------------");
        return result;
    }

    /**
     *
     * @param protobufJson
     * @return
     */
    private Artifact[] getArtifacts(JSONObject protobufJson) {
        Artifact[] result = new Artifact[1];

        return result;
    }

    /**
     *
     * @param protobufJson
     * @return
     */
    private Parameter[] getParameters(JSONObject protobufJson) {
        Parameter[] result = new Parameter[1];
        return result;
    }

    /**
     *
     * @param protobufJson
     * @return
     */
    @SuppressWarnings("unchecked")
    private Provide[] getProvides(JSONObject protobufJson) {
        JSONObject service = (JSONObject) protobufJson.get("service");
        JSONArray listOfOperations = (JSONArray) service.get("listOfOperations");
        JSONArray listOfMessages = (JSONArray) protobufJson.get("listOfMessages");

        Provide[] result = new Provide[listOfOperations.size()];

        JSONObject operation = null;
        JSONArray listOfInputMessages = null;
        String operationName = null;
        int operationCnt = 0;

        // Iterator<JSONObject> itr = listOfOperations.iterator();
        // 这里Iterator中的元素类型为Object，产生了赋值错误，所以将泛型类型改为Object，再迭代过程中再将Objuct转为字符串再转为JSONObject。
        Iterator<Object> itr = listOfOperations.iterator();
        // Iterator<JSONObject> inputMsgItr = null;
        Iterator<Object> inputMsgItr = null;
        JSONObject inputMessage = null;
        String inputMsgName = null;
        JSONArray inputMsgJsonArray = null;

        Request request = null;
        Response response = null;
        Provide provide = null;
        @SuppressWarnings("unused")
        int inputMsgCnt = 0;

        while (itr.hasNext()) {
            // operation = itr.next();
            operation = JSON.parseObject(itr.next().toString());
            operationName = operation.get("operationName").toString();
            // Construct the format : for each output Message get the message name and then
            // get collect the message details
            listOfInputMessages = (JSONArray) operation.get("listOfInputMessages");
            inputMsgItr = listOfInputMessages.iterator();
            inputMsgCnt = 0;
            inputMsgJsonArray = new JSONArray();
            while (inputMsgItr.hasNext()) {
                inputMessage = (JSONObject) inputMsgItr.next();
                inputMsgName = (String) inputMessage.get("inputMessageName");
                inputMsgJsonArray.add(getMsgJson(inputMsgName, listOfMessages));
            }
            request = new Request(inputMsgJsonArray, "");
            response = new Response(new JSONArray(), "");
            provide = new Provide(operationName, request, response);
            result[operationCnt] = provide;
            operationCnt++;
        }

        return result;
    }

    /**
     *
     * @param protobufJson
     * @return
     */
    @SuppressWarnings("unchecked")
    private Call[] getCalls(JSONObject protobufJson) {

        JSONObject service = (JSONObject) protobufJson.get("service");
        JSONArray listOfOperations = (JSONArray) service.get("listOfOperations");
        JSONArray listOfMessages = (JSONArray) protobufJson.get("listOfMessages");

        Call[] result = new Call[listOfOperations.size()];

        JSONObject operation = null;

        JSONArray listOfOutputMessages = null;

        String operationName = null;
        int operationCnt = 0;

        // Iterator<JSONObject> itr = listOfOperations.iterator();
        Iterator<Object> itr = listOfOperations.iterator();
        // Iterator<JSONObject> outputMsgItr = null;
        Iterator<Object> outputMsgItr = null;
        JSONObject outputMessage = null;
        String outputMsgName = null;
        JSONArray outputMsgJsonArray = null;

        Request request = null;
        Response response = null;
        Call call = null;
        @SuppressWarnings("unused")
        int outputMsgCnt = 0;

        while (itr.hasNext()) {
            operation = JSON.parseObject( itr.next().toString());
            operationName = operation.get("operationName").toString();
            // Construct the format : for each output Message get the message name and then
            // get collect the message details
            listOfOutputMessages = (JSONArray) operation.get("listOfOutputMessages");
            outputMsgJsonArray = new JSONArray();
            outputMsgItr = listOfOutputMessages.iterator();
            outputMsgCnt = 0;
            while (outputMsgItr.hasNext()) {
                outputMessage = (JSONObject) outputMsgItr.next();
                outputMsgName = (String) outputMessage.get("outPutMessageName");
                outputMsgJsonArray.add(getMsgJson(outputMsgName, listOfMessages));
            }
            request = new Request(outputMsgJsonArray, "");
            response = new Response(new JSONArray(), "");
            call = new Call(operationName, request, response);
            result[operationCnt] = call;
            operationCnt++;
        }
        return result;
    }

    /**
     *
     * @param msgName
     * @param listOfMessages
     * @return
     */
    private JSONObject getMsgJson(String msgName, JSONArray listOfMessages) {
        @SuppressWarnings("unchecked")
        // Iterator<JSONObject> itr = listOfMessages.iterator();
        Iterator<Object> itr = listOfMessages.iterator();
        JSONObject message = null;
        String messageName = null;
        while (itr.hasNext()) {
            message = (JSONObject) itr.next();
            messageName = (String) message.get("messageName");
            if (messageName.equals(msgName)) {
                logger.debug("message : " + message.toJSONString());
                break;
            }
        }
        return message;
    }
}
