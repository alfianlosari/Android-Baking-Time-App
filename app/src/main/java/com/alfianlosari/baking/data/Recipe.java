package com.alfianlosari.baking.data;

import android.content.ContentValues;

import com.alfianlosari.baking.provider.RecipeContract;

/**
 * Created by alfianlosari on 29/12/17.
 */

public final class Recipe {

    public int id;
    public String name;
    public int servings;
    public String image;
    public Ingredient[] ingredients;
    public Step[] steps;

    public ContentValues contentValues() {
        ContentValues cv = new ContentValues();
        cv.put(RecipeContract.COLUMN_ID, id);
        cv.put(RecipeContract.COLUMN_IMAGE, image);
        cv.put(RecipeContract.COLUMN_NAME, name);
        cv.put(RecipeContract.COLUMN_SERVINGS, servings);
        return cv;
    }

}
