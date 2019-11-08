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

public class MapOutput implements Serializable {

	private static final long serialVersionUID = -3626431575255694502L;
	private String message_name;
	private DataMapOutputField[] output_fields;

	public String getMessage_name() {
		return message_name;
	}

	public void setMessage_name(String message_name) {
		this.message_name = message_name;
	}

	public DataMapOutputField[] getOutput_fields() {
		return output_fields;
	}

	public void setOutput_fields(DataMapOutputField[] output_fields) {
		this.output_fields = output_fields;
	}

}
