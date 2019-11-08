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

package com.wyy.domain.cdump;

import java.io.Serializable;

public class Target implements Serializable{
	private static final long serialVersionUID = -4739084610588775205L;

	private String name;
	private String description;

	//private String id;

	public Target(){

	}


	public Target(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}


	/*public Target(String name, String id) {
		super();
		this.name = name;
		this.id = id;
	}

	public Target(String name, String description, String id) {
		super();
		this.name = name;
		this.description = description;
	}*/
	/*public Target(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}
	public Target(String name, String description, String id) {
		super();
		this.name = name;
		this.description = description;
	}*/

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	/*public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}*/




}
