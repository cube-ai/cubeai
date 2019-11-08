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

public class FieldMap implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -2619340234448624842L;
	String map_action;
	String input_field_message_name;
	String input_field_tag_id;
	String output_field_message_name;
	String output_field_tag_id;

	public String getMap_action() {
		return map_action;
	}
	public void setMap_action(String map_action) {
		this.map_action = map_action;
	}
	public String getInput_field_message_name() {
		return input_field_message_name;
	}
	public void setInput_field_message_name(String input_field_message_name) {
		this.input_field_message_name = input_field_message_name;
	}
	public String getInput_field_tag_id() {
		return input_field_tag_id;
	}
	public void setInput_field_tag_id(String input_field_tag_id) {
		this.input_field_tag_id = input_field_tag_id;
	}
	public String getOutput_field_message_name() {
		return output_field_message_name;
	}
	public void setOutput_field_message_name(String output_field_message_name) {
		this.output_field_message_name = output_field_message_name;
	}
	public String getOutput_field_tag_id() {
		return output_field_tag_id;
	}
	public void setOutput_field_tag_id(String output_field_tag_id) {
		this.output_field_tag_id = output_field_tag_id;
	}


}
