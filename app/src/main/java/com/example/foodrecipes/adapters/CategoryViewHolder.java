package com.example.foodrecipes.adapters;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.foodrecipes.R;
import com.example.foodrecipes.models.Recipe;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    OnRecipeListener mOnRecipeListener;
    CircleImageView categoryImage;
    TextView mCategoryName;
    private RequestManager mRequestManager;

    public CategoryViewHolder(@NonNull View itemView, OnRecipeListener onRecipeListener, RequestManager requestManager) {
        super(itemView);
        mOnRecipeListener = onRecipeListener;
        mCategoryName = itemView.findViewById(R.id.category_text);
        categoryImage = itemView.findViewById(R.id.category_image);
        mRequestManager = requestManager;
        itemView.setOnClickListener(this);
    }

    public void onBind(Recipe recipe) {
        Uri imageUri = Uri.parse("android.resource://com.example.foodrecipes/drawable/" + recipe.getImage_url());

        mRequestManager
                .load(imageUri)
                .into(categoryImage);
    }

    @Override
    public void onClick(View view) {
        mOnRecipeListener.onCategorySelected(mCategoryName.getText().toString());
    }
}
