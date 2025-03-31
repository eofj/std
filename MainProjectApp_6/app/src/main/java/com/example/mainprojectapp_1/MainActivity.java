package com.example.mainprojectapp_1;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mainprojectapp_1.api.DiaryApiService;
import com.example.mainprojectapp_1.api.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // BottomNavigationView를 찾고 색상 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        ColorStateList itemIconColorStateList = getResources().getColorStateList(R.color.nav_item_selector);
        bottomNavigationView.setItemIconTintList(itemIconColorStateList);
        bottomNavigationView.setItemTextColor(getResources().getColorStateList(R.color.nav_text_selector));

        // ✅ 로그인 후 이동할 목적지가 있다면 해당 프래그먼트로 진입
        String goTo = getIntent().getStringExtra("goTo");
        Fragment startFragment;
        if ("mypage".equals(goTo)) {
            startFragment = new MyPageFragment();
            bottomNavigationView.setSelectedItemId(R.id.nav_mypage);
        } else {
            startFragment = new HomeFragment();
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, startFragment)
                .commit();

        // 내비게이션 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    // 내비게이션 아이템 클릭 -> 각 프래그먼트로 전환
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            if(item.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();

            } else if (item.getItemId() == R.id.nav_map) {
                selectedFragment = new MapFragment();

            } else if (item.getItemId() == R.id.nav_diary) {
                selectedFragment = new DiaryFragment();

            } else if (item.getItemId() == R.id.nav_mypage) {
                selectedFragment = new MyPageFragment();
            }

            // 선택된 프래그먼트로 전환
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();

            return true;
        }
    };

    /**
     * 🔥 서버에 좋아요(하트) 상태 업데이트 요청하는 메서드
     */
    public void updateDiaryFavoriteStatus(Long diaryId, boolean isFavorite) {
        DiaryApiService apiService = RetrofitClient.getDiaryApiService();

        Call<Void> call = apiService.updateFavoriteStatus(
                diaryId,
                Collections.singletonMap("isFavorite", isFavorite)
        );
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("MainActivity", "서버에 좋아요 상태 업데이트 성공: " + diaryId);
                } else {
                    Log.e("MainActivity", "서버 좋아요 업데이트 실패 (응답 오류): " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MainActivity", "서버 좋아요 업데이트 실패: " + t.getMessage());
            }
        });
    }
}