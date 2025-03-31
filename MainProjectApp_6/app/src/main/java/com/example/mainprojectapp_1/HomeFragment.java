package com.example.mainprojectapp_1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.mainprojectapp_1.adapter.AdBannerAdapter;
import com.example.mainprojectapp_1.adapter.CategoriesAdapter;
import com.example.mainprojectapp_1.api.RestaurantApiService;
import com.example.mainprojectapp_1.api.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class HomeFragment extends Fragment {

    private EditText searchEditText;
    private ViewPager2 adBannerViewPager;
    private AdBannerAdapter adBannerAdapter;
    private CategoriesAdapter categoriesAdapter;
    private RecyclerView categoriesRecyclerView;
    private List<FoodCategories> categoriesList;
    private List<Integer> adBannerImages;

    private TextView recommendedRestaurantName, recommendedRestaurantMenu, recommendedRestaurantRating, recommendedRestaurantAddress;
    private ImageView recommendedRestaurantImage;
    private Button btnViewRestaurant, btnRandomRestaurant;

    private List<Restaurant> restaurantList = new ArrayList<>();
    private Restaurant selectedRestaurant = null;

    private TextView currentLocationTextView;
    private FusedLocationProviderClient fusedLocationClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 검색창 설정
        searchEditText = view.findViewById(R.id.search_box);
        searchEditText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT);

        searchEditText.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    Intent intent = new Intent(getActivity(), RestaurantListActivity.class);
                    intent.putExtra("SEARCH_QUERY", query);
                    startActivity(intent);
                    searchEditText.postDelayed(() -> hideKeyboard(), 100);
                }
                return true;
            }
            return false;
        });

        // 카테고리 설정
        categoriesRecyclerView = view.findViewById(R.id.categories_recyclerview);
        categoriesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));

        categoriesList = new ArrayList<>();
        categoriesList.add(new FoodCategories(R.drawable.img_food_korean, "한식"));
        categoriesList.add(new FoodCategories(R.drawable.img_food_chinese, "중식"));
        categoriesList.add(new FoodCategories(R.drawable.img_food_japanese, "일식"));
        categoriesList.add(new FoodCategories(R.drawable.img_food_western, "양식"));

        categoriesAdapter = new CategoriesAdapter(categoriesList);
        categoriesRecyclerView.setAdapter(categoriesAdapter);
        categoriesAdapter.setOnItemClickListener(position -> {
            FoodCategories selectedCategory = categoriesList.get(position);
            Intent intent = new Intent(getActivity(), RestaurantListActivity.class);
            intent.putExtra("CATEGORY_NAME", selectedCategory.getName());
            startActivity(intent);
        });

        // 광고 배너 설정 (ViewPager2)
        adBannerViewPager = view.findViewById(R.id.ad_banner_viewpager);
        adBannerImages = new ArrayList<>();
        adBannerImages.add(R.drawable.banner_hamburger);
        adBannerImages.add(R.drawable.banner_pizza);
        adBannerImages.add(R.drawable.banner_korean);
        adBannerAdapter = new AdBannerAdapter(getContext(), adBannerImages);
        adBannerViewPager.setAdapter(adBannerAdapter);
        adBannerViewPager.setOffscreenPageLimit(3);
        adBannerViewPager.setClipToPadding(false);
        adBannerViewPager.setClipChildren(false);
        adBannerViewPager.setPadding(40, 0, 140, 0);
        adBannerViewPager.setPageTransformer((page, position) -> {
            float pageTranslationX = 40 * position; // 40은 paddingStart
            page.setTranslationX(pageTranslationX);

            float scaleFactor = 0.9f + (1 - Math.abs(position)) * 0.1f;
            page.setScaleY(scaleFactor);
            page.setAlpha(0.8f + (1 - Math.abs(position)) * 0.2f);
        });


        // 랜덤 추천 식당
        recommendedRestaurantName = view.findViewById(R.id.recommendation_name);
        recommendedRestaurantMenu = view.findViewById(R.id.recommendation_menu);
        recommendedRestaurantRating = view.findViewById(R.id.recommendation_rating);
        recommendedRestaurantImage = view.findViewById(R.id.recommendation_image);
        btnViewRestaurant = view.findViewById(R.id.btn_view_restaurant);
        btnRandomRestaurant = view.findViewById(R.id.btn_random_restaurant);

        btnRandomRestaurant.setOnClickListener(v -> showRandomRecommendation());

        currentLocationTextView = view.findViewById(R.id.current_location);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        getCurrentLocation();

        fetchAllRestaurants();

        return view;
    }

    // 모든 식당 데이터를 가져오는 메서드
    private void fetchAllRestaurants() {
        RestaurantApiService restaurantApi = RetrofitClient.getRestaurantApiService();
        Call<List<Restaurant>> call = restaurantApi.getAllRestaurants();

        call.enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    restaurantList.clear();
                    restaurantList.addAll(response.body());

                    // 랜덤 추천 식당 표시
                    showRandomRecommendation();
                }
            }

            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                Log.e("RestaurantFetch", "식당 데이터 불러오기 실패: " + t.getMessage());
            }
        });
    }

    // 랜덤 추천 식당을 선택하는 메서드
    private void showRandomRecommendation() {
        if (!restaurantList.isEmpty()) {
            Random random = new Random();
            int index = random.nextInt(restaurantList.size());
            Restaurant randomRestaurant = restaurantList.get(index);

            Log.d("RandomSelection", "새로운 추천 식당 인덱스: " + index + ", 이름: " + randomRestaurant.getName());

            selectedRestaurant = randomRestaurant;

            recommendedRestaurantName.setText(randomRestaurant.getName());
            recommendedRestaurantMenu.setText("📍 추천 메뉴: " + (randomRestaurant.getMenuList().isEmpty() ? "-" : randomRestaurant.getMenuList().get(0)));
            recommendedRestaurantRating.setText("⭐ " + randomRestaurant.getRating());

            /*Glide.with(this)
                    .load(randomRestaurant.getImage())
                    .into(recommendedRestaurantImage);*/
            //여기 오류생겨서 살짝만 고쳤어요
            if (isAdded() && getContext() != null) {
                Glide.with(requireContext())
                        .load(randomRestaurant.getImage())
                        .into(recommendedRestaurantImage);
            }
        }

        btnViewRestaurant.setOnClickListener(v -> {
            if (selectedRestaurant != null) {
                Intent intent = new Intent(getActivity(), RestaurantDetailActivity.class);
                intent.putExtra("restaurantId", selectedRestaurant.getId());
                startActivity(intent);
            }
        });
    }

    // 키보드 숨기기 메서드 (로그 추가)
    private void hideKeyboard() {
        if (getActivity() == null) return;

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            searchEditText.clearFocus();
            Log.d("KeyboardEvent", "키보드 숨기기 성공");
        }
    }

    private void getCurrentLocation() {
        if (getContext() == null) return;

        Log.d("LocationCheck", "🔍 위치 가져오기 시작");

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        Log.d("LocationCheck", "📍 위치 가져오기 성공: 위도 = " + location.getLatitude() + ", 경도 = " + location.getLongitude());

                        Geocoder geocoder = new Geocoder(getContext(), Locale.KOREA);
                        try {
                            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addressList != null && !addressList.isEmpty()) {
                                Address address = addressList.get(0);

                                String fullAddress = address.getAddressLine(0);
                                String road = address.getThoroughfare();         // 도로명
                                String number = address.getSubThoroughfare();   // 건물번호

                                String adminArea = address.getAdminArea();         // 시
                                String subAdminArea = address.getSubAdminArea();   // 구
                                String locality = address.getLocality();           // 시 or 구
                                String subLocality = address.getSubLocality();     // 동
                                String featureName = address.getFeatureName();     // 건물명

                                Log.d("LocationCheck", "🦾 전체 주소: " + fullAddress);
                                Log.d("LocationCheck", "🚗 도로명: " + road + ", 번호: " + number);
                                Log.d("LocationCheck", "📍 지역정보 - adminArea: " + adminArea + ", subLocality: " + subLocality);
                                Log.d("LocationCheck", "💼 featureName: " + featureName);

                                if ((road == null || road.trim().isEmpty()) && fullAddress != null) {
                                    Pattern pattern = Pattern.compile("(\\S+\ub85c|\\S+\uae38)");
                                    Matcher matcher = pattern.matcher(fullAddress);
                                    if (matcher.find()) {
                                        road = matcher.group(1);
                                        Log.d("LocationCheck", "📌 파싱된 도로명: " + road);
                                    }
                                }

                                if ((number == null || number.trim().isEmpty()) && featureName != null && featureName.matches("\\d+")) {
                                    number = featureName;
                                }

                                if (subAdminArea == null) {
                                    subAdminArea = subLocality;
                                }

                                if (featureName != null && (
                                        featureName.equals(adminArea) ||
                                                featureName.equals("대한민국") ||
                                                featureName.trim().isEmpty())) {
                                    featureName = null;
                                }

                                String simpleAddress;

                                if (adminArea != null && subAdminArea != null && road != null && number != null) {
                                    simpleAddress = adminArea + " " + subAdminArea + " " + road + " " + number;
                                } else if (adminArea != null && subAdminArea != null && road != null) {
                                    simpleAddress = adminArea + " " + subAdminArea + " " + road;
                                } else if (adminArea != null && subLocality != null) {
                                    simpleAddress = adminArea + " " + subLocality;
                                } else if (adminArea != null && featureName != null) {
                                    simpleAddress = adminArea + " " + featureName;
                                } else {
                                    Pattern cutPattern = Pattern.compile("^(.*?번지)");
                                    Matcher cutMatcher = cutPattern.matcher(fullAddress);
                                    if (cutMatcher.find()) {
                                        simpleAddress = cutMatcher.group(1).replaceFirst("^대한민국\\s*", "").trim();
                                    } else {
                                        simpleAddress = fullAddress.replaceFirst("^대한민국\\s*", "").trim();
                                    }
                                }

                                Log.d("LocationCheck", "✅ 최종 표시 주소: " + simpleAddress);
                                currentLocationTextView.setText(simpleAddress);
                            } else {
                                Log.e("LocationCheck", "❗ 주소 리스트가 비어 있음");
                            }
                        } catch (Exception e) {
                            Log.e("LocationCheck", "❌ 주소 변환 실패: " + e.getMessage());
                        }
                    } else {
                        Log.e("LocationCheck", "❗ 위치 정보가 null");
                    }
                })
                .addOnFailureListener(e -> Log.e("LocationCheck", "❌ 위치 가져오기 실패: " + e.getMessage()));
    }

}
