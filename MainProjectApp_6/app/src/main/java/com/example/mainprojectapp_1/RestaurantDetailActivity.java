package com.example.mainprojectapp_1;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mainprojectapp_1.adapter.MenuAdapter;
import com.example.mainprojectapp_1.api.RestaurantApiService;
import com.example.mainprojectapp_1.api.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDetailActivity extends AppCompatActivity {
    private ImageView restaurantImage, backButton;
    private TextView restaurantName, restaurantAddress, restaurantPhone, restaurantHours;
    private int restaurantId;
    private RestaurantApiService restaurantApi;

    private RecyclerView menuRecyclerView;
    private MenuAdapter adapter;
    private List<MenuItem> menuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        // UI 요소 연결
        restaurantImage = findViewById(R.id.restaurant_image);
        restaurantName = findViewById(R.id.restaurant_name);
        restaurantAddress = findViewById(R.id.restaurant_address);
        restaurantPhone = findViewById(R.id.restaurant_phone);
        restaurantHours = findViewById(R.id.restaurant_hours);
        backButton = findViewById(R.id.back_button2);

        // RecyclerView 초기화
        menuRecyclerView = findViewById(R.id.restaurant_menu);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // RecyclerView 어댑터 초기화
        menuList = new ArrayList<>();
        adapter = new MenuAdapter(menuList);
        menuRecyclerView.setAdapter(adapter);

        // Intent에서 식당 ID 가져오기
        restaurantId = getIntent().getIntExtra("restaurantId", -1);
        Log.d("IntentTest", "받은 restaurantId: " + restaurantId);

        if (restaurantId == -1) {
            Toast.makeText(this, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // RetrofitClient 사용하여 API 호출
        restaurantApi = RetrofitClient.getRestaurantApiService();
        fetchRestaurantDetails(restaurantId);

        // 뒤로 가기 버튼 클릭 - 현재 액티비티 종료
        backButton.setOnClickListener(v -> {
            Log.d("ButtonClick", "뒤로 가기 버튼 클릭됨");
            finish();
        });
    }

    private void fetchRestaurantDetails(int id) {
        String requestUrl = "http://13.125.46.254:8080/api/restaurants/json/" + id;
        Log.d("RestaurantAPI", "요청 URL: " + requestUrl);

        restaurantApi.getRestaurantDetail(id).enqueue(new Callback<Restaurant>() {
            @Override
            public void onResponse(Call<Restaurant> call, Response<Restaurant> response) {
                Log.d("DetailAPI", "응답 코드: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Restaurant restaurant = response.body();
                    Log.d("DetailAPI", "식당 이름: " + restaurant.getName());
                    Log.d("DetailAPI", "식당 주소: " + restaurant.getPlace());
                    Log.d("DetailAPI", "식당 전화번호: " + restaurant.getPhoneNumber());
                    Log.d("DetailAPI", "식당 영업시간: " + restaurant.getBusinessHours());
                    Log.d("DetailAPI", "식당 이미지 URL: " + restaurant.getImage());
                    Log.d("DetailAPI", "메뉴 목록: " + restaurant.getMenuList());
                    Log.d("DetailAPI", "가격 목록: " + restaurant.getPriceList());

                    updateUI(restaurant);
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("DetailAPI", "에러 응답 내용: " + errorBody);
                    } catch (IOException e) {
                        Log.e("DetailAPI", "에러 바디 읽기 실패", e);
                    }
                    Toast.makeText(RestaurantDetailActivity.this, "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                    clearUI();
                }
            }

            @Override
            public void onFailure(Call<Restaurant> call, Throwable t) {
                Log.e("DetailAPI", "네트워크 오류 발생: " + t.getMessage());
                Toast.makeText(RestaurantDetailActivity.this, "네트워크 오류 발생", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Restaurant restaurant) {
        restaurantName.setText(restaurant.getName());
        restaurantAddress.setText(restaurant.getPlace());
        restaurantPhone.setText(restaurant.getPhoneNumber());
        restaurantHours.setText(restaurant.getBusinessHours());

        List<MenuItem> menuList = new ArrayList<>();
        List<String> menuNames = restaurant.getMenuList();
        List<Integer> menuPrices = restaurant.getPriceList();

        if (menuNames != null && menuPrices != null) {
            for (int i = 0; i < menuNames.size(); i++) {
                int price = (i < menuPrices.size()) ? menuPrices.get(i) : 0;
                menuList.add(new MenuItem(menuNames.get(i), price));
            }
        }

        adapter.updateMenu(menuList);

        Glide.with(this)
                .load("http://13.125.46.254:8080/restaurants/image/" + restaurant.getId())
                .error(R.drawable.img_food_korean)
                .into(restaurantImage);
    }

    private void clearUI() {
        restaurantName.setText("");
        restaurantAddress.setText("주소: 불러오기 실패");
        restaurantPhone.setText("전화번호: 불러오기 실패");
        restaurantHours.setText("영업시간: 불러오기 실패");
        menuRecyclerView.setAdapter(new MenuAdapter(new ArrayList<>()));
        restaurantImage.setImageResource(R.drawable.banner_hamburger);
    }
}
