package com.example.mainprojectapp_1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 2초 후 MainActivity로 이동
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // MainActivity로 이동
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();  // SplashActivity 종료
            }
        }, 2000);  // 2초 딜레이

    }
}