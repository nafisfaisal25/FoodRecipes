package com.example.foodrecipes.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.requests.RecipeApiClient;

import java.util.List;

public class RecipeRepository {
    private static RecipeRepository mRecipeRepository;
    private RecipeApiClient mRecipeAliClient;
    private String mQuery;
    private int mPageNumber;
    private MutableLiveData<Boolean> mIsQueryExhausted = new MutableLiveData<>();
    private MediatorLiveData<List<Recipe>> mRecipes = new MediatorLiveData<>();


    private RecipeRepository() {
        mRecipeAliClient = RecipeApiClient.getInstance();
        initMediator();
    }

    public static RecipeRepository getInstance() {
        if (mRecipeRepository == null) {
            mRecipeRepository = new RecipeRepository();
        }
        return mRecipeRepository;
    }

    private void initMediator() {
        LiveData<List<Recipe>> recipeListApiSource = mRecipeAliClient.getRecipes();
        mRecipes.addSource(recipeListApiSource, recipes -> {
            if (recipes != null) {
                mRecipes.setValue(recipes);
                doneQuery(recipes);
            } else {
                // need to search from cache
                doneQuery(null);
            }
        });
    }

    private void doneQuery(List<Recipe> list) {
        if (list != null) {
            if (list.size() % 30 != 0) {
                mIsQueryExhausted.setValue(true);
            }
        } else {
            mIsQueryExhausted.setValue(true);
        }
    }

    public LiveData<Boolean> isQueryExhausted() {
        return mIsQueryExhausted;
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipes;
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
        mIsQueryExhausted.setValue(false);
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

    public void setRecipe(Recipe recipe) {
        mRecipeAliClient.setRecipe(recipe);
    }
}
