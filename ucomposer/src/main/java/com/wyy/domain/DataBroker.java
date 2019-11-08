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
 * Representation of data brokers object under training clients. This is NOT a
 * representation or Node type Data Broker.
 */

public class DataBroker implements Serializable {
	private final static long serialVersionUID = -5917310849172760370L;

	@JsonProperty("name")
	private String name = null;
	@JsonProperty("operation_signature")
	private BaseOperationSignature operationSignature = null;

	/**
	 * No args constructor for use in serialization
	 *
	 */
	public DataBroker() {
		super();
	}

	/**
	 *
	 * @param operationSignature
	 *            This is the operation signature.
	 * @param name
	 *            Name of the data source
	 */
	public DataBroker(String name, BaseOperationSignature operationSignature) {
		super();
		this.name = name;
		this.operationSignature = operationSignature;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("operation_signature")
	public BaseOperationSignature getOperationSignature() {
		return operationSignature;
	}

	@JsonProperty("operation_signature")
	public void setOperationSignature(BaseOperationSignature operationSignature) {
		this.operationSignature = operationSignature;
	}

}
