/*
 * 
 */
package se.brewingsystem.android.gcm.command;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;

import de.greenrobot.event.EventBus;
import se.brewingsystem.android.R;
import se.brewingsystem.android.events.AlarmEvent;
import se.brewingsystem.android.gcm.GCMCommand;
import se.brewingsystem.android.utilities.LogUtils;

import static se.brewingsystem.android.utilities.LogUtils.makeLogTag;

/**
 * Created by Daniel on 29.01.14.
 */
public class AlarmCommand extends GCMCommand {
    
    /** The Constant TAG. */
    private static final String TAG = makeLogTag("AlarmCommand");

    /**
     * Execute.
     *
     * @param context the context
     * @param extraData the extra data
     */
    @Override
    public void execute(Context context, String extraData) {
        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.minion_alarm);
        Notification.Builder builder = new Notification.Builder(context)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_emergency)
                .setTicker(extraData)
                .setContentTitle(context.getString(R.string.alarm_notification_title))
                .setContentText(extraData)
                .setLights(Color.RED, 2000, 1000)
                .setSound(sound)
                .setVibrate(getVibratePattern(1000, 1000, 20))
                .setAutoCancel(true);

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(0, builder.build());
        LogUtils.logI(TAG, "execute called");
        EventBus.getDefault().post(new AlarmEvent());
    }

    /**
     * Gets the vibrate pattern.
     *
     * @param vibrate how long it should be vibrated
     * @param sleep how long it should be sleeping
     * @param times how often should this pattern be repeated
     * @return the vibrate pattern
     */
    private static long[] getVibratePattern(int vibrate, int sleep, int times){
        long[] vibratePattern = new long[times*2 - 1];

        for (int i = 0; i < times; i+= 2){
            vibratePattern[i] = vibrate;
            vibratePattern[i+1] = sleep;
        }
        return vibratePattern;
    }
}
