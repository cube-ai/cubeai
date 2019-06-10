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
 * 模型收藏
 */
@ApiModel(description = "模型收藏")
@Entity
@Table(name = "solution_favorite")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SolutionFavorite implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_login")
    private String userLogin;

    @Column(name = "solution_uuid")
    private String solutionUuid;

    @Column(name = "solution_name")
    private String solutionName;

    @Column(name = "solution_author")
    private String solutionAuthor;

    @Column(name = "solution_created_date")
    private Instant solutionCreatedDate;

    @Column(name = "favorite_date")
    private Instant favoriteDate;

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

    public SolutionFavorite userLogin(String userLogin) {
        this.userLogin = userLogin;
        return this;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getSolutionUuid() {
        return solutionUuid;
    }

    public SolutionFavorite solutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
        return this;
    }

    public void setSolutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
    }

    public String getSolutionName() {
        return solutionName;
    }

    public SolutionFavorite solutionName(String solutionName) {
        this.solutionName = solutionName;
        return this;
    }

    public void setSolutionName(String solutionName) {
        this.solutionName = solutionName;
    }

    public String getSolutionAuthor() {
        return solutionAuthor;
    }

    public SolutionFavorite solutionAuthor(String solutionAuthor) {
        this.solutionAuthor = solutionAuthor;
        return this;
    }

    public void setSolutionAuthor(String solutionAuthor) {
        this.solutionAuthor = solutionAuthor;
    }

    public Instant getSolutionCreatedDate() {
        return solutionCreatedDate;
    }

    public SolutionFavorite solutionCreatedDate(Instant solutionCreatedDate) {
        this.solutionCreatedDate = solutionCreatedDate;
        return this;
    }

    public void setSolutionCreatedDate(Instant solutionCreatedDate) {
        this.solutionCreatedDate = solutionCreatedDate;
    }

    public Instant getFavoriteDate() {
        return favoriteDate;
    }

    public SolutionFavorite favoriteDate(Instant favoriteDate) {
        this.favoriteDate = favoriteDate;
        return this;
    }

    public void setFavoriteDate(Instant favoriteDate) {
        this.favoriteDate = favoriteDate;
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
        SolutionFavorite solutionFavorite = (SolutionFavorite) o;
        if (solutionFavorite.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), solutionFavorite.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SolutionFavorite{" +
            "id=" + getId() +
            ", userLogin='" + getUserLogin() + "'" +
            ", solutionUuid='" + getSolutionUuid() + "'" +
            ", solutionName='" + getSolutionName() + "'" +
            ", solutionAuthor='" + getSolutionAuthor() + "'" +
            ", solutionCreatedDate='" + getSolutionCreatedDate() + "'" +
            ", favoriteDate='" + getFavoriteDate() + "'" +
            "}";
    }
}
