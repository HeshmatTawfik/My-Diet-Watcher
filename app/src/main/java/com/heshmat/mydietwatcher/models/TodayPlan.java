package com.heshmat.mydietwatcher.models;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;

public class TodayPlan {
    private String date;
    private Diet diet;
    private HashMap<String,Food> eatenFood;

    public TodayPlan(String date, Diet diet) {
        this.date = date;
        this.diet = diet;
    }

    public TodayPlan() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Diet getDiet() {
        return diet;
    }

    public void setDiet(Diet diet) {
        this.diet = diet;
    }

    public HashMap<String, Food> getEatenFood() {
        return eatenFood;
    }

    public void setEatenFood(HashMap<String, Food> eatenFood) {
        this.eatenFood = eatenFood;
    }
    @Exclude
    public double totalKcal() {
        double total = 0;

        for (Food f : eatenFood.values()) {
            double foodKcal = (f.getAmount() * f.getNutrients().getEnercKcal()) / 100;
            total += foodKcal;

        }
        return total;
    }
    @Exclude
    public double totalFats() {
        double total = 0;

        for (Food f : eatenFood.values()) {
            double foodFats = (f.getAmount() * f.getNutrients().getFat()) / 100;
            total += foodFats;

        }
        return total;
    }
    @Exclude
    public double totalProteins() {
        double total = 0;
        for (Food f : eatenFood.values()) {
            double foodPro = (f.getAmount() * f.getNutrients().getProcnt()) / 100;
            total += foodPro;
        }
        return total;
    }
}
