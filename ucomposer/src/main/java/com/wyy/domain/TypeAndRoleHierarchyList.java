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

package com.wyy.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/*Representation of Type And Role Hierarchy List
 */

public class TypeAndRoleHierarchyList implements Serializable {

	private static final long serialVersionUID = -7458058944769738381L;

	@JsonProperty("name")
	private String name;

	@JsonProperty("role")
	private String role;

	/**
	 * Standard POJO no-arg constructor
	 */
	public TypeAndRoleHierarchyList() {
		super();
	}

	public TypeAndRoleHierarchyList(String name, String role) {
		super();
		this.name = name;
		this.role = role;

	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("role")
	public String getRole() {
		return role;
	}

	@JsonProperty("role")
	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "TypeAndRoleHierarchyList [name = " + name + ", role = " + role + "]";
	}
}
