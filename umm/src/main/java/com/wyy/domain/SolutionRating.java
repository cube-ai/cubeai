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
 * 模型评价
 * 一个用户针对一个solution只能由一条评价记录，由solutionUuid和userLogin来唯一确定。
 */
@ApiModel(description = "模型评价 一个用户针对一个solution只能由一条评价记录，由solutionUuid和userLogin来唯一确定。")
@Entity
@Table(name = "solution_rating")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SolutionRating implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_login")
    private String userLogin;

    @Column(name = "solution_uuid")
    private String solutionUuid;

    @Column(name = "rating_score")
    private Integer ratingScore;

    @Column(name = "rating_text")
    private String ratingText;

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

    public String getUserLogin() {
        return userLogin;
    }

    public SolutionRating userLogin(String userLogin) {
        this.userLogin = userLogin;
        return this;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getSolutionUuid() {
        return solutionUuid;
    }

    public SolutionRating solutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
        return this;
    }

    public void setSolutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
    }

    public Integer getRatingScore() {
        return ratingScore;
    }

    public SolutionRating ratingScore(Integer ratingScore) {
        this.ratingScore = ratingScore;
        return this;
    }

    public void setRatingScore(Integer ratingScore) {
        this.ratingScore = ratingScore;
    }

    public String getRatingText() {
        return ratingText;
    }

    public SolutionRating ratingText(String ratingText) {
        this.ratingText = ratingText;
        return this;
    }

    public void setRatingText(String ratingText) {
        this.ratingText = ratingText;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public SolutionRating createdDate(Instant createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public SolutionRating modifiedDate(Instant modifiedDate) {
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
        SolutionRating solutionRating = (SolutionRating) o;
        if (solutionRating.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), solutionRating.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SolutionRating{" +
            "id=" + getId() +
            ", userLogin='" + getUserLogin() + "'" +
            ", solutionUuid='" + getSolutionUuid() + "'" +
            ", ratingScore=" + getRatingScore() +
            ", ratingText='" + getRatingText() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", modifiedDate='" + getModifiedDate() + "'" +
            "}";
    }
}
