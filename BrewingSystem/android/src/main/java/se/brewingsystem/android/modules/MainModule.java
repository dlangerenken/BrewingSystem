package se.brewingsystem.android.modules;

import android.util.Log;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import gson.Serializer;
import retrofit.Endpoint;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;
import se.brewingsystem.android.network.INetworkCommunication;
import se.brewingsystem.android.utilities.IMessageHelper;
import se.brewingsystem.android.utilities.MessageHelper;
import se.brewingsystem.android.utilities.PrefUtilities;


/**
 * Created by Daniel on 18.12.2014.
 */
public class MainModule extends AbstractModule {

    /**
     * Returns the network communication which is used in the whole application
     * @param utilities
     * @return
     */
    @Singleton
    @Provides
    public INetworkCommunication getRealNetworkCommunication(final PrefUtilities utilities) {
        Gson gson = Serializer.getInstance();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(new Endpoint() {
                    @Override
                    public String getUrl() {
                        return utilities.getServerURL() + "/";
                    }

                    @Override
                    public String getName() {
                        return "default";
                    }
                })
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(new MyErrorHandler()).
        setLog(new RestAdapter.Log() {
            @Override
            public void log(String msg) {
                Log.i("MainModule", msg);
            }
        })
                .build();
        return restAdapter.create(INetworkCommunication.class);
    }

    /**
     * ErrorHandler which is used for debugging
     */
    class MyErrorHandler implements ErrorHandler {
        @Override public Throwable handleError(RetrofitError cause) {
            return cause;
        }
    }
    @Override
    protected void configure() {
        binder.bind(IMessageHelper.class).to(MessageHelper.class);
    }
}
