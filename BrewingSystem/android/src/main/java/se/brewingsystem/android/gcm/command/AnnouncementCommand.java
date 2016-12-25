/*
 * 
 */
package se.brewingsystem.android.gcm.command;

import android.content.Context;

import de.greenrobot.event.EventBus;
import se.brewingsystem.android.R;
import se.brewingsystem.android.events.UpdateMessageEvent;
import se.brewingsystem.android.gcm.GCMCommand;
import se.brewingsystem.android.utilities.LogUtils;

import static se.brewingsystem.android.utilities.LogUtils.makeLogTag;



/**
 * The Class AnnouncementCommand.
 */
public class AnnouncementCommand extends GCMCommand {
    
    /** The Constant TAG. */
    private static final String TAG = makeLogTag("AnnouncementCommand");

    /** The Constant INFO_ID. */
    public static final int INFO_ID = 43;
    /**
     * Execute.
     *
     * @param context the context
     * @param extraData the extra data
     */
    @Override
    public void execute(Context context, String extraData) {
        LogUtils.logI(TAG, "Received GCM message: " + extraData);
        displayNotification(context, extraData, null, R.drawable.ic_launcher, INFO_ID);
        EventBus.getDefault().post(new UpdateMessageEvent());
    }

}
