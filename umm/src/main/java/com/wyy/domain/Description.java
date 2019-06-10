package com.wyy.domain;

import io.swagger.annotations.ApiModel;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * 模型描述
 */
@ApiModel(description = "模型描述")
@Entity
@Table(name = "description")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Description implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "solution_uuid")
    private String solutionUuid;

    @Column(name = "author_login")
    private String authorLogin;

    @Lob
    @Column(name = "content")
    private String content;

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

    public Description solutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
        return this;
    }

    public void setSolutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
    }

    public String getAuthorLogin() {
        return authorLogin;
    }

    public Description authorLogin(String authorLogin) {
        this.authorLogin = authorLogin;
        return this;
    }

    public void setAuthorLogin(String authorLogin) {
        this.authorLogin = authorLogin;
    }

    public String getContent() {
        return content;
    }

    public Description content(String content) {
        this.content = content;
        return this;
    }

    public void setContent(String content) {
        this.content = content;
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
        Description description = (Description) o;
        if (description.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), description.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Description{" +
            "id=" + getId() +
            ", solutionUuid='" + getSolutionUuid() + "'" +
            ", authorLogin='" + getAuthorLogin() + "'" +
            ", content='" + getContent() + "'" +
            "}";
    }
}
