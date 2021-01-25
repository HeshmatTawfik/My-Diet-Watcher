package com.heshmat.mydietwatcher.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Nutrients {
    @Expose
    @SerializedName("FIBTG")
    private double fibtg;
    @Expose
    @SerializedName("CHOCDF")
    private double chocdf;
    @Expose
    @SerializedName("FAT")
    private double fat;
    @Expose
    @SerializedName("PROCNT")
    private double procnt;
    @Expose
    @SerializedName("ENERC_KCAL")
    private double enercKcal;

    public double getFibtg() {
        return fibtg;
    }

    public void setFibtg(double fibtg) {
        this.fibtg = fibtg;
    }

    public double getChocdf() {
        return chocdf;
    }

    public void setChocdf(double chocdf) {
        this.chocdf = chocdf;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getProcnt() {
        return procnt;
    }

    public void setProcnt(double procnt) {
        this.procnt = procnt;
    }

    public double getEnercKcal() {
        return enercKcal;
    }

    public void setEnercKcal(double enercKcal) {
        this.enercKcal = enercKcal;
    }

    @Override
    public String toString() {
        return "Nutrients{" +
                "fibtg=" + fibtg +
                ", chocdf=" + chocdf +
                ", fat=" + fat +
                ", procnt=" + procnt +
                ", enercKcal=" + enercKcal +
                '}';
    }
}
