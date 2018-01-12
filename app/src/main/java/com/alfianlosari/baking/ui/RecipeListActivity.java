package com.alfianlosari.baking.ui;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.alfianlosari.baking.R;
import com.alfianlosari.baking.adapter.RecipeListCursorAdapter;
import com.alfianlosari.baking.api.BakingAPI;
import com.alfianlosari.baking.data.Ingredient;
import com.alfianlosari.baking.data.Recipe;
import com.alfianlosari.baking.data.Step;
import com.alfianlosari.baking.provider.BakingProvider;
import com.alfianlosari.baking.provider.RecipeContract;
import com.alfianlosari.baking.resources.SimpleIdlingResource;
import com.alfianlosari.baking.service.RecipeListStepService;


import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeListActivity extends AppCompatActivity implements RecipeListCursorAdapter.RecipeClickItemListener{

    private static final int FETCH_RECIPES_ID = 101;
    private static final int CURSOR_RECIPES_ID = 102;

    private ProgressBar mProgressBar;
    private RecipeListCursorAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private BakingAPI mBakingAPI;
    public static final String[] MAIN_RECIPES_PROJECTION = {
            RecipeContract.COLUMN_ID,
            RecipeContract.COLUMN_NAME
    };

    public static final int INDEX_RECIPE_ID = 0;
    public static final int INDEX_RECIPE_NAME = 1;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    private final LoaderManager.LoaderCallbacks<Recipe[]> mAsyncTaskLoaderCallback = new LoaderManager.LoaderCallbacks<Recipe[]>() {
        @Override
        public Loader<Recipe[]> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Recipe[]>(RecipeListActivity.this) {
                Recipe[] mRecipes;
                @Override
                protected void onStartLoading() {
                    if (mIdlingResource != null) {
                        mIdlingResource.setIdleState(false);
                    }

                    if (mRecipes != null) {
                        deliverResult(mRecipes);
                    } else {
                        mProgressBar.setVisibility(View.VISIBLE);
                        forceLoad();
                    }
                }

                @Override
                public Recipe[] loadInBackground() {
                    try {
                        Recipe[] recipes = mBakingAPI.listRecipes().execute().body();
                        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

                        operations.add(ContentProviderOperation.newDelete(BakingProvider.BakingRecipes.CONTENT_URI)
                                .withSelection(null, null)
                                .build());

                        operations.add(ContentProviderOperation.newDelete(BakingProvider.BakingIngredients.CONTENT_URI)
                                .withSelection(null, null)
                                .build());

                        operations.add(ContentProviderOperation.newDelete(BakingProvider.BakingSteps.CONTENT_URI)
                                .withSelection(null, null)
                                .build());


                        for (Recipe recipe: recipes) {
                            ContentValues recipeContentValues = recipe.contentValues();
                            operations.add(ContentProviderOperation.newInsert(BakingProvider.BakingRecipes.CONTENT_URI)
                                .withValues(recipeContentValues)
                                .build());


                            for (Ingredient ingredient: recipe.ingredients) {
                                ContentValues ingredientContentValues = ingredient.contentValuesWithRecipeId(recipe.id);
                                operations.add(ContentProviderOperation.newInsert(BakingProvider.BakingIngredients.CONTENT_URI)
                                        .withValues(ingredientContentValues)
                                        .build());
                            }

                            for (Step step: recipe.steps) {
                                ContentValues stepContentValues = step.contentValuesWithRecipeId(recipe.id);
                                operations.add(ContentProviderOperation.newInsert(BakingProvider.BakingSteps.CONTENT_URI)
                                        .withValues(stepContentValues)
                                        .build());                        }
                        }
                        getContentResolver().applyBatch(BakingProvider.AUTHORITY, operations);

                        return recipes;

                    } catch(IOException e) {
                        e.printStackTrace();
                        return null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }

                }

                @Override
                public void deliverResult(Recipe[] data) {
                    mRecipes = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<Recipe[]> loader, Recipe[] data) {
            mProgressBar.setVisibility(View.INVISIBLE);
            RecipeListStepService.startActionUpdateRecipeWidgets(RecipeListActivity.this);
            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(true);
            }

        }

        @Override
        public void onLoaderReset(Loader<Recipe[]> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<Cursor> mCursorLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(RecipeListActivity.this,
                    BakingProvider.BakingRecipes.CONTENT_URI,
                    MAIN_RECIPES_PROJECTION,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapCursor(null);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.pb_loading);
        mRecyclerView = findViewById(R.id.rv_recipes);
        String recyclerViewTag = (String) mRecyclerView.getTag();
        if (recyclerViewTag.equals("linear")) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
        } else {
            GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
            mRecyclerView.setLayoutManager(layoutManager);
        }


        mAdapter = new RecipeListCursorAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BakingAPI.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mBakingAPI = retrofit.create(BakingAPI.class);

        getSupportLoaderManager().restartLoader(FETCH_RECIPES_ID, null, this.mAsyncTaskLoaderCallback);
        getSupportLoaderManager().restartLoader(CURSOR_RECIPES_ID, null, this.mCursorLoaderCallback);
    }

    @Override
    public void onClick(long id) {
        SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        prefEditor.putLong(RecipeDetailActivity.RECIPE_ID, id);
        prefEditor.apply();

        RecipeListStepService.startActionUpdateRecipeWidgets(this);

        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(getResources().getString(R.string.intent_recipe_id_key), id);
        startActivity(intent);
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }
}
