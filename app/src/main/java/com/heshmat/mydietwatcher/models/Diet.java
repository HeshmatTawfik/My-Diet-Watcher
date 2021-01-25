package com.heshmat.mydietwatcher.models;

import com.google.firebase.firestore.Exclude;

import java.util.List;

public class Diet {
    private String id;
    private String name;
    private long createdAt;
    private double totalCalories;
    public static Diet currentCreatedDiet;
    List<Food> foods;

    public Diet() {
    }

    public Diet(String id, String name, long createdAt, double totalCalories, List<Food> foods) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.totalCalories = totalCalories;
        this.foods = foods;
    }

    public Diet(String name, long createdAt) {
        this.name = name;
        this.createdAt = createdAt;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public double totalFats() {
        double total = 0;

        for (Food f : getFoods()) {
            double foodFats = (f.getAmount() * f.getNutrients().getFat()) / 100;
            total += foodFats;

        }
        return total;
    }
    @Exclude
    public double totalProteins() {
        double total = 0;
        for (Food f : getFoods()) {
            double foodPro = (f.getAmount() * f.getNutrients().getProcnt()) / 100;
            total += foodPro;
        }
        return total;
    }
}
