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

/*Representation of Output field
 */

public class OutputField implements Serializable {

	private static final long serialVersionUID = 1872377984128344741L;

	@JsonProperty("tag")
	private String tag;

	@JsonProperty("name")
	private String name;

	@JsonProperty("type_and_role_hierarchy_list")
	private TypeAndRoleHierarchyList[] typeAndRoleHierarchyList;

	@JsonProperty("message_signature")
	private String messageSignature;

	@JsonProperty("parameter_tag")
	private String parameterTag;

	@JsonProperty("parameter_name")
	private String parameterName;

	@JsonProperty("parameter_type")
	private String parameterType;

	@JsonProperty("parameter_role")
	private String parameterRole;

	@JsonProperty("target_name")
	private String targetName;

	@JsonProperty("mapped_to_field")
	private String mappedToField;

	@JsonProperty("error_indicator")
	private String errorIndicator;

	@JsonProperty("other_attributes")
	private String otherAttributes;

	/**
	 * Standard POJO no-arg constructor
	 */
	public OutputField() {
		super();
	}

	public OutputField(String tag, String name, TypeAndRoleHierarchyList[] typeAndRoleHierarchyList,
                       String messageSignature, String parameterTag, String parameterName, String parameterType,
                       String parameterRole, String targetName, String mappedToField, String errorIndicator,
                       String otherAttributes) {
		super();
		this.tag = tag;
		this.name = name;
		this.typeAndRoleHierarchyList = typeAndRoleHierarchyList;
		this.messageSignature = messageSignature;
		this.parameterTag = parameterTag;
		this.parameterName = parameterName;
		this.parameterType = parameterType;
		this.parameterRole = parameterRole;
		this.targetName = targetName;
		this.mappedToField = mappedToField;
		this.errorIndicator = errorIndicator;
		this.otherAttributes = otherAttributes;
	}

	@JsonProperty("parameter_tag")
	public String getParameterTag() {
		return parameterTag;
	}

	@JsonProperty("parameter_tag")
	public void setParameterTag(String parameterTag) {
		this.parameterTag = parameterTag;
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

	@JsonProperty("parameter_role")
	public String getParameterRole() {
		return parameterRole;
	}

	@JsonProperty("parameter_role")
	public void setParameterRole(String parameterRole) {
		this.parameterRole = parameterRole;
	}

	@JsonProperty("target_name")
	public String getTargetName() {
		return targetName;
	}

	@JsonProperty("target_name")
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	@JsonProperty("other_attributes")
	public String getOtherAttributes() {
		return otherAttributes;
	}

	@JsonProperty("other_attributes")
	public void setOtherAttributes(String otherAttributes) {
		this.otherAttributes = otherAttributes;
	}

	@JsonProperty("tag")
	public String getTag() {
		return tag;
	}

	@JsonProperty("tag")
	public void setTag(String tag) {
		this.tag = tag;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("type_and_role_hierarchy_list")
	public TypeAndRoleHierarchyList[] getTypeAndRoleHierarchyList() {
		return typeAndRoleHierarchyList;
	}

	@JsonProperty("type_and_role_hierarchy_list")
	public void setTypeAndRoleHierarchyList(TypeAndRoleHierarchyList[] typeAndRoleHierarchyList) {
		this.typeAndRoleHierarchyList = typeAndRoleHierarchyList;
	}

	@JsonProperty("message_signature")
	public String getMessageSignature() {
		return messageSignature;
	}

	@JsonProperty("message_signature")
	public void setMessageSignature(String messageSignature) {
		this.messageSignature = messageSignature;
	}

	@JsonProperty("mapped_to_field")
	public String getMappedToField() {
		return mappedToField;
	}

	@JsonProperty("mapped_to_field")
	public void setMappedToField(String mappedToField) {
		this.mappedToField = mappedToField;
	}

	@JsonProperty("error_indicator")
	public String getErrorIndicator() {
		return errorIndicator;
	}

	@JsonProperty("error_indicator")
	public void setErrorIndicator(String errorIndicator) {
		this.errorIndicator = errorIndicator;
	}

	@Override
	public String toString() {
		return "OutputField [tag = " + tag + ", name = " + name + ", typeAndRoleHierarchyList = "
				+ typeAndRoleHierarchyList + ",  parameterTag " + parameterTag + ",  parameterName " + parameterName
				+ ",  parameterType " + parameterType + ",  parameterRole " + parameterRole + ", targetName "
				+ targetName + ",   otherAttributes " + otherAttributes + ",   mappedToField " + mappedToField
				+ ",   errorIndicator " + errorIndicator + ",   messageSignature " + messageSignature + "]";

	}
}
