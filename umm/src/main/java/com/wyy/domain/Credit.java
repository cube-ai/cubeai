package com.wyy.domain;

import io.swagger.annotations.ApiModel;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * 积分
 */
@ApiModel(description = "积分")
@Entity
@Table(name = "credit")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Credit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_login")
    private String userLogin;

    @Column(name = "credit")
    private Long credit;

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

    public Credit userLogin(String userLogin) {
        this.userLogin = userLogin;
        return this;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public Long getCredit() {
        return credit;
    }

    public Credit credit(Long credit) {
        this.credit = credit;
        return this;
    }

    public void setCredit(Long credit) {
        this.credit = credit;
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
        Credit credit = (Credit) o;
        if (credit.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), credit.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Credit{" +
            "id=" + getId() +
            ", userLogin='" + getUserLogin() + "'" +
            ", credit=" + getCredit() +
            "}";
    }
}
