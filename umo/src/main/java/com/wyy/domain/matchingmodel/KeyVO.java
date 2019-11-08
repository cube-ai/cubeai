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

package com.wyy.domain.matchingmodel;

import java.io.Serializable;

public class KeyVO implements Serializable {

	private static final long serialVersionUID = 6428821893743872728L;

	private String portType;
	private int numberofFields;
	private boolean nestedMessage;

	public String getPortType() {
		return portType;
	}

	public void setPortType(String portType) {
		this.portType = portType;
	}

	public int getNumberofFields() {
		return numberofFields;
	}

	public void setNumberofFields(int numberofFields) {
		this.numberofFields = numberofFields;
	}

	public boolean isNestedMessage() {
		return nestedMessage;
	}

	public void setNestedMessage(boolean nestedMessage) {
		this.nestedMessage = nestedMessage;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (nestedMessage ? 1231 : 1237);
		result = prime * result + numberofFields;
		result = prime * result + ((portType == null) ? 0 : portType.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyVO other = (KeyVO) obj;
		if (nestedMessage != other.nestedMessage)
			return false;
		if (numberofFields != other.numberofFields)
			return false;
		if (portType == null) {
			if (other.portType != null)
				return false;
		} else if (!portType.equals(other.portType))
			return false;
		return true;
	}



}
