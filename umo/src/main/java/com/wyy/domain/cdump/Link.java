package com.wyy.domain.cdump;

import java.io.Serializable;

public class Link implements Serializable{
    String userId;
    String solutionId;
    String linkId;
    String linkName;
    String sourceNodeName;
    String sourceNodeId;
    String targetNodeName;
    String targetNodeId;
    String sourceNodeRequirement;
    String targetNodeCapabilityName;
    Property property;
    private String input= "";
    private String output= "";
    private String start= "";
    private String end= "";

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getSourceNodeName() {
        return sourceNodeName;
    }

    public void setSourceNodeName(String sourceNodeName) {
        this.sourceNodeName = sourceNodeName;
    }

    public String getSourceNodeId() {
        return sourceNodeId;
    }

    public void setSourceNodeId(String sourceNodeId) {
        this.sourceNodeId = sourceNodeId;
    }

    public String getTargetNodeName() {
        return targetNodeName;
    }

    public void setTargetNodeName(String targetNodeName) {
        this.targetNodeName = targetNodeName;
    }

    public String getTargetNodeId() {
        return targetNodeId;
    }

    public void setTargetNodeId(String targetNodeId) {
        this.targetNodeId = targetNodeId;
    }

    public String getSourceNodeRequirement() {
        return sourceNodeRequirement;
    }

    public void setSourceNodeRequirement(String sourceNodeRequirement) {
        this.sourceNodeRequirement = sourceNodeRequirement;
    }

    public String getTargetNodeCapabilityName() {
        return targetNodeCapabilityName;
    }

    public void setTargetNodeCapabilityName(String targetNodeCapabilityName) {
        this.targetNodeCapabilityName = targetNodeCapabilityName;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSolutionId() {
        return solutionId;
    }

    public void setSolutionId(String solutionId) {
        this.solutionId = solutionId;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    @Override
    public String toString() {
        return "Link{" +
            "userId='" + userId + '\'' +
            ", solutionId='" + solutionId + '\'' +
            ", linkId='" + linkId + '\'' +
            ", linkName='" + linkName + '\'' +
            ", sourceNodeName='" + sourceNodeName + '\'' +
            ", sourceNodeId='" + sourceNodeId + '\'' +
            ", targetNodeName='" + targetNodeName + '\'' +
            ", targetNodeId='" + targetNodeId + '\'' +
            ", sourceNodeRequirement='" + sourceNodeRequirement + '\'' +
            ", targetNodeCapabilityName='" + targetNodeCapabilityName + '\'' +
            ", property=" + property +
            ", input='" + input + '\'' +
            ", output='" + output + '\'' +
            ", start='" + start + '\'' +
            ", end='" + end + '\'' +
            '}';
    }
}
