package com.wyy.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A VerifyCode.
 */
@Entity
@Table(name = "verify_code")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class VerifyCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "jhi_expire")
    private Instant expire;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public VerifyCode code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getExpire() {
        return expire;
    }

    public VerifyCode expire(Instant expire) {
        this.expire = expire;
        return this;
    }

    public void setExpire(Instant expire) {
        this.expire = expire;
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
        VerifyCode verifyCode = (VerifyCode) o;
        if (verifyCode.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), verifyCode.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "VerifyCode{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", expire='" + getExpire() + "'" +
            "}";
    }
}
