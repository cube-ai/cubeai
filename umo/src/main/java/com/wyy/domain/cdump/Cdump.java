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

public class Cdump implements Serializable{

	private static final long serialVersionUID = -4497733479659006216L;

	private String cname;
	private String version;
	private String solutionId;
	private String ctime;
	private String mtime;
	private String probeIndicator;
	private java.util.List<Nodes> nodes;
	private java.util.List<Relations> relations;
	private boolean validSolution;


	public boolean isValidSolution() {
		return validSolution;
	}
	public void setValidSolution(boolean validSolution) {
		this.validSolution = validSolution;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	public String getSolutionId() {
		return solutionId;
	}
	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
	public String getMtime() {
		return mtime;
	}
	public void setMtime(String mtime) {
		this.mtime = mtime;
	}
	public java.util.List<Nodes> getNodes() {
		return nodes;
	}
	public void setNodes(java.util.List<Nodes> nodes) {
		this.nodes = nodes;
	}
	public java.util.List<Relations> getRelations() {
		return relations;
	}
	public void setRelations(java.util.List<Relations> relations) {
		this.relations = relations;
	}
	public String getProbeIndicator() {
		return probeIndicator;
	}
	public void setProbeIndicator(String probeIndicator) {
		this.probeIndicator = probeIndicator;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((solutionId == null) ? 0 : solutionId.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cdump other = (Cdump) obj;
		if (solutionId == null) {
			if (other.solutionId != null)
				return false;
		} else if (!solutionId.equals(other.solutionId))
			return false;
		return true;
	}

    @Override
    public String toString() {
        return "Cdump{" +
            "cname='" + cname + '\'' +
            ", version='" + version + '\'' +
            ", solutionId='" + solutionId + '\'' +
            ", ctime='" + ctime + '\'' +
            ", mtime='" + mtime + '\'' +
            ", probeIndicator='" + probeIndicator + '\'' +
            ", nodes=" + nodes +
            ", relations=" + relations +
            ", validSolution=" + validSolution +
            '}';
    }
}
