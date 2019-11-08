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

package com.wyy.domain.protobuf;

import java.io.Serializable;
import java.util.List;

public class ProtoBufClass implements Serializable{
	private static final long serialVersionUID = 1L;

	public ProtoBufClass() {
	}

	private List<MessageBody> listOfMessages = null;

	/**
	 * @return the listOfMessages
	 */
	public List<MessageBody> getListOfMessages() {
		return listOfMessages;
	}
	/**
	 * @param listOfMessages the listOfMessages to set
	 */
	public void setListOfMessages(List<MessageBody> listOfMessages) {
		this.listOfMessages = listOfMessages;
	}


}
