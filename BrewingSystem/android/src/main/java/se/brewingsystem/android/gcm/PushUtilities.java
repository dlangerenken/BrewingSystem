package se.brewingsystem.android.gcm;

import android.content.Context;
import android.text.TextUtils;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.brewingsystem.android.R;
import se.brewingsystem.android.network.INetworkCommunication;
import se.brewingsystem.android.utilities.CommonUtilities;
import se.brewingsystem.android.utilities.LogUtils;
import se.brewingsystem.android.utilities.PrefUtilities;

import static se.brewingsystem.android.utilities.LogUtils.logE;
import static se.brewingsystem.android.utilities.LogUtils.logW;
import static se.brewingsystem.android.utilities.LogUtils.makeLogTag;


/**
 * Helper class used to communicate with the server.
 */
public final class PushUtilities {

    /** The Constant TAG. */
    private static final String TAG = makeLogTag("GCM");
    
    /** The m context. */
    private final Context mContext;
    
    /** The m network communication. */
    private final INetworkCommunication mNetworkCommunication;
    
    /** The m pref utilities. */
    private final PrefUtilities mPrefUtilities;

    /**
     * Instantiates a new push utilities.
     *
     * @param context the context
     * @param networkCommunication the network communication
     * @param prefUtilities the pref utilities
     */
    @Inject
    public PushUtilities(Context context, INetworkCommunication networkCommunication, PrefUtilities prefUtilities) {
        mContext = context;
        mNetworkCommunication = networkCommunication;
        mPrefUtilities = prefUtilities;
    }

    /**
     * Register this account/device pair within the server.
     *
     * @param regId the reg id
     */
    void register(final String regId, final Callback<String> callback) {
        LogUtils.logI(TAG, "registering device (regId = " + regId + ")");
        mNetworkCommunication.subscribePush(regId, new Callback<String>() {
                    @Override
                    public void success(String result, Response response) {
                        String message = mContext
                                .getString(R.string.server_registered);
                        CommonUtilities.displayMessage(mContext, message);
                        mPrefUtilities.setRegisteredOnServer(true, regId);
                        if (callback != null) {
                            callback.success(result, response);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // Here we are simplifying and retrying on
                        // any error; in a
                        // real
                        // application, it should retry only on
                        // unrecoverable errors
                        // (like HTTP error code 503).
                        logE(TAG, "Failed to register after several retries",
                                error);
                        unregisterDevice();
                        if (callback != null) {
                            callback.failure(null);
                        }
                    }
                }
        );
    }

    /**
     * Unregister this account/device pair within the server.
     *
     * @param regId the reg id
     */
    public void unregister(final String regId) {
        LogUtils.logI(TAG, "unregistering device (regId = " + regId + ")");
        Map<String, String> params = new HashMap<>();
        params.put("regId", regId);
        mNetworkCommunication.unsubscribePush(regId, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        unregisterDevice();
                        String message = mContext
                                .getString(R.string.server_unregistered);
                        CommonUtilities.displayMessage(mContext, message);
                        // Regardless of server success, clear local preferences
                        mPrefUtilities.setRegisteredOnServer(false, null);
                    }

                    @Override
                    public void failure(RetrofitError error) {
// At this point the device is unregistered from GCM,
                        // but still
                        // registered in the server.
                        // We could try to unregister again, but it is not
                        // necessary:
                        // if the server tries to send a message to the device,
                        // it will get
                        // a "NotRegistered" error message and should unregister
                        // the device.
                        String message = mContext.getString(
                                R.string.server_unregister_error,
                                error.getMessage());
                        CommonUtilities.displayMessage(mContext, message);
                        GCMRegistrarCompat.clearRegistrationId(mContext);
                        // Regardless of server success, clear local preferences
                        mPrefUtilities.setRegisteredOnServer(false, null);
                    }
                }
        );
    }

    /**
     * Relog.
     */
    public void relog() {
        mPrefUtilities.setRegisteredOnServer(false, "");
        registerGCMClient(null);
    }

    /**
     * Register gcm client.
     *
     */
    public void registerGCMClient(Callback<String> callback) {
        GCMRegistrarCompat.checkDevice();
        GCMRegistrarCompat.checkManifest(mContext);

        final String regId = GCMRegistrarCompat.getRegistrationId(mContext);

        if (TextUtils.isEmpty(regId)) {
            new RegisterTask(mContext).execute(CommonUtilities.SENDER_ID);
        } else {
            checkLoginOnServer(callback, regId);
        }
    }

    /**
     * Check login on server.
     *
     * @param regId the reg id
     */
    private void checkLoginOnServer(Callback<String> callback, String regId) {
        // Device is already registered on GCM, needs to check if it is
        // registered on our server as well.
        if (mPrefUtilities.isRegisteredOnServer()) {
            // Skips registration.
            LogUtils.logI(TAG, "Already registered on the server");
            if (callback != null) {
                callback.success("success", null);
            }
        } else {
            register(regId, callback);
        }
    }

    /**
     * Unregister device.
     */
    public void unregisterDevice() {
        try {
            GCMRegistrarCompat.clearRegistrationId(mContext);
        } catch (Exception e) {
            logW(TAG, "C2DM unregistration error", e);
        }
    }

    /**
     * The Class RegisterTask.
     */
    private class RegisterTask extends
            GCMRegistrarCompat.BaseRegisterTask {

        /**
         * Instantiates a new register task.
         *
         * @param context the context
         */
        RegisterTask(Context context) {
            super(context);
        }

        /**
         * On post execute.
         *
         * @param regId the reg id
         */
        @Override
        protected void onPostExecute(String regId) {
            checkLoginOnServer(null, regId);
        }
    }
}
