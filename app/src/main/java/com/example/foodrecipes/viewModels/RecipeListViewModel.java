package com.example.foodrecipes.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.repositories.RecipeRepository;

import java.util.List;

public class RecipeListViewModel extends ViewModel {

    private RecipeRepository mRecipeRepository;

    public RecipeListViewModel() {
        mRecipeRepository = RecipeRepository.getInstance();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return  mRecipeRepository.getRecipes();
    }

    public void searchRecipesAPi(String query, int pageNumber) {
        mRecipeRepository.searchRecipesAPi(query, pageNumber);
    }
}
