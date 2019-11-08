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

/**
 *
 */
package com.wyy.domain.cdump;

import java.io.Serializable;

public class Message implements Serializable{
	private static final long serialVersionUID = -1288328507537289159L;

	private String messageName;
	private Argument[] messageargumentList;

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
	public Argument[] getMessageargumentList() {
		return messageargumentList;
	}
	/**
	 * @param messageargumentList the messageargumentList to set
	 */
	public void setMessageargumentList(Argument[] messageargumentList) {
		this.messageargumentList = messageargumentList;
	}




}
