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
 * 异步任务
 */
@ApiModel(description = "异步任务")
@Entity
@Table(name = "task")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "task_name")
    private String taskName;

    /**
     * 任务类型，直接用中文字符串表示。目前包括：
     * 模型上线，模型验证
     */
    @ApiModelProperty(value = "任务类型，直接用中文字符串表示。目前包括： 模型上线，模型验证")
    @Column(name = "task_type")
    private String taskType;

    /**
     * 任务状态，直接用中文字符串表示。目前包括：
     * 等待调度，正在执行，成功，失败
     */
    @ApiModelProperty(value = "任务状态，直接用中文字符串表示。目前包括： 等待调度，正在执行，成功，失败")
    @Column(name = "task_status")
    private String taskStatus;

    /**
     * 任务进度，取值为0-100的整数
     */
    @Max(value = 100)
    @ApiModelProperty(value = "任务进度，取值为0-100的整数")
    @Column(name = "task_progress")
    private Integer taskProgress;

    @Size(max = 512)
    @Column(name = "description", length = 512)
    private String description;

    @Column(name = "target_uuid")
    private String targetUuid;

    @Column(name = "user_login")
    private String userLogin;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

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

    public Task uuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTaskName() {
        return taskName;
    }

    public Task taskName(String taskName) {
        this.taskName = taskName;
        return this;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskType() {
        return taskType;
    }

    public Task taskType(String taskType) {
        this.taskType = taskType;
        return this;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public Task taskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
        return this;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Integer getTaskProgress() {
        return taskProgress;
    }

    public Task taskProgress(Integer taskProgress) {
        this.taskProgress = taskProgress;
        return this;
    }

    public void setTaskProgress(Integer taskProgress) {
        this.taskProgress = taskProgress;
    }

    public String getDescription() {
        return description;
    }

    public Task description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetUuid() {
        return targetUuid;
    }

    public Task targetUuid(String targetUuid) {
        this.targetUuid = targetUuid;
        return this;
    }

    public void setTargetUuid(String targetUuid) {
        this.targetUuid = targetUuid;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public Task userLogin(String userLogin) {
        this.userLogin = userLogin;
        return this;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Task startDate(Instant startDate) {
        this.startDate = startDate;
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public Task endDate(Instant endDate) {
        this.endDate = endDate;
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
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
        Task task = (Task) o;
        if (task.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), task.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Task{" +
            "id=" + getId() +
            ", uuid='" + getUuid() + "'" +
            ", taskName='" + getTaskName() + "'" +
            ", taskType='" + getTaskType() + "'" +
            ", taskStatus='" + getTaskStatus() + "'" +
            ", taskProgress=" + getTaskProgress() +
            ", description='" + getDescription() + "'" +
            ", targetUuid='" + getTargetUuid() + "'" +
            ", userLogin='" + getUserLogin() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            "}";
    }
}
