package com.example.mainprojectapp_1.api;

import com.example.mainprojectapp_1.DiaryEntry;
import com.example.mainprojectapp_1.Restaurant;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RestaurantApiService {

    // 주변 식당 리스트 가져오기
    @GET("/restaurants/json")
    Call<List<Restaurant>> getAllRestaurants();

    @GET("/restaurants/json")
    Call<ResponseBody> getAllRestaurantsRaw(); // 🔍 로그 출력용 테스트 전용!

    // 식당 상세 정보 가져오기 (수정된 경로)
    @GET("/restaurants/json/{id}")  // 경로 수정
    Call<Restaurant> getRestaurantDetail(@Path("id") int id);

    // 특정 식당과 연결된 일기 정보 가져오기
    @GET("/api/restaurants/{id}/diary")
    Call<DiaryEntry> getDiaryForRestaurant(@Path("id") long restaurantId);

    // 카테고리별 식당 조회
//    @GET("/api/restaurants/category")
//    Call<List<Restaurant>> getRestaurantsByCategory(@Query("kategorie") String kategorie);

}
