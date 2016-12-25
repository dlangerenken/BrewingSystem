/*
 * 
 */
package se.brewingsystem.android.network;


import java.util.List;
import java.util.Map;

import general.BrewingProcess;
import general.BrewingState;
import general.Protocol;
import general.LogSummary;
import general.Recipe;
import general.RecipeSummary;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;


/**
 * This interface provides every interaction with the server (e.g. push, recipe, protocol, brewing status)
 * Created by Daniel on 29.11.2014.
 */
public interface INetworkCommunication {

    @GET("/alive")
    void isServerAlive(Callback<Boolean> callback);

    @POST("/push/subscribe")
    void subscribePush(@Query("regId") String regId, Callback<String> callback);

    @POST("/push/unsubscribe")
    void unsubscribePush(@Query("regId") String regId, Callback<String> callback);

    @POST("/recipes/create")
    void createRecipe(@QueryMap Map<String, String> query, Callback<String> callback);

    @GET("/recipes/{id}/")
    void getRecipe(@Path("id") String recipeId, Callback<Recipe> callback);

    @GET("/recipes/")
    void getRecipes(Callback<List<RecipeSummary>> callback);

    @GET("/protocols/{id}/")
    void getProtocol(@Path("id") int protocolId, Callback<Protocol> callback);

    @GET("/protocols/")
    void getProtocols(Callback<List<LogSummary>> callback);

    @GET("/brewing/")
    void getCurrentBrewingStatus(Callback<BrewingProcess> callback);

    @POST("/brewing/iodine")
    void confirmIodineTest(@Query("duration") Integer duration, Callback<BrewingState> callback);

    @POST("/brewing/start")
    void startBrewing(@Query("recipeId") String recipeId, Callback<BrewingState> callback);

    @POST("/brewing/confirm")
    void confirmStep(@QueryMap Map<String, String> query, Callback<BrewingState> callback);
}
