package com.alfianlosari.baking.service;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.alfianlosari.baking.R;
import com.alfianlosari.baking.ui.RecipeDetailActivity;
import com.alfianlosari.baking.widget.RecipeIngredientInfoWidget;
import com.alfianlosari.baking.provider.BakingProvider;
import com.alfianlosari.baking.provider.RecipeContract;


public class RecipeListIngredientService extends IntentService {

    public static final String ACTION_UPDATE_RECIPE_SELECTED = "com.alfianlosari.baking.action.recipe_selected";

    public RecipeListIngredientService() {
        super("RecipeListIngredientService");
    }

    public static void startActionUpdateRecipeWidgets(Context context) {
        Intent intent = new Intent(context, RecipeListIngredientService.class);
        intent.setAction(ACTION_UPDATE_RECIPE_SELECTED);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_RECIPE_SELECTED.equals(action)) {
                handleUpdateRecipeWidget();
            }
        }
    }

    private void handleUpdateRecipeWidget() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        long recipeId = pref.getLong(RecipeDetailActivity.RECIPE_ID, RecipeContract.INVALID_RECIPE_ID);

        String recipeName = null;

        if (recipeId != RecipeContract.INVALID_RECIPE_ID) {
            Cursor cursor = getContentResolver().query(BakingProvider.BakingRecipes.withId(recipeId),
                    null,
                    null,
                    null,
                    null);

            if (cursor.getCount() > 0 && cursor.moveToNext()) {
                recipeName = cursor.getString(cursor.getColumnIndex(RecipeContract.COLUMN_NAME));
            }
            cursor.close();
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeIngredientInfoWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
        RecipeIngredientInfoWidget.updateRecipeWidgets(this, appWidgetManager, recipeName, appWidgetIds);

    }
}
