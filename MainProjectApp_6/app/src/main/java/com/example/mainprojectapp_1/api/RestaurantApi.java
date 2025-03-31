package com.example.mainprojectapp_1.api;

import com.example.mainprojectapp_1.Restaurant;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestaurantApi {
    @GET("/restaurants/json")
    Call<List<Restaurant>> getRestaurants(@Query("categorie") String categoryName);

    @GET("/restaurants/{id}")  // 서버에서 해당 ID의 식당 정보를 가져옴
    Call<Restaurant> getRestaurantDetail(@Path("id") int id);
}
