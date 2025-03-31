package com.example.mainprojectapp_1;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private int id;
    private String name;
    private double rating;
    private String phoneNumber;
    private String businessHours;
    private String closedDays;
    private String breakTime;
    private String kiosk;
    private String place;
    private double latitude;
    private double longitude;
    private String image;

    @SerializedName("kategorie")  // ✅ JSON의 "kategorie"와 매핑
    private String category;

    @SerializedName("menu")  // ✅ JSON의 "menu" 필드를 리스트로 매핑
    private List<String> menuList;

    @SerializedName("price")  // ✅ JSON의 "price" 필드를 리스트로 매핑
    private List<Integer> priceList;

    // ✅ JSON 데이터에 맞는 생성자
    public Restaurant(int id, String name, String image, String businessHours, String closedDays, String breakTime,
                      String phoneNumber, String place, double rating, double latitude, double longitude,
                      String category, String kiosk, List<String> menuList, List<Integer> priceList) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.businessHours = businessHours;
        this.closedDays = closedDays;
        this.breakTime = breakTime;
        this.phoneNumber = phoneNumber;
        this.place = place;
        this.rating = rating;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
        this.kiosk = kiosk;
        this.menuList = menuList != null ? menuList : new ArrayList<>();
        this.priceList = priceList != null ? priceList : new ArrayList<>();
    }

    // ✅ Getter 메서드 (Setter 제거)
    public int getId() { return id; }
    public String getName() { return name; }
    public double getRating() { return rating; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getBusinessHours() { return businessHours; }
    public String getClosedDays() { return closedDays; }
    public String getBreakTime() { return breakTime; }
    public String getKiosk() { return kiosk; }
    public String getPlace() { return place; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getImage() {
        return image != null ? "http://13.125.46.254:8080/restaurants/image/" + this.id : null;
    }
    public String getCategory() { return category; }
    public List<String> getMenuList() { return menuList; }
    public List<Integer> getPriceList() { return priceList; }

    // ✅ 더미 데이터 생성 (테스트용)
    public static List<Restaurant> getDummyRestaurants() {
        List<Restaurant> dummyList = new ArrayList<>();
        List<String> sampleMenu = new ArrayList<>();
        sampleMenu.add("김밥");
        sampleMenu.add("라면");

        List<Integer> samplePrice = new ArrayList<>();
        samplePrice.add(3000);
        samplePrice.add(4000);

        dummyList.add(new Restaurant(
                1,   // ID
                "김밥천국",   // Name
                "image_url",   // Image
                "09:00 - 21:00",   // Business Hours
                "월요일",   // Closed Days
                "15:00 - 17:00",   // Break Time
                "02-1234-5678",   // Phone
                "서울특별시 강남구",   // Address
                4.5,   // Rating
                37.544512,
                126.676950,
                "한식",   // Category
                "없음",   // Kiosk
                sampleMenu,   // Menu
                samplePrice   // Price
        ));

        return dummyList;
    }
}
