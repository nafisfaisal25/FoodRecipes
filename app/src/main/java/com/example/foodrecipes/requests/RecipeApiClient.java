package com.example.foodrecipes.requests;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.foodrecipes.AppExecutors;
import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.requests.responses.RecipeResponse;
import com.example.foodrecipes.requests.responses.RecipeSearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

import static com.example.foodrecipes.util.Constants.API_KEY;
import static com.example.foodrecipes.util.Constants.BASE_URL;
import static com.example.foodrecipes.util.Constants.NETWORK_TIMEOUT;

public class RecipeApiClient {
    private static final String TAG = "RecipeApiClient";
    private static RecipeApiClient mRecipeApiClient;
    MutableLiveData<List<Recipe>> mRecipes;
    private RetrieveRecipesRunnable mRetrieveRecipesRunnable;
    private MutableLiveData<Recipe> mRecipe;
    private RetrieveRecipeRunnable mRetrieveRecipeRunnable;

    private RecipeApiClient(){
        mRecipes = new MutableLiveData<>();
        mRecipe = new MutableLiveData<>();
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

    public LiveData<Recipe> getRecipe() {
        return mRecipe;
    }

    public void searchRecipesApi(String query, int pageNumber) {
        if (mRetrieveRecipesRunnable != null) {
            mRetrieveRecipesRunnable = null;
        }
        mRetrieveRecipesRunnable = new RetrieveRecipesRunnable(query, pageNumber);
        final Future handler = AppExecutors.getInstance().getNetworkIO().submit(mRetrieveRecipesRunnable);

        AppExecutors.getInstance().getNetworkIO().schedule(() -> {
            handler.cancel(true);
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void getRecipeApi(String recipeId) {
        if (mRetrieveRecipeRunnable != null) {
            mRetrieveRecipeRunnable = null;
        }
        mRetrieveRecipeRunnable = new RetrieveRecipeRunnable(recipeId);
        final Future handler = AppExecutors.getInstance().getNetworkIO().submit(mRetrieveRecipeRunnable);

        AppExecutors.getInstance().getNetworkIO().schedule(() -> {
            handler.cancel(true);
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private class RetrieveRecipesRunnable implements Runnable {
        String query;
        int pageNumber;
        boolean cancelRequest;

        public RetrieveRecipesRunnable(String query, int pageNumber) {
            this.query = query;
            this.pageNumber = pageNumber;
            this.cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipes(query, pageNumber).execute();
                if (cancelRequest == true) {
                    return;
                }

                if (response.code() == 200) {
                    List<Recipe> recipes = new ArrayList<>(((RecipeSearchResponse) response.body()).getRecipes());
                    if (pageNumber == 1) {
                        mRecipes.postValue(recipes);
                    } else {
                        List<Recipe> currentRecipes = mRecipes.getValue();
                        currentRecipes.addAll(recipes);
                        mRecipes.postValue(currentRecipes);
                    }
                } else {
                    Log.d(TAG, "run: Error " + response.errorBody().toString());
                    mRecipes.postValue(null);
                }

            } catch (IOException e) {
                Log.d(TAG, "exception: Error " + e.getMessage().toString());
                mRecipes.postValue(null);
            }
        }

        private Call<RecipeSearchResponse> getRecipes(String query, int page) {
            return ServiceGenerator.getRecipeApi().searchRecipe(API_KEY, query, String.valueOf(page));
        }

        private void cancelRequest() {
            cancelRequest = true;
        }

    }

    private class RetrieveRecipeRunnable implements Runnable {
        String recipeId;
        boolean cancelRequest;

        public RetrieveRecipeRunnable(String recipeId) {
            this.recipeId = recipeId;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipe(recipeId).execute();
                if (cancelRequest == true) {
                    return;
                }

                if (response.code() == 200) {
                    Recipe recipe = ((RecipeResponse) response.body()).getRecipe();
                    mRecipe.postValue(recipe);
                } else {
                    Log.d(TAG, "run: Error " + response.errorBody().toString());
                    mRecipe.postValue(null);
                }

            } catch (IOException e) {
                Log.d(TAG, "exception: Error " + e.getMessage().toString());
                mRecipe.postValue(null);
            }
        }

        private Call<RecipeResponse> getRecipe(String recipeId) {
            return ServiceGenerator.getRecipeApi().getRecipe(API_KEY, recipeId);
        }

        private void cancelRequest() {
            cancelRequest = true;
        }
    }

    private void cancelRequest() {
        if (mRetrieveRecipeRunnable != null) {
            mRetrieveRecipeRunnable.cancelRequest();
        }

        if (mRetrieveRecipesRunnable != null) {
            mRetrieveRecipesRunnable.cancelRequest();
        }
    }
}


