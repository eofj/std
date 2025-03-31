package com.example.mainprojectapp_1;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int spanCount, spacing;
    private boolean includeEdge;

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // column index

        // 아이템 간격 설정
        if (includeEdge) {
            // 첫 번째 열 아이템의 좌측 여백
            outRect.left = spacing - column * spacing / spanCount;
            // 마지막 열 아이템의 우측 여백
            outRect.right = (column + 1) * spacing / spanCount;

            // 첫 번째 행 아이템의 상단 여백
            if (position < spanCount) {
                outRect.top = spacing;
            }
            // 모든 아이템에 하단 여백
            outRect.bottom = spacing;
        } else {
            // 모든 아이템에 좌측, 우측 여백 설정
            outRect.left = column * spacing / spanCount;
            outRect.right = spacing - (column + 1) * spacing / spanCount;
            // 그리드 두 번째 이후부터 상단 여백
            if (position >= spanCount) {
                outRect.top = spacing;
            }
        }
    }
}
