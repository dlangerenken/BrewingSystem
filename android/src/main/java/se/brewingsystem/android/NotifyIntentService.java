/*
 * 
 */
package se.brewingsystem.android;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import gson.Serializer;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.service.RoboIntentService;
import se.brewingsystem.android.events.BrewingStateChangedEvent;
import se.brewingsystem.android.events.IodineTestPositiveEvent;
import se.brewingsystem.android.gcm.command.AnnouncementCommand;
import se.brewingsystem.android.gcm.command.MessageCommand;
import se.brewingsystem.android.network.INetworkCommunication;
import general.BrewingState;


/**
 * Created by Daniel on 20.12.2014.
 */
public class NotifyIntentService extends RoboIntentService{

    /** The Constant ACTION_CONFIRM. */
    public static final String ACTION_CONFIRM = "se.brewingsystem.android.CONFIRM";

    /** The m network communication. */
    @Inject
    private INetworkCommunication mNetworkCommunication;

    /**
     * Instantiates a new notify intent service.
     */
    public NotifyIntentService() {
        super("NotifyIntentService");
    }

    /**
     * On handle intent.
     *
     * @param intent the intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (ACTION_CONFIRM.equals(intent.getAction())) {
            int state = intent.getIntExtra("state", -1);
            BrewingState brewingStep = BrewingState.fromValue(state);
            if (brewingStep.getState() == BrewingState.State.MASHING && brewingStep.getPosition() == BrewingState.Position.IODINE){
                confirmIodine();
            }else{
                confirmStep(state);
            }
        }
    }

    /*
     * counter for tries
     */
    private int tries = 0;

    /*
     * max tries
     */
    private final int maxTriesBeforeHide = 3;

    /**
     * Confirm iodine.
     */
    private void confirmIodine() {
        mNetworkCommunication.confirmIodineTest(0, new Callback<BrewingState>() {
            @Override
            public void success(BrewingState brewingState, Response response) {
                cancelNotifications(NotifyIntentService.this);
                tries = 0;
                IodineTestPositiveEvent event = new IodineTestPositiveEvent();
                EventBus.getDefault().post(event);
            }

            @Override
            public void failure(RetrofitError error) {
                if (tries < maxTriesBeforeHide){
                    tries++;
                }else {
                    cancelNotifications(NotifyIntentService.this);
                    tries = 0;
                }
            }
        });
    }

    /**
     * Cancels all notifications which are currently visible
     * @param context Context required to receive the notification manager
     */
    public static void cancelNotifications(Context context){
        if (context != null) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(MessageCommand.CONFIRM_ID);
            notificationManager.cancel(AnnouncementCommand.INFO_ID);
        }
    }

    /**
     * Confirm step.
     *
     * @param state the state
     */
    private void confirmStep(int state) {
        Map<String, String> queryMap = new HashMap<>();
        BrewingState brewingState = BrewingState.fromValue(state);
        queryMap.put("state", Serializer.getInstance().toJson(brewingState));
        mNetworkCommunication.confirmStep(queryMap, new Callback<BrewingState>() {
            @Override
            public void success(BrewingState brewingState, Response response) {
                cancelNotifications(NotifyIntentService.this);
                BrewingStateChangedEvent event = new BrewingStateChangedEvent();
                event.brewingState = brewingState;
                EventBus.getDefault().post(event);
            }

            @Override
            public void failure(RetrofitError error) {
                cancelNotifications(NotifyIntentService.this);
            }
        });
    }


}
