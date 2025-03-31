package com.example.mainprojectapp_1.api;

import com.kakao.sdk.user.model.User;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UserApiService {

    @GET("/user/profile")
    Call<User> getUserProfile();
}
