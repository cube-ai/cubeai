package com.wyy.dto;

import com.wyy.domain.cdump.Nodes;

import java.io.Serializable;

public class SolutionNodes implements Serializable{
    String userId;
    String solutionId;
    String cdumpVersion;
    String name;
    Nodes node;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCdumpVersion() {
        return cdumpVersion;
    }

    public void setCdumpVersion(String cdumpVersion) {
        this.cdumpVersion = cdumpVersion;
    }

    public Nodes getNode() {
        return node;
    }

    public void setNode(Nodes node) {
        this.node = node;
    }

    @Override
    public String toString() {
        return "SolutionNodes{" +
            "userId='" + userId + '\'' +
            ", solutionId='" + solutionId + '\'' +
            ", cdumpVersion='" + cdumpVersion + '\'' +
            ", name='" + name + '\'' +
            ", node=" + node +
            '}';
    }
}
