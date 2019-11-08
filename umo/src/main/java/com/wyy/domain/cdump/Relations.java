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

import java.io.Serializable;
import java.util.List;

public class Relations implements Serializable{
	private static final long serialVersionUID = -972174523695525071L;

	private String linkName = "";
	private String linkId = "";
	private String sourceNodeName = "";
	private String targetNodeName = "";
	private String targetNodeId = "";
	private String targetNodeCapability = "";
	private String sourceNodeId = "";
	private String sourceNodeRequirement;
    private String input= "";
    private String output= "";
    private String start= "";
    private String end= "";

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    /**
	 * @return the sourceNodeName
	 */
	public String getSourceNodeName() {
		return sourceNodeName;
	}
	/**
	 * @param sourceNodeName the sourceNodeName to set
	 */
	public void setSourceNodeName(String sourceNodeName) {
		this.sourceNodeName = sourceNodeName;
	}
	/**
	 * @return the targetNodeName
	 */
	public String getTargetNodeName() {
		return targetNodeName;
	}
	/**
	 * @param targetNodeName the targetNodeName to set
	 */
	public void setTargetNodeName(String targetNodeName) {
		this.targetNodeName = targetNodeName;
	}
	/**
	 * @return the targetNodeCapability
	 */
	public String getTargetNodeCapability() {
		return targetNodeCapability;
	}
	/**
	 * @param targetNodeCapability the targetNodeCapability to set
	 */
	public void setTargetNodeCapability(String targetNodeCapability) {
		this.targetNodeCapability = targetNodeCapability;
	}
	/**
	 * @return the sourceNodeRequirement
	 */
	public String getSourceNodeRequirement() {
		return sourceNodeRequirement;
	}
	/**
	 * @param sourceNodeRequirement the sourceNodeRequirement to set
	 */
	public void setSourceNodeRequirement(String sourceNodeRequirement) {
		this.sourceNodeRequirement = sourceNodeRequirement;
	}
	public String getLinkName() {
		return linkName;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	public String getLinkId() {
		return linkId;
	}
	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}


	public String getTargetNodeId() {
		return targetNodeId;
	}
	public void setTargetNodeId(String targetNodeId) {
		this.targetNodeId = targetNodeId;
	}

	public String getSourceNodeId() {
		return sourceNodeId;
	}
	public void setSourceNodeId(String sourceNodeId) {
		this.sourceNodeId = sourceNodeId;
	}

}
