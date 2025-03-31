package com.example.mainprojectapp_1;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class kakaoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //카카오 SDK 초기화
        KakaoSdk.init(this, "9b3b670801922c79813047f579eff46b");
    }
}
