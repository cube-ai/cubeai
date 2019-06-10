package com.wyy.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model for a row in the composite solution mapping table
 */
@ApiModel(description = "Model for a row in the composite solution mapping table")
@Entity
@Table(name = "composite_solution_map")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CompositeSolutionMap implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "Composite (parent) solution ID", required = true)
    @Column(name = "parent_uuid")
    private String parentUuid;

    @ApiModelProperty(value = "Member (child) solution ID", required = true)
    @Column(name = "child_uuid")
    private String childUuid;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
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
