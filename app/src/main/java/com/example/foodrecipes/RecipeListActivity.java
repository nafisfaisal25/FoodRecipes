package com.example.foodrecipes;

import android.os.Bundle;
import android.util.Log;


import androidx.lifecycle.ViewModelProvider;

import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.requests.RecipeApi;
import com.example.foodrecipes.requests.ServiceGenerator;
import com.example.foodrecipes.requests.responses.RecipeSearchResponse;
import com.example.foodrecipes.util.Constants;
import com.example.foodrecipes.viewModels.RecipeListViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeListActivity extends BaseActivity {
    private static final String TAG = "RecipeListActivity";
    private RecipeListViewModel mRecipeListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mRecipeListViewModel = new ViewModelProvider(this).get(RecipeListViewModel.class);
        subscribeObservers();

    }

    private void subscribeObservers() {
        mRecipeListViewModel.getRecipes().observe(this, recipes -> {

        });
    }

    private void testSearchQuery() {
        RecipeApi recipeApi = ServiceGenerator.getRecipeApi();
        Call<RecipeSearchResponse> searchResponse = recipeApi.searchRecipe(
                Constants.API_KEY,
                "chicken breast",
                "1"
        );

        searchResponse.enqueue(new Callback<RecipeSearchResponse>() {
            @Override
            public void onResponse(Call<RecipeSearchResponse> call, Response<RecipeSearchResponse> response) {
                Log.d(TAG, "onResponse: server response : " + response.toString());
                if (response.code() == 200) {
                    Log.d(TAG, "onResponse: " + response.body().toString());
                    List<Recipe> recipes = new ArrayList<>(response.body().getRecipes());
                    recipes.forEach(recipe -> {
                        Log.d(TAG, "onResponse: " + recipe.getRecipe_id());
                    });
                } else {
                    try {
                        Log.d(TAG, "onResponse: " + response.errorBody().toString());
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<RecipeSearchResponse> call, Throwable t) {

            }
        });
    }
}
