package com.heshmat.mydietwatcher.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Qualifiers {
    @Expose
    @SerializedName("label")
    private String label;
    @Expose
    @SerializedName("uri")
    private String uri;

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
