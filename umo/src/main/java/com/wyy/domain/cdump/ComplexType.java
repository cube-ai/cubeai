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


import com.wyy.domain.protobuf.MessageargumentList;

import java.io.Serializable;
import java.util.List;


public class ComplexType implements Serializable {

	private static final long serialVersionUID = -8550690156793239836L;

	private String messageName ="";
	private List<MessageargumentList> messageargumentList;

	/**
	 * @return the messageName
	 */
	public String getMessageName() {
		return messageName;
	}
	/**
	 * @param messageName the messageName to set
	 */
	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}
	/**
	 * @return the messageargumentList
	 */
	public List<MessageargumentList> getMessageargumentList() {
		return messageargumentList;
	}
	/**
	 * @param messageargumentList the messageargumentList to set
	 */
	public void setMessageargumentList(List<MessageargumentList> messageargumentList) {
		this.messageargumentList = messageargumentList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((messageargumentList == null) ? 0 : messageargumentList.hashCode());
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
		ComplexType other = (ComplexType) obj;
		if (messageargumentList == null) {
			if (other.messageargumentList != null)
				return false;
		} else if (!messageargumentList.equals(other.messageargumentList))
			return false;
		return true;
	}


}
