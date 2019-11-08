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

public class DataMapOutputField implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1480016409521727866L;
	private String tag;
	private String role;
	private String name;
	private String type;
	//private ComplexType complexType;

	/**
	 * @return the tag
	 */
	public String gettag() {
		return tag;
	}
	/**
	 * @param tag the tag to set
	 */
	public void settag(String tag) {
		this.tag = tag;
	}
	/**
	 * @return the role
	 */
	public String getrole() {
		return role;
	}
	/**
	 * @param role the role to set
	 */
	public void setrole(String role) {
		this.role = role;
	}
	/**
	 * @return the name
	 */
	public String getname() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setname(String name) {
		this.name = name;
	}
	/**
	 * @return the type
	 */
	public String gettype() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void settype(String type) {
		this.type = type;
	}

}
