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


package com.wyy.domain.matchingmodel;

import java.io.Serializable;

public class ModelDetailVO implements Serializable {


	private static final long serialVersionUID = -1778736077567965538L;

	private String modelName;
	private String modelId;
	private String version;

	private String tgifFileNexusURI;
	private String protobufJsonString;


	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getModelId() {
		return modelId;
	}
	public void setModelId(String modelId) {
		this.modelId = modelId;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	public String getTgifFileNexusURI() {
		return tgifFileNexusURI;
	}
	public void setTgifFileNexusURI(String tgifFileNexusURI) {
		this.tgifFileNexusURI = tgifFileNexusURI;
	}
	public String getProtobufJsonString() {
		return protobufJsonString;
	}
	public void setProtobufJsonString(String protobufJsonString) {
		this.protobufJsonString = protobufJsonString;
	}

}
