package com.example.foodrecipes.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.repositories.RecipeRepository;

import java.util.List;

public class RecipeListViewModel extends ViewModel {

    private RecipeRepository mRecipeRepository;
    private boolean mIsViewingRecipes;
    private boolean mIsPerformingAQuery;

    public RecipeListViewModel() {
        mIsViewingRecipes = false;
        mIsPerformingAQuery = false;
        mRecipeRepository = RecipeRepository.getInstance();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return  mRecipeRepository.getRecipes();
    }

    public void searchRecipesAPi(String query, int pageNumber) {
        mIsViewingRecipes = true;
        mIsPerformingAQuery = true;
        mRecipeRepository.searchRecipesAPi(query, pageNumber);
    }

    public boolean getIsViewingRecipes() {
        return mIsViewingRecipes;
    }

    public void setIsViewingRecipes(boolean isViewingRecipes) {
        mIsViewingRecipes = isViewingRecipes;
    }

    public void searchNextPage() {
        if (!mIsPerformingAQuery && mIsViewingRecipes) {
            mIsPerformingAQuery = true;
            mRecipeRepository.searchNextPage();
        }
    }

    public boolean getIsPerformingAQuery() {
        return mIsPerformingAQuery;
    }

    public void setIsPerformingAQuery(boolean mIsPerformingAQuery) {
        this.mIsPerformingAQuery = mIsPerformingAQuery;
    }
}
