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
 * Dependent of each Node in the blueprint.json. IMPORTANT: This is not a List by itself.
 */

public class ConnectedTo implements Serializable {
	private static final long serialVersionUID = 5749775315078650369L;

	@JsonProperty("containerName")
	private String containerName = null;
	@JsonProperty("baseOperationSignature")
	private BaseOperationSignature operationSignature = null;

	/**
	 * Standard POJO no-arg constructor
	 */
	public ConnectedTo() {
		super();
	}

	/**
	 * ConnectedTo Constructor
	 *
	 * @param containerName
	 *            Name of the container
	 * @param operationSignature
	 *            Operation signature
	 */

	public ConnectedTo(String containerName, BaseOperationSignature operationSignature) {
		super();
		this.containerName = containerName;
		this.operationSignature = operationSignature;
	}

	@JsonProperty("containerName")
	public String getContainerName() {
		return containerName;
	}

	@JsonProperty("containerName")
	public void setContainerName(String containerName) {
		this.containerName = containerName;
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
		return "Component [containerName=" + containerName + ", operationSignature=" + operationSignature + "]";
	}

}
