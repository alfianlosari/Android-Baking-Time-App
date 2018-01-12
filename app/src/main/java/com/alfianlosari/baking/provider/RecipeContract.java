package com.alfianlosari.baking.provider;

import android.support.annotation.NonNull;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by alfianlosari on 30/12/17.
 */

public class RecipeContract {

    public static long INVALID_RECIPE_ID = -1;

    @DataType(DataType.Type.INTEGER)
    @PrimaryKey(onConflict = ConflictResolutionType.REPLACE)
    public static final String COLUMN_ID = "_id";

    @DataType(DataType.Type.TEXT)
    @NonNull
    public static final String COLUMN_NAME = "name";

    @DataType(DataType.Type.INTEGER)
    @NonNull
    public static final String COLUMN_SERVINGS = "servings";

    @DataType(DataType.Type.TEXT)
    @NonNull
    public static final String COLUMN_IMAGE = "image";

}
