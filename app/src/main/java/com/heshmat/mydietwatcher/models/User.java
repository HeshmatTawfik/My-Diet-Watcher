package com.heshmat.mydietwatcher.models;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.heshmat.mydietwatcher.DatabaseInstance;
import com.heshmat.mydietwatcher.FormattingDate;
import com.heshmat.mydietwatcher.StaticFields;
import com.heshmat.mydietwatcher.activities.HomeActivity;
import com.heshmat.mydietwatcher.activities.InfoFormActivity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;

public class User {
    public static User currentUser = new User();
    private String id;
    private String name;
    private long birthdate;
    private double weight;
    private double height;
    private String lifeStyle;
    private String imgUrl;
    private HashMap<String,Diet> diets;

    public HashMap<String, Diet> getDiets() {
        return diets;
    }

    public void setDiets(HashMap<String, Diet> diets) {
        this.diets = diets;
    }


    public double getNeededCAL() {

        return neededCAL;
    }

    public void setNeededCAL() {
        int age=FormattingDate.getAge(new Date(this.birthdate));
        if (this.gender.equals(StaticFields.MALE)) {
           // 10 x weight (kg) + 6.25 x height (cm) – 5 x age (y) + 5
            this.neededCAL = (10*weight+6.25*height-5*age+5)*activeRate(lifeStyle);
        } else {
            //10 x weight (kg) + 6.25 x height (cm) – 5 x age (y) – 161
            this.neededCAL = (10*weight+6.25*height-5*age-161)*activeRate(lifeStyle);


        }
    }

    private double neededCAL;

    private String gender;

    public User() {
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public User(String id, String name, long birthdate, double weight, double height, String lifeStyle) {
        this.id = id;
        this.name = name;
        this.birthdate = birthdate;
        this.weight = weight;
        this.height = height;
        this.lifeStyle = lifeStyle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(long birthdate) {
        this.birthdate = birthdate;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getLifeStyle() {
        return lifeStyle;
    }

    public void setLifeStyle(String lifeStyle) {
        this.lifeStyle = lifeStyle;
    }

    @Exclude
    public static User getCurrentUser() {
        return currentUser == null ? new User() : currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        User.currentUser = currentUser;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public static double activeRate(String activeRate) {
        HashMap<String, Double> activeRateMap = new HashMap<>();
        activeRateMap.put(StaticFields.SEDENTARY, 1.25);
        activeRateMap.put(StaticFields.LIGHTLY_ACTIVE, 1.375);
        activeRateMap.put(StaticFields.MODERATELY_ACTIVE, 1.55);
        activeRateMap.put(StaticFields.VERY_ACTIVE, 1.725);
        activeRateMap.put(StaticFields.EXTRA_ACTIVE, 1.9);
        return (double) activeRateMap.get(activeRate);
    }

    public static void userRedirect(Activity current) {
        DatabaseInstance.getInstance().collection(StaticFields.USER_COLLECTION).document(User.currentUser.getId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && Objects.requireNonNull(task.getResult()).exists()) {
                    User.currentUser = task.getResult().toObject(User.class);
                    current.startActivity(new Intent(current, HomeActivity.class));
                    current.finish();
                } else if (task.isSuccessful() && !Objects.requireNonNull(task.getResult()).exists()) {
                    current.startActivity(new Intent(current, InfoFormActivity.class));
                    current.finish();
                } else if (!task.isSuccessful()) {
                    Toast.makeText(current, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


}
