package com.example.foodrecipes;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipes.adapters.OnRecipeListener;
import com.example.foodrecipes.adapters.RecipeRecyclerAdapter;
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
        if (!mRecipeListViewModel.getIsViewingRecipes()) {
            displaySearchCategories();
        }

        setSupportActionBar(findViewById(R.id.toolbar));
    }

    private void displaySearchCategories() {
        mRecipeListViewModel.setIsViewingRecipes(false);
        mRecipeRecyclerAdapter.displaySearchCategories();
    }

    private void initRecyclerView() {
        mRecipeRecyclerAdapter = new RecipeRecyclerAdapter(this);
        VerticalSpacingItemDecoration verticalSpacingItemDecoration = new VerticalSpacingItemDecoration(30);
        mRecyclerView.addItemDecoration(verticalSpacingItemDecoration);
        mRecyclerView.setAdapter(mRecipeRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void subscribeObservers() {
        mRecipeListViewModel.getRecipes().observe(this, recipes -> {
            if (recipes != null) {
                Testing.printRecipes(recipes, "recipes test");
                if (mRecipeListViewModel.getIsViewingRecipes()) {
                    mRecipeRecyclerAdapter.setRecipes(recipes);
                }
            }
        });
    }

    private void initSearchView() {
        final SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (mRecipeListViewModel.getIsViewingRecipes()) {
                    searchRecipes(query);
                } else {
                    mRecipeRecyclerAdapter.filterCategory(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mRecipeListViewModel.getIsViewingRecipes()) {
                    searchRecipes(newText);
                } else {
                    mRecipeRecyclerAdapter.filterCategory(newText);
                }
                return false;
            }
        });
    }

    private void searchRecipes(String query) {
        mRecipeRecyclerAdapter.displayLoading();
        mRecipeListViewModel.searchRecipesAPi(query, 1);
    }

    @Override
    public void onItemSelected(int position) {

    }

    @Override
    public void onCategorySelected(String category) {
        searchRecipes(category);
    }

    @Override
    public void onBackPressed() {
        if (mRecipeListViewModel.getIsViewingRecipes()) {
            displaySearchCategories();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_categories) {
            displaySearchCategories();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
