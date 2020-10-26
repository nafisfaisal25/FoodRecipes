package com.example.foodrecipes.util;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int mVerticalSpacingHeight;

    public VerticalSpacingItemDecoration(int verticalSpacingHeight) {
        this.mVerticalSpacingHeight = verticalSpacingHeight;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.top = mVerticalSpacingHeight;
    }
}
