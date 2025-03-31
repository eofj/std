package com.example.mainprojectapp_1.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mainprojectapp_1.R;

import java.util.List;

public class AdBannerAdapter extends RecyclerView.Adapter<AdBannerAdapter.AdBannerViewHolder> {
    private List<Integer> bannerImages;
    private Context context;

    public AdBannerAdapter(Context context, List<Integer> bannerImages) {
        this.context = context;
        this.bannerImages = bannerImages;
    }

    @Override
    public AdBannerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_item, parent, false);
        return new AdBannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdBannerViewHolder holder, int position) {
        holder.bannerImage.setImageResource(bannerImages.get(position));


        // 광고 배너 클릭 리스너 설정
        holder.bannerImage.setOnClickListener(v -> {
            // 다이얼로그로 띄울 추가 이미지 리소스 ID
            int extraImageResId = R.drawable.banner_business; // drawable 폴더의 배너 이미지

            // Dialog 생성
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_image); // 이미지만 보여줄 레이아웃 사용

            // 이미지뷰 설정
            ImageView imageView = dialog.findViewById(R.id.dialog_image_view);
            imageView.setImageResource(extraImageResId);

            // 이미지 크기 설정
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    600
            );
            imageView.setLayoutParams(params);

            dialog.show();
        });
    }


    @Override
    public int getItemCount() {
        return bannerImages.size();
    }

    public static class AdBannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage;

        public AdBannerViewHolder(View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.banner_image);
        }
    }
}

