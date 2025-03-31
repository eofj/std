package com.example.mainprojectapp_1;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class DiaryEntry {
    private Long id;

    @SerializedName("photoPath")
    private String baseImageUrl;

    @SerializedName("menuName")
    private String title;

    @SerializedName("address")  // ✅ "place" → "address" 로 수정됨
    private String location;

    @SerializedName("diaryText")
    private String content;

    private float rating;

    @SerializedName("heart")
    private boolean isFavorite;

    @SerializedName("restaurantName")
    private String restaurantName;

    private List<String> additionalImages;

    // 생성자
    public DiaryEntry(Long id, String baseImageUrl, String title, String location, String content, float rating, boolean isFavorite) {
        this.id = id;
        this.baseImageUrl = baseImageUrl;
        this.title = title;
        this.location = location;
        this.content = content;
        this.rating = rating;
        this.isFavorite = isFavorite;
        this.additionalImages = new ArrayList<>();
    }

    // Getter
    public Long getId() { return id; }
    public String getBaseImageUrl() { return baseImageUrl; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getContent() { return content; }
    public float getRating() { return rating; }
    public boolean isFavorite() { return isFavorite; }
    public String getRestaurantName() { return restaurantName; }
    public String getImageUrl() { return baseImageUrl; }
    public String getFoodName() { return title; }

    // Setter
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public void setFavorite(boolean favorite) { this.isFavorite = favorite; }

    // 이미지 관련 유틸
    public List<String> getAdditionalImages() {
        return additionalImages;
    }

    public void addAdditionalImage(String imageUri) {
        if (additionalImages.size() < 5) {
            additionalImages.add(imageUri);
        }
    }

    public void removeAdditionalImage(int index) {
        if (index >= 0 && index < additionalImages.size()) {
            additionalImages.remove(index);
        }
    }
}
