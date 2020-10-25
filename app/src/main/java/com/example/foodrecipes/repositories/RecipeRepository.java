package com.example.foodrecipes.repositories;

import androidx.lifecycle.LiveData;

import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.requests.RecipeApiClient;

import java.util.List;

public class RecipeRepository {
    private static RecipeRepository mRecipeRepository;
    private RecipeApiClient mRecipeAliClient;
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

    public void searchRecipesAPi(String query, int pageNumber) {
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        mRecipeAliClient.searchRecipesApi(query, pageNumber);
    }
}
