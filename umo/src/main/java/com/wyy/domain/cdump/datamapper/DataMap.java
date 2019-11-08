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
package com.wyy.domain.cdump.datamapper;

import java.io.Serializable;

public class DataMap implements Serializable {
	private static final long serialVersionUID = 5437495964588153222L;

	private MapInputs[] map_inputs;
	private MapOutput[] map_outputs;

	public MapInputs[] getMap_inputs() {
		return map_inputs;
	}

	public void setMap_inputs(MapInputs[] map_inputs) {
		this.map_inputs = map_inputs;
	}

	/**
	 * @return the map_outputs
	 */
	public MapOutput[] getMap_outputs() {
		return map_outputs;
	}

	/**
	 * @param map_outputs the map_outputs to set
	 */
	public void setMap_outputs(MapOutput[] map_outputs) {
		this.map_outputs = map_outputs;
	}




}
