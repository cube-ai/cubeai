package com.wyy.domain;

import io.swagger.annotations.ApiModel;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * 解决方案（AI模型）
 */
@ApiModel(description = "解决方案（AI模型）")
@Entity
@Table(name = "solution")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Solution implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "author_login")
    private String authorLogin;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "company")
    private String company;

    @Column(name = "name")
    private String name;

    @Column(name = "version")
    private String version;

    @Column(name = "summary")
    private String summary;

    @Column(name = "tag_1")
    private String tag1;

    @Column(name = "tag_2")
    private String tag2;

    @Column(name = "tag_3")
    private String tag3;

    @Column(name = "subject_1")
    private String subject1;

    @Column(name = "subject_2")
    private String subject2;

    @Column(name = "subject_3")
    private String subject3;

    @Column(name = "display_order")
    private Long displayOrder;

    @Lob
    @Column(name = "picture_url")
    private String pictureUrl;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "model_type")
    private String modelType;

    @Column(name = "toolkit_type")
    private String toolkitType;

    @Column(name = "star_count")
    private Long starCount;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "download_count")
    private Long downloadCount;

    @Column(name = "comment_count")
    private Long commentCount;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "modified_date")
    private Instant modifiedDate;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
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

    public Long getStarCount() {
        return starCount;
    }

    public Solution starCount(Long starCount) {
        this.starCount = starCount;
        return this;
    }

    public void setStarCount(Long starCount) {
        this.starCount = starCount;
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
            ", starCount=" + getStarCount() +
            ", viewCount=" + getViewCount() +
            ", downloadCount=" + getDownloadCount() +
            ", commentCount=" + getCommentCount() +
            ", createdDate='" + getCreatedDate() + "'" +
            ", modifiedDate='" + getModifiedDate() + "'" +
            "}";
    }
}
