package com.example.mainprojectapp_1.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.mainprojectapp_1.R;
import com.example.mainprojectapp_1.Restaurant;

import java.lang.ref.WeakReference;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private WeakReference<Context> contextRef;
    private List<Restaurant> restaurantList;
    private OnItemClickListener listener;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList, OnItemClickListener listener) {
        this.contextRef = new WeakReference<>(context);
        this.restaurantList = restaurantList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = contextRef.get();
        if (context == null) return null;

        View view = LayoutInflater.from(context).inflate(R.layout.restaurant_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        holder.restaurantName.setText(restaurant.getName());
        holder.restaurantAddress.setText(restaurant.getPlace());

        String imageUrl = restaurant.getImage();

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.img_wait2)
                .error(R.drawable.img_wait2)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("GlideError", "이미지 로드 실패: " + (e != null ? e.getMessage() : "Unknown error"));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("GlideSuccess", "이미지 로드 성공!");
                        return false;
                    }
                })
                .into(holder.restaurantImage);



        // 아이템 클릭 리스너
        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onItemClick(restaurant));
        }
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    // 리스트 업데이트 메서드 추가 (필터링된 데이터 적용)
    public void updateList(List<Restaurant> newList) {
        restaurantList.clear();
        restaurantList.addAll(newList);
        notifyDataSetChanged(); // UI 갱신
    }

    // 아이템 클릭 리스너 인터페이스
    public interface OnItemClickListener {
        void onItemClick(Restaurant restaurant);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantName, restaurantAddress;
        ImageView restaurantImage;

        public ViewHolder(View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.restaurant_name);
            restaurantAddress = itemView.findViewById(R.id.restaurant_address);
            restaurantImage = itemView.findViewById(R.id.restaurant_image);
        }
    }
}
