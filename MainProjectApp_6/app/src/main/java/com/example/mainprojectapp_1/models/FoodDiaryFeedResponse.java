package com.example.mainprojectapp_1.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FoodDiaryFeedResponse implements Serializable {
    private Long id;
    @SerializedName("photoPath")
    private String photoPath;
    private Boolean heart;
    private String restaurantName;
    private String place;
    private Float rating;
    private String menuName;
    private String diaryText;

    public Long getId() { return id; }
    public String getPhotoPath() { return photoPath; }
    public Boolean getHeart() { return heart; }
    public String getRestaurantName() { return restaurantName; }
    public String getPlace() { return place; }
    public Float getRating() { return rating; }
    public String getMenuName() { return menuName; }
    public String getDiaryText() { return diaryText; }
}