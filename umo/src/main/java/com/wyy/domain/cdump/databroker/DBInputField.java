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

public class DBInputField implements Serializable{

	private static final long serialVersionUID = 5104542735630234525L;

	private String name;
	private String type;
	private String checked;
	private String mapped_to_field;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the checked
	 */
	public String getChecked() {
		return checked;
	}
	/**
	 * @param checked the checked to set
	 */
	public void setChecked(String checked) {
		this.checked = checked;
	}
	/**
	 * @return the mapped_to_field
	 */
	public String getMapped_to_field() {
		return mapped_to_field;
	}
	/**
	 * @param mapped_to_field the mapped_to_field to set
	 */
	public void setMapped_to_field(String mapped_to_field) {
		this.mapped_to_field = mapped_to_field;
	}

	@Override
	public String toString() {
		return "DBInputField [name=" + name + ", type=" + type + ", checked=" + checked + ", mapped_to_field="
				+ mapped_to_field + "]";
	}



}
