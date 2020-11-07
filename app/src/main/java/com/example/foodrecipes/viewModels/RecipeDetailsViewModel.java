package com.example.foodrecipes.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.repositories.RecipeRepository;

public class RecipeDetailsViewModel extends ViewModel {
    RecipeRepository mRecipeRepository;

    public RecipeDetailsViewModel() {
        mRecipeRepository = RecipeRepository.getInstance();
    }

    public LiveData<Recipe> getRecipe() {
        return mRecipeRepository.getRecipe();
    }

    public void getRecipeApi(String recipeId) {
        mRecipeRepository.getRecipesAPi(recipeId);
    }

}
