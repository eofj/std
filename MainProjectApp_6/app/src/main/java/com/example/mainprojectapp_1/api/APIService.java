package com.example.mainprojectapp_1.api;

import com.example.mainprojectapp_1.models.LoginRequest;
import com.example.mainprojectapp_1.models.LoginResponse;
import com.example.mainprojectapp_1.models.SignUpRequest;
import com.example.mainprojectapp_1.models.SignUpResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface APIService {

    // 로그인 API 요청
    @POST("/api/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // 회원가입 API 요청
    @POST("/api/register")
    Call<SignUpResponse> register(@Body SignUpRequest request);

}
