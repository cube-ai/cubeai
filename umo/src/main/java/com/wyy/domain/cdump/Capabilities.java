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

public class Capabilities implements Serializable{

	private static final long serialVersionUID = 1136087024327606271L;

	private String id;
	private String name;
	private CapabilityTarget target;
	private String target_type;
	private String properties;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	/*public Target getTarget() {
		return target;
	}
	public void setTarget(Target target) {
		this.target = target;
	}*/

	/**
	 * @return the target
	 */
	public CapabilityTarget getTarget() {
		return target;
	}
	/**
	 * @param target the target to set
	 */
	public void setTarget(CapabilityTarget target) {
		this.target = target;
	}
	public String getTarget_type() {
		return target_type;
	}
	public void setTarget_type(String target_type) {
		this.target_type = target_type;
	}
	public String getProperties() {
		return properties;
	}
	public void setProperties(String properties) {
		this.properties = properties;
	}





}
