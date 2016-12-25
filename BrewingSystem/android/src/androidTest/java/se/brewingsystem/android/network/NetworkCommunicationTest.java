package se.brewingsystem.android.network;

/**
 * Created by Daniel on 13.02.2015.
 */

import android.test.InstrumentationTestCase;

import com.google.gson.Gson;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;

import gson.Serializer;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import se.brewingsystem.android.utilities.PrefUtilities;


/**
 * This class checks if the network communication works as expected
 */
public class NetworkCommunicationTest extends InstrumentationTestCase {
    /**
     * The injector which can inject objects later on
     */
    private INetworkCommunication networkCommunication;

    /**
     * starts the network-communication and instantiates appropriate controller
     */
    @Before
    public void before(){
        Gson gson = Serializer.getInstance();
        PrefUtilities prefUtilities = new PrefUtilities(getInstrumentation().getContext());
        String serverUrl = prefUtilities.getServerURL() + "/";
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(serverUrl)
                .setConverter(new GsonConverter(gson))
                .build();
        networkCommunication = restAdapter.create(INetworkCommunication.class);
    }

    /**
     * Tests if the isAlive-Interface works as expected
     */
    public void testAlive(){
        networkCommunication.isServerAlive(new Callback<Boolean>() {
            @Override
            public void success(Boolean alive, Response response) {
                Assert.assertTrue(alive);
            }

            @Override
            public void failure(RetrofitError error) {
                Assert.fail();
            }
        });
    }

    /**
     * Resets the network-communication
     */
    @After
    public void after(){
        networkCommunication = null;
    }
}
