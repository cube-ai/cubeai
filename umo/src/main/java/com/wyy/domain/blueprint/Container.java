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

public class Container implements Serializable{

	private static final long serialVersionUID = 7033995176723370491L;

	private String containerName;
	private BaseOperationSignature baseOperationSignature;

	/**
	 * @return the containerName
	 */
	public String getContainerName() {
		return containerName;
	}
	/**
	 * @param containerName the containerName to set
	 */
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
	/**
	 * @return the baseOperationSignature
	 */
	public BaseOperationSignature getBaseOperationSignature() {
		return baseOperationSignature;
	}
	/**
	 * @param baseOperationSignature the baseOperationSignature to set
	 */
	public void setBaseOperationSignature(BaseOperationSignature baseOperationSignature) {
		this.baseOperationSignature = baseOperationSignature;
	}

}
