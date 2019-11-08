package com.wyy.domain;

public class CompositeSolution extends Solution{

    private String probeIndicator;

    public String getProbeIndicator() {
        return probeIndicator;
    }

    public void setProbeIndicator(String probeIndicator) {
        this.probeIndicator = probeIndicator;
    }

    @Override
    public String toString() {
        return super.toString() + " " + "CompositeSolution{" +
            "probeIndicator='" + probeIndicator + '\'' +
            '}';
    }
}
