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
    private String status;
    private Instant createdDate;
    private Instant modifiedDate;
    private String pictureUrl;
    private Long starCount;
    private Long callCount;
    private String demoUrl;
    private String subject1;
    private String subject2;
    private String subject3;
    private Long displayOrder;

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

    public String getStatus() {
        return status;
    }

    public Deployment status(String status) {
        this.status = status;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Long getStarCount() {
        return starCount;
    }

    public Deployment starCount(Long starCount) {
        this.starCount = starCount;
        return this;
    }

    public void setStarCount(Long starCount) {
        this.starCount = starCount;
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

    public String getDemoUrl() {
        return demoUrl;
    }

    public Deployment demoUrl(String demoUrl) {
        this.demoUrl = demoUrl;
        return this;
    }

    public void setDemoUrl(String demoUrl) {
        this.demoUrl = demoUrl;
    }

    public String getSubject1() {
        return subject1;
    }

    public Deployment subject1(String subject1) {
        this.subject1 = subject1;
        return this;
    }

    public void setSubject1(String subject1) {
        this.subject1 = subject1;
    }

    public String getSubject2() {
        return subject2;
    }

    public Deployment subject2(String subject2) {
        this.subject2 = subject2;
        return this;
    }

    public void setSubject2(String subject2) {
        this.subject2 = subject2;
    }

    public String getSubject3() {
        return subject3;
    }

    public Deployment subject3(String subject3) {
        this.subject3 = subject3;
        return this;
    }

    public void setSubject3(String subject3) {
        this.subject3 = subject3;
    }

    public Long getDisplayOrder() {
        return displayOrder;
    }

    public Deployment displayOrder(Long displayOrder) {
        this.displayOrder = displayOrder;
        return this;
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
            ", status='" + getStatus() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", modifiedDate='" + getModifiedDate() + "'" +
            ", pictureUrl='" + getPictureUrl() + "'" +
            ", starCount=" + getStarCount() +
            ", callCount=" + getCallCount() +
            ", demoUrl='" + getDemoUrl() + "'" +
            ", subject1='" + getSubject1() + "'" +
            ", subject2='" + getSubject2() + "'" +
            ", subject3='" + getSubject3() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            "}";
    }
}

