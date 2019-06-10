package com.wyy.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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

    @Size(max = 512)
    @Column(name = "co_authors", length = 512)
    private String coAuthors;

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

    @Size(max = 512)
    @Column(name = "picture_url", length = 512)
    private String pictureUrl;

    @Column(name = "active")
    private Boolean active;

    /**
     * 模型类型，直接用中文字符串表示。主要包括：分类, 预测, 回归, 数据源, 数据变换, ...
     */
    @ApiModelProperty(value = "模型类型，直接用中文字符串表示。主要包括：分类, 预测, 回归, 数据源, 数据变换, ...")
    @Column(name = "model_type")
    private String modelType;

    /**
     * 工具箱类型，直接用中文字符串表示。主要包括：
     * 模型组合（Composite-Solution）, 模型编排（Design-Studio）, H2O, Probe, R, Scikit-Learn, TensorFlow, Training-Client, Data-Broker, ONAP, ...
     */
    @ApiModelProperty(value = "工具箱类型，直接用中文字符串表示。主要包括： 模型组合（Composite-Solution）, 模型编排（Design-Studio）, H2O, Probe, R, Scikit-Learn, TensorFlow, Training-Client, Data-Broker, ONAP, ...")
    @Column(name = "toolkit_type")
    private String toolkitType;

    /**
     * 验证状态，直接用中文字符串表示。主要包括：提交验证，正在验证，验证通过，验证失败，未验证
     */
    @ApiModelProperty(value = "验证状态，直接用中文字符串表示。主要包括：提交验证，正在验证，验证通过，验证失败，未验证")
    @Column(name = "validation_status")
    private String validationStatus;

    /**
     * 发布状态，直接用中文字符串表示。包括：下架， 上架
     */
    @ApiModelProperty(value = "发布状态，直接用中文字符串表示。包括：下架， 上架")
    @Column(name = "publish_status")
    private String publishStatus;

    /**
     * 上架、下架申请状态，直接用中文字符串表示，包括3种状态：无申请，申请上架, 申请下架
     * publishRequest为申请上架时，publishStatus必定为下架。
     * publishRequest为申请下架时，publishStatus必定为上架。
     * publishRequest为无申请时，publishStatus可为上架或下架。
     */
    @ApiModelProperty(value = "上架、下架申请状态，直接用中文字符串表示，包括3种状态：无申请，申请上架, 申请下架 publishRequest为申请上架时，publishStatus必定为下架。 publishRequest为申请下架时，publishStatus必定为上架。 publishRequest为无申请时，publishStatus可为上架或下架。")
    @Column(name = "publish_request")
    private String publishRequest;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "modified_date")
    private Instant modifiedDate;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "download_count")
    private Long downloadCount;

    @Column(name = "last_download")
    private Instant lastDownload;

    @Column(name = "comment_count")
    private Long commentCount;

    @Column(name = "rating_count")
    private Long ratingCount;

    @Column(name = "rating_average")
    private Double ratingAverage;

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
