package com.example.foodrecipes.repositories;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.foodrecipes.AppExecutors;
import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.persistence.RecipeDao;
import com.example.foodrecipes.persistence.RecipeDatabase;
import com.example.foodrecipes.requests.RecipeApiClient;
import com.example.foodrecipes.requests.ServiceGenerator;
import com.example.foodrecipes.requests.responses.ApiResponse;
import com.example.foodrecipes.requests.responses.RecipeResponse;
import com.example.foodrecipes.requests.responses.RecipeSearchResponse;
import com.example.foodrecipes.util.Constants;
import com.example.foodrecipes.util.NetworkBoundResource;
import com.example.foodrecipes.util.Resource;

import java.util.List;

public class RecipeRepository {
    private static final String TAG = "RecipeRepository";
    private static RecipeRepository instance;
    private RecipeDao recipeDao;

    private RecipeRepository(Context context) {
        recipeDao = RecipeDatabase.getInstance(context).getRecipeDao();
    }

    public static RecipeRepository getInstance(Context context) {
        if (instance == null) {
            instance = new RecipeRepository(context);
        }
        return instance;
    }

    public LiveData<Resource<List<Recipe>>> searchRecipesApi(final String query, final int pageNumber) {
        return new NetworkBoundResource<List<Recipe>, RecipeSearchResponse>(AppExecutors.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull RecipeSearchResponse item) {
                if (item.getRecipes() != null) {
                    Recipe []recipes = new Recipe[item.getRecipes().size()];
                    int index = 0;
                    for(Long rowId: recipeDao.insertRecipes(item.getRecipes().toArray(recipes))) {
                        if (rowId == -1) {
                            recipeDao.updateRecipe(
                                    recipes[index].getRecipe_id(),
                                    recipes[index].getTitle(),
                                    recipes[index].getPublisher(),
                                    recipes[index].getImage_url(),
                                    recipes[index].getSocial_rank()
                            );
                        }
                        index++;
                    }
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Recipe> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Recipe>> loadFromDb() {
                return recipeDao.searchRecipes(query, pageNumber);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<RecipeSearchResponse>> createCall() {
                return ServiceGenerator.getRecipeApi().searchRecipe(
                        Constants.API_KEY,
                        query,
                        String.valueOf(pageNumber)
                );
            }
        }.getAsLiveData();
    }


    public LiveData<Resource<Recipe>> searchRecipeApi(String recipeId) {
        return new NetworkBoundResource<Recipe, RecipeResponse >(AppExecutors.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull RecipeResponse item) {
                if (item.getRecipe() != null) {
                    item.getRecipe().setTimestamp((int) (System.currentTimeMillis() / 1000));
                    recipeDao.insertRecipe(item.getRecipe());
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable Recipe data) {
                Log.d(TAG, "shouldFetch: recipe: " + data.toString());
                Log.d(TAG, "shouldFetch: currentTime: " + System.currentTimeMillis() / 1000);
                Log.d(TAG, "shouldFetch: last_refreshed" + data.getTimestamp());
                Log.d(TAG, "shouldFetch: time_dif: " + (System.currentTimeMillis() / 1000 - data.getTimestamp()) / 60 / 60 / 24);
                if (System.currentTimeMillis() / 1000 - data.getTimestamp() > Constants.RECIPE_REFRESH_TIME) {
                    Log.d(TAG, "shouldFetch: yes");
                    return true;
                }
                Log.d(TAG, "shouldFetch: no");
                return false;
            }

            @NonNull
            @Override
            protected LiveData<Recipe> loadFromDb() {
                return recipeDao.getRecipe(recipeId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<RecipeResponse>> createCall() {
                return ServiceGenerator.getRecipeApi().getRecipe(
                        Constants.API_KEY,
                        recipeId
                );
            }
        }.getAsLiveData();
    }
}
