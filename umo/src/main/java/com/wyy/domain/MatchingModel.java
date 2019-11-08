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

import java.io.Serializable;

;

/**
 *
 *
 *
 */
public class MatchingModel implements Serializable{

	private static final long serialVersionUID = 3118322899709122632L;
	private String matchingModelName;
	private String tgifFileNexusURI;
	/**
	 * @return the matchingModelName
	 */
	public String getMatchingModelName() {
		return matchingModelName;
	}
	/**
	 * @param matchingModelName the matchingModelName to set
	 */
	public void setMatchingModelName(String matchingModelName) {
		this.matchingModelName = matchingModelName;
	}
	/**
	 * @return the tgifFileNexusURI
	 */
	public String getTgifFileNexusURI() {
		return tgifFileNexusURI;
	}
	/**
	 * @param tgifFileNexusURI the tgifFileNexusURI to set
	 */
	public void setTgifFileNexusURI(String tgifFileNexusURI) {
		this.tgifFileNexusURI = tgifFileNexusURI;
	}

    @Override
    public int hashCode() {
        return matchingModelName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MatchingModel other = (MatchingModel) obj;
        return this.matchingModelName.equals(other.matchingModelName);
    }
}
