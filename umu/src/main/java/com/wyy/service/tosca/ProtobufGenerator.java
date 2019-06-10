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

import com.wyy.service.tosca.vo.protobuf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileReader;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.Collections;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;


public class ProtobufGenerator {

    boolean isMessage = false;
    boolean isItservice = false;
    boolean isItMessage = false;
    int servicesLineCount = 0;

    private static final Logger logger = LoggerFactory.getLogger(ProtobufGenerator.class);
    // Protobuf's variable declaration section started
    final Pattern pattern = Pattern.compile("\\((.*?)\\)");
    private List<MessageargumentList> messageargumentList = null;
    private List<InputMessage> listOfInputMessages = null;
    private List<OutputMessage> listOfOputPutMessages = null;
    private List<Operation> listOfOperation = null;

    private ProtoBufClass protoBufClass = null;

    private MessageBody messageBody = null;
    private List<MessageBody> messageBodyList = null;
    private Service service = null;
    private Operation operation = null;
    private List<String> listOfInputAndOutputMessage = null;
    private List<Option> listOfOption = null;

    // Start to declare new variables as per new complex protobuf file.
    private ProtoBufClass duplicateProtoBufClass = null;

    public String createProtoJson( File schemaFile ) {
        // TODO:解析字符串称为类属性，然后生成json字符串

        // 复制的acumos代码
        int servicesLineCount = 0;
        protoBufClass = new ProtoBufClass();
        messageBodyList = new ArrayList<>();
        listOfInputAndOutputMessage = new ArrayList<>();
        listOfOption = new ArrayList<>();
        BufferedReader br = null;
        FileReader fr = null;
        String protoBufToJsonString = "";
        try {
            fr = new FileReader(schemaFile);
            br = new BufferedReader(fr);
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                // protoBufToJsonString = protoBufToJsonString + (sCurrentLine.replace("\t", ""));
                parseLine(sCurrentLine.replace("\t", ""));
            }
            Gson gson = new Gson();
            protoBufToJsonString = gson.toJson(protoBufClass);
            try {
                List<MessageBody> expendedmessageBodyList = constructSubMessageBody(protoBufToJsonString);
                protoBufClass.setListOfMessages(expendedmessageBodyList);
                Gson gson1 = new Gson();
                protoBufToJsonString = gson1.toJson(protoBufClass);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            servicesLineCount = 0;
        } catch(Exception e){
            e.printStackTrace();
        }
        return protoBufToJsonString;
    }

    /**
     *
     * @param line
     * @throw Exception
     */
    private void parseLine(String line) throws Exception {
        // 该方法抛出的异常为designStodio自己实现的ServiceException改为Exception

        try {
            // construct syntax
            if (line.contains("syntax")) {
                protoBufClass.setSyntax(constructSyntax(line));
            }
            // construct package
            if (line.startsWith("package")) {
                protoBufClass.setPackageName(constructPackage(line));
            }
            /* if (Properties.isOptionKeywordRequirede().equals("true")) {
                if (line.startsWith("option")) {
                    constructOption(line);
                    protoBufClass.setListOfOption(listOfOption);
                }
            }*/ // 参数文件中这个地方的值为false
            // start package

            if (line.startsWith("message")) {
                // logger.debug("-------------- costructMessage() strated ---------------");
                isMessage = false;
                messageBody = new MessageBody();

                messageBody = costructMessage(line, messageBody, null);

            } else if (isMessage && !line.contains("}")) {
                messageargumentList = new ArrayList<MessageargumentList>();
                messageBody = costructMessage(line, messageBody, messageargumentList);
            } else if (isMessage && line.contains("}")) {
                messageBodyList.add(messageBody);
                protoBufClass.setListOfMessages(messageBodyList);
                isMessage = false;
                // logger.debug("-------------- costructMessage() end ---------------");
            }

            if (line.startsWith("service")) {
                // logger.debug("-------------- constructService() started ---------------");
                service = new Service();
                service = constructService(line, service);

            } else if (isItservice && !line.contains("}") && !line.isEmpty()) {
                operation = new Operation();
                listOfOperation = new ArrayList<Operation>();
                line = line.replace(";", "").replace("\t", "").trim();
                String operationType = "";
                String operationName = "";
                String inputParameterString = "";
                String outPutParameterString = "";

                String line1 = line.split("returns")[0];
                operationType = line1.split(" ", 2)[0].trim();
                String line2 = line1.split(" ", 2)[1].replace(" ", "").replace("(", "%br%").replace(")", "").trim();
                operationName = line2.split("%br%")[0].trim();
                inputParameterString = line2.split("%br%")[1].trim();
                outPutParameterString = line.split("returns")[1].replace("(", "").replace(")", "").trim();
                operation.setOperationType(operationType);
                operation.setOperationName(operationName);
                listOfInputMessages = constructInputMessage(inputParameterString);
                listOfOputPutMessages = constructOutputMessage(outPutParameterString);

                operation.setListOfInputMessages(listOfInputMessages);
                operation.setListOfOutputMessages(listOfOputPutMessages);
                if (service.getListOfOperations() != null && !service.getListOfOperations().isEmpty()) {
                    service.getListOfOperations().add(operation);
                } else {
                    listOfOperation.add(operation);
                    service.setListOfOperations(listOfOperation);
                    protoBufClass.setService(service);
                }
            } else if (isItservice && line.contains("}") && !line.isEmpty()) {
                isItservice = false;
                // logger.debug("-------------- constructService() end ---------------");
            }
        } catch (Exception ex) {
            // logger.error(" --------------- Exception Occured  parseLine() --------------", ex);
            // throw new ServiceException(" --------------- Exception Occured while parsing protobuf --------------",
                // Properties.getDecryptionErrorCode(), "Error while parsing protoBuf file", ex.getCause());
        }

    }

    /**
     *
     * @param line
     * @return
     */
    private String constructSyntax(String line) {
        // logger.debug("-------------- constructSyntax() strated ---------------");
        String removequotes = "";
        try {
            if (line.startsWith("syntax")) {
                String[] fields = line.split("=");
                String removeSemicolon = fields[1].replace(";", "");
                String trimString = removeSemicolon.trim();
                removequotes = trimString.replaceAll("^\"|\"$", "");
            }
        } catch (Exception ex) {
            // logger.error(" --------------- Exception Occured  constructSyntax() --------------", ex);
        }
        // logger.debug("-------------- constructSyntax() end ---------------");
        return removequotes;

    }

    /**
     *
     * @param line
     * @return
     */
    private String constructPackage(String line) {
        // logger.debug("-------------- constructPackage() strated ---------------");
        String[] fields = line.split(" ");
        // logger.debug("-------------- constructPackage() end ---------------");
        return fields[1].replace(";", "");

    }

    /**
     *
     * @param line
     * @return
     */
    private String constructOption(String line) {
        Option optionInstance = new Option();
        StringTokenizer st = new StringTokenizer(line, " ");
        String removequotes = "";
        while (st.hasMoreElements()) {
            st.nextElement();
            optionInstance.setKey(st.nextElement().toString());
            st.nextElement();
            String removeSemicolon = st.nextElement().toString().replace(";", "");
            String trimString = removeSemicolon.trim();
            removequotes = trimString.replaceAll("^\"|\"$", "");
            optionInstance.setValue(removequotes);
        }
        listOfOption.add(optionInstance);
        return "";
    }

    /**
     *
     * @param line
     * @param messageBody
     * @param messageargumentList
     * @return
     */
    private MessageBody costructMessage(String line, MessageBody messageBody,
                                        List<MessageargumentList> messageargumentList) {
        try {
            if (line.startsWith("message")) {
                /*
                 * int openCurlybacketPosition; String messageValue = "";
                 */
                /*
                 * openCurlybacketPosition = line.indexOf("{"); messageValue = line.substring(8,
                 * openCurlybacketPosition); messageBody.setMessageName(messageValue.trim());
                 */
                String[] fields = line.split(" ");
                messageBody.setMessageName(fields[1]);
            }
            if (isMessage) {

                if (line.endsWith(";") || line.contains(";")) {
                    StringTokenizer st = new StringTokenizer(line, " ");
                    String[] fields = line.split(" ");
                    MessageargumentList messageargumentListInstance = new MessageargumentList();

                    for (int i = 0; i < fields.length; i++) {
                        if (!fields[i].isEmpty()) {
                            if (st.countTokens() == 5) {// count total number of
                                // token in line.
                                messageargumentListInstance.setRole(fields[i]);
                                messageargumentListInstance.setType(fields[i + 1]);
                                messageargumentListInstance.setName(fields[i + 2]);
                                messageargumentListInstance.setTag(fields[i + 4].replace(";", ""));
                            } else {
                                messageargumentListInstance.setRole("");
                                messageargumentListInstance.setType(fields[i]);
                                messageargumentListInstance.setName(fields[i + 1]);
                                messageargumentListInstance.setTag(fields[i + 3].replace(";", ""));
                            }
                            break;
                        }
                    }
                    SortComparator sortComparator = SortFactory.getComparator();
                    if (messageBody.getMessageargumentList() != null) {
                        messageBody.getMessageargumentList().add(messageargumentListInstance);
                        Collections.sort(messageBody.getMessageargumentList(), sortComparator);
                    } else {
                        messageargumentList.add(messageargumentListInstance);
                        messageBody.setMessageargumentList(messageargumentList);
                    }
                }
            }
            if (line.contains("}")) {
                isMessage = false;
            } else {
                isMessage = true;
            }
        } catch (Exception ex) {
            // logger.error(" --------------- Exception Occured  costructMessage() --------------", ex);
        }
        return messageBody;
    }

    /**
     *
     * @param line
     * @param service
     * @return
     */
    private Service constructService(String line, Service service) {

        try {
            /*
             * String servicesName = ""; int openCurlybacketPosition;
             */
            /*
             * openCurlybacketPosition = line.indexOf("{"); servicesName = line.substring(8,
             * openCurlybacketPosition); service.setName(servicesName.trim());
             */
            String[] fields = line.split(" ");
            service.setName(fields[1]);
            if (line.contains("}")) {
                isItservice = false;
            } else {
                isItservice = true;
            }
        } catch (Exception ex) {
            // logger.error(" --------------- Exception Occured  constructService() --------------", ex);
        }
        return service;

    }

    /**
     *
     * @param inputParameterString
     * @return
     */
    private List<InputMessage> constructInputMessage(String inputParameterString) {
        // logger.debug("-------------- constructInputMessage() strated ---------------");
        listOfInputMessages = new ArrayList<InputMessage>();
        try {

            String[] inPutParameterArray = inputParameterString.split(",");
            for (int i = 0; i < inPutParameterArray.length; i++) {
                InputMessage inputMessage = new InputMessage();
                inputMessage.setInputMessageName(inPutParameterArray[i]);
                listOfInputMessages.add(inputMessage);
                listOfInputAndOutputMessage.add(inputMessage.getInputMessageName());
            }
        } catch (Exception ex) {
            // logger.error(" --------------- Exception Occured  constructInputMessage() --------------", ex);
        }
        // logger.debug("-------------- constructInputMessage() end ---------------");
        return listOfInputMessages;

    }

    /**
     *
     * @param outPutParameterString
     * @return
     */
    private List<OutputMessage> constructOutputMessage(String outPutParameterString) {
        // logger.debug("-------------- constructOutputMessage() strated ---------------");
        listOfOputPutMessages = new ArrayList<OutputMessage>();
        try {
            String[] outPutParameterArray = outPutParameterString.split(",");
            for (int i = 0; i < outPutParameterArray.length; i++) {
                OutputMessage outputMessage = new OutputMessage();
                outputMessage.setOutPutMessageName(outPutParameterArray[i]);
                listOfOputPutMessages.add(outputMessage);
                listOfInputAndOutputMessage.add(outputMessage.getOutPutMessageName());
            }
        } catch (Exception ex) {
            // logger.error("-------------- constructOutputMessage() end ---------------", ex);
        }
        // logger.debug("-------------- constructOutputMessage() end ---------------");
        return listOfOputPutMessages;
    }

    /**
     *
     * @param protoBufToJsonString
     * @return
     */
    private List<MessageBody> constructSubMessageBody(String protoBufToJsonString) throws Exception {
        logger.debug("-------------- constructconstructSubMessageBody() strated ---------------");
        List<MessageBody> expendedmessageBodyList = new ArrayList<MessageBody>();
        try {
            // private List<MessageBody> duplicateexpendedmessageBodyList = new
            // ArrayList<MessageBody>();
            List<MessageBody> parentNotExistList = new ArrayList<MessageBody>();
            List<String> expendedChildmessageBodyList = new ArrayList<String>();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
            protoBufClass = mapper.readValue(protoBufToJsonString, ProtoBufClass.class);
            duplicateProtoBufClass = mapper.readValue(protoBufToJsonString, ProtoBufClass.class);
            List<MessageBody> sourceMessageBody = protoBufClass.getListOfMessages();
            List<MessageBody> destinationMessageBody = duplicateProtoBufClass.getListOfMessages();
            // String[] basicProtobufTypes = { "string", "int32", "int64" };
            // String basicProtobufTypes = Properties.getProtobufBasicType();
            String basicProtobufTypes = "string,int32,int64";
            String[] basicProtobufTypesArray = basicProtobufTypes.split(",");
            List<String> basicProtobufTypesList = Arrays.asList(basicProtobufTypesArray);
            int i;
            boolean isSubMessageFound = false;
            for (i = 0; i < sourceMessageBody.size(); i++) {
                String sourceMessageName = sourceMessageBody.get(i).getMessageName();
                List<MessageargumentList> messageargumentList = sourceMessageBody.get(i).getMessageargumentList();
                List<MessageargumentList> comlpexmessageargumentList = new ArrayList<MessageargumentList>();
                int j = 0;
                for (j = 0; j < destinationMessageBody.size(); j++) {
                    String destinationMessageName = destinationMessageBody.get(j).getMessageName(); // change
                    // before
                    // it
                    // was
                    // messageBody
                    if (sourceMessageName.equals(destinationMessageName)) {
                        continue;
                    }
                    List<MessageargumentList> parentmessageNamemessageargumentList = destinationMessageBody.get(j)
                        .getMessageargumentList();
                    if (null != parentmessageNamemessageargumentList
                        && !parentmessageNamemessageargumentList.isEmpty()) {
                        for (int k = 0; k < parentmessageNamemessageargumentList.size(); k++) {
                            String type = parentmessageNamemessageargumentList.get(k).getType();
                            if (!basicProtobufTypesList.contains(type) && type.equals(sourceMessageName)) {
                                isSubMessageFound = true;
                                MessageBody expendedmessageBody = null;
                                List<MessageargumentList> messageargumentListInstanceList = null;
                                String message = "";
                                if (expendedmessageBodyList != null && !expendedmessageBodyList.isEmpty()) {
                                    for (int ei = 0; ei < expendedmessageBodyList.size(); ei++) {
                                        expendedmessageBody = expendedmessageBodyList.get(ei);
                                        message = expendedmessageBody.getMessageName();
                                        if (message == destinationMessageBody.get(j).getMessageName()) {
                                            messageargumentListInstanceList = expendedmessageBody
                                                .getMessageargumentList();
                                            break;
                                        } else {
                                            messageargumentListInstanceList = new ArrayList<MessageargumentList>();
                                            expendedmessageBody = new MessageBody();
                                        }

                                    }
                                } else {
                                    messageargumentListInstanceList = new ArrayList<MessageargumentList>();
                                    expendedmessageBody = new MessageBody();
                                }
                                if (expendedChildmessageBodyList.contains(destinationMessageName)) {
                                    MessageargumentList messageargumentListInstance = new MessageargumentList();
                                    ComplexType complexType = null;
                                    for (MessageBody messageBody0 : expendedmessageBodyList) {
                                        List<MessageargumentList> list = messageBody0.getMessageargumentList();
                                        MessageargumentList messageArgumentList = null;
                                        int messageArgumentListIndex = 0;
                                        for (int jj = 0; jj < list.size(); jj++) {
                                            messageArgumentList = messageBody0.getMessageargumentList().get(jj);
                                            if (messageArgumentList.getComplexType() != null) {
                                                complexType = messageArgumentList.getComplexType();
                                                messageArgumentListIndex = jj;
                                            }
                                        }
                                        // MessageargumentList
                                        // messageArgumentList =
                                        // messageBody0.getMessageargumentList().get(0);

                                        // complexType =
                                        // messageArgumentList.getComplexType();
                                        List<MessageargumentList> complexMessageargumentList = complexType
                                            .getMessageargumentList();
                                        ComplexType complexTypeObject = null;
                                        for (int ii = 0; ii < complexMessageargumentList.size(); ii++) {
                                            MessageargumentList MessageargumentList = complexMessageargumentList
                                                .get(ii);
                                            if (MessageargumentList.getType().equals(sourceMessageName)) {
                                                String parentTag = MessageargumentList.getTag();
                                                List<MessageargumentList> comlpexmessageargumentList20 = new ArrayList<MessageargumentList>();
                                                complexTypeObject = new ComplexType();
                                                for (int l = 0; l < messageargumentList.size(); l++) {
                                                    MessageargumentList messageargumentList12 = (MessageargumentList) messageargumentList
                                                        .get(l);
                                                    MessageargumentList complexMessageargumentInstance = new MessageargumentList();
                                                    complexMessageargumentInstance
                                                        .setRole(messageargumentList12.getRole());
                                                    complexMessageargumentInstance
                                                        .setType(messageargumentList12.getType());
                                                    complexMessageargumentInstance
                                                        .setName(messageargumentList12.getName());
                                                    complexMessageargumentInstance
                                                        .setTag(parentTag + "." + messageargumentList12.getTag());
                                                    comlpexmessageargumentList20.add(complexMessageargumentInstance);
                                                }
                                                complexTypeObject.setMessageName(sourceMessageName);
                                                complexTypeObject.setMessageargumentList(comlpexmessageargumentList20);
                                                messageargumentListInstance.setRole(MessageargumentList.getRole());
                                                messageargumentListInstance.setType(MessageargumentList.getType());
                                                messageargumentListInstance.setComplexType(complexTypeObject);
                                                messageargumentListInstance.setName(MessageargumentList.getName());
                                                messageargumentListInstance.setTag(MessageargumentList.getTag());
                                                complexMessageargumentList.set(ii, messageargumentListInstance);
                                                complexType.setMessageargumentList(complexMessageargumentList);
                                                messageArgumentList.setComplexType(complexType);
                                                messageBody0.getMessageargumentList().set(messageArgumentListIndex,
                                                    messageArgumentList);

                                            }

                                        }

                                    }

                                } else {
                                    expendedmessageBody.setMessageName(destinationMessageBody.get(j).getMessageName());
                                    MessageargumentList messageargumentListInstance = new MessageargumentList();
                                    MessageargumentList messageargumentListInstanceSympletype = null;
                                    List<MessageargumentList> messageargumentListInstanceSympletypePreList = new ArrayList<MessageargumentList>();
                                    List<MessageargumentList> messageargumentListInstanceSympletypePostList = new ArrayList<MessageargumentList>();
                                    int simpleTypePreIndex = k;
                                    int simpleTypePostIndex = k + 1;
                                    if (simpleTypePreIndex > 0) {
                                        for (int kk = 0; kk < simpleTypePreIndex; kk++) {
                                            messageargumentListInstanceSympletype = new MessageargumentList();
                                            messageargumentListInstanceSympletype
                                                .setRole(parentmessageNamemessageargumentList.get(kk).getRole());
                                            messageargumentListInstanceSympletype
                                                .setType(parentmessageNamemessageargumentList.get(kk).getType());
                                            messageargumentListInstanceSympletype
                                                .setName(parentmessageNamemessageargumentList.get(kk).getName());
                                            messageargumentListInstanceSympletype
                                                .setTag(parentmessageNamemessageargumentList.get(kk).getTag());
                                            messageargumentListInstanceSympletypePreList
                                                .add(messageargumentListInstanceSympletype);
                                        }

                                    }
                                    List<MessageargumentList> postList = parentmessageNamemessageargumentList
                                        .subList(simpleTypePostIndex, parentmessageNamemessageargumentList.size());

                                    messageargumentListInstance
                                        .setRole(parentmessageNamemessageargumentList.get(k).getRole());
                                    messageargumentListInstance
                                        .setType(parentmessageNamemessageargumentList.get(k).getType());
                                    String parentTag = parentmessageNamemessageargumentList.get(k).getTag();

                                    ComplexType complexType = new ComplexType();

                                    complexType.setMessageName(sourceMessageName);
                                    for (int l = 0; l < messageargumentList.size(); l++) {
                                        MessageargumentList messageargumentList12 = (MessageargumentList) messageargumentList
                                            .get(l);
                                        MessageargumentList complexMessageargumentInstance = new MessageargumentList();
                                        complexMessageargumentInstance.setRole(messageargumentList12.getRole());
                                        complexMessageargumentInstance.setType(messageargumentList12.getType());
                                        complexMessageargumentInstance.setName(messageargumentList12.getName());
                                        complexMessageargumentInstance
                                            .setTag(parentTag + "." + messageargumentList12.getTag());
                                        comlpexmessageargumentList.add(complexMessageargumentInstance);

                                    }
                                    complexType.setMessageargumentList(comlpexmessageargumentList);
                                    messageargumentListInstance.setComplexType(complexType);
                                    messageargumentListInstance
                                        .setName(parentmessageNamemessageargumentList.get(k).getName());
                                    messageargumentListInstance
                                        .setTag(parentmessageNamemessageargumentList.get(k).getTag());
                                    if (messageargumentListInstanceList != null
                                        && messageargumentListInstanceList.size() != 0) {
                                        messageargumentListInstanceList.add(messageargumentListInstance);
                                        expendedmessageBody.setMessageargumentList(messageargumentListInstanceList);
                                    } else {
                                        messageargumentListInstanceList
                                            .addAll(messageargumentListInstanceSympletypePreList);
                                        messageargumentListInstanceList.add(messageargumentListInstance);
                                        messageargumentListInstanceList
                                            .addAll(messageargumentListInstanceSympletypePostList);
                                        expendedmessageBody.setMessageargumentList(messageargumentListInstanceList);
                                    }
                                    expendedChildmessageBodyList.add(sourceMessageBody.get(i).getMessageName());
                                    if (message == destinationMessageBody.get(j).getMessageName()) {

                                    } else {
                                        expendedmessageBodyList.add(expendedmessageBody);
                                    }

                                }
                                break;
                            } else {
                                isSubMessageFound = false;
                            }
                        }
                    }
                    if (isSubMessageFound) {
                        isSubMessageFound = true;
                        break;
                    }
                }
                if (!isSubMessageFound) {
                    parentNotExistList.add(sourceMessageBody.get(i));
                }
            }
            // System.out.println(expendedmessageBodyList.size());
            final int size = parentNotExistList.size();
            boolean dulicateMessageIsFound = false;
            for (int ij = 0; ij < size; ij++) {
                String duplicateMessage = parentNotExistList.get(ij).getMessageName();
                for (int j = 0; j < expendedmessageBodyList.size(); j++) {
                    if (duplicateMessage.equals(expendedmessageBodyList.get(j).getMessageName())) {
                        dulicateMessageIsFound = true;
                        break;
                    } else {
                        dulicateMessageIsFound = false;
                    }
                }
                if (!dulicateMessageIsFound) {
                    expendedmessageBodyList.add(parentNotExistList.get(ij));
                    dulicateMessageIsFound = false;
                }
            }
        } catch (Exception ex) {
            // logger.error("-------------- constructconstructSubMessageBody() end ---------------" + ex);
            ex.printStackTrace();
        }
        // logger.debug("-------------- constructconstructSubMessageBody() end ---------------");
        return expendedmessageBodyList;
    }
}
