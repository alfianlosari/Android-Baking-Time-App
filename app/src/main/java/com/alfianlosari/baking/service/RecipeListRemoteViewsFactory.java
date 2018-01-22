package com.alfianlosari.baking.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.alfianlosari.baking.R;
import com.alfianlosari.baking.provider.IngredientContract;
import com.alfianlosari.baking.ui.RecipeDetailActivity;
import com.alfianlosari.baking.provider.BakingProvider;
import com.alfianlosari.baking.provider.RecipeContract;
import com.alfianlosari.baking.provider.StepContract;

/**
 * Created by alfianlosari on 07/01/18.
 */

public class RecipeListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    Cursor mCursor;
    long mRecipeId;

    public RecipeListRemoteViewsFactory(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate() {}

    @Override
    public void onDataSetChanged() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        long recipeId = pref.getLong(RecipeDetailActivity.RECIPE_ID, RecipeContract.INVALID_RECIPE_ID);
        if (recipeId == RecipeContract.INVALID_RECIPE_ID) return;
        mRecipeId = recipeId;
        Uri RECIPE_URI = BakingProvider.BakingIngredients.withRecipeId(recipeId);
        if (mCursor != null) mCursor.close();
        mCursor = mContext.getContentResolver().query(
                RECIPE_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mCursor == null || mCursor.getCount() == 0) return 0;
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        if (mCursor == null || mCursor.getCount() == 0) return null;
        mCursor.moveToPosition(i);


        RemoteViews views =  new RemoteViews(mContext.getPackageName(), R.layout.recipe_ingredient_widget_item);
        String ingredient = mCursor.getString(mCursor.getColumnIndex(IngredientContract.COLUMN_INGREDIENT));

        String text = ""
                + formatNumber(mCursor.getFloat(mCursor.getColumnIndex(IngredientContract.COLUMN_QUANTITY))) + " "
                + mCursor.getString(mCursor.getColumnIndex(IngredientContract.COLUMN_MEASURE)) + " "
                + mCursor.getString(mCursor.getColumnIndex(IngredientContract.COLUMN_INGREDIENT));


        views.setTextViewText(R.id.textview_widget_step, text);

        Bundle extras = new Bundle();
        extras.putLong(mContext.getResources().getString(R.string.intent_recipe_id_key), mRecipeId);
        Intent fillIntent = new Intent();
        fillIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.textview_widget_step, fillIntent);
        return views;
    }

    public String formatNumber(float d) {
        if (d == (long) d) {
            return String.format("%d", (long) d);
        } else {
            return String.format("%.1f", d);
        }
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
