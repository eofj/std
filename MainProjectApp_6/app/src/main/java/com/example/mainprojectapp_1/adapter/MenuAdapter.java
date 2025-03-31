package com.example.mainprojectapp_1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainprojectapp_1.MenuItem;
import com.example.mainprojectapp_1.R;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<MenuItem> menuList;

    public MenuAdapter(List<MenuItem> menuList) {
        this.menuList = menuList;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_item, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = menuList.get(position);
        holder.menuName.setText(item.getName());

        // 가격을 숫자에서 문자열로 변환하여 설정
        holder.menuPrice.setText(String.valueOf(item.getPrice()));
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    // 메뉴 리스트를 갱신하는 메서드 추가
    public void updateMenu(List<MenuItem> newMenuList) {
        this.menuList = newMenuList;
        notifyDataSetChanged(); // RecyclerView를 갱신
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView menuName, menuPrice;

        public MenuViewHolder(View itemView) {
            super(itemView);
            menuName = itemView.findViewById(R.id.menu_name);
            menuPrice = itemView.findViewById(R.id.menu_price);
        }
    }
}
