package com.example.foodrecipes.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.foodrecipes.R;
import com.example.foodrecipes.models.Recipe;

public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView mTitle;
    TextView mPublisher;
    TextView mSocialRating;
    ImageView mItemImage;
    OnRecipeListener mOnRecipeListener;
    RequestManager mRequestManager;

    public RecipeViewHolder(@NonNull View itemView, OnRecipeListener onRecipeListener, RequestManager requestManager) {
        super(itemView);
        mOnRecipeListener = onRecipeListener;
        mTitle = itemView.findViewById(R.id.recipe_title);
        mPublisher = itemView.findViewById(R.id.recipe_publisher);
        mSocialRating = itemView.findViewById(R.id.recipe_social_score);
        mItemImage = itemView.findViewById(R.id.recipe_image);
        mRequestManager = requestManager;
        itemView.setOnClickListener(this);
    }

    public void onBind(Recipe recipe) {
        mRequestManager
                .load(recipe.getImage_url())
                .into(mItemImage);
    }

    @Override
    public void onClick(View view) {
        mOnRecipeListener.onItemSelected(getAdapterPosition());
    }
}
