package com.example.foodrecipes.repositories;

import android.content.Context;

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
import com.example.foodrecipes.requests.responses.RecipeSearchResponse;
import com.example.foodrecipes.util.Constants;
import com.example.foodrecipes.util.NetworkBoundResource;
import com.example.foodrecipes.util.Resource;

import java.util.List;

public class RecipeRepository {
//    private static RecipeRepository mRecipeRepository;
//    private RecipeApiClient mRecipeAliClient;
//    private String mQuery;
//    private int mPageNumber;
//    private MutableLiveData<Boolean> mIsQueryExhausted = new MutableLiveData<>();
//    private MediatorLiveData<List<Recipe>> mRecipes = new MediatorLiveData<>();
//
//
//    private RecipeRepository() {
//        mRecipeAliClient = RecipeApiClient.getInstance();
//        initMediator();
//    }
//
//    public static RecipeRepository getInstance() {
//        if (mRecipeRepository == null) {
//            mRecipeRepository = new RecipeRepository();
//        }
//        return mRecipeRepository;
//    }
//
//    private void initMediator() {
//        LiveData<List<Recipe>> recipeListApiSource = mRecipeAliClient.getRecipes();
//        mRecipes.addSource(recipeListApiSource, recipes -> {
//            if (recipes != null) {
//                mRecipes.setValue(recipes);
//                doneQuery(recipes);
//            } else {
//                // need to search from cache
//                doneQuery(null);
//            }
//        });
//    }
//
//    private void doneQuery(List<Recipe> list) {
//        if (list != null) {
//            if (list.size() % 30 != 0) {
//                mIsQueryExhausted.setValue(true);
//            }
//        } else {
//            mIsQueryExhausted.setValue(true);
//        }
//    }
//
//    public LiveData<Boolean> isQueryExhausted() {
//        return mIsQueryExhausted;
//    }
//
//    public LiveData<List<Recipe>> getRecipes() {
//        return mRecipes;
//    }
//
//    public LiveData<Recipe> getRecipe() {
//        return mRecipeAliClient.getRecipe();
//    }
//
//    public LiveData<Boolean> getRecipeRequestTimeout() {
//        return mRecipeAliClient.getRecipeRequestTimeout();
//    }
//
//    public void searchRecipesAPi(String query, int pageNumber) {
//        if (pageNumber == 0) {
//            pageNumber = 1;
//        }
//        mIsQueryExhausted.setValue(false);
//        mPageNumber = pageNumber;
//        mQuery = query;
//        mRecipeAliClient.searchRecipesApi(query, pageNumber);
//    }
//
//    public void getRecipesAPi(String recipeId) {
//        mRecipeAliClient.getRecipeApi(recipeId);
//    }
//
//    public void searchNextPage() {
//        searchRecipesAPi(mQuery, mPageNumber + 1);
//    }
//
//    public void setRecipe(Recipe recipe) {
//        mRecipeAliClient.setRecipe(recipe);
//    }


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
}
