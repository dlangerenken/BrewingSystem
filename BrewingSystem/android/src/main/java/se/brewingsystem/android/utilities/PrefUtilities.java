/*
 * 
 */
package se.brewingsystem.android.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Calendar;
import java.util.Date;

import static se.brewingsystem.android.utilities.LogUtils.makeLogTag;


/**
 * Created by Daniel on 30.11.2014.
 */
@Singleton
public class PrefUtilities {
    
    /** The Constant TAG. */
    private static final String TAG = makeLogTag("PrefUtilities");
    
    /** The Constant PROPERTY_REGISTERED_TS. */
    private static final String PROPERTY_REGISTERED_TS = "registered_ts";
    
    /** The Constant PROPERTY_REG_ID. */
    private static final String PROPERTY_REG_ID = "reg_id";
    
    /** The Constant SERVER_URL. */
    private static final String SERVER_URL = "server_url";
    
    /** The preferences. */
    private final SharedPreferences preferences;

    /**
     * Instantiates a new pref utilities.
     *
     * @param context the context
     */
    @Inject
    public PrefUtilities(Context context) {
        preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
    }

    /**
     * Checks if is registered on server.
     *
     * @return true, if is registered on server
     */
    public boolean isRegisteredOnServer() {
        // Find registration threshold
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        long yesterdayTS = cal.getTimeInMillis();
        long regTS = preferences.getLong(PROPERTY_REGISTERED_TS, 0);
        if (regTS > yesterdayTS) {
            LogUtils.logV(TAG, "GCM registration current. regTS=" + regTS + " yesterdayTS=" + yesterdayTS);
            return true;
        } else {
            LogUtils.logV(TAG, "GCM registration expired. regTS=" + regTS + " yesterdayTS=" + yesterdayTS);
            return false;
        }
    }

    /**
     * Sets whether the device was successfully registered in the server side.
     *
     * @param flag  True if registration was successful, false otherwise
     * @param gcmId True if registration was successful, false otherwise
     */
    public void setRegisteredOnServer(boolean flag, String gcmId) {
        LogUtils.logD(TAG, "Setting registered on server status as: " + flag);
        SharedPreferences.Editor editor = preferences.edit();
        if (flag) {
            editor.putLong(PROPERTY_REGISTERED_TS, new Date().getTime());
            editor.putString(PROPERTY_REG_ID, gcmId);
        } else {
            editor.remove(PROPERTY_REG_ID);
            editor.remove(PROPERTY_REGISTERED_TS);
        }
        editor.apply();
    }

    /**
     * Sets the server url.
     *
     * @param url the new server url
     */
    public void setServerUrl(String url) {
        preferences.edit().putString(SERVER_URL, url).apply();
    }

    /**
     * Gets the server url.
     *
     * @return the server url
     */
    public String getServerURL() {
        return preferences.getString(SERVER_URL, CommonUtilities.SERVER_URL);
    }

}
