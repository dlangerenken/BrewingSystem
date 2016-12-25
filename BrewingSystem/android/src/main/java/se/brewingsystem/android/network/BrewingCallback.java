package se.brewingsystem.android.network;


import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Callback-Extension which offers a onEnd-Method for simplifying most tasks
 * @param <T>
 */
public abstract class BrewingCallback<T> implements Callback<T> {

    @Override
    public final void success(T t, Response response) {
        onSuccess(t, response);
        onEnd();
    }

    /**
     * Called if the request was successfull
     * @param t object which was expected
     * @param response response which can be used for getting http-response codes
     */
    public void onSuccess(T t, Response response){
        /*
         * empty as it does not need to be overriden
         */
    }

    /**
     * Called if an error occured
     * @param error error which can be used for receiving http-response code etc
     */
    public void onFailure(RetrofitError error){
        /*
         * empty as it does not need to be overriden
         */
    }

    /**
     * Method which is called every time to simplify a "cooldown"-method
     */
    public void onEnd(){
        /*
         * empty as it does not need to be overriden
         */
    }

    @Override
    public final void failure(RetrofitError error) {
        onFailure(error);
        onEnd();
    }
}
