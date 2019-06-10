package com.wyy.domain;

import java.time.Instant;
import java.util.Objects;


public class TaskStep {

    private Long id;
    private String taskUuid;
    private String stepName;
    private String stepStatus;
    private Integer stepProgress;
    private String description;
    private Instant stepDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskUuid() {
        return taskUuid;
    }

    public void setTaskUuid(String taskUuid) {
        this.taskUuid = taskUuid;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStepStatus() {
        return stepStatus;
    }

    public void setStepStatus(String stepStatus) {
        this.stepStatus = stepStatus;
    }

    public Integer getStepProgress() {
        return stepProgress;
    }

    public void setStepProgress(Integer stepProgress) {
        this.stepProgress = stepProgress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getStepDate() {
        return stepDate;
    }

    public void setStepDate(Instant stepDate) {
        this.stepDate = stepDate;
    }

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
