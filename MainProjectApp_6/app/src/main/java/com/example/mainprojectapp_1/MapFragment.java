package com.example.mainprojectapp_1;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mainprojectapp_1.api.DiaryApiService;
import com.example.mainprojectapp_1.api.RetrofitClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import android.location.Location;

import android.location.Geocoder;
import android.location.Address;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap mMap;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private TextView restaurantNameTextView, restaurantAddressTextView;
    private ImageView restaurantImageView;
    private ImageButton btnWriteDiary, btnHeart;
    private Map<Marker, Restaurant> markerRestaurantMap = new HashMap<>();
    private Restaurant selectedRestaurant;
    private boolean isFavorite = false;

    private DiaryApiService diaryApiService;

    private FusedLocationProviderClient fusedLocationClient;

    private TextView addressTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mukspot, container, false);

        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        View bottomSheet = view.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(200);

        restaurantNameTextView = view.findViewById(R.id.restaurant_name);
        restaurantAddressTextView = view.findViewById(R.id.restaurant_address);
        restaurantImageView = view.findViewById(R.id.restaurant_image);
        btnWriteDiary = view.findViewById(R.id.btn_write_diary);
        btnHeart = view.findViewById(R.id.btn_favorite);

        btnWriteDiary.setOnClickListener(v -> openDiaryEditFragment());
        btnHeart.setOnClickListener(v -> toggleFavorite());

        diaryApiService = RetrofitClient.getDiaryApiService();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        addressTextView = view.findViewById(R.id.text_current_address);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));
        } catch (Exception e) {
            Log.e("MukSpotFragment", "지도 스타일 적용 실패", e);
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                            Log.d("MapFragment", "✅ 유저 현재 위치로 이동함");

                            Geocoder geocoder = new Geocoder(requireContext(), Locale.KOREA);
                            try {
                                List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                if (addressList != null && !addressList.isEmpty()) {
                                    Address address = addressList.get(0);
                                    String fullAddress = address.getAddressLine(0);

                                    String road = address.getThoroughfare();
                                    String number = address.getSubThoroughfare();
                                    String adminArea = address.getAdminArea();
                                    String subAdminArea = address.getSubAdminArea();
                                    String subLocality = address.getSubLocality();
                                    String featureName = address.getFeatureName();

                                    if ((road == null || road.trim().isEmpty()) && fullAddress != null) {
                                        Pattern pattern = Pattern.compile("(\\S+\ub85c|\\S+\uae38)");
                                        Matcher matcher = pattern.matcher(fullAddress);
                                        if (matcher.find()) {
                                            road = matcher.group(1);
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

                                    if (addressTextView != null) {
                                        addressTextView.setText(simpleAddress);
                                    }
                                    Log.d("주소변환", "📍 주소: " + simpleAddress);
                                }
                            } catch (Exception e) {
                                Log.e("주소변환", "❌ 주소 변환 실패", e);
                                if (addressTextView != null) {
                                    addressTextView.setText("주소 불러오기 실패");
                                }
                            }
                        } else {
                            Log.w("MapFragment", "⚠️ 위치 정보 없음, 기본 위치 유지");
                            LatLng defaultLocation = new LatLng(37.5440129, 126.6768610);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));

                            if (addressTextView != null) {
                                addressTextView.setText("위치 불러오기 실패");
                            }
                        }
                    });
        } else {
            Log.w("MapFragment", "⚠️ 위치 권한 없음, 기본 위치로 설정");
            LatLng defaultLocation = new LatLng(37.5440129, 126.6768610);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));

            if (addressTextView != null) {
                addressTextView.setText("위치 불러오기 실패");
            }
        }

        fetchRestaurantData();

        mMap.setOnMarkerClickListener(marker -> {
            selectedRestaurant = markerRestaurantMap.get(marker);
            if (selectedRestaurant != null) {
                showBottomSheet(selectedRestaurant);
                fetchDiaryDataForRestaurant(selectedRestaurant.getId());
            }
            return false;
        });
    }

    private void fetchRestaurantData() {
        RetrofitClient.getRestaurantApiService().getAllRestaurants().enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Restaurant> restaurantList = response.body();
                    for (Restaurant restaurant : restaurantList) {
                        LatLng position = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(position)
                                .title(restaurant.getName())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        Marker marker = mMap.addMarker(markerOptions);
                        markerRestaurantMap.put(marker, restaurant);
                    }
                    Log.d("MapFragment", "✅ 마커 총 개수: " + restaurantList.size());
                } else {
                    Log.e("MapFragment", "❌ 식당 리스트 응답 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                Log.e("MapFragment", "❌ 서버 요청 실패: " + t.getMessage());
            }
        });
    }

    private void showBottomSheet(Restaurant restaurant) {
        restaurantNameTextView.setText(restaurant.getName());
        restaurantAddressTextView.setText(restaurant.getPlace());
        Glide.with(this).load(restaurant.getImage()).into(restaurantImageView);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void openDiaryEditFragment() {
        if (selectedRestaurant == null) return;
        Bundle bundle = new Bundle();
        bundle.putString("restaurant_name", selectedRestaurant.getName());
        bundle.putString("restaurant_address", selectedRestaurant.getPlace());
        bundle.putString("restaurant_image", selectedRestaurant.getImage());
        bundle.putLong("restaurant_id", selectedRestaurant.getId());  // ✅ 식당 ID 추가

        DiaryEditFragment diaryEditFragment = new DiaryEditFragment();
        diaryEditFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, diaryEditFragment)
                .addToBackStack(null)
                .commit();
    }

    private void toggleFavorite() {
        if (selectedRestaurant == null) return;
        isFavorite = !isFavorite;
        btnHeart.setImageResource(isFavorite ? R.drawable.heart : R.drawable.ic_heart);
        diaryApiService.updateFavoriteStatus(selectedRestaurant.getId(), Collections.singletonMap("isFavorite", isFavorite)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {}
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    private void fetchDiaryDataForRestaurant(long diaryId) {
        diaryApiService.getDiary(diaryId).enqueue(new Callback<DiaryEntry>() {
            @Override
            public void onResponse(Call<DiaryEntry> call, Response<DiaryEntry> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isFavorite = response.body().isFavorite();
                    btnHeart.setImageResource(isFavorite ? R.drawable.heart : R.drawable.ic_heart);
                }
            }
            @Override
            public void onFailure(Call<DiaryEntry> call, Throwable t) {}
        });
    }

    @Override public void onResume() { super.onResume(); mapView.onResume(); }
    @Override public void onPause() { super.onPause(); mapView.onPause(); }
    @Override public void onDestroy() { super.onDestroy(); mapView.onDestroy(); }
    @Override public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }
}
