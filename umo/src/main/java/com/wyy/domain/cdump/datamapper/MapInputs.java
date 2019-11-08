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

public class MapInputs implements Serializable {

	private static final long serialVersionUID = -8560444235646525017L;
	private String message_name;
	private DataMapInputField[] input_fields;

	public String getMessage_name() {
		return message_name;
	}

	public void setMessage_name(String message_name) {
		this.message_name = message_name;
	}

	public DataMapInputField[] getInput_fields() {
		return input_fields;
	}

	public void setInput_fields(DataMapInputField[] input_fields) {
		this.input_fields = input_fields;
	}

}
