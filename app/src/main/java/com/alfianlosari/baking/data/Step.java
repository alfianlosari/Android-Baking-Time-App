package com.alfianlosari.baking.data;

import android.content.ContentValues;

import com.alfianlosari.baking.provider.IngredientContract;
import com.alfianlosari.baking.provider.StepContract;

/**
 * Created by alfianlosari on 29/12/17.
 */

public final class Step {

    public int id;
    public String shortDescription;
    public String description;
    public String videoURL;
    public String thumbnailURL;

    public ContentValues contentValuesWithRecipeId(int recipeId) {
        ContentValues cv = new ContentValues();
        cv.put(StepContract.COLUMN_RECIPE_ID, recipeId);
        cv.put(StepContract.COLUMN_STEP_ORDER, id);
        cv.put(StepContract.COLUMN_DESCRIPTION, description);
        cv.put(StepContract.COLUMN_SHORT_DESCRIPTION, shortDescription);
        cv.put(StepContract.COLUMN_VIDEO_URL, videoURL);
        cv.put(StepContract.COLUMN_THUMBNAIL_URL, thumbnailURL);
        return cv;
    }

}
