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
import java.util.Objects;

/**
 * Model for a row in the composite solution mapping table
 */

public class CompositeSolutionMap implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String parentUuid;

    private String childUuid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public CompositeSolutionMap parentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
        return this;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    public String getChildUuid() {
        return childUuid;
    }

    public CompositeSolutionMap childUuid(String childUuid) {
        this.childUuid = childUuid;
        return this;
    }

    public void setChildUuid(String childUuid) {
        this.childUuid = childUuid;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CompositeSolutionMap compositeSolutionMap = (CompositeSolutionMap) o;
        if (compositeSolutionMap.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), compositeSolutionMap.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CompositeSolutionMap{" +
            "id=" + getId() +
            ", parentUuid='" + getParentUuid() + "'" +
            ", childUuid='" + getChildUuid() + "'" +
            "}";
    }
}
