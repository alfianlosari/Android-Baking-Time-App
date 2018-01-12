package com.alfianlosari.baking.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RemoteViews;

import com.alfianlosari.baking.R;
import com.alfianlosari.baking.service.RecipeListStepService;
import com.alfianlosari.baking.service.RecipeListWidgetService;
import com.alfianlosari.baking.ui.RecipeDetailActivity;
import com.alfianlosari.baking.ui.RecipeListActivity;
import com.alfianlosari.baking.ui.RecipeStepDetailActivity;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeStepInfoWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, String recipeName,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_step_info_widget);


        Intent intent = new Intent(context, RecipeListWidgetService.class);

        if (recipeName == null) {
            views.setViewVisibility(R.id.widget_recipe_name_text_view, View.GONE);
            Intent mainActivityIntent = new Intent(context, RecipeListActivity.class);
            PendingIntent emptyViewPendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_text_view, emptyViewPendingIntent);
        } else {
            views.setViewVisibility(R.id.widget_recipe_name_text_view, View.VISIBLE);
            views.setTextViewText(R.id.widget_recipe_name_text_view, recipeName);
        }

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        Intent appIntent;
        if (dpWidth >= 600) {
            appIntent = new Intent(context, RecipeDetailActivity.class);
        } else {
            appIntent = new Intent(context, RecipeStepDetailActivity.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_list_view, pendingIntent);


        views.setRemoteAdapter(R.id.widget_list_view, intent);
        views.setEmptyView(R.id.widget_list_view, R.id.widget_text_view);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateRecipeWidgets(Context context, AppWidgetManager appWidgetManager, String recipeName, int[] appWidgetIds) {
        for (int appWidgetId: appWidgetIds) {
            updateAppWidget(context, appWidgetManager, recipeName, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        RecipeListStepService.startActionUpdateRecipeWidgets(context);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        RecipeListStepService.startActionUpdateRecipeWidgets(context);
    }

    @Override
    public void onEnabled(Context context) {}

    @Override
    public void onDisabled(Context context) {}
}