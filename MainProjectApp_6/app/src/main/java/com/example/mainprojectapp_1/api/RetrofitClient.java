package com.example.mainprojectapp_1.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getInstance() { // 기존 getRetrofitInstance() 유지
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    //http://192.168.0.73:8080
                    .baseUrl("http://13.125.46.254:8080")  // ✅ 백엔드 서버 주소
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * ✅ DiaryApiService 객체 반환 (API 요청을 쉽게 처리)
     */
    public static DiaryApiService getDiaryApiService() {
        return getInstance().create(DiaryApiService.class);
    }

    public static RestaurantApiService getRestaurantApiService() {
        return getInstance().create(RestaurantApiService.class);
    }
}

