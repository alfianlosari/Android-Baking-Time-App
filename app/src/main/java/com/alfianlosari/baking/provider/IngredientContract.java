package com.alfianlosari.baking.provider;

import android.support.annotation.NonNull;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

/**
 * Created by alfianlosari on 30/12/17.
 */

public class IngredientContract {

    @DataType(DataType.Type.INTEGER)
    @PrimaryKey(onConflict = ConflictResolutionType.REPLACE)
    public static final String COLUMN_ID = "_id";

    @DataType(DataType.Type.TEXT)
    @NonNull
    public static final String COLUMN_MEASURE = "measure";

    @DataType(DataType.Type.TEXT)
    @NonNull
    public static final String COLUMN_INGREDIENT = "ingredient";

    @DataType(DataType.Type.REAL)
    public static final String COLUMN_QUANTITY = "quantity";

    @DataType(DataType.Type.INTEGER)
    @References(
            table = BakingDatabase.RECIPES,
            column = RecipeContract.COLUMN_ID
    )
    public static final String COLUMN_RECIPE_ID = "recipe_id";

}
