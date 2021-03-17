package com.example.foodrecipes.adapters;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.foodrecipes.R;
import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "RecipeRecyclerAdapter";
    private final static int RECIPE_TYPE = 1;
    private final static int LOADING_TYPE = 2;
    private final static int CATEGORY_TYPE = 3;
    private final static int EXHAUSTED_TYPE = 4;


    private List<Recipe> mRecipes;
    private List<Recipe> mRecipesBackUp;

    private OnRecipeListener mOnRecipeListener;
    private RequestManager mRequestManager;
    public RecipeRecyclerAdapter(OnRecipeListener mOnRecipeListener, RequestManager requestManager) {
        this.mOnRecipeListener = mOnRecipeListener;
        mRequestManager = requestManager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case RECIPE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recipe_list_item, parent, false);
                return new RecipeViewHolder(view, mOnRecipeListener, mRequestManager);
            case LOADING_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_list_item, parent, false);
                return new HorizontalProgressViewHolder(view);
            case EXHAUSTED_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_exhauted_query, parent, false);
                return new ExhaustedQueryViewHolder(view);
            case CATEGORY_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category_list_item, parent, false);
                return new CategoryViewHolder(view, mOnRecipeListener, mRequestManager);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        Log.d(TAG, "onBindViewHolder: position: " + position);
        if (viewType == RECIPE_TYPE) {
            ((RecipeViewHolder)holder).mTitle.setText(mRecipes.get(position).getTitle());
            ((RecipeViewHolder)holder).mPublisher.setText(mRecipes.get(position).getPublisher());
            ((RecipeViewHolder)holder).mSocialRating.setText(String.valueOf(Math.round(mRecipes.get(position).getSocial_rank())));
            ((RecipeViewHolder)holder).onBind(mRecipes.get(position));
        } else if (viewType == CATEGORY_TYPE) {
            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_launcher_background);
            ((CategoryViewHolder)holder).mCategoryName.setText(mRecipes.get(position).getTitle());
            Log.d(TAG, "onBindViewHolder: image_url: " + "android.resource://com.example.foodrecipes/drawable/" + mRecipes.get(position).getImage_url());
            ((CategoryViewHolder)holder).onBind(mRecipes.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mRecipes.get(position).getTitle().equals("Exhausted...")) {
            return EXHAUSTED_TYPE;
        } else if (getLoadingCondition(position)) {
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

    public void disPlayExhausted() {
        hideLoading();
        Recipe recipe = new Recipe();
        recipe.setTitle("Exhausted...");
        mRecipes.add(recipe);
        notifyDataSetChanged();
    }

    public void hideLoading() {
        if (isLoading() && !mRecipes.isEmpty()) {
            mRecipes.remove(mRecipes.size()-1);
            notifyDataSetChanged();
        }
    }

    public void disPlayOnlyLoading() {
        clearRecipeList();
        Recipe recipe = new Recipe();
        recipe.setTitle("Loading...");
        mRecipes.add(recipe);
        notifyDataSetChanged();
    }

    private void clearRecipeList() {
        if (mRecipes == null) {
            mRecipes = new ArrayList<>();
        } else {
            mRecipes.clear();
        }
        notifyDataSetChanged();
    }

    public void displayLoading() {
        if (mRecipes == null) {
            mRecipes = new ArrayList<>();
        }
        if (!isLoading()) {
            Recipe recipe = new Recipe();
            recipe.setTitle("Loading...");
            mRecipes.add(recipe);
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

    public Recipe getSelectedRecipe(int position) {
        if (mRecipes != null) {
            return mRecipes.get(position);
        }
        return null;
    }
}
