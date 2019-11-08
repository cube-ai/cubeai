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
import java.util.List;

/**
 * This class is a representation of blueprint.json
 *
 */
public class Blueprint implements Serializable {

	private static final long serialVersionUID = -8199926375109170778L;

	@JsonProperty("name")
	private String name = null;
	@JsonProperty("version")
	private String version = null;
	@JsonProperty("inputPorts")
	private List<InputPort> inputPorts = null;
	@JsonProperty("nodes")
	private List<Node> nodes = null;
	@JsonProperty("probeIndicator")
	private ArrayList<ProbeIndicator> probeIndicator = null;



	/**
	 * Standard POJO no-arg constructor
	 */
	public Blueprint() {
		super();
	}

	/**
	 * Standard POJO constructor initialized with field
	 *
	 * @param name
	 *            Name of the blueprint
	 * @param version
	 *            Version of the blueprint
	 * @param inputPorts
	 *            Name of the input ports of the composite model
	 * @param nodes
	 *            List of nodes of model
	 * @param probeIndicator
	 *            Indicates presence of Probe in the composite solution
	 *
	 */
	public Blueprint(String name, String version, List<InputPort> inputPorts, List<Node> nodes,
                     ArrayList<ProbeIndicator> probeIndicator) {
		super();
		this.name = name;
		this.version = version;
		this.inputPorts = inputPorts;
		this.nodes = nodes;
		this.probeIndicator = probeIndicator;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("version")
	public String getVersion() {
		return version;
	}

	@JsonProperty("version")
	public void setVersion(String version) {
		this.version = version;
	}

	@JsonProperty("input_ports")
	public List<InputPort> getInputPorts() {
		return inputPorts;
	}

	@JsonProperty("input_ports")
	public void setInputPorts(List<InputPort> inputPorts) {
		this.inputPorts = inputPorts;
	}

	@JsonProperty("nodes")
	public List<Node> getNodes() {
		return nodes;
	}

	@JsonProperty("nodes")
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	@JsonProperty("probeIndicator")
	public ArrayList<ProbeIndicator> getProbeIndicator() {
		return probeIndicator;
	}

	@JsonProperty("probeIndicator")
	public void setProbeIndicator(ArrayList<ProbeIndicator> probeIndicator) {
		this.probeIndicator = probeIndicator;
	}

	public Node getNodebyContainer(String container) {
		for (Node node : nodes) {
			if (node.getContainerName().equalsIgnoreCase(container))
				return node;
		}
		return null;
	}

	public Blueprint addNode(Node node) {
		if (this.nodes == null) {
			this.nodes = new ArrayList<>();
		}
		this.nodes.add(node);
		return this;
	}

    @Override
    public String toString() {
        return "Blueprint{" +
            "name='" + name + '\'' +
            ", version='" + version + '\'' +
            ", inputPorts=" + inputPorts +
            ", nodes=" + nodes +
            ", probeIndicator=" + probeIndicator +
            '}';
    }
}
