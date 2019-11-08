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
import java.util.ArrayList;

/**
 * Representation of Operation Signature list of a Node. IMPORTANT: This itself
 * is NOT an Arraylist.
 */

public class OperationSignatureList implements Serializable {
	private final static long serialVersionUID = -6436344519431883582L;

	@JsonProperty("nodeOperationSignature")
	private NodeOperationSignature operationSignature = null;
	@JsonProperty("connectedTo")
	private ArrayList<ConnectedTo> connectedTo = null;

	/**
	 * Standard POJO no-arg constructor
	 */
	public OperationSignatureList() {
		super();
	}

	/**
	 * Standard POJO constructor initialized with field
	 *
	 * @param operationSignature
	 *            This is the operation signature
	 * @param connectedTo
	 *            This is the connected to for an operation signature.
	 */
	public OperationSignatureList(NodeOperationSignature operationSignature, ArrayList<ConnectedTo> connectedTo) {
		super();
		this.operationSignature = operationSignature;
		this.connectedTo = connectedTo;
	}

	@JsonProperty("nodeOperationSignature")
	public NodeOperationSignature getOperationSignature() {
		return operationSignature;
	}

	@JsonProperty("nodeOperationSignature")
	public void setOperationSignature(NodeOperationSignature operationSignature) {
		this.operationSignature = operationSignature;
	}

	@JsonProperty("connectedTo")
	public ArrayList<ConnectedTo> getConnectedTo() {
		return connectedTo;
	}

	@JsonProperty("connectedTo")
	public void setConnectedTo(ArrayList<ConnectedTo> connectedTo) {
		this.connectedTo = connectedTo;
	}

	@Override
	public String toString() {

		return "OperationSignatureList [operationSignature=" + operationSignature + ", connectedTo=" + connectedTo
				+ "]";
	}

}
