package com.wyy.domain.cdump;

import java.io.Serializable;

public class ModifyNode implements Serializable{
    String userId;
    String solutionId;
    String nodeId;
    String nodeName;
    Ndata ndata;
    DataConnector dataConnector;

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

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Ndata getNdata() {
        return ndata;
    }

    public void setNdata(Ndata ndata) {
        this.ndata = ndata;
    }

    public DataConnector getDataConnector() {
        return dataConnector;
    }

    public void setDataConnector(DataConnector dataConnector) {
        this.dataConnector = dataConnector;
    }

    @Override
    public String toString() {
        return "ModifyNode{" +
            "userId='" + userId + '\'' +
            ", solutionId='" + solutionId + '\'' +
            ", nodeId='" + nodeId + '\'' +
            ", nodeName='" + nodeName + '\'' +
            ", ndata='" + ndata + '\'' +
            ", dataConnector=" + dataConnector +
            '}';
    }
}
