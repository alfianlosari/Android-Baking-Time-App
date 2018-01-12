package com.alfianlosari.baking.data;

import android.content.ContentValues;

import com.alfianlosari.baking.provider.IngredientContract;

/**
 * Created by alfianlosari on 29/12/17.
 */

public final class Ingredient {

    public float quantity;
    public String measure;
    public String ingredient;

    public ContentValues contentValuesWithRecipeId(int recipeId) {
        ContentValues cv = new ContentValues();
        cv.put(IngredientContract.COLUMN_RECIPE_ID, recipeId);
        cv.put(IngredientContract.COLUMN_INGREDIENT, ingredient);
        cv.put(IngredientContract.COLUMN_MEASURE, measure);
        cv.put(IngredientContract.COLUMN_QUANTITY, quantity);
        return cv;
    }


}
