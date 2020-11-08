package com.example.foodrecipes.repositories;

import androidx.lifecycle.LiveData;

import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.requests.RecipeApiClient;

import java.util.List;

public class RecipeRepository {
    private static RecipeRepository mRecipeRepository;
    private RecipeApiClient mRecipeAliClient;
    private String mQuery;
    private int mPageNumber;
    private RecipeRepository() {
        mRecipeAliClient = RecipeApiClient.getInstance();
    }

    public static RecipeRepository getInstance() {
        if (mRecipeRepository == null) {
            mRecipeRepository = new RecipeRepository();
        }
        return mRecipeRepository;
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipeAliClient.getRecipes();
    }

    public LiveData<Recipe> getRecipe() {
        return mRecipeAliClient.getRecipe();
    }

    public LiveData<Boolean> getRecipeRequestTimeout() {
        return mRecipeAliClient.getRecipeRequestTimeout();
    }

    public void searchRecipesAPi(String query, int pageNumber) {
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        mPageNumber = pageNumber;
        mQuery = query;
        mRecipeAliClient.searchRecipesApi(query, pageNumber);
    }

    public void getRecipesAPi(String recipeId) {
        mRecipeAliClient.getRecipeApi(recipeId);
    }

    public void searchNextPage() {
        searchRecipesAPi(mQuery, mPageNumber + 1);
    }
}
