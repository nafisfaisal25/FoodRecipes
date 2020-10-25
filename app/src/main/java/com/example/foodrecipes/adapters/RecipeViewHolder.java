package com.example.foodrecipes.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipes.R;

public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView mTitle;
    TextView mPublisher;
    TextView mSocialRating;
    ImageView mItemImage;
    OnRecipeListener mOnRecipeListener;

    public RecipeViewHolder(@NonNull View itemView, OnRecipeListener onRecipeListener) {
        super(itemView);
        mOnRecipeListener = onRecipeListener;
        mTitle = itemView.findViewById(R.id.recipe_title);
        mPublisher = itemView.findViewById(R.id.recipe_publisher);
        mSocialRating = itemView.findViewById(R.id.recipe_social_score);
        mItemImage = itemView.findViewById(R.id.recipe_image);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mOnRecipeListener.onItemSelected(getAdapterPosition());
    }
}
