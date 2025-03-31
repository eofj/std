package com.example.mainprojectapp_1;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mainprojectapp_1.api.DiaryApiService;
import com.example.mainprojectapp_1.api.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryDetailFragment extends Fragment {

    private static final String TAG = "DiaryDetail";

    private ImageView diaryImageView;
    private TextView foodNameTextView, locationTextView, contentTextView;
    private float rating;
    private ImageButton btnBack;
    private ImageButton favoriteButton;
    private boolean isFavorite = false;
    private Long diaryId;
    private Long restaurantId;
    private DiaryApiService diaryApiService;
    private String imageUrl, foodName, location, content, restaurantName;
    private TextView restaurantNameTextView;
    private RatingBar ratingBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary_detail, container, false);

        setHasOptionsMenu(true);

        diaryImageView = view.findViewById(R.id.diary_detail_image);
        foodNameTextView = view.findViewById(R.id.food_name);
        locationTextView = view.findViewById(R.id.location);
        contentTextView = view.findViewById(R.id.diary_content);
        btnBack = view.findViewById(R.id.btn_back);
        favoriteButton = view.findViewById(R.id.favorite_button);
        restaurantNameTextView = view.findViewById(R.id.restaurant_name);
        ratingBar = view.findViewById(R.id.diary_rating_bar);

        diaryApiService = RetrofitClient.getDiaryApiService();

        Bundle args = getArguments();
        if (args != null) {
            diaryId = args.getLong("diaryId", -1L);
            imageUrl = args.getString("imageUrl", "");
            restaurantName = args.getString("restaurant_name", "");
            foodName = args.getString("foodName", "");
            location = args.getString("location", "");
            content = args.getString("content", "");
            rating = args.getFloat("rating", 0f);

            Log.d(TAG, "📦 전달받은 초기값:");
            Log.d(TAG, "diaryId: " + diaryId);
            Log.d(TAG, "imageUrl: " + imageUrl);
            Log.d(TAG, "restaurantName: " + restaurantName);
            Log.d(TAG, "foodName: " + foodName);
            Log.d(TAG, "location: " + location);
            Log.d(TAG, "content: " + content);
            Log.d(TAG, "rating: " + rating);

            String fullImageUrl = "http://13.125.46.254:8080" + imageUrl;
            Glide.with(requireContext())
                    .load(fullImageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(diaryImageView);

            foodNameTextView.setText("메뉴: " + foodName);
            locationTextView.setText("위치: " + location);
            restaurantNameTextView.setText("식당명: " + restaurantName);
            contentTextView.setText(content);
            ratingBar.setRating(rating);
            updateFavoriteIcon();

            loadDiaryDetail(diaryId);
        }

        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        ImageButton moreButton = view.findViewById(R.id.btn_more);
        moreButton.setOnClickListener(v -> showPopupMenu(v));

        favoriteButton.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            updateFavoriteIcon();
            updateFavoriteStatusOnServer();
        });

        return view;
    }

    private void loadDiaryDetail(Long diaryId) {
        Log.d(TAG, "🚀 loadDiaryDetail() 호출됨, diaryId: " + diaryId);

        diaryApiService.getDiary(diaryId).enqueue(new Callback<DiaryEntry>() {
            @Override
            public void onResponse(Call<DiaryEntry> call, Response<DiaryEntry> response) {
                Log.d(TAG, "📡 서버 응답: 성공 여부 = " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    DiaryEntry diary = response.body();

                    foodName = diary.getFoodName();
                    content = diary.getContent();
                    imageUrl = diary.getImageUrl();
                    restaurantName = diary.getRestaurantName();
                    location = diary.getLocation();
                    rating = diary.getRating();
                    isFavorite = diary.isFavorite();
                    restaurantId = diary.getId(); // ⚠️ 실제 restaurantId로 대체 필요 시 수정

                    Log.d(TAG, "✅ 서버로부터 받은 값:");
                    Log.d(TAG, "restaurantId: " + restaurantId);
                    Log.d(TAG, "imageUrl: " + imageUrl);
                    Log.d(TAG, "foodName: " + foodName);
                    Log.d(TAG, "location: " + location);
                    Log.d(TAG, "restaurantName: " + restaurantName);
                    Log.d(TAG, "content: " + content);
                    Log.d(TAG, "rating: " + rating);
                    Log.d(TAG, "isFavorite: " + isFavorite);

                    foodNameTextView.setText("메뉴: " + foodName);
                    locationTextView.setText("위치: " + location);
                    restaurantNameTextView.setText("식당명: " + restaurantName);
                    contentTextView.setText(content);
                    ratingBar.setRating(rating);
                    updateFavoriteIcon();
                } else {
                    Log.w(TAG, "⚠️ 응답 성공 but body null");
                }
            }

            @Override
            public void onFailure(Call<DiaryEntry> call, Throwable t) {
                Log.e(TAG, "❌ loadDiaryDetail 실패", t);
            }
        });
    }

    private void updateFavoriteIcon() {
        favoriteButton.setImageResource(isFavorite ? R.drawable.heart : R.drawable.ic_heart);
    }

    private void updateFavoriteStatusOnServer() {
        if (diaryId == -1) return;

        DiaryEntry updatedDiary = new DiaryEntry(diaryId, imageUrl, foodName, location, content, rating, isFavorite);
        diaryApiService.updateDiary(diaryId, updatedDiary).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "❤️ 좋아요 상태 업데이트 성공");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "💔 좋아요 상태 업데이트 실패", t);
            }
        });

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateDiaryFavoriteStatus(diaryId, isFavorite);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.diary_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_edit) {
            openEditFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openEditFragment() {
        Log.d(TAG, "✏️ 수정 버튼 클릭 → DiaryEditFragment로 이동");

        DiaryEditFragment diaryEditFragment = new DiaryEditFragment();
        Bundle bundle = new Bundle();

        bundle.putLong("diaryId", diaryId != null ? diaryId : -1L);
        bundle.putLong("restaurant_id", restaurantId != null ? restaurantId : -1L);
        bundle.putString("restaurant_name", restaurantName);
        bundle.putString("restaurant_image", imageUrl);   // ✅ 수정됨
        bundle.putString("foodName", foodName);
        bundle.putString("restaurant_address", location);
        bundle.putFloat("rating", rating);
        bundle.putString("content", content);

        Log.d(TAG, "📤 번들로 전달하는 값:");
        Log.d(TAG, "restaurant_id: " + restaurantId);
        Log.d(TAG, "restaurant_name: " + restaurantName);
        Log.d(TAG, "imageUrl: " + imageUrl);
        Log.d(TAG, "foodName: " + foodName);
        Log.d(TAG, "location: " + location);
        Log.d(TAG, "rating: " + rating);
        Log.d(TAG, "content: " + content);

        diaryEditFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, diaryEditFragment)
                .addToBackStack(null)
                .commit();
    }

    private void showPopupMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), anchor);
        popupMenu.getMenuInflater().inflate(R.menu.diary_detail_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_edit) {
                openEditFragment();
                return true;
            } else if (item.getItemId() == R.id.menu_delete) {
                confirmDelete();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(requireContext())
                .setTitle("삭제 확인")
                .setMessage("정말로 이 일기를 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> deleteDiary())
                .setNegativeButton("취소", null)
                .show();
    }

    private void deleteDiary() {
        if (diaryId == null || diaryId == -1) return;

        diaryApiService.deleteDiaryWithImages(diaryId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "🗑️ 일기 삭제 성공");
                    Toast.makeText(getContext(), "일기가 삭제되었습니다", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Log.e(TAG, "❌ 삭제 실패 코드: " + response.code());
                    Toast.makeText(getContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "❌ 삭제 실패", t);
                Toast.makeText(getContext(), "서버 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
