package com.example.foodrecipes.viewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.repositories.RecipeRepository;
import com.example.foodrecipes.util.Resource;

public class RecipeDetailsViewModel extends AndroidViewModel {
    private RecipeRepository mRecipeRepository;
    private boolean mRecipeIsRetrieved;
    private String mRequestedRecipeId;

    public RecipeDetailsViewModel(Application application) {
        super(application);
        mRecipeRepository = RecipeRepository.getInstance(application);
    }

    public LiveData<Resource<Recipe>> searchRecipe(String recipeId) {
        return mRecipeRepository.searchRecipeApi(recipeId);
    }
}
