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
 * 任务步骤
 * 一个异步任务有多个步骤组成。每个步骤执行过程中，在该数据表中添加若干条步骤记录，其中的stepProgress依次递增。前端取出最新一条记录值来更新进度显示
 */
@ApiModel(description = "任务步骤 一个异步任务有多个步骤组成。每个步骤执行过程中，在该数据表中添加若干条步骤记录，其中的stepProgress依次递增。前端取出最新一条记录值来更新进度显示")
@Entity
@Table(name = "task_step")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TaskStep implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 步骤所属任务的uuid。
     */
    @ApiModelProperty(value = "步骤所属任务的uuid。")
    @Column(name = "task_uuid")
    private String taskUuid;

    @Column(name = "step_name")
    private String stepName;

    /**
     * 步骤状态，直接用中文字符串表示。目前包括：
     * 执行，成功，失败
     */
    @ApiModelProperty(value = "步骤状态，直接用中文字符串表示。目前包括： 执行，成功，失败")
    @Column(name = "step_status")
    private String stepStatus;

    /**
     * 步骤进度，取值为0-100的整数
     */
    @Max(value = 100)
    @ApiModelProperty(value = "步骤进度，取值为0-100的整数")
    @Column(name = "step_progress")
    private Integer stepProgress;

    @Size(max = 512)
    @Column(name = "description", length = 512)
    private String description;

    @Column(name = "step_date")
    private Instant stepDate;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskUuid() {
        return taskUuid;
    }

    public TaskStep taskUuid(String taskUuid) {
        this.taskUuid = taskUuid;
        return this;
    }

    public void setTaskUuid(String taskUuid) {
        this.taskUuid = taskUuid;
    }

    public String getStepName() {
        return stepName;
    }

    public TaskStep stepName(String stepName) {
        this.stepName = stepName;
        return this;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStepStatus() {
        return stepStatus;
    }

    public TaskStep stepStatus(String stepStatus) {
        this.stepStatus = stepStatus;
        return this;
    }

    public void setStepStatus(String stepStatus) {
        this.stepStatus = stepStatus;
    }

    public Integer getStepProgress() {
        return stepProgress;
    }

    public TaskStep stepProgress(Integer stepProgress) {
        this.stepProgress = stepProgress;
        return this;
    }

    public void setStepProgress(Integer stepProgress) {
        this.stepProgress = stepProgress;
    }

    public String getDescription() {
        return description;
    }

    public TaskStep description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getStepDate() {
        return stepDate;
    }

    public TaskStep stepDate(Instant stepDate) {
        this.stepDate = stepDate;
        return this;
    }

    public void setStepDate(Instant stepDate) {
        this.stepDate = stepDate;
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
        TaskStep taskStep = (TaskStep) o;
        if (taskStep.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), taskStep.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TaskStep{" +
            "id=" + getId() +
            ", taskUuid='" + getTaskUuid() + "'" +
            ", stepName='" + getStepName() + "'" +
            ", stepStatus='" + getStepStatus() + "'" +
            ", stepProgress=" + getStepProgress() +
            ", description='" + getDescription() + "'" +
            ", stepDate='" + getStepDate() + "'" +
            "}";
    }
}
