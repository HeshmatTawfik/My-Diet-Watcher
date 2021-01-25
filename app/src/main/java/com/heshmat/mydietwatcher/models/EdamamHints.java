package com.heshmat.mydietwatcher.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EdamamHints {
    @Expose
    @SerializedName("hints")
    private List<Hints> hints;
    public List<Hints> getHints() {
        ;
        return hints;
    }
    public List<Food> foodHints;

    public void setHints(List<Hints> hints) {
        this.hints = hints;
    }
    public void getFoodHints(){

    }
}
