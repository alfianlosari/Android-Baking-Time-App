package com.alfianlosari.baking.ui;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.alfianlosari.baking.R;

public class RecipeStepDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Long stepId = intent.getLongExtra(getResources().getString(R.string.intent_step_id_key), 0);
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
            fragment.setStepId(stepId);
            fragmentManager.beginTransaction()
                    .add(R.id.step_container, fragment)
                    .commit();

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Long stepId = intent.getLongExtra(getResources().getString(R.string.intent_step_id_key), 0);
        FragmentManager fragmentManager = getSupportFragmentManager();
        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
        fragment.setStepId(stepId);
        fragmentManager.beginTransaction()
                .replace(R.id.step_container, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
