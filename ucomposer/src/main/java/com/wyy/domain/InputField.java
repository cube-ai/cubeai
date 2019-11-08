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

/* Representation of Input fields in Map Inputs*/

public class InputField implements Serializable {

	private static final long serialVersionUID = 4500633480295491100L;

	@JsonProperty("name")
	private String name;

	@JsonProperty("type")
	private String type;

	@JsonProperty("checked")
	private String checked;

	@JsonProperty("source_name")
	private String sourceName;

	@JsonProperty("message_signature")
	private String messageSignature;

	@JsonProperty("parameter_name")
	private String parameterName;

	@JsonProperty("parameter_type")
	private String parameterType;

	@JsonProperty("parameter_tag")
	private String parameterTag;

	@JsonProperty("parameter_role")
	private String parameterRole;

	@JsonProperty("mapped_to_field")
	private String mappedToField;

	@JsonProperty("error_indicator")
	private String errorIndicator;

	@JsonProperty("other_attributes")
	private String otherAttributes;

	/**
	 * Standard POJO no-arg constructor
	 */
	public InputField() {
		super();
	}

	public InputField(String mappedToField, String name, String checked, String sourceName, String parameterName,
                      String parameterType, String parameterTag, String errorIndicator, String otherAttributes,
                      String messageSignature, String parameterRole) {
		super();
		this.mappedToField = mappedToField;
		this.name = name;
		this.checked = checked;
		this.sourceName = sourceName;
		this.parameterName = parameterName;
		this.parameterType = parameterType;
		this.parameterTag = parameterTag;
		this.errorIndicator = errorIndicator;
		this.otherAttributes = otherAttributes;
		this.messageSignature = messageSignature;
		this.parameterRole = parameterRole;
	}

	@JsonProperty("mapped_to_field")
	public String getMappedToField() {
		return mappedToField;
	}

	@JsonProperty("mapped_to_field")
	public void setMappedToField(String mappedToField) {
		this.mappedToField = mappedToField;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("checked")
	public String getChecked() {
		return checked;
	}

	@JsonProperty("checked")
	public void setChecked(String checked) {
		this.checked = checked;
	}

	@JsonProperty("source_name")
	public String getSourceName() {
		return sourceName;
	}

	@JsonProperty("source_name")
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	@JsonProperty("parameter_name")
	public String getParameterName() {
		return parameterName;
	}

	@JsonProperty("parameter_name")
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	@JsonProperty("parameter_type")
	public String getParameterType() {
		return parameterType;
	}

	@JsonProperty("parameter_type")
	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

	@JsonProperty("parameter_tag")
	public String getParameterTag() {
		return parameterTag;
	}

	@JsonProperty("parameter_tag")
	public void setParameterTag(String parameterTag) {
		this.parameterTag = parameterTag;
	}

	@JsonProperty("error_indicator")
	public String getErrorIndicator() {
		return errorIndicator;
	}

	@JsonProperty("error_indicator")
	public void setErrorIndicator(String errorIndicator) {
		this.errorIndicator = errorIndicator;
	}

	@JsonProperty("other_attributes")
	public String getOtherAttributes() {
		return otherAttributes;
	}

	@JsonProperty("other_attributes")
	public void setOtherAttributes(String otherAttributes) {
		this.otherAttributes = otherAttributes;
	}

	@JsonProperty("message_signature")
	public String getMessageSignature() {
		return messageSignature;
	}

	@JsonProperty("message_signature")
	public void setMessageSignature(String messageSignature) {
		this.messageSignature = messageSignature;
	}

	@JsonProperty("parameter_role")
	public String getParameterRole() {
		return parameterRole;
	}

	@JsonProperty("parameter_role")
	public void setParameterRole(String parameterRole) {
		this.parameterRole = parameterRole;
	}

	@Override
	public String toString() {
		return "InputField [mappedToField = " + mappedToField + ", name = " + name + ", type = " + type + ", checked = "
				+ checked + ", sourceName  = " + sourceName + ",  parameterName = " + parameterName
				+ ", parameterType = " + parameterType + ", parameterTag = " + parameterTag + ",errorIndicator = "
				+ errorIndicator + ", otherAttributes = " + otherAttributes + ", messageSignature = " + messageSignature
				+ ", parameterRole = " + parameterRole + "]";

	}
}
