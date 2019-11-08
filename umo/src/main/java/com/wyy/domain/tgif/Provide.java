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

public class Provide implements Serializable {

	private static final long serialVersionUID = 1L;
	private String route;
	private Request request;
	private Response response;

	public Provide() {

	}

	/**
	 *
	 * @param route
	 *            Route
	 * @param request
	 *            Request
	 * @param response
	 *            Response
	 */
	public Provide(String route, Request request, Response response) {
		super();
		this.route = route;
		this.request = request;
		this.response = response;
	}

	/**
	 * @return the route
	 */
	public String getRoute() {
		return route;
	}

	/**
	 * @param route
	 *            the route to set
	 */
	public void setRoute(String route) {
		this.route = route;
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
	 * @return the respose
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
