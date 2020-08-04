package com.wyy.domain;

import io.swagger.annotations.ApiModel;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * 积分变更记录
 */
@ApiModel(description = "积分变更记录")
@Entity
@Table(name = "credit_history")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CreditHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_login")
    private String userLogin;

    @Column(name = "credit_plus")
    private Long creditPlus;

    @Column(name = "current_credit")
    private Long currentCredit;

    @Column(name = "jhi_comment")
    private String comment;

    @Column(name = "modify_date")
    private Instant modifyDate;

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

    public CreditHistory userLogin(String userLogin) {
        this.userLogin = userLogin;
        return this;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public Long getCreditPlus() {
        return creditPlus;
    }

    public CreditHistory creditPlus(Long creditPlus) {
        this.creditPlus = creditPlus;
        return this;
    }

    public void setCreditPlus(Long creditPlus) {
        this.creditPlus = creditPlus;
    }

    public Long getCurrentCredit() {
        return currentCredit;
    }

    public CreditHistory currentCredit(Long currentCredit) {
        this.currentCredit = currentCredit;
        return this;
    }

    public void setCurrentCredit(Long currentCredit) {
        this.currentCredit = currentCredit;
    }

    public String getComment() {
        return comment;
    }

    public CreditHistory comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getModifyDate() {
        return modifyDate;
    }

    public CreditHistory modifyDate(Instant modifyDate) {
        this.modifyDate = modifyDate;
        return this;
    }

    public void setModifyDate(Instant modifyDate) {
        this.modifyDate = modifyDate;
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
        CreditHistory creditHistory = (CreditHistory) o;
        if (creditHistory.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), creditHistory.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CreditHistory{" +
            "id=" + getId() +
            ", userLogin='" + getUserLogin() + "'" +
            ", creditPlus=" + getCreditPlus() +
            ", currentCredit=" + getCurrentCredit() +
            ", comment='" + getComment() + "'" +
            ", modifyDate='" + getModifyDate() + "'" +
            "}";
    }
}
