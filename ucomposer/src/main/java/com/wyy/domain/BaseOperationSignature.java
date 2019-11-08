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

package com.wyy.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Representation of Operation Signature
 */

public class BaseOperationSignature implements Serializable {

	private final static long serialVersionUID = 832153494386063512L;

	@JsonProperty("operationName")
	private String operationName = null;
//	@JsonProperty("input_message_name")
//	private String inputMessageName = null;
//	@JsonProperty("output_message_name")
//	private String outputMessageName = null;

	/**
	 * Standard POJO no-arg constructor
	 */
	public BaseOperationSignature() {
		super();
	}

	/**
	 * Standard POJO constructor initialized with field
	 * @param operationName
	 *            This is the operation name
//	 * @param inputMessageName
//	 *            This is the input msg name
//	 * @param outputMessageName
//	 *            This is the output msg name
	 */
	public BaseOperationSignature(String operationName, String inputMessageName, String outputMessageName) {
		super();
		this.operationName = operationName;
//		this.inputMessageName = inputMessageName;
//		this.outputMessageName = outputMessageName;
	}

	@JsonProperty("operationName")
	public String getOperationName() {
		return operationName;
	}

	@JsonProperty("operationName")
	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

//	@JsonProperty("input_message_name")
//	public String getInputMessageName() {
//		return inputMessageName;
//	}
//
//	@JsonProperty("input_message_name")
//	public void setInputMessageName(String inputMessageName) {
//		this.inputMessageName = inputMessageName;
//	}
//
//	@JsonProperty("output_message_name")
//	public String getOutputMessageName() {
//		return outputMessageName;
//	}
//
//	@JsonProperty("output_message_name")
//	public void setOutputMessageName(String outputMessageName) {
//		this.outputMessageName = outputMessageName;
//	}

//	@Override
//	public String toString() {
//
//		return "OperationSignature [operationName=" + operationName + ", inputMessageName=" + inputMessageName
//				+ ", outputMessageName=" + outputMessageName + "]";
//	}

    @Override
    public String toString() {
        return "OperationSignature{" +
            "operationName='" + operationName + '\'' +
            '}';
    }
}
