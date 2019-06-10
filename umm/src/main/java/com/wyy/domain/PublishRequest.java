package com.wyy.domain;

import io.swagger.annotations.ApiModel;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * 上架/下架申请及审批记录.
 */
@ApiModel(description = "上架/下架申请及审批记录.")
@Entity
@Table(name = "publish_request")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PublishRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "solution_uuid")
    private String solutionUuid;

    @Column(name = "solution_name")
    private String solutionName;

    @Column(name = "request_user_login")
    private String requestUserLogin;

    @Column(name = "request_type")
    private String requestType;

    @Column(name = "request_reason")
    private String requestReason;

    @Column(name = "request_date")
    private Instant requestDate;

    @Column(name = "reviewed")
    private Boolean reviewed;

    @Column(name = "review_user_login")
    private String reviewUserLogin;

    @Column(name = "review_date")
    private Instant reviewDate;

    @Column(name = "review_result")
    private String reviewResult;

    @Column(name = "review_comment")
    private String reviewComment;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSolutionUuid() {
        return solutionUuid;
    }

    public PublishRequest solutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
        return this;
    }

    public void setSolutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
    }

    public String getSolutionName() {
        return solutionName;
    }

    public PublishRequest solutionName(String solutionName) {
        this.solutionName = solutionName;
        return this;
    }

    public void setSolutionName(String solutionName) {
        this.solutionName = solutionName;
    }

    public String getRequestUserLogin() {
        return requestUserLogin;
    }

    public PublishRequest requestUserLogin(String requestUserLogin) {
        this.requestUserLogin = requestUserLogin;
        return this;
    }

    public void setRequestUserLogin(String requestUserLogin) {
        this.requestUserLogin = requestUserLogin;
    }

    public String getRequestType() {
        return requestType;
    }

    public PublishRequest requestType(String requestType) {
        this.requestType = requestType;
        return this;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestReason() {
        return requestReason;
    }

    public PublishRequest requestReason(String requestReason) {
        this.requestReason = requestReason;
        return this;
    }

    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    public Instant getRequestDate() {
        return requestDate;
    }

    public PublishRequest requestDate(Instant requestDate) {
        this.requestDate = requestDate;
        return this;
    }

    public void setRequestDate(Instant requestDate) {
        this.requestDate = requestDate;
    }

    public Boolean isReviewed() {
        return reviewed;
    }

    public PublishRequest reviewed(Boolean reviewed) {
        this.reviewed = reviewed;
        return this;
    }

    public void setReviewed(Boolean reviewed) {
        this.reviewed = reviewed;
    }

    public String getReviewUserLogin() {
        return reviewUserLogin;
    }

    public PublishRequest reviewUserLogin(String reviewUserLogin) {
        this.reviewUserLogin = reviewUserLogin;
        return this;
    }

    public void setReviewUserLogin(String reviewUserLogin) {
        this.reviewUserLogin = reviewUserLogin;
    }

    public Instant getReviewDate() {
        return reviewDate;
    }

    public PublishRequest reviewDate(Instant reviewDate) {
        this.reviewDate = reviewDate;
        return this;
    }

    public void setReviewDate(Instant reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getReviewResult() {
        return reviewResult;
    }

    public PublishRequest reviewResult(String reviewResult) {
        this.reviewResult = reviewResult;
        return this;
    }

    public void setReviewResult(String reviewResult) {
        this.reviewResult = reviewResult;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public PublishRequest reviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
        return this;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
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
        PublishRequest publishRequest = (PublishRequest) o;
        if (publishRequest.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), publishRequest.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PublishRequest{" +
            "id=" + getId() +
            ", solutionUuid='" + getSolutionUuid() + "'" +
            ", solutionName='" + getSolutionName() + "'" +
            ", requestUserLogin='" + getRequestUserLogin() + "'" +
            ", requestType='" + getRequestType() + "'" +
            ", requestReason='" + getRequestReason() + "'" +
            ", requestDate='" + getRequestDate() + "'" +
            ", reviewed='" + isReviewed() + "'" +
            ", reviewUserLogin='" + getReviewUserLogin() + "'" +
            ", reviewDate='" + getReviewDate() + "'" +
            ", reviewResult='" + getReviewResult() + "'" +
            ", reviewComment='" + getReviewComment() + "'" +
            "}";
    }
}
