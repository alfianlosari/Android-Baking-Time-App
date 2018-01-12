package com.alfianlosari.baking.provider;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by alfianlosari on 30/12/17.
 */

@Database(version = BakingDatabase.VERSION)
public class BakingDatabase {

    public static final int VERSION = 1;

    @Table(RecipeContract.class)
    public static final String RECIPES = "recipes";

    @Table(IngredientContract.class)
    public static final String INGREDIENTS = "ingredients";

    @Table(StepContract.class)
    public static final String STEPS = "steps";

}
