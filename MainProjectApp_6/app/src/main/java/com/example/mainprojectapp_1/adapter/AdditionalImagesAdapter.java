package com.example.mainprojectapp_1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mainprojectapp_1.R;

import java.util.List;

public class AdditionalImagesAdapter extends RecyclerView.Adapter<AdditionalImagesAdapter.ViewHolder> {

    private final List<String> imageUris;
    private final OnImageRemoveListener removeListener;

    public interface OnImageRemoveListener {
        void onImageRemove(String uri);
    }

    public AdditionalImagesAdapter(List<String> imageUris, OnImageRemoveListener removeListener) {
        this.imageUris = imageUris;
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_additional_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUri = imageUris.get(position);
        Context context = holder.itemView.getContext();
        Glide.with(context).load(imageUri).into(holder.imageView);

        holder.btnRemove.setOnClickListener(v -> removeListener.onImageRemove(imageUri));
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}
