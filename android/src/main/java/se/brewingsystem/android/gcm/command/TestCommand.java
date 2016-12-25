/*
 * 
 */
package se.brewingsystem.android.gcm.command;

import android.content.Context;

import se.brewingsystem.android.R;
import se.brewingsystem.android.gcm.GCMCommand;
import se.brewingsystem.android.utilities.LogUtils;

import static se.brewingsystem.android.utilities.LogUtils.makeLogTag;



/**
 * The Class TestCommand.
 */
public class TestCommand extends GCMCommand {
    
    /** The Constant TAG. */
    private static final String TAG = makeLogTag("TestCommand");


    /** The Constant TEST_ID. */
    public static final int TEST_ID = 44;

    /**
     * Execute.
     *
     * @param context the context
     * @param extraData the extra data
     */
    @Override
    public void execute(Context context, String extraData) {
        LogUtils.logI(TAG, "Received GCM message: " + extraData);
        displayNotification(context, extraData, null, R.drawable.ic_refresh, TEST_ID);
    }
}
