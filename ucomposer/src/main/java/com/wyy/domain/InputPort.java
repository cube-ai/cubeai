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
 * This class is a representation of Input Port in blueprint
 *
 */

public class InputPort implements Serializable {

	private final static long serialVersionUID = 4874295563788554992L;
	@JsonProperty("containerName")
	private String container = null;
	@JsonProperty("baseOperationSignature")
	private BaseOperationSignature operationSignature = null;

	/**
	 * Standard POJO no-arg constructor
	 */
	public InputPort() {
		super();
	}

	/**
	 *
	 * @param operationSignature
	 *            An operation - it further has a name, input message and output
	 *            message
	 * @param container
	 *            Name of the container in the dockerinfo.json
	 */
	public InputPort(String container, BaseOperationSignature operationSignature) {
		super();
		this.container = container;
		this.operationSignature = operationSignature;
	}

	@JsonProperty("containerName")
	public String getContainerName() {
		return container;
	}

	@JsonProperty("containerName")
	public void setContainerName(String container) {
		this.container = container;
	}

	@JsonProperty("baseOperationSignature")
	public BaseOperationSignature getOperationSignature() {
		return operationSignature;
	}

	@JsonProperty("baseOperationSignature")
	public void setOperationSignature(BaseOperationSignature operationSignature) {
		this.operationSignature = operationSignature;
	}

	@Override
	public String toString() {

		return "InputPort [container=" + container + ", operationSignature =" + operationSignature + "]";
	}

}
