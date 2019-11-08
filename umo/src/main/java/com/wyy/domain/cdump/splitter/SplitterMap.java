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


package com.wyy.domain.cdump.splitter;

import java.io.Serializable;

public class SplitterMap implements Serializable {

	private static final long serialVersionUID = -2099602526865715137L;

	private String splitter_type;
	private String input_message_signature;
	private SplitterMapInput[] map_inputs;
	private SplitterMapOutput[] map_outputs;

	public String getSplitter_type() {
		return splitter_type;
	}
	public void setSplitter_type(String splitter_type) {
		this.splitter_type = splitter_type;
	}
	public String getInput_message_signature() {
		return input_message_signature;
	}
	public void setInput_message_signature(String input_message_signature) {
		this.input_message_signature = input_message_signature;
	}
	public SplitterMapInput[] getMap_inputs() {
		return map_inputs;
	}
	public void setMap_inputs(SplitterMapInput[] map_inputs) {
		this.map_inputs = map_inputs;
	}
	public SplitterMapOutput[] getMap_outputs() {
		return map_outputs;
	}
	public void setMap_outputs(SplitterMapOutput[] map_outputs) {
		this.map_outputs = map_outputs;
	}


}
