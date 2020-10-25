package com.example.foodrecipes.util;

import android.util.Log;

import com.example.foodrecipes.models.Recipe;

import java.util.List;

public class Testing {

    public static void printRecipes(List<Recipe> recipes, String tag) {
        recipes.forEach(recipe -> {
            Log.d(tag, "printRecipes: title: " + recipe.getTitle());
        });
    }
}
