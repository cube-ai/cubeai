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

package com.wyy.domain.cdump.collator;

import java.io.Serializable;

public class CollatorMap implements Serializable {

	private static final long serialVersionUID = -6615382514857867003L;

	private String collator_type;
	private String output_message_signature;
	private CollatorMapInput[] map_inputs;
	private CollatorMapOutput[] map_outputs;

	public String getCollator_type() {
		return collator_type;
	}

	public void setCollator_type(String collator_type) {
		this.collator_type = collator_type;
	}

	public CollatorMapInput[] getMap_inputs() {
		return map_inputs;
	}

	public void setMap_inputs(CollatorMapInput[] map_inputs) {
		this.map_inputs = map_inputs;
	}

	public CollatorMapOutput[] getMap_outputs() {
		return map_outputs;
	}

	public void setMap_outputs(CollatorMapOutput[] map_outputs) {
		this.map_outputs = map_outputs;
	}

	public String getOutput_message_signature() {
		return output_message_signature;
	}

	public void setOutput_message_signature(String output_message_signature) {
		this.output_message_signature = output_message_signature;
	}


}
