package com.heshmat.mydietwatcher.models;

import com.google.firebase.firestore.Exclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;

public class Food implements Cloneable{
    @Expose
    @SerializedName("image")
    private String image;
    @Expose
    @SerializedName("categoryLabel")
    private String categorylabel;
    @Expose
    @SerializedName("category")
    private String category;
    @Expose
    @SerializedName("nutrients")
    private Nutrients nutrients;
    @Expose
    @SerializedName("label")
    private String label;
    @Expose
    @SerializedName("foodId")
    private String foodid;
    private double amount=100;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategorylabel() {
        return categorylabel;
    }

    public void setCategorylabel(String categorylabel) {
        this.categorylabel = categorylabel;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Nutrients getNutrients() {
        return nutrients;
    }

    public void setNutrients(Nutrients nutrients) {
        this.nutrients = nutrients;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFoodid() {
        return foodid;
    }

    public void setFoodid(String foodid) {
        this.foodid = foodid;
    }
    @Exclude
    public double totalPro(){
        return (this.getAmount() * this.getNutrients().getProcnt()) / 100;
    }
    @Exclude
    public double totalFat(){
        return (this.getAmount() * this.getNutrients().getFat()) / 100;
    }
    @Exclude
    public double totalKcal(){
        return (this.getAmount() * this.getNutrients().getEnercKcal()) / 100;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Food food=((Food)obj);
        return this.foodid.equals(food.foodid);
    }
    @Exclude
    public Object clone() throws
            CloneNotSupportedException
    {
        return super.clone();
    }
}
