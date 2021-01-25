package com.heshmat.mydietwatcher.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Measures {
    @Expose
    @SerializedName("qualified")
    private List<Qualified> qualified;
    @Expose
    @SerializedName("weight")
    private double weight;
    @Expose
    @SerializedName("label")
    private String label;
    @Expose
    @SerializedName("uri")
    private String uri;

    public List<Qualified> getQualified() {
        return qualified;
    }

    public void setQualified(List<Qualified> qualified) {
        this.qualified = qualified;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
