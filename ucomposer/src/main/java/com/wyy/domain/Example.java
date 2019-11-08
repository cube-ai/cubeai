package com.wyy.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Example implements Serializable{
    @JsonProperty("http-method")
    private String httpMethod = null;
    @JsonProperty("model-method")
    private String modelMethod = null;
    @JsonProperty("body")
    private Object body = null;

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getModelMethod() {
        return modelMethod;
    }

    public void setModelMethod(String modelMethod) {
        this.modelMethod = modelMethod;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Example{" +
            "httpMethod='" + httpMethod + '\'' +
            ", modelMethod='" + modelMethod + '\'' +
            ", body='" + body + '\'' +
            '}';
    }
}
