package com.alfianlosari.baking.service;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by alfianlosari on 07/01/18.
 */

public class RecipeListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RecipeListRemoteViewsFactory(getApplicationContext());
    }
}
