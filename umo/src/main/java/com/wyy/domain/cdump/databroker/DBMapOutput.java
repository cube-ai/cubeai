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

public class DBMapOutput implements Serializable {

	private static final long serialVersionUID = -2003941950541351800L;

	private DBOutputField output_field;

	/**
	 * @return the output_field
	 */
	public DBOutputField getOutput_field() {
		return output_field;
	}

	/**
	 * @param output_field the output_field to set
	 */
	public void setOutput_field(DBOutputField output_field) {
		this.output_field = output_field;
	}

	@Override
	public String toString() {
		return "DBMapOutput [output_field=" + output_field + "]";
	}




}
