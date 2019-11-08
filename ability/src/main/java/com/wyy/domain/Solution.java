package com.wyy.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;


public class Solution implements Serializable{

    private Long id;
    private String uuid;
    private String authorLogin;
    private String authorName;
    private String company;
    private String coAuthors;
    private String name;
    private String version;
    private String summary;
    private String tag1;
    private String tag2;
    private String tag3;
    private String subject1;
    private String subject2;
    private String subject3;
    private Long displayOrder;
    private String pictureUrl;
    private Boolean active;
    private String modelType;
    private String toolkitType;
    private String validationStatus;
    private String publishStatus;
    private String publishRequest;
    private Instant createdDate;
    private Instant modifiedDate;
    private Long viewCount;
    private Long downloadCount;
    private Instant lastDownload;
    private Long commentCount;
    private Long ratingCount;
    private Double ratingAverage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public Solution uuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAuthorLogin() {
        return authorLogin;
    }

    public Solution authorLogin(String authorLogin) {
        this.authorLogin = authorLogin;
        return this;
    }

    public void setAuthorLogin(String authorLogin) {
        this.authorLogin = authorLogin;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Solution authorName(String authorName) {
        this.authorName = authorName;
        return this;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCompany() {
        return company;
    }

    public Solution company(String company) {
        this.company = company;
        return this;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCoAuthors() {
        return coAuthors;
    }

    public Solution coAuthors(String coAuthors) {
        this.coAuthors = coAuthors;
        return this;
    }

    public void setCoAuthors(String coAuthors) {
        this.coAuthors = coAuthors;
    }

    public String getName() {
        return name;
    }

    public Solution name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public Solution version(String version) {
        this.version = version;
        return this;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSummary() {
        return summary;
    }

    public Solution summary(String summary) {
        this.summary = summary;
        return this;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTag1() {
        return tag1;
    }

    public Solution tag1(String tag1) {
        this.tag1 = tag1;
        return this;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public Solution tag2(String tag2) {
        this.tag2 = tag2;
        return this;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public String getTag3() {
        return tag3;
    }

    public Solution tag3(String tag3) {
        this.tag3 = tag3;
        return this;
    }

    public void setTag3(String tag3) {
        this.tag3 = tag3;
    }

    public String getSubject1() {
        return subject1;
    }

    public Solution subject1(String subject1) {
        this.subject1 = subject1;
        return this;
    }

    public void setSubject1(String subject1) {
        this.subject1 = subject1;
    }

    public String getSubject2() {
        return subject2;
    }

    public Solution subject2(String subject2) {
        this.subject2 = subject2;
        return this;
    }

    public void setSubject2(String subject2) {
        this.subject2 = subject2;
    }

    public String getSubject3() {
        return subject3;
    }

    public Solution subject3(String subject3) {
        this.subject3 = subject3;
        return this;
    }

    public void setSubject3(String subject3) {
        this.subject3 = subject3;
    }

    public Long getDisplayOrder() {
        return displayOrder;
    }

    public Solution displayOrder(Long displayOrder) {
        this.displayOrder = displayOrder;
        return this;
    }

    public void setDisplayOrder(Long displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public Solution pictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
        return this;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Boolean isActive() {
        return active;
    }

    public Solution active(Boolean active) {
        this.active = active;
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getModelType() {
        return modelType;
    }

    public Solution modelType(String modelType) {
        this.modelType = modelType;
        return this;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getToolkitType() {
        return toolkitType;
    }

    public Solution toolkitType(String toolkitType) {
        this.toolkitType = toolkitType;
        return this;
    }

    public void setToolkitType(String toolkitType) {
        this.toolkitType = toolkitType;
    }

    public String getValidationStatus() {
        return validationStatus;
    }

    public Solution validationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
        return this;
    }

    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
    }

    public String getPublishStatus() {
        return publishStatus;
    }

    public Solution publishStatus(String publishStatus) {
        this.publishStatus = publishStatus;
        return this;
    }

    public void setPublishStatus(String publishStatus) {
        this.publishStatus = publishStatus;
    }

    public String getPublishRequest() {
        return publishRequest;
    }

    public Solution publishRequest(String publishRequest) {
        this.publishRequest = publishRequest;
        return this;
    }

    public void setPublishRequest(String publishRequest) {
        this.publishRequest = publishRequest;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Solution createdDate(Instant createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public Solution modifiedDate(Instant modifiedDate) {
        this.modifiedDate = modifiedDate;
        return this;
    }

    public void setModifiedDate(Instant modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public Solution viewCount(Long viewCount) {
        this.viewCount = viewCount;
        return this;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Long getDownloadCount() {
        return downloadCount;
    }

    public Solution downloadCount(Long downloadCount) {
        this.downloadCount = downloadCount;
        return this;
    }

    public void setDownloadCount(Long downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Instant getLastDownload() {
        return lastDownload;
    }

    public Solution lastDownload(Instant lastDownload) {
        this.lastDownload = lastDownload;
        return this;
    }

    public void setLastDownload(Instant lastDownload) {
        this.lastDownload = lastDownload;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public Solution commentCount(Long commentCount) {
        this.commentCount = commentCount;
        return this;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public Long getRatingCount() {
        return ratingCount;
    }

    public Solution ratingCount(Long ratingCount) {
        this.ratingCount = ratingCount;
        return this;
    }

    public void setRatingCount(Long ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Double getRatingAverage() {
        return ratingAverage;
    }

    public Solution ratingAverage(Double ratingAverage) {
        this.ratingAverage = ratingAverage;
        return this;
    }

    public void setRatingAverage(Double ratingAverage) {
        this.ratingAverage = ratingAverage;
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
        Solution solution = (Solution) o;
        if (solution.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), solution.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Solution{" +
            "id=" + getId() +
            ", uuid='" + getUuid() + "'" +
            ", authorLogin='" + getAuthorLogin() + "'" +
            ", authorName='" + getAuthorName() + "'" +
            ", company='" + getCompany() + "'" +
            ", coAuthors='" + getCoAuthors() + "'" +
            ", name='" + getName() + "'" +
            ", version='" + getVersion() + "'" +
            ", summary='" + getSummary() + "'" +
            ", tag1='" + getTag1() + "'" +
            ", tag2='" + getTag2() + "'" +
            ", tag3='" + getTag3() + "'" +
            ", subject1='" + getSubject1() + "'" +
            ", subject2='" + getSubject2() + "'" +
            ", subject3='" + getSubject3() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", pictureUrl='" + getPictureUrl() + "'" +
            ", active='" + isActive() + "'" +
            ", modelType='" + getModelType() + "'" +
            ", toolkitType='" + getToolkitType() + "'" +
            ", validationStatus='" + getValidationStatus() + "'" +
            ", publishStatus='" + getPublishStatus() + "'" +
            ", publishRequest='" + getPublishRequest() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", modifiedDate='" + getModifiedDate() + "'" +
            ", viewCount=" + getViewCount() +
            ", downloadCount=" + getDownloadCount() +
            ", lastDownload='" + getLastDownload() + "'" +
            ", commentCount=" + getCommentCount() +
            ", ratingCount=" + getRatingCount() +
            ", ratingAverage=" + getRatingAverage() +
            "}";
    }
}
