package com.example.foodrecipes.viewModels;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.repositories.RecipeRepository;
import com.example.foodrecipes.util.Resource;

import java.util.List;

public class RecipeListViewModel extends AndroidViewModel {
    private static final String TAG = "RecipeListViewModel";

    public enum ViewState{CATEGORIES, RECIPES}

    private RecipeRepository mRecipeRepository;
    private MutableLiveData<ViewState> mViewStateLiveData;
    private MediatorLiveData<Resource<List<Recipe>>> mRecipes = new MediatorLiveData<>();


    private boolean isQueryExhausted;
    private boolean isPerformingQuery;
    private int pageNumber;
    private String query;
    private boolean cancelRequest;

    private long requestStartTime;



    public RecipeListViewModel(Application application) {
        super(application);
        mRecipeRepository = RecipeRepository.getInstance(application);
        initViewState();
    }

    private void initViewState() {
        if (mViewStateLiveData == null) {
            mViewStateLiveData = new MutableLiveData<>();
            setViewState(ViewState.CATEGORIES);
        }
    }

    public LiveData<ViewState> getViewStateLiveData() {
        return mViewStateLiveData;
    }

    public void setViewState(ViewState state) {
        mViewStateLiveData.setValue(state);
    }

    public LiveData<Resource<List<Recipe>>> getRecipes() {
        return mRecipes;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void searchRecipesApi(String query, int pageNumber) {
        if (!isPerformingQuery) {
            if (pageNumber == 0) {
                pageNumber = 1;
            }
            this.pageNumber = pageNumber;
            this.query = query;
            isQueryExhausted = false;
            executeQuery();
        }
    }

    public void searchNextPage() {
        if (!isQueryExhausted) {
            searchRecipesApi(query, pageNumber+1);
        }
    }

    private void executeQuery() {
        requestStartTime = System.currentTimeMillis();
        cancelRequest = false;
        isPerformingQuery = true;
        setViewState(ViewState.RECIPES);
        LiveData<Resource<List<Recipe>>> repositorySource = mRecipeRepository.searchRecipesApi(query, pageNumber);
        mRecipes.addSource(repositorySource, listResource -> {
            if (!cancelRequest) {
                if (listResource != null) {
                    mRecipes.setValue(listResource);
                    isPerformingQuery = false;
                    if (listResource.status == Resource.Status.SUCCESS) {
                        Log.d(TAG, "executeQuery: Request time: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds");
                        if (listResource.data != null && listResource.data.size() == 0) {
                            isQueryExhausted = true;
                            mRecipes.setValue(
                                    new Resource<>(
                                            Resource.Status.ERROR,
                                            listResource.data,
                                            "No more results.")
                            );
                        } else {
                            mRecipes.removeSource(repositorySource);
                        }
                    } else if(listResource.status == Resource.Status.ERROR) {
                        Log.d(TAG, "executeQuery: Request time: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds");
                        mRecipes.removeSource(repositorySource);
                    }
                } else {
                    mRecipes.removeSource(repositorySource);
                }
            } else {
                mRecipes.removeSource(repositorySource);
            }

        });
    }

    public void cancelRequest() {
        cancelRequest = true;
    }

}
