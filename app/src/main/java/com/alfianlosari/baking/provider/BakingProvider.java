package com.alfianlosari.baking.provider;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by alfianlosari on 30/12/17.
 */

@ContentProvider(
        authority = BakingProvider.AUTHORITY,
        database = BakingDatabase.class
)
public class BakingProvider {

    public static final String AUTHORITY = "com.alfianlosari.baking.provider";

    @TableEndpoint(table = BakingDatabase.RECIPES)
    public static class BakingRecipes {
        @ContentUri(
                path = "recipes",
                type = "vnd.android.cursor.dir/recipes",
                defaultSort = RecipeContract.COLUMN_NAME + " ASC"
        )
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/recipes");

        @InexactContentUri(
                path = "recipes/#",
                name = "RECIPE_ID",
                type = "vnd.android.cursor.item/recipes",
                whereColumn = RecipeContract.COLUMN_ID,
                pathSegment = 1
        )
        public static Uri withId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/recipes/" + id);
        }
    }


    @TableEndpoint(table = BakingDatabase.INGREDIENTS)
    public static class BakingIngredients {
        @ContentUri(
                path = "ingredients",
                type = "vnd.android.cursor.dir/ingredients",
                defaultSort = IngredientContract.COLUMN_ID + " ASC"
        )
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/ingredients");

        @InexactContentUri(
                path = "ingredients/#",
                name = "INGREDIENT_ID",
                type = "vnd.android.cursor.item/ingredients",
                whereColumn = IngredientContract.COLUMN_ID,
                pathSegment = 1
        )
        public static Uri withId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/ingredients/" + id);
        }

        @InexactContentUri(
                path = "recipes/#/ingredients",
                type = "vnd.android.cursor.dir/ingredients",
                name = "INGREDIENTS_RECIPE_ID",
                whereColumn = IngredientContract.COLUMN_RECIPE_ID,
                defaultSort = IngredientContract.COLUMN_INGREDIENT_ORDER + " ASC",
                pathSegment = 1
        )
        public static Uri withRecipeId(long recipeId) {
            return Uri.parse("content://" + AUTHORITY + "/recipes/" + recipeId + "/ingredients");
        }

    }

    @TableEndpoint(table = BakingDatabase.STEPS)
    public static class BakingSteps {
        @ContentUri(
                path = "steps",
                type = "vnd.android.cursor.dir/steps",
                defaultSort = StepContract.COLUMN_STEP_ORDER + " ASC"
        )
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/steps");

        @InexactContentUri(
                path = "steps/#",
                name = "STEP_ID",
                type = "vnd.android.cursor.item/steps",
                whereColumn = StepContract.COLUMN_ID,
                pathSegment = 1
        )
        public static Uri withId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/steps/" + id);
        }

        @InexactContentUri(
                path = "recipes/#/steps",
                type = "vnd.android.cursor.dir/steps",
                name = "STEPS_RECIPE_ID",
                whereColumn = StepContract.COLUMN_RECIPE_ID,
                defaultSort = StepContract.COLUMN_STEP_ORDER + " ASC",
                pathSegment = 1
        )
        public static Uri withRecipeId(long recipeId) {
            return Uri.parse("content://" + AUTHORITY + "/recipes/" + recipeId + "/steps");
        }
    }


}
