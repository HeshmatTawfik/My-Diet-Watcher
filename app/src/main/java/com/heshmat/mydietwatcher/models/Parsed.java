package com.heshmat.mydietwatcher.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Parsed {
    @Expose
    @SerializedName("food")
    private Food food;

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }
}
