package com.example.mainprojectapp_1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainprojectapp_1.adapter.FeedAdapter;
import com.example.mainprojectapp_1.api.DiaryApiService;
import com.example.mainprojectapp_1.api.RetrofitClient;
import com.example.mainprojectapp_1.model.FoodDiaryFeedResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryFragment extends Fragment {

    private RecyclerView recyclerView;
    private FeedAdapter feedAdapter;
    private TextView diaryCountTextView;
    private List<FoodDiaryFeedResponse> diaryEntries = new ArrayList<>();
    private DiaryApiService diaryApiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dining_diary, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        diaryCountTextView = view.findViewById(R.id.diary_count);
        diaryApiService = RetrofitClient.getDiaryApiService();

        fetchDiariesFromServer();

        feedAdapter = new FeedAdapter(getContext(), diaryEntries, diary -> {
            DiaryDetailFragment diaryDetailFragment = new DiaryDetailFragment();

            Bundle bundle = new Bundle();
            bundle.putLong("diaryId", diary.getId());
            bundle.putString("imageUrl", diary.getPhotoPath());
            bundle.putString("foodName", diary.getMenuName());
            bundle.putString("location", diary.getPlace());
            bundle.putString("content", diary.getDiaryText());
            bundle.putFloat("rating", diary.getRating() != null ? diary.getRating() : 0f);
            bundle.putBoolean("isFavorite", diary.getHeart() != null && diary.getHeart());
            bundle.putString("restaurant_name", diary.getRestaurantName());

            diaryDetailFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, diaryDetailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // 간격을 8dp로 설정 (여기서 원하는 간격으로 수정 가능)

        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing); // 예: 8dp
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, spacing, true));
        recyclerView.setAdapter(feedAdapter);
        return view;
    }

    private void fetchDiariesFromServer() {
        Log.d("DiaryFetch", "📡 서버에 피드 요청 시작");

        diaryApiService.getFeedPhotos().enqueue(new Callback<List<FoodDiaryFeedResponse>>() {
            @Override
            public void onResponse(Call<List<FoodDiaryFeedResponse>> call, Response<List<FoodDiaryFeedResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    diaryEntries.clear();
                    diaryEntries.addAll(response.body());

                    Log.d("DiaryFetch", "✅ 피드 응답 성공 - 개수: " + diaryEntries.size());
                    for (FoodDiaryFeedResponse entry : diaryEntries) {
                        Log.d("DiaryFetch", "📖 일기: ID=" + entry.getId() + ", 사진=" + entry.getPhotoPath());
                    }

                    feedAdapter.notifyDataSetChanged();
                    diaryCountTextView.setText("총 " + diaryEntries.size() + "개");
                } else {
                    Log.e("DiaryFetch", "❌ 응답 실패 - 코드: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<FoodDiaryFeedResponse>> call, Throwable t) {
                Log.e("DiaryFetch", "❌ 서버 통신 실패", t);
            }
        });
    }

}
