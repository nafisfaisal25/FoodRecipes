package com.example.foodrecipes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.foodrecipes.adapters.OnRecipeListener;
import com.example.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.util.Testing;
import com.example.foodrecipes.util.VerticalSpacingItemDecoration;
import com.example.foodrecipes.viewModels.RecipeListViewModel;

public class RecipeListActivity extends BaseActivity implements OnRecipeListener {
    private static final String TAG = "RecipeListActivity";
    private RecipeListViewModel mRecipeListViewModel;
    private RecyclerView mRecyclerView;
    private RecipeRecyclerAdapter mRecipeRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mRecyclerView = findViewById(R.id.recycler_view);
        initRecyclerView();
        mRecipeListViewModel = new ViewModelProvider(this).get(RecipeListViewModel.class);
        subscribeObservers();
        initSearchView();
        setSupportActionBar(findViewById(R.id.toolbar));
    }

    private void displaySearchCategories() {
        mRecipeRecyclerAdapter.displaySearchCategories();
    }

    RequestManager initGuide() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    private void initRecyclerView() {
        mRecipeRecyclerAdapter = new RecipeRecyclerAdapter(this, initGuide());
        VerticalSpacingItemDecoration verticalSpacingItemDecoration = new VerticalSpacingItemDecoration(30);
        mRecyclerView.addItemDecoration(verticalSpacingItemDecoration);
        mRecyclerView.setAdapter(mRecipeRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!mRecyclerView.canScrollVertically(1) && mRecipeListViewModel.getViewStateLiveData().getValue() == RecipeListViewModel.ViewState.RECIPES) {
                    mRecipeListViewModel.searchNextPage();
                }
            }
        });
    }

    private void subscribeObservers() {

        mRecipeListViewModel.getRecipes().observe(this, listResource -> {
            if (listResource != null) {
                Log.d(TAG, "subscribeObservers: getRecipes: status: " + listResource.status);
                if (listResource.data != null) {
                    switch (listResource.status) {
                        case LOADING: {
                            if (mRecipeListViewModel.getPageNumber() > 1) {
                                mRecipeRecyclerAdapter.displayLoading();
                            } else {
                                mRecipeRecyclerAdapter.disPlayOnlyLoading();
                            }
                            break;
                        }

                        case ERROR: {
                            Log.e(TAG, "subscribeObservers: Error message" +  listResource.message);
                            Log.e(TAG, "subscribeObservers: status: ERROR, #recipes: " + listResource.data.size());
                            mRecipeRecyclerAdapter.hideLoading();
                            mRecipeRecyclerAdapter.setRecipes(listResource.data);
                            Toast.makeText(this, listResource.message, Toast.LENGTH_SHORT).show();

                            if (listResource.message.equals("No more results.")) {
                                mRecipeRecyclerAdapter.disPlayExhausted();
                            }
                            break;
                        }

                        case SUCCESS: {
                            Log.d(TAG, "subscribeObservers: status: SUCCESS, #recipes: " + listResource.data.size());
                            mRecipeRecyclerAdapter.hideLoading();
                            mRecipeRecyclerAdapter.setRecipes(listResource.data);
                            break;
                        }
                    }
                }
            }
        });

        mRecipeListViewModel.getViewStateLiveData().observe(this, viewState -> {
            if (viewState != null) {
                switch (viewState) {
                    case RECIPES: {
                        break;
                    }

                    case CATEGORIES: {
                        displaySearchCategories();
                        break;
                    }
                }
            }
        });
    }

    private void initSearchView() {
        final SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchRecipes(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void searchRecipes(String query) {
        mRecipeListViewModel.searchRecipesApi(query, 1);
        findViewById(R.id.search_view).clearFocus();
    }

    @Override
    public void onItemSelected(int position) {
        Intent intent = new Intent(this, RecipeDetailsActivity.class);
        intent.putExtra("recipe", mRecipeRecyclerAdapter.getSelectedRecipe(position));
        startActivity(intent);
    }

    @Override
    public void onCategorySelected(String category) {
        searchRecipes(category);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_categories) {
            mRecipeListViewModel.setViewState(RecipeListViewModel.ViewState.CATEGORIES);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (mRecipeListViewModel.getViewStateLiveData().getValue() == RecipeListViewModel.ViewState.CATEGORIES) {
            super.onBackPressed();
        } else {
            mRecipeListViewModel.cancelRequest();
            mRecipeListViewModel.setViewState(RecipeListViewModel.ViewState.CATEGORIES);
        }
    }
}
