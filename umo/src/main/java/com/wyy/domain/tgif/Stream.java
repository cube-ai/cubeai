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

public class Stream implements Serializable {

	private static final long serialVersionUID = 1L;
	private String[] subscribes;
	private String[] publishes;

	public Stream() {
		super();
	}

	/**
	 *
	 * @param subscribes
	 *            String array
	 * @param publishes
	 *            String array
	 */
	public Stream(String[] subscribes, String[] publishes) {
		super();
		this.subscribes = subscribes;
		this.publishes = publishes;
	}

	/**
	 * @return the subscribes
	 */
	public String[] getSubscribes() {
		return subscribes;
	}

	/**
	 * @param subscribes
	 *            the subscribes to set
	 */
	public void setSubscribes(String[] subscribes) {
		this.subscribes = subscribes;
	}

	/**
	 * @return the publishes
	 */
	public String[] getPublishes() {
		return publishes;
	}

	/**
	 * @param publishes
	 *            the publishes to set
	 */
	public void setPublishes(String[] publishes) {
		this.publishes = publishes;
	}

}
