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

package com.wyy.domain.tgif;

import java.io.Serializable;

public class Call implements Serializable {

	private static final long serialVersionUID = 1L;
	private String config_key;
	private Request request;
	private Response response;

	public Call() {

	}

	/**
	 *
	 * @param config_key
	 *            Config key
	 * @param request
	 *            Request
	 * @param response
	 *            Response
	 */
	public Call(String config_key, Request request, Response response) {
		super();
		this.config_key = config_key;
		this.request = request;
		this.response = response;
	}

	/**
	 * @return the config_key
	 */
	public String getConfig_key() {
		return config_key;
	}

	/**
	 * @param config_key
	 *            the config_key to set
	 */
	public void setConfig_key(String config_key) {
		this.config_key = config_key;
	}

	/**
	 * @return the request
	 */
	public Request getRequest() {
		return request;
	}

	/**
	 * @param request
	 *            the request to set
	 */
	public void setRequest(Request request) {
		this.request = request;
	}

	/**
	 * @return the response
	 */
	public Response getResponse() {
		return response;
	}

	/**
	 * @param response
	 *            the response to set
	 */
	public void setResponse(Response response) {
		this.response = response;
	}

}
