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

package com.wyy.domain.cdump.databroker;

import java.io.Serializable;

public class DBMapInput implements Serializable {

	private static final long serialVersionUID = 8260073504366872566L;

	private DBInputField input_field;

	/**
	 * @return the input_field
	 */
	public DBInputField getInput_field() {
		return input_field;
	}

	/**
	 * @param input_field the input_field to set
	 */
	public void setInput_field(DBInputField input_field) {
		this.input_field = input_field;
	}

	@Override
	public String toString() {
		return "DBMapInput [input_field=" + input_field + "]";
	}




}
