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

package com.wyy.domain.cdump;

import java.io.Serializable;
import java.util.Arrays;

public class Nodes implements Serializable {

	private static final long serialVersionUID = 5437495964588153222L;

	private String name;
	private String nodeId;
	private String nodeSolutionId;
	private String nodeVersion;
	private String protoUri;
	private Requirements[] requirements;
	private Property[] properties;
	private Capabilities[] capabilities;

	private Ndata ndata;
	private String type;


	/**
	 * @return the ndata
	 */
	public Ndata getNdata() {
		return ndata;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the nodeId
	 */
	public String getNodeId() {
		return nodeId;
	}

	/**
	 * @param nodeId
	 *            the nodeId to set
	 */
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	/**
	 * @return the nodeSolutionId
	 */
	public String getNodeSolutionId() {
		return nodeSolutionId;
	}

	/**
	 * @param nodeSolutionId
	 *            the nodeSolutionId to set
	 */
	public void setNodeSolutionId(String nodeSolutionId) {
		this.nodeSolutionId = nodeSolutionId;
	}

	/**
	 * @return the nodeVersion
	 */
	public String getNodeVersion() {
		return nodeVersion;
	}

	/**
	 * @param nodeVersion
	 *            the nodeVersion to set
	 */
	public void setNodeVersion(String nodeVersion) {
		this.nodeVersion = nodeVersion;
	}

	/**
	 * @return the requirements
	 */
	public Requirements[] getRequirements() {
		return requirements;
	}

	/**
	 * @param requirements
	 *            the requirements to set
	 */
	public void setRequirements(Requirements[] requirements) {
		this.requirements = requirements;
	}

	/**
	 * @return the properties
	 */
	public Property[] getProperties() {
		return properties;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(Property[] properties) {
		this.properties = properties;
	}

	/**
	 * @return the capabilities
	 */
	public Capabilities[] getCapabilities() {
		return capabilities;
	}

	/**
	 * @param capabilities
	 *            the capabilities to set
	 */
	public void setCapabilities(Capabilities[] capabilities) {
		this.capabilities = capabilities;
	}

	/**
	public Ndata getNdata() {
		return ndata;
	}

	/**
	 * @param ndata
	 *            the ndata to set
	 */
	public void setNdata(Ndata ndata) {
		this.ndata = ndata;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the protoUri
	 */
	public String getProtoUri() {
		return protoUri;
	}

	/**
	 * @param protoUri
	 *            the protoUri to set
	 */
	public void setProtoUri(String protoUri) {
		this.protoUri = protoUri;
	}

    @Override
    public String toString() {
        return "Nodes{" +
            "name='" + name + '\'' +
            ", nodeId='" + nodeId + '\'' +
            ", nodeSolutionId='" + nodeSolutionId + '\'' +
            ", nodeVersion='" + nodeVersion + '\'' +
            ", protoUri='" + protoUri + '\'' +
            ", requirements=" + Arrays.toString(requirements) +
            ", properties=" + Arrays.toString(properties) +
            ", capabilities=" + Arrays.toString(capabilities) +
            ", ndata=" + ndata +
            ", type=" + type +
            '}';
    }
}
