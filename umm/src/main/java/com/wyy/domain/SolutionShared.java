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
 * 模型分享
 */
@ApiModel(description = "模型分享")
@Entity
@Table(name = "solution_shared")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SolutionShared implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_user_login")
    private String fromUserLogin;

    @Column(name = "to_user_login")
    private String toUserLogin;

    @Column(name = "solution_uuid")
    private String solutionUuid;

    @Column(name = "solution_name")
    private String solutionName;

    @Column(name = "solution_author")
    private String solutionAuthor;

    @Column(name = "solution_created_date")
    private Instant solutionCreatedDate;

    @Column(name = "share_date")
    private Instant shareDate;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromUserLogin() {
        return fromUserLogin;
    }

    public SolutionShared fromUserLogin(String fromUserLogin) {
        this.fromUserLogin = fromUserLogin;
        return this;
    }

    public void setFromUserLogin(String fromUserLogin) {
        this.fromUserLogin = fromUserLogin;
    }

    public String getToUserLogin() {
        return toUserLogin;
    }

    public SolutionShared toUserLogin(String toUserLogin) {
        this.toUserLogin = toUserLogin;
        return this;
    }

    public void setToUserLogin(String toUserLogin) {
        this.toUserLogin = toUserLogin;
    }

    public String getSolutionUuid() {
        return solutionUuid;
    }

    public SolutionShared solutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
        return this;
    }

    public void setSolutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
    }

    public String getSolutionName() {
        return solutionName;
    }

    public SolutionShared solutionName(String solutionName) {
        this.solutionName = solutionName;
        return this;
    }

    public void setSolutionName(String solutionName) {
        this.solutionName = solutionName;
    }

    public String getSolutionAuthor() {
        return solutionAuthor;
    }

    public SolutionShared solutionAuthor(String solutionAuthor) {
        this.solutionAuthor = solutionAuthor;
        return this;
    }

    public void setSolutionAuthor(String solutionAuthor) {
        this.solutionAuthor = solutionAuthor;
    }

    public Instant getSolutionCreatedDate() {
        return solutionCreatedDate;
    }

    public SolutionShared solutionCreatedDate(Instant solutionCreatedDate) {
        this.solutionCreatedDate = solutionCreatedDate;
        return this;
    }

    public void setSolutionCreatedDate(Instant solutionCreatedDate) {
        this.solutionCreatedDate = solutionCreatedDate;
    }

    public Instant getShareDate() {
        return shareDate;
    }

    public SolutionShared shareDate(Instant shareDate) {
        this.shareDate = shareDate;
        return this;
    }

    public void setShareDate(Instant shareDate) {
        this.shareDate = shareDate;
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
        SolutionShared solutionShared = (SolutionShared) o;
        if (solutionShared.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), solutionShared.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SolutionShared{" +
            "id=" + getId() +
            ", fromUserLogin='" + getFromUserLogin() + "'" +
            ", toUserLogin='" + getToUserLogin() + "'" +
            ", solutionUuid='" + getSolutionUuid() + "'" +
            ", solutionName='" + getSolutionName() + "'" +
            ", solutionAuthor='" + getSolutionAuthor() + "'" +
            ", solutionCreatedDate='" + getSolutionCreatedDate() + "'" +
            ", shareDate='" + getShareDate() + "'" +
            "}";
    }
}
