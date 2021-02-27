package com.example.foodrecipes.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.repositories.RecipeRepository;

public class RecipeDetailsViewModel extends ViewModel {
    private RecipeRepository mRecipeRepository;
    private boolean mRecipeIsRetrieved;
    private String mRequestedRecipeId;

    public RecipeDetailsViewModel() {
        mRecipeRepository = RecipeRepository.getInstance();
        mRecipeIsRetrieved = false;
    }

    public LiveData<Recipe> getRecipe() {
        return mRecipeRepository.getRecipe();
    }

    public LiveData<Boolean> getRecipeRequestTimeout() {
        return mRecipeRepository.getRecipeRequestTimeout();
    }

    public void getRecipeApi(String recipeId) {
        mRequestedRecipeId = recipeId;
        mRecipeRepository.getRecipesAPi(recipeId);
    }

    public boolean didRecipeRetrieved() {
       return mRecipeIsRetrieved;
    }

    public void setRecipeRetrieved(boolean flag) {
        mRecipeIsRetrieved = flag;
    }

    public void setRequestedRecipeId(String requestedRecipeId) {
        this.mRequestedRecipeId = requestedRecipeId;
    }

    public String getRequestedRecipeId() {
        return mRequestedRecipeId;
    }

    public void setRecipe(Recipe recipe) {
        mRecipeRepository.setRecipe(recipe);
    }
}
