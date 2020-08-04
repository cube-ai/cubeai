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
 * 应用
 */
@ApiModel(description = "应用")
@Entity
@Table(name = "application")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Application implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "summary")
    private String summary;

    @Size(max = 512)
    @Column(name = "url", length = 512)
    private String url;

    @Lob
    @Column(name = "picture_url")
    private String pictureUrl;

    @Column(name = "subject_1")
    private String subject1;

    @Column(name = "subject_2")
    private String subject2;

    @Column(name = "subject_3")
    private String subject3;

    @Column(name = "owner")
    private String owner;

    @Column(name = "need_roles")
    private String needRoles;

    @Column(name = "display_order")
    private Long displayOrder;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_by")
    private String modifiedBy;

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

    public Application uuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public Application name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public Application summary(String summary) {
        this.summary = summary;
        return this;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public Application url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public Application pictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
        return this;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getSubject1() {
        return subject1;
    }

    public Application subject1(String subject1) {
        this.subject1 = subject1;
        return this;
    }

    public void setSubject1(String subject1) {
        this.subject1 = subject1;
    }

    public String getSubject2() {
        return subject2;
    }

    public Application subject2(String subject2) {
        this.subject2 = subject2;
        return this;
    }

    public void setSubject2(String subject2) {
        this.subject2 = subject2;
    }

    public String getSubject3() {
        return subject3;
    }

    public Application subject3(String subject3) {
        this.subject3 = subject3;
        return this;
    }

    public void setSubject3(String subject3) {
        this.subject3 = subject3;
    }

    public String getOwner() {
        return owner;
    }

    public Application owner(String owner) {
        this.owner = owner;
        return this;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getNeedRoles() {
        return needRoles;
    }

    public Application needRoles(String needRoles) {
        this.needRoles = needRoles;
        return this;
    }

    public void setNeedRoles(String needRoles) {
        this.needRoles = needRoles;
    }

    public Long getDisplayOrder() {
        return displayOrder;
    }

    public Application displayOrder(Long displayOrder) {
        this.displayOrder = displayOrder;
        return this;
    }

    public void setDisplayOrder(Long displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Application createdBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public Application modifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
        return this;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Application createdDate(Instant createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public Application modifiedDate(Instant modifiedDate) {
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
        Application application = (Application) o;
        if (application.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), application.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Application{" +
            "id=" + getId() +
            ", uuid='" + getUuid() + "'" +
            ", name='" + getName() + "'" +
            ", summary='" + getSummary() + "'" +
            ", url='" + getUrl() + "'" +
            ", pictureUrl='" + getPictureUrl() + "'" +
            ", subject1='" + getSubject1() + "'" +
            ", subject2='" + getSubject2() + "'" +
            ", subject3='" + getSubject3() + "'" +
            ", owner='" + getOwner() + "'" +
            ", needRoles='" + getNeedRoles() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", createdBy='" + getCreatedBy() + "'" +
            ", modifiedBy='" + getModifiedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", modifiedDate='" + getModifiedDate() + "'" +
            "}";
    }
}
