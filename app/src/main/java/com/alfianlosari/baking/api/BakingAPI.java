package com.alfianlosari.baking.api;

import com.alfianlosari.baking.data.Recipe;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by alfianlosari on 31/12/17.
 */

public interface BakingAPI {
    String ENDPOINT = "https://d17h27t6h515a5.cloudfront.net";

    @GET("topher/2017/May/59121517_baking/baking.json")
    Call<Recipe[]> listRecipes();
}
