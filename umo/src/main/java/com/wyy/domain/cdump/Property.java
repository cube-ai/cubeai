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

import com.wyy.domain.cdump.collator.CollatorMap;
import com.wyy.domain.cdump.databroker.DataBrokerMap;
import com.wyy.domain.cdump.datamapper.DataMap;
import com.wyy.domain.cdump.splitter.SplitterMap;

import java.io.Serializable;

public class Property implements Serializable {

	private static final long serialVersionUID = -8446568394003104679L;

	private DataMap data_map;
	private DataBrokerMap data_broker_map;
	private CollatorMap collator_map;
	private SplitterMap splitter_map;

	public DataMap getData_map() {
		return data_map;
	}

	public void setData_map(DataMap data_map) {
		this.data_map = data_map;
	}

	public DataBrokerMap getData_broker_map() {
		return data_broker_map;
	}

	public void setData_broker_map(DataBrokerMap data_broker_map) {
		this.data_broker_map = data_broker_map;
	}

	public CollatorMap getCollator_map() {
		return collator_map;
	}

	public void setCollator_map(CollatorMap collator_map) {
		this.collator_map = collator_map;
	}

	public SplitterMap getSplitter_map() {
		return splitter_map;
	}

	public void setSplitter_map(SplitterMap splitter_map) {
		this.splitter_map = splitter_map;
	}


}
