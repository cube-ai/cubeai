package com.wyy.domain;

import io.swagger.annotations.ApiModel;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * 关注
 */
@ApiModel(description = "关注")
@Entity
@Table(name = "star")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Star implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_login")
    private String userLogin;

    @Column(name = "target_type")
    private String targetType;

    @Column(name = "target_uuid")
    private String targetUuid;

    @Column(name = "star_date")
    private Instant starDate;

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

    public Star userLogin(String userLogin) {
        this.userLogin = userLogin;
        return this;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getTargetType() {
        return targetType;
    }

    public Star targetType(String targetType) {
        this.targetType = targetType;
        return this;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetUuid() {
        return targetUuid;
    }

    public Star targetUuid(String targetUuid) {
        this.targetUuid = targetUuid;
        return this;
    }

    public void setTargetUuid(String targetUuid) {
        this.targetUuid = targetUuid;
    }

    public Instant getStarDate() {
        return starDate;
    }

    public Star starDate(Instant starDate) {
        this.starDate = starDate;
        return this;
    }

    public void setStarDate(Instant starDate) {
        this.starDate = starDate;
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
        Star star = (Star) o;
        if (star.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), star.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Star{" +
            "id=" + getId() +
            ", userLogin='" + getUserLogin() + "'" +
            ", targetType='" + getTargetType() + "'" +
            ", targetUuid='" + getTargetUuid() + "'" +
            ", starDate='" + getStarDate() + "'" +
            "}";
    }
}
