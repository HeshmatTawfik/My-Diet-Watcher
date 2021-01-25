package com.heshmat.mydietwatcher.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Qualified {
    @Expose
    @SerializedName("weight")
    private int weight;
    @Expose
    @SerializedName("qualifiers")
    private List<Qualifiers> qualifiers;

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<Qualifiers> getQualifiers() {
        return qualifiers;
    }

    public void setQualifiers(List<Qualifiers> qualifiers) {
        this.qualifiers = qualifiers;
    }
}
