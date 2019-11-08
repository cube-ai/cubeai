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
 * Representation of Probe Indicator in the blueprint.json
 */
public class ProbeIndicator implements Serializable {
	private final static long serialVersionUID = 6463163730071836107L;
	@JsonProperty("value")
	private String value;

	/**
	 * Standard POJO no-arg constructor
	 */
	public ProbeIndicator() {
		super();
	}

	/**
	 *
	 * @param value
	 *            Indicates whether Probe is present in the solution.
	 */
	public ProbeIndicator(String value) {
		super();
		this.value = value;
	}

	@JsonProperty("value")
	public String getValue() {
		return value;
	}

	@JsonProperty("value")
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "ProbeIndicator [value=" + value + "]";
	}
}
