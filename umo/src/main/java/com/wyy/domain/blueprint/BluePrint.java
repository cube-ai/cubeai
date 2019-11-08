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

package com.wyy.domain.blueprint;
import java.io.Serializable;
import java.util.List;

public class BluePrint implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String version;
	private List<Container> inputPorts;
	private List<BluePrintNode> nodes;
	private List<ProbeIndicator> probeIndicator;

	/**
	 * @return the inputPorts
	 */
	public List<Container> getInputPorts() {
		return inputPorts;
	}
	/**
	 * @param input_ports the inputPorts to set
	 */
	public void setInputPorts(List<Container> input_ports) {
		this.inputPorts = input_ports;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the nodes
	 */
	public List<BluePrintNode> getNodes() {
		return nodes;
	}
	/**
	 * @param nodes the nodes to set
	 */
	public void setNodes(List<BluePrintNode> nodes) {
		this.nodes = nodes;
	}
	/**
	 * @return the probeIndicator
	 */
	public List<ProbeIndicator> getProbeIndicator() {
		return probeIndicator;
	}
	/**
	 * @param probeIndicator the probeIndicator to set
	 */
	public void setProbeIndicator(List<ProbeIndicator> probeIndicator) {
		this.probeIndicator = probeIndicator;
	}

}
