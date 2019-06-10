package com.wyy.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * 构件（Artifact）。 Stored in Nexus.
 * 一个Solution中可以包含多个Artifact，一个Artifact唯一属于一个Soultion。
 */
@ApiModel(description = "构件（Artifact）。 Stored in Nexus. 一个Solution中可以包含多个Artifact，一个Artifact唯一属于一个Soultion。")
@Entity
@Table(name = "artifact")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Artifact implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "solution_uuid")
    private String solutionUuid;

    @Column(name = "name")
    private String name;

    /**
     * Artifact类型，直接用中文字符串表示。主要包括：
     * document, 蓝图文件, CDUMP文件, DOCKER镜像, 数据源, 元数据, H2O模型, 模型镜像, R模型, SCIKIT模型,
     * TENSORFLOW模型, TOSCA模板, TOSCA生成器输入文件, TOSCA-SCHEMA, TOSCA翻译, PROTOBUF文件
     */
    @ApiModelProperty(value = "Artifact类型，直接用中文字符串表示。主要包括： document, 蓝图文件, CDUMP文件, DOCKER镜像, 数据源, 元数据, H2O模型, 模型镜像, R模型, SCIKIT模型, TENSORFLOW模型, TOSCA模板, TOSCA生成器输入文件, TOSCA-SCHEMA, TOSCA翻译, PROTOBUF文件")
    @Column(name = "jhi_type")
    private String type;

    @Size(max = 512)
    @Column(name = "url", length = 512)
    private String url;

    @Column(name = "file_size")
    private Long fileSize;

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

    public String getSolutionUuid() {
        return solutionUuid;
    }

    public Artifact solutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
        return this;
    }

    public void setSolutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
    }

    public String getName() {
        return name;
    }

    public Artifact name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public Artifact type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public Artifact url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public Artifact fileSize(Long fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Artifact createdDate(Instant createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public Artifact modifiedDate(Instant modifiedDate) {
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
        Artifact artifact = (Artifact) o;
        if (artifact.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), artifact.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Artifact{" +
            "id=" + getId() +
            ", solutionUuid='" + getSolutionUuid() + "'" +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            ", url='" + getUrl() + "'" +
            ", fileSize=" + getFileSize() +
            ", createdDate='" + getCreatedDate() + "'" +
            ", modifiedDate='" + getModifiedDate() + "'" +
            "}";
    }
}
