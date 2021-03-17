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
    }

    private void setParentVisibility(boolean visibility) {
        if (visibility == true) {
            mParentView.setVisibility(View.VISIBLE);
        } else {
            mParentView.setVisibility(View.INVISIBLE);
        }
    }

    private void subscribeObserver(String recipeId) {
        mRecipeDetailsViewModel.searchRecipe(recipeId).observe(this, recipeResource -> {
            if (recipeResource != null) {
                if (recipeResource.data != null) {
                    switch(recipeResource.status) {
                        case LOADING: {
                            setProgressbarVisibility(true);
                            break;
                        }

                        case ERROR: {
                            Log.e(TAG, "subscribeObserver: status: ERROR, recipes: " + recipeResource.data.getTitle());
                            Log.e(TAG, "subscribeObserver: error message: " + recipeResource.message);
                            setParentVisibility(true);
                            setProgressbarVisibility(false);
                            setInfoToLayout(recipeResource.data);
                            break;
                        }

                        case SUCCESS: {
                            setParentVisibility(true);
                            setProgressbarVisibility(false);
                            setInfoToLayout(recipeResource.data);
                            break;
                        }
                    }
                }
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
        if (ingredients == null) {
            ((TextView)findViewById(R.id.recipe_ingredients)).setText("Error retrieving data! Check network connection.");
            return;
        }
        String ingredient = "";
        for (int i = 0; i < ingredients.length; i++) {
            ingredient += ingredients[i] + "\n";
        }
        ((TextView)findViewById(R.id.recipe_ingredients)).setText(ingredient);
    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra("recipe")) {
            Recipe recipe = getIntent().getParcelableExtra("recipe");
            subscribeObserver(recipe.getRecipe_id());
        }
    }


}
