package com.example.foodrecipes.adapters;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.foodrecipes.R;
import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "RecipeRecyclerAdapter";
    private final static int RECIPE_TYPE = 1;
    private final static int LOADING_TYPE = 2;
    private final static int CATEGORY_TYPE = 3;

    private List<Recipe> mRecipes;
    private List<Recipe> mRecipesBackUp;

    private OnRecipeListener mOnRecipeListener;

    public RecipeRecyclerAdapter(OnRecipeListener mOnRecipeListener) {
        this.mOnRecipeListener = mOnRecipeListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case RECIPE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recipe_list_item, parent, false);
                return new RecipeViewHolder(view, mOnRecipeListener);
            case LOADING_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_list_item, parent, false);
                return new HorizontalProgressViewHolder(view);
            case CATEGORY_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category_list_item, parent, false);
                return new CategoryViewHolder(view, mOnRecipeListener);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        Log.d(TAG, "onBindViewHolder: position: " + position);
        if (viewType == RECIPE_TYPE) {
            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_launcher_background);
            ((RecipeViewHolder)holder).mTitle.setText(mRecipes.get(position).getTitle());
            ((RecipeViewHolder)holder).mPublisher.setText(mRecipes.get(position).getPublisher());
            ((RecipeViewHolder)holder).mSocialRating.setText(String.valueOf(Math.round(mRecipes.get(position).getSocial_rank())));
            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(mRecipes.get(position).getImage_url())
                    .into(((RecipeViewHolder)holder).mItemImage);
        } else if (viewType == CATEGORY_TYPE) {
            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_launcher_background);
            ((CategoryViewHolder)holder).mCategoryName.setText(mRecipes.get(position).getTitle());

            Uri imageUri = Uri.parse("android.resource://com.example.foodrecipes/drawable/" + mRecipes.get(position).getImage_url());
            Log.d(TAG, "onBindViewHolder: image_url: " + "android.resource://com.example.foodrecipes/drawable/" + mRecipes.get(position).getImage_url());

            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(imageUri)
                    .into(((CategoryViewHolder)holder).categoryImage);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getLoadingCondition(position)) {
            return LOADING_TYPE;
        } else if (mRecipes.get(position).getSocial_rank() == -1){
            return CATEGORY_TYPE;
        } else {
            return RECIPE_TYPE;
        }
    }

    private boolean getLoadingCondition(int position) {
        return mRecipes.get(position).getTitle().equals("Loading...") || (position == mRecipes.size() - 1 && position != 0);
    }

    public void displayLoading() {
        if (!isLoading()) {
            Recipe recipe = new Recipe();
            recipe.setTitle("Loading...");
            List<Recipe> recipes = Arrays.asList(recipe);
            mRecipes = recipes;
            notifyDataSetChanged();
        }
    }

    public void displaySearchCategories() {
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < Constants.DEFAULT_SEARCH_CATEGORIES.length; i++) {
            Recipe recipe = new Recipe();
            recipe.setImage_url(Constants.DEFAULT_SEARCH_CATEGORY_IMAGES[i]);
            recipe.setTitle(Constants.DEFAULT_SEARCH_CATEGORIES[i]);
            recipe.setSocial_rank(-1);
            recipes.add(recipe);
        }
        mRecipes = recipes;
        mRecipesBackUp = recipes;
        Log.d(TAG, "displaySearchCategories: recipes_size: " + mRecipes.size());
        notifyDataSetChanged();
    }

    private boolean isLoading() {
        if (mRecipes != null && mRecipes.size() > 0) {
            if (mRecipes.get(mRecipes.size() - 1).getTitle().equals("Loading...")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return mRecipes != null ? mRecipes.size() : 0;
    }

    public void setRecipes(List<Recipe> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    public void filterCategory(String query) {
        List<Recipe> recipes = new ArrayList<>();
        mRecipesBackUp.forEach(recipe -> {
            if (recipe.getTitle().toLowerCase().contains(query.toLowerCase())) {
                recipes.add(recipe);
            }
        });
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    public Recipe getSelectedRecipe(int position) {
        if (mRecipes != null) {
            return mRecipes.get(position);
        }
        return null;
    }
}
