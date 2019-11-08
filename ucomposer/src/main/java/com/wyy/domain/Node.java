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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representation of a Node in the composite solution as specified in the
 * blueprint.json
 */

public class Node implements Serializable {

	private static final long serialVersionUID = 3561091761587012180L;

	@JsonProperty("containerName")
	private String container = null;

	@JsonProperty("nodeType")
	private String nodeType = null;

	@JsonProperty("image")
	private String image = null;

	@JsonProperty("protoUri")
	private String protoUri = null;

	@JsonProperty("operationSignatureLists")
	private ArrayList<OperationSignatureList> operationSignatureList = null; // OperationSignatureList
																				// itself
																				// is
																				// NOT
																				// a
																				// Arraylist
	@JsonProperty("dataSources")
    @JsonIgnore
	private List<DataSource> dataSources = null;

	@JsonProperty("data_broker_map")
    @JsonIgnore
    private DataBrokerMap dataBrokerMap;

    @JsonIgnore
	@JsonProperty("collator_map")
	private CollatorMap collatorMap;

    @JsonIgnore
	@JsonProperty("splitter_map")
	private SplitterMap splitterMap;

	private boolean outputAvailable = false;

	private String nodeOutput = null;

	private List<Node> immediateAncestors = new ArrayList<Node>();

	public boolean beingProcessedByAThread = false;

	/**
	 * Standard POJO no-arg constructor
	 */
	public Node() {
		super();
	}

	/**
	 * @param operationSignatureList
	 *            List of operations supported by the node
	 * @param protoUri
	 *            Url of protofile : required to be passed to the Probe
	 * @param container
	 *            Name of the container
	 * @param image
	 *            Url of the docker image of the named node in Nexus.
	 *            Information consumed by deployer
	 * @param dataSources
	 *            Required by the data broker
	 * @param nodeType
	 *            Type of the node: DataMapper or MLModel or DataBroker or
	 *            TrainingClient or Probe
	 * @param dataBrokerMap
	 *            Data broker info data structure.
	 * @param collatorMap
	 *            Collator info structure
	 * @param splitterMap
	 *            Splitter info structure
	 * @param outputAvailable
	 *            Says if the node's output is available.
	 * @param nodeOutput
	 *            The node's output after it is called.
	 * @param immediateAncestors
	 *            The immediate ancestors of the node.
	 * @param beingProcessedByAThread
	 *            Says if the node is being processed by a thread
	 */
	public Node(String container, String nodeType, String image, String protoUri,
                ArrayList<OperationSignatureList> operationSignatureList, List<DataSource> dataSources,
                DataBrokerMap dataBrokerMap, CollatorMap collatorMap, SplitterMap splitterMap, boolean outputAvailable,
                String nodeOutput, List<Node> immediateAncestors, boolean beingProcessedByAThread) {
		super();
		this.container = container;
		this.nodeType = nodeType;
		this.image = image;
		this.protoUri = protoUri;
		this.operationSignatureList = operationSignatureList;
		this.dataSources = dataSources;
		this.dataBrokerMap = dataBrokerMap;
		this.collatorMap = collatorMap;
		this.splitterMap = splitterMap;
		this.outputAvailable = outputAvailable;
		this.nodeOutput = nodeOutput;
		this.immediateAncestors = immediateAncestors;
		this.beingProcessedByAThread = beingProcessedByAThread;
	}

	@JsonProperty("containerName")
	public String getContainerName() {
		return container;
	}

	@JsonProperty("containerName")
	public void setContainerName(String container) {
		this.container = container;
	}

	@JsonProperty("nodeType")
	public String getNodeType() {
		return nodeType;
	}

	@JsonProperty("nodeType")
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	@JsonProperty("image")
	public String getImage() {
		return image;
	}

	@JsonProperty("image")
	public void setImage(String image) {
		this.image = image;
	}

	@JsonProperty("protoUri")
	public String getProtoUri() {
		return protoUri;
	}

	@JsonProperty("protoUri")
	public void setProtoUri(String protoUri) {
		this.protoUri = protoUri;
	}

	@JsonProperty("operationSignatureLists")
	public ArrayList<OperationSignatureList> getOperationSignatureList() {
		return operationSignatureList;
	}

	@JsonProperty("operationSignatureLists")
	public void setOperationSignatureList(ArrayList<OperationSignatureList> operationSignatureList) {
		this.operationSignatureList = operationSignatureList;
	}

	@JsonProperty("dataSources")
	public List<DataSource> getDataSources() {
		return dataSources;
	}

	@JsonProperty("dataSources")
	public void setDataSources(List<DataSource> dataSources) {
		this.dataSources = dataSources;
	}

	@JsonProperty("data_broker_map")
	public DataBrokerMap getDataBrokerMap() {
		return dataBrokerMap;
	}

	@JsonProperty("data_broker_map")
	public void setDataBrokerMap(DataBrokerMap dataBrokerMap) {
		this.dataBrokerMap = dataBrokerMap;
	}

	@JsonProperty("collator_map")
	public CollatorMap getCollatorMap() {
		return collatorMap;
	}

	@JsonProperty("collator_map")
	public void setCollatorMap(CollatorMap collatorMap) {
		this.collatorMap = collatorMap;
	}

	public boolean isOutputAvailable() {
		return outputAvailable;
	}

	public void setOutputAvailable(boolean outputAvailable) {
		this.outputAvailable = outputAvailable;
	}

	public String getNodeOutput() {
		return nodeOutput;
	}

	public void setNodeOutput(String nodeOutput) {
		this.nodeOutput = nodeOutput;
	}

	public List<Node> getImmediateAncestors() {
		return immediateAncestors;
	}

	public void setImmediateAncestors(List<Node> immediateAncestors) {
		this.immediateAncestors = immediateAncestors;
	}

	public SplitterMap getSplitterMap() {
		return splitterMap;
	}

	public void setSplitterMap(SplitterMap splitterMap) {
		this.splitterMap = splitterMap;
	}

	public boolean isBeingProcessedByAThread() {
		return beingProcessedByAThread;
	}

	public void setBeingProcessedByAThread(boolean beingProcessedByAThread) {
		this.beingProcessedByAThread = beingProcessedByAThread;
	}

	public boolean immediateAncestorsOutputAvailable() {
		// Check all ancestors for output
		for (Node n : this.immediateAncestors)

		{
			if (!n.isOutputAvailable()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		return "Node [container=" + container + ", image=" + image + ", protoUri=" + protoUri + ", nodeType=" + nodeType
				+ ", dataBrokerMap=" + dataBrokerMap + ",collatorMap=" + collatorMap + ",splitterMap=" + splitterMap
				+ ",outputAvailable=" + outputAvailable + ",nodeOutput=" + nodeOutput + ",immediateAncestors="
				+ immediateAncestors + "]";
	}

}
