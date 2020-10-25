package com.example.foodrecipes;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipes.adapters.OnRecipeListener;
import com.example.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.example.foodrecipes.util.Testing;
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
    }

    private void initRecyclerView() {
        mRecipeRecyclerAdapter = new RecipeRecyclerAdapter(this);
        mRecyclerView.setAdapter(mRecipeRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void subscribeObservers() {
        mRecipeListViewModel.getRecipes().observe(this, recipes -> {
            if (recipes != null) {
                Testing.printRecipes(recipes, "recipes test");
                mRecipeRecyclerAdapter.setRecipes(recipes);
            }
        });
    }

    private void searchRecipesAPi(String query, int pageNumber) {
        mRecipeListViewModel.searchRecipesAPi(query, pageNumber);
    }

    private void initSearchView() {
        final SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mRecipeRecyclerAdapter.displayLoading();
                mRecipeListViewModel.searchRecipesAPi(query, 1);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mRecipeRecyclerAdapter.displayLoading();
                mRecipeListViewModel.searchRecipesAPi(newText, 1);
                return false;
            }
        });
    }

    @Override
    public void onItemSelected(int position) {

    }

    @Override
    public void onCategorySelected(String category) {

    }
}
