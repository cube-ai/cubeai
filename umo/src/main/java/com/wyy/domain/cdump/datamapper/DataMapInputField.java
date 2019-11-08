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

public class DataMapInputField implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = -7735575155080257503L;
	private String tag;
	private String role;
	private String name;
	private String type;
	//private ComplexType complexType;
	private String mapped_to_message;
	private String mapped_to_field;

	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMapped_to_message() {
		return mapped_to_message;
	}
	public void setMapped_to_message(String mapped_to_message) {
		this.mapped_to_message = mapped_to_message;
	}
	public String getMapped_to_field() {
		return mapped_to_field;
	}
	public void setMapped_to_field(String mapped_to_field) {
		this.mapped_to_field = mapped_to_field;
	}

}
