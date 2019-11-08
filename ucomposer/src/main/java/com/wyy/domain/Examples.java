package com.wyy.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class Examples implements Serializable{
    @JsonProperty("examples")
    private  List<Example> examples;

    public List<Example> getExamples() {
        return examples;
    }

    public void setExamples(List<Example> examples) {
        this.examples = examples;
    }
    //    {
//        "examples": [{
//        "http-method": "POST",
//            "model-method": "end_subtract",
//            "body": {
//            "one_data": 10
//        }
//    }]
//    }
}
