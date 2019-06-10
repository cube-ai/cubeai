package com.wyy.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A Article.
 */
@Entity
@Table(name = "article")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "author_login")
    private String authorLogin;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "subject_1")
    private String subject1;

    @Column(name = "subject_2")
    private String subject2;

    @Column(name = "subject_3")
    private String subject3;

    @Column(name = "title")
    private String title;

    @Column(name = "summary")
    private String summary;

    @Column(name = "tag_1")
    private String tag1;

    @Column(name = "tag_2")
    private String tag2;

    @Column(name = "tag_3")
    private String tag3;

    @Size(max = 512)
    @Column(name = "picture_url", length = 512)
    private String pictureUrl;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "display_order")
    private Long displayOrder;

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

    public Article uuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAuthorLogin() {
        return authorLogin;
    }

    public Article authorLogin(String authorLogin) {
        this.authorLogin = authorLogin;
        return this;
    }

    public void setAuthorLogin(String authorLogin) {
        this.authorLogin = authorLogin;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Article authorName(String authorName) {
        this.authorName = authorName;
        return this;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getSubject1() {
        return subject1;
    }

    public Article subject1(String subject1) {
        this.subject1 = subject1;
        return this;
    }

    public void setSubject1(String subject1) {
        this.subject1 = subject1;
    }

    public String getSubject2() {
        return subject2;
    }

    public Article subject2(String subject2) {
        this.subject2 = subject2;
        return this;
    }

    public void setSubject2(String subject2) {
        this.subject2 = subject2;
    }

    public String getSubject3() {
        return subject3;
    }

    public Article subject3(String subject3) {
        this.subject3 = subject3;
        return this;
    }

    public void setSubject3(String subject3) {
        this.subject3 = subject3;
    }

    public String getTitle() {
        return title;
    }

    public Article title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public Article summary(String summary) {
        this.summary = summary;
        return this;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTag1() {
        return tag1;
    }

    public Article tag1(String tag1) {
        this.tag1 = tag1;
        return this;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public Article tag2(String tag2) {
        this.tag2 = tag2;
        return this;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public String getTag3() {
        return tag3;
    }

    public Article tag3(String tag3) {
        this.tag3 = tag3;
        return this;
    }

    public void setTag3(String tag3) {
        this.tag3 = tag3;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public Article pictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
        return this;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getContent() {
        return content;
    }

    public Article content(String content) {
        this.content = content;
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getDisplayOrder() {
        return displayOrder;
    }

    public Article displayOrder(Long displayOrder) {
        this.displayOrder = displayOrder;
        return this;
    }

    public void setDisplayOrder(Long displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Article createdDate(Instant createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public Article modifiedDate(Instant modifiedDate) {
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
        Article article = (Article) o;
        if (article.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), article.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Article{" +
            "id=" + getId() +
            ", uuid='" + getUuid() + "'" +
            ", authorLogin='" + getAuthorLogin() + "'" +
            ", authorName='" + getAuthorName() + "'" +
            ", subject1='" + getSubject1() + "'" +
            ", subject2='" + getSubject2() + "'" +
            ", subject3='" + getSubject3() + "'" +
            ", title='" + getTitle() + "'" +
            ", summary='" + getSummary() + "'" +
            ", tag1='" + getTag1() + "'" +
            ", tag2='" + getTag2() + "'" +
            ", tag3='" + getTag3() + "'" +
            ", pictureUrl='" + getPictureUrl() + "'" +
            ", content='" + getContent() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", createdDate='" + getCreatedDate() + "'" +
            ", modifiedDate='" + getModifiedDate() + "'" +
            "}";
    }
}
