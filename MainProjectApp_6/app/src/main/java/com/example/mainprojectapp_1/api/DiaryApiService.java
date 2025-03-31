package com.example.mainprojectapp_1.api;

import com.example.mainprojectapp_1.DiaryEntry;
import com.example.mainprojectapp_1.Restaurant;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface DiaryApiService {

    // ✅ 전체 피드 조회 (사진만)
    @GET("/fooddiary/feed")
    Call<List<com.example.mainprojectapp_1.model.FoodDiaryFeedResponse>> getFeedPhotos();

    @GET("/api/diaries")
    Call<List<DiaryEntry>> getAllDiaries();

    // ✅ 상세 조회
    @GET("/fooddiary/detail/{id}")
    Call<DiaryEntry> getDiary(@Path("id") long diaryId);

    // ✅ 좋아요
    @PUT("/fooddiary/diaries/{id}/favorite")
    Call<Void> updateFavoriteStatus(@Path("id") long diaryId, @Body Map<String, Boolean> body);

    // ✅ JSON 기반 저장/수정 (사용 안함 - Multipart 방식 사용)
    @POST("/fooddiary/diaries")
    Call<Void> createDiary(@Body DiaryEntry diaryEntry);

    @PUT("/api/diaries/{id}")
    Call<Void> updateDiary(@Path("id") long diaryId, @Body DiaryEntry diaryEntry);

    // ✅ 삭제
    @DELETE("/api/diaries/{id}")
    Call<Void> deleteDiary(@Path("id") long diaryId);

    @DELETE("/fooddiary/{id}")
    Call<ResponseBody> deleteDiaryWithImages(@Path("id") long diaryId);

    // ✅ 새 일기 작성 (이미지 포함)
    @Multipart
    @POST("/fooddiary")
    Call<ResponseBody> createDiaryWithImages(
            @Part("restaurantId") RequestBody restaurantId,
            @Part("diaryText") RequestBody diaryText,
            @Part("rating") RequestBody rating,
            @Part("menuName") RequestBody menuName,
            @Part MultipartBody.Part photo
    );

    // ✅ 일기 수정 (이미지 포함) ← 이거 우리가 수정!
    @Multipart
    @PUT("/fooddiary/{id}")
    Call<ResponseBody> updateDiaryWithImages(
            @Path("id") long diaryId,
            @Part("restaurantId") RequestBody restaurantId,
            @Part("diaryText") RequestBody diaryText,
            @Part("rating") RequestBody rating,
            @Part("menuName") RequestBody menuName,
            @Part MultipartBody.Part photo
    );

    // ✅ 식당 목록
    @GET("json")
    Call<List<Restaurant>> getAllRestaurants();
}
