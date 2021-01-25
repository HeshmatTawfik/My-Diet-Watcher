package com.heshmat.mydietwatcher.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Hints {
    @Expose
    @SerializedName("measures")
    private List<Measures> measures;
    @Expose
    @SerializedName("food")
    private Food food;

    public List<Measures> getMeasures() {
        return measures;
    }

    public void setMeasures(List<Measures> measures) {
        this.measures = measures;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }
}
