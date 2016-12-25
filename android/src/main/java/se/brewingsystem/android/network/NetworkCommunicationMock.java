/*
 * 
 */
package se.brewingsystem.android.network;

import android.os.Handler;


import java.util.List;
import java.util.Map;

import general.BrewingProcess;
import general.BrewingState;
import general.LogSummary;
import general.Protocol;
import general.Recipe;
import general.RecipeSummary;
import retrofit.Callback;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import utilities.DummyBuilder;


/**
 * This class mocks the network-communication and returns plausible results directly after a request is made.
 */
public class NetworkCommunicationMock implements INetworkCommunication {

    /** Time the response should wait until it's executed (to simulate network). */
    private static final int TIME_TO_SLEEP = 1500;

    /**
     * Sends the response to the success-listener.
     *
     * @param <T> Type of object, which is send to the successlistener
     * @param successListener listener which should receive the response
     * @param response response which should be send to the listener
     */
    private <T> void sendDelayed(final Callback<T> successListener, final T response,int timeToSleep){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (successListener != null) {
                    successListener.success(response, null);
                }
            }
        }, timeToSleep);
    }

    /**
     * Sends the response to the success-listener.
     *
     * @param <T> Type of object, which is send to the successlistener
     * @param successListener listener which should receive the response
     * @param response response which should be send to the listener
     */
    private <T> void sendDelayed(final Callback<T> successListener, final T response){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (successListener != null) {
                    successListener.success(response, null);
                }
            }
        }, TIME_TO_SLEEP);
    }

    @Override
    public void isServerAlive(Callback<Boolean> callback) {
        sendDelayed(callback, true);
    }

    @Override
    public void subscribePush(@Query("regId") String regId, Callback<String> callback) {
        sendDelayed(callback, "1234");
    }

    @Override
    public void unsubscribePush(@Query("regId") String regId, Callback<String> callback) {
        sendDelayed(callback, "1234");
    }

    @Override
    public void createRecipe(@QueryMap Map<String, String> queryMap, Callback<String> callback) {
        sendDelayed(callback, "42", 5000);
    }

    @Override
    public void getRecipe(@Path("id") String recipeId, Callback<Recipe> callback) {
        sendDelayed(callback, DummyBuilder.getRecipe());
    }

    @Override
    public void getRecipes(Callback<List<RecipeSummary>> callback) {
        sendDelayed(callback, DummyBuilder.getRecipes());
    }

    @Override
    public void getProtocol(@Path("id") int protocolId, Callback<Protocol> callback) {
        sendDelayed(callback, DummyBuilder.getProtocol());
    }

    @Override
    public void getProtocols(Callback<List<LogSummary>> callback) {
        sendDelayed(callback, DummyBuilder.getProtocols());
    }

    @Override
    public void getCurrentBrewingStatus(Callback<BrewingProcess> callback) {
        sendDelayed(callback, DummyBuilder.getBrewingProcess());
    }

    @Override
    public void confirmIodineTest(@Query("duration") Integer duration, Callback<BrewingState> callback) {
        sendDelayed(callback, DummyBuilder.getBrewingState());
    }

    @Override
    public void startBrewing(@Query("recipeId") String recipeId, Callback<BrewingState> callback) {
        sendDelayed(callback, DummyBuilder.getBrewingState());
    }

    @Override
    public void confirmStep(@QueryMap Map<String, String> queryMap, Callback<BrewingState> callback) {
        sendDelayed(callback, DummyBuilder.getBrewingState());
    }
}
