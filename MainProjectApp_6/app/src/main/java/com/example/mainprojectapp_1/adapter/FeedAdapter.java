package com.example.mainprojectapp_1.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mainprojectapp_1.R;
import com.example.mainprojectapp_1.model.FoodDiaryFeedResponse;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(FoodDiaryFeedResponse diary);
    }

    private Context context;
    private List<FoodDiaryFeedResponse> diaryList;
    private OnItemClickListener listener;

    public FeedAdapter(Context context, List<FoodDiaryFeedResponse> diaryList, OnItemClickListener listener) {
        this.context = context;
        this.diaryList = diaryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_diary, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        FoodDiaryFeedResponse diary = diaryList.get(position);

        Log.d("FeedAdapter", "📷 photoPath = " + diary.getPhotoPath());
        Log.d("FeedAdapter", "객체 전체 = " + diary.toString());

        if (diary.getPhotoPath() != null && !diary.getPhotoPath().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load("http://13.125.46.254:8080" + diary.getPhotoPath())
                    .into(holder.diaryImage);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.placeholder_image) // drawable에 기본 이미지 넣어줘!
                    .into(holder.diaryImage);
        }

        // 하트 상태
        if (Boolean.TRUE.equals(diary.getHeart())) {
            holder.favoriteButton.setImageResource(R.drawable.ic_heart);
        } else {
            holder.favoriteButton.setImageResource(R.drawable.heart);
        }

        // 클릭 이벤트
        holder.itemView.setOnClickListener(v -> listener.onItemClick(diary));
    }

    @Override
    public int getItemCount() {
        return diaryList != null ? diaryList.size() : 0;
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder {
        ImageView diaryImage;
        ImageButton favoriteButton;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            diaryImage = itemView.findViewById(R.id.diary_image);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
        }
    }

}
