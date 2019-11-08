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

/*Representation of Map outputs
 */
public class MapOutputs implements Serializable {

	private static final long serialVersionUID = -4241340660626658486L;

	@JsonProperty("output_field")
	private OutputField outputField;

	/**
	 * Standard POJO no-arg constructor
	 */
	public MapOutputs() {
		super();
	}

	public MapOutputs(OutputField outputField) {
		super();
		this.outputField = outputField;
	}

	@JsonProperty("output_field")
	public OutputField getOutputField() {
		return outputField;
	}

	@JsonProperty("output_field")
	public void setOutputField(OutputField outputField) {
		this.outputField = outputField;
	}

	@Override
	public String toString() {
		return "MapOutputs [outputField = " + outputField + "]";
	}
}
