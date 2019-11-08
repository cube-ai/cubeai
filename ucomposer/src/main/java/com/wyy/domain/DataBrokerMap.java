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

/*Representation of Data broker Map
 */

public class DataBrokerMap implements Serializable {

	private static final long serialVersionUID = -7169022079856239669L;

	@JsonProperty("map_inputs")
	private MapInputs[] mapInputs;

	@JsonProperty("data_broker_type")
	private String dataBrokerType;

	@JsonProperty("csv_file_field_separator")
	private String csvFileFieldSeparator;

	@JsonProperty("target_system_url")
	private String targetSystemUrl;

	@JsonProperty("map_outputs")
	private MapOutputs[] mapOutputs;

	@JsonProperty("local_system_data_file_path")
	private String localSystemDataFilePath;

	@JsonProperty("first_row")
	private String firstRow;

	@JsonProperty("script")
	private String script;

	/**
	 * Standard POJO no-arg constructor
	 */
	public DataBrokerMap() {
		super();
	}

	public DataBrokerMap(String dataBrokerType, String csvFileFieldSeparator, String targetSystemUrl,
			MapOutputs[] mapOutputs, String localSystemDataFilePath, String firstRow, String script) {
		super();
		this.dataBrokerType = dataBrokerType;
		this.csvFileFieldSeparator = csvFileFieldSeparator;
		this.targetSystemUrl = targetSystemUrl;
		this.mapOutputs = mapOutputs;
		this.localSystemDataFilePath = localSystemDataFilePath;
		this.firstRow = firstRow;
		this.script = script;
	}

	@JsonProperty("map_inputs")
	public MapInputs[] getMapInputs() {
		return mapInputs;
	}

	@JsonProperty("map_inputs")
	public void setMapInputs(MapInputs[] mapInputs) {
		this.mapInputs = mapInputs;
	}

	@JsonProperty("data_broker_type")
	public String getDataBrokerType() {
		return dataBrokerType;
	}

	@JsonProperty("data_broker_type")
	public void setDataBrokerType(String dataBrokerType) {
		this.dataBrokerType = dataBrokerType;
	}

	@JsonProperty("csv_file_field_separator")
	public String getCsvFileFieldSeparator() {
		return csvFileFieldSeparator;
	}

	@JsonProperty("csv_file_field_separator")
	public void setCsvFileFieldSeparator(String csvFileFieldSeparator) {
		this.csvFileFieldSeparator = csvFileFieldSeparator;
	}

	@JsonProperty("target_system_url")
	public String getTargetSystemUrl() {
		return targetSystemUrl;
	}

	@JsonProperty("target_system_url")
	public void setTargetSystemUrl(String targetSystemUrl) {
		this.targetSystemUrl = targetSystemUrl;
	}

	@JsonProperty("map_outputs")
	public MapOutputs[] getMapOutputs() {
		return mapOutputs;
	}

	@JsonProperty("map_outputs")
	public void setMapOutputs(MapOutputs[] mapOutputs) {
		this.mapOutputs = mapOutputs;
	}

	@JsonProperty("local_system_data_file_path")
	public String getLocalSystemDataFilePath() {
		return localSystemDataFilePath;
	}

	@JsonProperty("local_system_data_file_path")
	public void setLocalSystemDataFilePath(String localSystemDataFilePath) {
		this.localSystemDataFilePath = localSystemDataFilePath;
	}

	@JsonProperty("first_row")
	public String getFirstRow() {
		return firstRow;
	}

	@JsonProperty("first_row")
	public void setFirstRow(String firstRow) {
		this.firstRow = firstRow;
	}

	@JsonProperty("script")
	public String getScript() {
		return script;
	}

	@JsonProperty("script")
	public void setScript(String script) {
		this.script = script;
	}

	@Override
	public String toString() {
		return "DataBrokerMap [mapInputs = " + mapInputs + ", dataBrokerType = " + dataBrokerType
				+ ", csvFileFieldSeparator = " + csvFileFieldSeparator + ", targetSystemUrl = " + targetSystemUrl
				+ ", mapOutputs = " + mapOutputs + ", localSystemDataFilePath = " + localSystemDataFilePath
				+ ", firstRow = " + firstRow + ", script = " + script + "]";
	}
}
