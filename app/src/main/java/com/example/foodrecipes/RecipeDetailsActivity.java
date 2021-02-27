package com.example.foodrecipes;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.viewModels.RecipeDetailsViewModel;

public class RecipeDetailsActivity extends BaseActivity {

    private static final String TAG = "RecipeDetailsActivity";
    RecipeDetailsViewModel mRecipeDetailsViewModel;
    CardView mParentView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        mRecipeDetailsViewModel = new ViewModelProvider(this).get(RecipeDetailsViewModel.class);
        mParentView = findViewById(R.id.card_view);
        setParentVisibility(false);
        setProgressbarVisibility(true);
        getIncomingIntent();
        subscribeObserver();
    }

    private void setParentVisibility(boolean visibility) {
        if (visibility == true) {
            mParentView.setVisibility(View.VISIBLE);
        } else {
            mParentView.setVisibility(View.INVISIBLE);
        }
    }

    private void subscribeObserver() {
        mRecipeDetailsViewModel.getRecipe().observe(this, recipe -> {
            if (recipe != null && recipe.getRecipe_id().equals(mRecipeDetailsViewModel.getRequestedRecipeId())) {
                mRecipeDetailsViewModel.setRecipeRetrieved(true);
                setParentVisibility(true);
                setProgressbarVisibility(false);
                setInfoToLayout(recipe);
            }
        });

        mRecipeDetailsViewModel.getRecipeRequestTimeout().observe(this, aBoolean -> {
            if (aBoolean && !mRecipeDetailsViewModel.didRecipeRetrieved()) {
                showErrorScreen("Error retrieving data. Check network connection.");
            }
        });
    }

    private void showErrorScreen(String errorMessage) {
        setParentVisibility(true);
        setProgressbarVisibility(false);
        ((TextView)findViewById(R.id.recipe_title)).setText("Error retrieving recipe");
        ((TextView)findViewById(R.id.recipe_social_score)).setText("");
        setIngredients(new String[]{errorMessage});
        Glide.with(getApplicationContext())
                .load(R.drawable.ic_launcher_background)
                .into((ImageView) findViewById(R.id.recipe_image));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecipeDetailsViewModel.setRecipe(null);
    }
}
