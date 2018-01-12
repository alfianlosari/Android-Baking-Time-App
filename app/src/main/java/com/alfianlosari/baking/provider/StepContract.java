package com.alfianlosari.baking.provider;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

/**
 * Created by alfianlosari on 30/12/17.
 */


public class StepContract {

    @DataType(DataType.Type.INTEGER)
    @PrimaryKey(onConflict = ConflictResolutionType.REPLACE)
    public static final String COLUMN_ID = "_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_SHORT_DESCRIPTION = "short_description";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_DESCRIPTION = "description";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_VIDEO_URL = "video_url";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    public static final String COLUMN_STEP_ORDER = "step_order";

    @DataType(DataType.Type.INTEGER)
    @References(
            table = BakingDatabase.RECIPES,
            column = RecipeContract.COLUMN_ID
    )
    public static final String COLUMN_RECIPE_ID = "recipe_id";

}
