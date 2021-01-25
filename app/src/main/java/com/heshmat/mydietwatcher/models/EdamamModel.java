package com.heshmat.mydietwatcher.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EdamamModel {

    @Expose
    @SerializedName("parsed")
    private java.util.List<Parsed> parsed;
    @Expose
    @SerializedName("text")
    private String text;
    @SerializedName("hints")
    private java.util.List<Hints> hints;
    private List<Food> allFood;

    public List<Parsed> getParsed() {
        return parsed;
    }
    public void setParsed(List<Parsed> parsed) {
        this.parsed = parsed;
    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Hints> getHints() {
        return hints;
    }

    public void setHints(List<Hints> hints) {
        this.hints = hints;
    }

    public List<Food> getAllFood() {
        if (allFood==null)
        allFood=new ArrayList<>();
        if (getParsed()!=null&&!getParsed().isEmpty()){
            for (Parsed p:getParsed())
            allFood.add(0,p.getFood());
        }
        if (getHints()!=null&&!getHints().isEmpty()){
            for (Hints h:getHints())
                if (!allFood.contains(h.getFood()))
                allFood.add(h.getFood());
        }
        return allFood;
    }
}
