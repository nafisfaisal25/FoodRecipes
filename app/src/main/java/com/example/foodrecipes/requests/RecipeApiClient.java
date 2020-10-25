package com.example.foodrecipes.requests;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.foodrecipes.AppExecutors;
import com.example.foodrecipes.models.Recipe;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.example.foodrecipes.util.Constants.NETWORK_TIMEOUT;

public class RecipeApiClient {
    private static RecipeApiClient mRecipeApiClient;
    MutableLiveData<List<Recipe>> mRecipes;

    private RecipeApiClient(){
        mRecipes = new MutableLiveData<>();
    }

    public static RecipeApiClient getInstance() {
        if (mRecipeApiClient == null) {
            mRecipeApiClient = new RecipeApiClient();
        }
        return mRecipeApiClient;
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipes;
    }

    public void searchRecipeApi() {
        final Future handler = AppExecutors.getInstance().getNetworkIO().submit(() -> {
            //todo retrieve data from rest api
        });

        AppExecutors.getInstance().getNetworkIO().schedule(() -> {
            handler.cancel(true);
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

}
