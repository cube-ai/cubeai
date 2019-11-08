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

public class Tgif implements Serializable {

	private static final long serialVersionUID = 1L;

	private Self self;
	private Stream streams;
	private Service services;
	private Parameter[] parameters;
	private Auxiliary auxiliary;
	private TgifArtifact[] artifacts;

	public Tgif() {
		super();
	}

	/**
	 *
	 * @param self
	 *            Self
	 * @param streams
	 *            Stream
	 * @param services
	 *            Service
	 * @param parameters
	 *            Array of Parameter
	 * @param auxiliary
	 *            Auxiliary
	 * @param artifacts
	 *            Array of TgifArtifact
	 */
	public Tgif(Self self, Stream streams, Service services, Parameter[] parameters, Auxiliary auxiliary,
                TgifArtifact[] artifacts) {
		super();
		this.self = self;
		this.streams = streams;
		this.services = services;
		this.parameters = parameters;
		this.auxiliary = auxiliary;
		this.artifacts = artifacts;
	}

	/**
	 * @return the self
	 */
	public Self getSelf() {
		return self;
	}

	/**
	 * @param self
	 *            the self to set
	 */
	public void setSelf(Self self) {
		this.self = self;
	}

	/**
	 * @return the streams
	 */
	public Stream getStreams() {
		return streams;
	}

	/**
	 * @param streams
	 *            the streams to set
	 */
	public void setStreams(Stream streams) {
		this.streams = streams;
	}

	/**
	 * @return the services
	 */
	public Service getServices() {
		return services;
	}

	/**
	 * @param services
	 *            the services to set
	 */
	public void setServices(Service services) {
		this.services = services;
	}

	/**
	 * @return the parameters
	 */
	public Parameter[] getParameters() {
		return parameters;
	}

	/**
	 * @param parameters
	 *            the parameters to set
	 */
	public void setParameters(Parameter[] parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the auxilary
	 */
	public Auxiliary getAuxiliary() {
		return auxiliary;
	}

	/**
	 * @param auxiliary
	 *            the auxiliary to set
	 */
	public void setAuxiliary(Auxiliary auxiliary) {
		this.auxiliary = auxiliary;
	}

	/**
	 * @return the artifacts
	 */
	public TgifArtifact[] getArtifacts() {
		return artifacts;
	}

	/**
	 * @param artifacts
	 *            the artifacts to set
	 */
	public void setArtifacts(TgifArtifact[] artifacts) {
		this.artifacts = artifacts;
	}

}
