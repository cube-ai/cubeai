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

public class BluePrintNode implements Serializable {

	private static final long serialVersionUID = -6799798022353553155L;

	private String containerName;
	private String nodeType;
	private String dockerImageURL;
	private String protoUri;
	private List<OperationSignatureList> operationSignatureLists;
	private BPDataBrokerMap bpDataBrokerMap;
	private BPSplitterMap bpSplitterMap;
	private BPCollatorMap bpCollatorMap;
	private List<DataSource> dataSources;

	public String getContainerName() {
		return containerName;
	}
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
	public String getDockerImageURL() {
		return dockerImageURL;
	}
	public void setDockerImageURL(String dockerImageURL) {
		this.dockerImageURL = dockerImageURL;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public String getProtoUri() {
		return protoUri;
	}
	public void setProtoUri(String protoUri) {
		this.protoUri = protoUri;
	}
	public List<OperationSignatureList> getOperationSignatureLists() {
		return operationSignatureLists;
	}
	public void setOperationSignatureLists(List<OperationSignatureList> operationSignatureLists) {
		this.operationSignatureLists = operationSignatureLists;
	}
	public List<DataSource> getDataSources() {
		return dataSources;
	}
	public void setDataSources(List<DataSource> dataSources) {
		this.dataSources = dataSources;
	}
	public BPDataBrokerMap getBpDataBrokerMap() {
		return bpDataBrokerMap;
	}
	public void setBpDataBrokerMap(BPDataBrokerMap bpDataBrokerMap) {
		this.bpDataBrokerMap = bpDataBrokerMap;
	}
	public BPSplitterMap getBpSplitterMap() {
		return bpSplitterMap;
	}
	public void setBpSplitterMap(BPSplitterMap bpSplitterMap) {
		this.bpSplitterMap = bpSplitterMap;
	}
	public BPCollatorMap getBpCollatorMap() {
		return bpCollatorMap;
	}
	public void setBpCollatorMap(BPCollatorMap bpCollatorMap) {
		this.bpCollatorMap = bpCollatorMap;
	}


}
