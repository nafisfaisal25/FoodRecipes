package com.example.foodrecipes;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.viewModels.RecipeDetailsViewModel;

public class RecipeDetailsActivity extends BaseActivity {
    RecipeDetailsViewModel mRecipeDetailsViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        mRecipeDetailsViewModel = new ViewModelProvider(this).get(RecipeDetailsViewModel.class);
        getIncomingIntent();
        subscribeObserver();
    }

    private void subscribeObserver() {
        mRecipeDetailsViewModel.getRecipe().observe(this, recipe -> {
            if (recipe != null) {
                setInfoToLayout(recipe);
            }
        });
    }

    private void setInfoToLayout(Recipe recipe) {
        if (recipe != null) {
            ((TextView)findViewById(R.id.recipe_title)).setText(recipe.getTitle());
            ((TextView)findViewById(R.id.recipe_social_score)).setText(String.valueOf(Math.round(recipe.getSocial_rank())));
            setIngredients(recipe.getIngredients());
            setImage(recipe.getImage_url());
        }
    }

    private void setImage(String url) {
        Glide.with(getApplicationContext())
                .load(url)
                .into((ImageView) findViewById(R.id.recipe_image));
    }

    private void setIngredients(String[]ingredients) {
        String ingredient = "";
        for (int i = 0; i < ingredients.length; i++) {
            ingredient += ingredients[i] + "\n";
        }
        ((TextView)findViewById(R.id.recipe_ingredients)).setText(ingredient);
    }

    private Recipe getIncomingIntent() {
        if (getIntent().hasExtra("recipe")) {
            Recipe recipe = getIntent().getParcelableExtra("recipe");
            mRecipeDetailsViewModel.getRecipeApi(recipe.getRecipe_id());
        }
        return null;
    }
}
