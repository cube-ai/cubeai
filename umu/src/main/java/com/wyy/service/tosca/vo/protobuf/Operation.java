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

package com.wyy.service.tosca.vo.protobuf;

import java.io.Serializable;
import java.util.List;

/**
 *
 *
 *
 */
public class Operation implements Serializable{

    private static final long serialVersionUID = 6913785969226335666L;
    private String operationType = "";
    private String operationName = "";
    private List<InputMessage> listOfInputMessages;
    private List<OutputMessage> listOfOutputMessages;

    public String getOperationType() {
        return operationType;
    }
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
    public String getOperationName() {
        return operationName;
    }
    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }
    public List<InputMessage> getListOfInputMessages() {
        return listOfInputMessages;
    }
    public void setListOfInputMessages(List<InputMessage> listOfInputMessages) {
        this.listOfInputMessages = listOfInputMessages;
    }
    public List<OutputMessage> getListOfOutputMessages() {
        return listOfOutputMessages;
    }
    public void setListOfOutputMessages(List<OutputMessage> listOfOutputMessages) {
        this.listOfOutputMessages = listOfOutputMessages;
    }

}

