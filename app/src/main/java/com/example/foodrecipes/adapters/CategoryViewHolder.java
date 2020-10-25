package com.example.foodrecipes.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipes.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    OnRecipeListener mOnRecipeListener;
    CircleImageView categoryImage;
    TextView mCategoryName;

    public CategoryViewHolder(@NonNull View itemView, OnRecipeListener onRecipeListener) {
        super(itemView);
        mOnRecipeListener = onRecipeListener;
        mCategoryName = itemView.findViewById(R.id.category_text);
        categoryImage = itemView.findViewById(R.id.category_image);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mOnRecipeListener.onCategorySelected(mCategoryName.getText().toString());
    }
}
