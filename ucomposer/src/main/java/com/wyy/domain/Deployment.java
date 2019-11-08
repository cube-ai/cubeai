package com.wyy.domain;

import java.time.Instant;
import java.util.Objects;


public class Deployment {

    private Long id;
    private String uuid;
    private String deployer;
    private String solutionUuid;
    private String solutionName;
    private String solutionAuthor;
    private Integer k8sPort;
    private Boolean isPublic;
    private Instant createdDate;
    private Instant modifiedDate;
    private String pictureUrl;
    private String modelType;
    private String toolkitType;
    private Long callCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public Deployment uuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDeployer() {
        return deployer;
    }

    public Deployment deployer(String deployer) {
        this.deployer = deployer;
        return this;
    }

    public void setDeployer(String deployer) {
        this.deployer = deployer;
    }

    public String getSolutionUuid() {
        return solutionUuid;
    }

    public Deployment solutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
        return this;
    }

    public void setSolutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
    }

    public String getSolutionName() {
        return solutionName;
    }

    public Deployment solutionName(String solutionName) {
        this.solutionName = solutionName;
        return this;
    }

    public void setSolutionName(String solutionName) {
        this.solutionName = solutionName;
    }

    public String getSolutionAuthor() {
        return solutionAuthor;
    }

    public Deployment solutionAuthor(String solutionAuthor) {
        this.solutionAuthor = solutionAuthor;
        return this;
    }

    public void setSolutionAuthor(String solutionAuthor) {
        this.solutionAuthor = solutionAuthor;
    }

    public Integer getk8sPort() {
        return k8sPort;
    }

    public Deployment k8sPort(Integer k8sPort) {
        this.k8sPort = k8sPort;
        return this;
    }

    public void setk8sPort(Integer k8sPort) {
        this.k8sPort = k8sPort;
    }

    public Boolean isIsPublic() {
        return isPublic;
    }

    public Deployment isPublic(Boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Deployment createdDate(Instant createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public Deployment modifiedDate(Instant modifiedDate) {
        this.modifiedDate = modifiedDate;
        return this;
    }

    public void setModifiedDate(Instant modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getModelType() {
        return modelType;
    }

    public Deployment modelType(String modelType) {
        this.modelType = modelType;
        return this;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getToolkitType() {
        return toolkitType;
    }

    public Deployment toolkitType(String toolkitType) {
        this.toolkitType = toolkitType;
        return this;
    }

    public void setToolkitType(String toolkitType) {
        this.toolkitType = toolkitType;
    }

    public Long getCallCount() {
        return callCount;
    }

    public Deployment callCount(Long callCount) {
        this.callCount = callCount;
        return this;
    }

    public void setCallCount(Long callCount) {
        this.callCount = callCount;
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
        Deployment deployment = (Deployment) o;
        if (deployment.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), deployment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Deployment{" +
            "id=" + getId() +
            ", uuid='" + getUuid() + "'" +
            ", deployer='" + getDeployer() + "'" +
            ", solutionUuid='" + getSolutionUuid() + "'" +
            ", solutionName='" + getSolutionName() + "'" +
            ", solutionAuthor='" + getSolutionAuthor() + "'" +
            ", k8sPort=" + getk8sPort() +
            ", isPublic='" + isIsPublic() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", modifiedDate='" + getModifiedDate() + "'" +
            ", pictureUrl='" + getPictureUrl() + "'" +
            ", modelType='" + getModelType() + "'" +
            ", toolkitType='" + getToolkitType() + "'" +
            ", callCount=" + getCallCount() +
            "}";
    }
}

