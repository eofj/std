package com.example.mainprojectapp_1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainprojectapp_1.adapter.RestaurantAdapter;
import com.example.mainprojectapp_1.api.RestaurantApiService;
import com.example.mainprojectapp_1.api.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantListActivity extends AppCompatActivity {

    private TextView categoryTitleTextView;
    private RecyclerView restaurantRecyclerView;
    private RestaurantAdapter restaurantAdapter;
    private List<Restaurant> restaurantList = new ArrayList<>();
    private List<Restaurant> allRestaurants = new ArrayList<>(); // 모든 식당 데이터 저장
    private String selectedCategory; // 사용자가 선택한 카테고리
    private String searchQuery; // 검색어

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        // 카테고리 이름 받아오기
        categoryTitleTextView = findViewById(R.id.category_title);
        restaurantRecyclerView = findViewById(R.id.restaurant_recyclerview);

        // 카테고리 또는 검색어를 Intent로 받아오기
        Intent intent = getIntent();
        selectedCategory = intent.getStringExtra("CATEGORY_NAME");
        searchQuery = intent.getStringExtra("SEARCH_QUERY");

        // RecyclerView 설정
        restaurantList = new ArrayList<>();
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        restaurantAdapter = new RestaurantAdapter(this, restaurantList, new RestaurantAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Restaurant restaurant) {
                // 클릭 시 상세 페이지로 이동
                Intent intent = new Intent(RestaurantListActivity.this, RestaurantDetailActivity.class);
                intent.putExtra("restaurantId", restaurant.getId());
                startActivity(intent);
            }
        });
        restaurantRecyclerView.setAdapter(restaurantAdapter);

        // 검색 또는 카테고리 필터링 실행
        if (searchQuery != null && !searchQuery.isEmpty()) {
            categoryTitleTextView.setText("🔎 '" + searchQuery + "' 검색 결과");
            fetchAllRestaurantsWithQuery(searchQuery);
        } else if (selectedCategory != null && !selectedCategory.isEmpty()) {
            categoryTitleTextView.setText(selectedCategory + " 식당 리스트");
            fetchAllRestaurantsWithCategory(selectedCategory);
        }
    }

    private void fetchAllRestaurantsWithCategory(String category) {
        RestaurantApiService restaurantApi = RetrofitClient.getRestaurantApiService();
        Call<List<Restaurant>> call = restaurantApi.getAllRestaurants();

        call.enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allRestaurants.clear();
                    allRestaurants.addAll(response.body()); // 전체 식당 데이터 저장
                    filterRestaurantsByCategory(category); // 필터링하여 UI 업데이트
                } else {
                    Toast.makeText(RestaurantListActivity.this, "식당 데이터 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                Toast.makeText(RestaurantListActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAllRestaurantsWithQuery(String query) {
        RestaurantApiService restaurantApi = RetrofitClient.getRestaurantApiService();
        Call<List<Restaurant>> call = restaurantApi.getAllRestaurants();

        call.enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allRestaurants.clear();
                    allRestaurants.addAll(response.body());
                    filterRestaurantsByQuery(query);
                } else {
                    Toast.makeText(RestaurantListActivity.this, "식당 데이터 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                Toast.makeText(RestaurantListActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterRestaurantsByCategory(String category) {
        List<Restaurant> filteredList = new ArrayList<>();

        for (Restaurant restaurant : allRestaurants) {
            if (restaurant.getCategory() != null && restaurant.getCategory().trim().equals(category.trim())) {
                filteredList.add(restaurant);
            }
        }

        if (filteredList.isEmpty()) {
            Log.d("FilterTest", "필터링된 결과 없음 → 전체 데이터 표시");
            restaurantAdapter.updateList(allRestaurants);
        } else {
            restaurantAdapter.updateList(filteredList);
        }
    }

    private void filterRestaurantsByQuery(String query) {
        List<Restaurant> filteredList = new ArrayList<>();

        for (Restaurant restaurant : allRestaurants) {
            if (restaurant.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(restaurant);
            }
        }

        if (filteredList.isEmpty()) {
            Log.d("SearchTest", "검색 결과 없음 → 전체 데이터 표시");
            restaurantAdapter.updateList(allRestaurants);
        } else {
            restaurantAdapter.updateList(filteredList);
        }
    }

    // 뒤로가기
    public void onBackBtn(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }
}
