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
import java.util.Arrays;

public class DBOutputField implements Serializable {

	private static final long serialVersionUID = 4348254387011784964L;

	private String tag;
	private String name;
	private DBOTypeAndRoleHierarchy[] type_and_role_hierarchy_list;

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}
	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}
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
	 * @return the type_and_role_hierarchy_list
	 */
	public DBOTypeAndRoleHierarchy[] getType_and_role_hierarchy_list() {
		return type_and_role_hierarchy_list;
	}
	/**
	 * @param type_and_role_hierarchy_list the type_and_role_hierarchy_list to set
	 */
	public void setType_and_role_hierarchy_list(DBOTypeAndRoleHierarchy[] type_and_role_hierarchy_list) {
		this.type_and_role_hierarchy_list = type_and_role_hierarchy_list;
	}

	@Override
	public String toString() {
		return "DBOutputField [tag=" + tag + ", name=" + name + ", type_and_role_hierarchy_list="
				+ Arrays.toString(type_and_role_hierarchy_list) + "]";
	}



}
