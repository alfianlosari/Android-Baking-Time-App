package com.alfianlosari.baking.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.alfianlosari.baking.R;
import com.alfianlosari.baking.adapter.RecipeDetailCursorAdapter;
import com.alfianlosari.baking.provider.BakingProvider;
import com.alfianlosari.baking.provider.IngredientContract;
import com.alfianlosari.baking.provider.RecipeContract;
import com.alfianlosari.baking.provider.StepContract;
import com.alfianlosari.baking.service.RecipeListIngredientService;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailCursorAdapter.RecipeDetailStepClickListener {

    private long mStepId;
    private long mRecipeId;
    public static final String RECIPE_ID = "RECIPE_ID";
    private static final String LAYOUT_MANAGER_POSITION = "LAYOUT_MANAGER_POSITION";

    private static final int CURSOR_INGREDIENTS_ID = 101;
    private static final int CURSOR_STEPS_ID = 102;
    private boolean mTwoPane;
    private Parcelable layoutSavedPosition;

    private RecipeDetailCursorAdapter mAdapter;
    private RecyclerView mRecyclerView;

    public static final String[] INGREDIENTS_PROJECTION = {
            IngredientContract.COLUMN_ID,
            IngredientContract.COLUMN_INGREDIENT,
            IngredientContract.COLUMN_MEASURE,
            IngredientContract.COLUMN_QUANTITY,
            IngredientContract.COLUMN_INGREDIENT_ORDER
    };



    public static final String[] STEPS_PROJECTION = {
            StepContract.COLUMN_ID,
            StepContract.COLUMN_STEP_ORDER,
            StepContract.COLUMN_SHORT_DESCRIPTION
    };

    private LoaderManager.LoaderCallbacks<Cursor> mCursorIngredientLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(RecipeDetailActivity.this,
                    BakingProvider.BakingIngredients.withRecipeId(mRecipeId),
                    INGREDIENTS_PROJECTION,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mAdapter.swapIngredientsCursor(data);
            restoreLayoutManagerPosition();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapIngredientsCursor(null);
        }
    };

    private LoaderManager.LoaderCallbacks<Cursor> mCursorStepsLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(RecipeDetailActivity.this,
                    BakingProvider.BakingSteps.withRecipeId(mRecipeId),
                    STEPS_PROJECTION,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mAdapter.swapStepsCursor(data);
            restoreLayoutManagerPosition();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapStepsCursor(null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        if (findViewById(R.id.linear_layout) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = findViewById(R.id.rv_detail_recipe);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new RecipeDetailCursorAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        Intent intent = getIntent();
        mRecipeId =  intent.getLongExtra(getResources().getString(R.string.intent_recipe_id_key), -1);
        mStepId = intent.getLongExtra(getResources().getString(R.string.intent_step_id_key), -1);


        if (mRecipeId != -1) {
            Cursor cursor = getContentResolver().query(
                    BakingProvider.BakingRecipes.withId(mRecipeId),
                    null,
                    null,
                    null,
                    null);

            if (cursor != null && cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(RecipeContract.COLUMN_NAME));
                getSupportActionBar().setTitle(name);
                cursor.close();
                getSupportLoaderManager().restartLoader(CURSOR_INGREDIENTS_ID, null, this.mCursorIngredientLoaderCallback);
                getSupportLoaderManager().restartLoader(CURSOR_STEPS_ID, null, this.mCursorStepsLoaderCallback);
            }
        }

        if (mTwoPane) {
            if (savedInstanceState == null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();

                if (mStepId != -1) {
                    fragment.setStepId(mStepId);
                }

                fragmentManager.beginTransaction()
                        .add(R.id.recipe_step_container, fragment)
                        .commit();


            }

        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        mRecipeId =  intent.getLongExtra(getResources().getString(R.string.intent_recipe_id_key), -1);
        mStepId = intent.getLongExtra(getResources().getString(R.string.intent_step_id_key), -1);

        if (mRecipeId != -1 && mStepId != -1) {
            setIntent(intent);
            FragmentManager fragmentManager = getSupportFragmentManager();
            RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();

            if (mStepId != -1) {
                fragment.setStepId(mStepId);
            }

            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_step_container, fragment)
                    .commit();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreLayoutManagerPosition();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                prefEditor.putLong(RecipeDetailActivity.RECIPE_ID, RecipeContract.INVALID_RECIPE_ID);
                prefEditor.apply();

                RecipeListIngredientService.startActionUpdateRecipeWidgets(this);
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickRecipe(long id) {
        if (mTwoPane) {

            RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
            fragment.setStepId(id);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_step_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, RecipeStepDetailActivity.class);
            intent.putExtra(getResources().getString(R.string.intent_step_id_key), id);
            startActivity(intent);
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LAYOUT_MANAGER_POSITION, mRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        layoutSavedPosition = savedInstanceState.getParcelable(LAYOUT_MANAGER_POSITION);
        super.onRestoreInstanceState(savedInstanceState);

    }

    private void restoreLayoutManagerPosition() {
        if (layoutSavedPosition != null) {
            mRecyclerView.getLayoutManager().onRestoreInstanceState(layoutSavedPosition);
        }
    }
}
