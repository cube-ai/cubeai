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


package com.wyy.domain.cdump.splitter;

import java.io.Serializable;

public class SplitterInputField implements Serializable{

	private static final long serialVersionUID = 5530807834979193156L;

	private String parameter_name;
	private String parameter_type;
	private String parameter_tag;
	private String parameter_role;


	public String getParameter_role() {
		return parameter_role;
	}
	public void setParameter_role(String parameter_role) {
		this.parameter_role = parameter_role;
	}
	public String getParameter_name() {
		return parameter_name;
	}
	public void setParameter_name(String parameter_name) {
		this.parameter_name = parameter_name;
	}
	public String getParameter_type() {
		return parameter_type;
	}
	public void setParameter_type(String parameter_type) {
		this.parameter_type = parameter_type;
	}
	public String getParameter_tag() {
		return parameter_tag;
	}
	public void setParameter_tag(String parameter_tag) {
		this.parameter_tag = parameter_tag;
	}



}
