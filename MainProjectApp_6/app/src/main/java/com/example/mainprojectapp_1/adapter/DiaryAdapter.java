package com.example.mainprojectapp_1.adapter;  // ✅ 패키지 선언이 최상단에 위치

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.mainprojectapp_1.DiaryEntry;
import com.example.mainprojectapp_1.R;
import com.example.mainprojectapp_1.api.DiaryApiService;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {
    private List<DiaryEntry> diaryEntries;
    private OnDiaryClickListener listener;
    private DiaryApiService diaryApiService; // ✅ 서버 API 연동을 위한 Retrofit 인터페이스

    public DiaryAdapter(List<DiaryEntry> diaryEntries, DiaryApiService diaryApiService) {
        this.diaryEntries = diaryEntries;
        this.diaryApiService = diaryApiService;
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diary, parent, false);
        return new DiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        DiaryEntry diaryEntry = diaryEntries.get(position);

        // ✅ Glide를 사용해 이미지 로드
        Glide.with(holder.itemView.getContext())
                .load("http://13.125.46.254:8080" + diaryEntry.getImageUrl())
                .error(R.drawable.placeholder_image) // 실패 시 대체 이미지
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        Log.e("Glide", "이미지 로딩 실패", e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("Glide", "이미지 로딩 성공");
                        return false;
                    }
                })
                .into(holder.diaryImage);

        // ✅ 하트(좋아요) 상태 설정
        holder.favoriteButton.setImageResource(diaryEntry.isFavorite() ? R.drawable.heart : R.drawable.ic_heart);

        // ✅ 하트 버튼 클릭 이벤트 (서버 업데이트 포함)
        holder.favoriteButton.setOnClickListener(v -> {
            boolean newFavoriteStatus = !diaryEntry.isFavorite();
            diaryEntry.setFavorite(newFavoriteStatus);
            holder.favoriteButton.setImageResource(newFavoriteStatus ? R.drawable.heart : R.drawable.ic_heart);

            // 🔥 서버에 좋아요 상태 업데이트 요청
            updateFavoriteStatusOnServer(diaryEntry);
        });

        // ✅ 앨범 클릭 시 상세 페이지로 이동하도록 이벤트 추가
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDiaryClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return diaryEntries.size();
    }

    public static class DiaryViewHolder extends RecyclerView.ViewHolder {
        ImageView diaryImage;
        ImageButton favoriteButton;

        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            diaryImage = itemView.findViewById(R.id.diary_image);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
        }
    }

    public interface OnDiaryClickListener {
        void onDiaryClick(int position);
    }

    public void setOnDiaryClickListener(OnDiaryClickListener listener) {
        this.listener = listener;
    }

    // ✅ 서버에 좋아요 상태 업데이트 요청
    private void updateFavoriteStatusOnServer(DiaryEntry diaryEntry) {
        Call<Void> call = diaryApiService.updateFavoriteStatus(diaryEntry.getId(), Collections.singletonMap("isFavorite", diaryEntry.isFavorite()));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("서버에 좋아요 상태 업데이트 성공: " + diaryEntry.getId());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.err.println("서버 좋아요 업데이트 실패: " + t.getMessage());
            }
        });
    }
}
