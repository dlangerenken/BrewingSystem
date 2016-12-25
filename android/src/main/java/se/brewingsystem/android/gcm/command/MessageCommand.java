/*
 * 
 */
package se.brewingsystem.android.gcm.command;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;

import com.google.inject.Inject;

import de.greenrobot.event.EventBus;
import general.MaltAddition;
import messages.MaltAdditionMessage;
import se.brewingsystem.android.BaseApplication;
import se.brewingsystem.android.MainActivity;
import se.brewingsystem.android.NotifyIntentService;
import se.brewingsystem.android.R;
import se.brewingsystem.android.events.UpdateMessageEvent;
import se.brewingsystem.android.gcm.GCMCommand;
import se.brewingsystem.android.utilities.IMessageHelper;
import general.BrewingState;
import messages.ConfirmationRequestMessage;
import messages.Message;
import messages.PreNotificationMessage;
import se.brewingsystem.android.utilities.LogUtils;

import static se.brewingsystem.android.utilities.LogUtils.makeLogTag;


/**
 * Created by Daniel on 29.01.14.
 */
public class MessageCommand extends GCMCommand {
    
    /** The Constant TAG. */
    private static final String TAG = makeLogTag("MessageCommand");
    
    /** The Constant CONFIRM_ID. */
    public static final int CONFIRM_ID = 42;


    @Inject
    private IMessageHelper messageHelper;

    /**
     * Message needs to be injected to get the message helper
     */
    public MessageCommand(){
        BaseApplication.inject(this);
    }

    /**
     * Execute.
     *
     * @param context the context
     * @param extraData the extra data
     */
    @Override
    public void execute(Context context, String extraData) {
        Log.d(TAG, extraData);

        Message message = gson.fromJson(extraData, Message.class);
        if (message instanceof ConfirmationRequestMessage) {
            ConfirmationRequestMessage requestMessage = (ConfirmationRequestMessage) message;
            handleConfirmationRequest(context, requestMessage);
        }else if (message instanceof PreNotificationMessage){
            PreNotificationMessage preNotificationMessage = (PreNotificationMessage) message;
            handlePreNotification(preNotificationMessage, context);
        }else {
            /*
             * Return text of message if not handled before
             */
            new AnnouncementCommand().execute(context, message.getMessage());
        }
        EventBus.getDefault().post(new UpdateMessageEvent());
    }

    /**
     * Handle pre notification.
     *
     * @param preNotificationMessage the pre notification message
     * @param context the context
     */
    private void handlePreNotification(PreNotificationMessage preNotificationMessage, Context context) {
        Message contentMessage = preNotificationMessage.getContent();
        if (contentMessage != null){
            //there is going to be a message sent after a given timespan
            if (contentMessage instanceof MaltAdditionMessage){
                MaltAdditionMessage maltAdditionMessage = (MaltAdditionMessage) contentMessage;
                handlePreIngredientAdditionMessage(context, maltAdditionMessage);
            }else {
                LogUtils.logE(TAG, "Message is not an Ingredient Addition");
            }
        }
    }

    /*
     * This method is called when a notification was received, which tells us,
     * that there is going to be an addition of an ingredient in the near future
     */
    /**
     * Handle pre ingredient addition message.
     *
     * @param context the context
       * @param maltAdditionMessage the ingredient addition message
         * @param maltAdditionMessage the ingredient addition message
     */
              private void handlePreIngredientAdditionMessage(Context context, MaltAdditionMessage maltAdditionMessage) {
                       if (maltAdditionMessage.getMaltAdditions() != null && maltAdditionMessage.getMaltAdditions().size() > 0){
                              MaltAddition addition = maltAdditionMessage.getMaltAdditions().iterator().next();
                    long inputTime = addition.getInputTime();
                    String malt = addition.getName();
                    String amount = addition.getAmount() + " " + addition.getUnit().toString();
            String time = String.format(context.getString(R.string.duration_min_format), (int) inputTime / 1000 / 60 + "");

            String announcement = String.format(context.getString(R.string.malt_addition_pre_notification_text),
                    time, malt, amount);
        /*
         * delegate the time to the general announcement-command
         */
            new AnnouncementCommand().execute(context, announcement);
        }
    }

    /*
    * we need to do the confirmation
    */
    /**
     * Handle confirmation request.
     *
     * @param context the context
     * @param requestMessage the request message
     */
    private void handleConfirmationRequest(Context context, ConfirmationRequestMessage requestMessage) {
        final BrewingState brewingStep = requestMessage.getBrewingStep();
        if (brewingStep.getType() != BrewingState.Type.REQUEST) {
            return;
        }

        Intent intent = new Intent(context, NotifyIntentService.class);
        intent.setAction(NotifyIntentService.ACTION_CONFIRM);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("state", requestMessage.getBrewingStep().toValue());

        PendingIntent pIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Builder builder = getNotificationBuilder(context, requestMessage.getMessage(), R.drawable.ic_launcher);
        if (brewingStep.getState() == BrewingState.State.MASHING && brewingStep.getPosition() == BrewingState.Position.IODINE){
            //open iodine test notification
            builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, context.getString(R.string.iodin_test_message_option_no), createActivityService(context));
            builder.addAction(android.R.drawable.ic_menu_add, context.getString(R.string.iodin_test_message_option_yes), pIntent);
        } else {
            builder.addAction(android.R.drawable.ic_menu_add, context.getString(R.string.manual_step_notification_option_confirm), pIntent);
        }
        builder.setContentTitle(messageHelper.getTextFromBrewingState(brewingStep));
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(CONFIRM_ID, builder.build());

        LogUtils.logD(TAG, requestMessage.getClass().getSimpleName());
    }

    /**
     * Creates the activity service.
     *
     * @param context the context
     * @return the pending intent
     */
    private PendingIntent createActivityService(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(MainActivity.ACTION_IODINE_TEST_REQUIRED);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    /**
     * Gets the notification builder.
     *
     * @param context the context
     * @param message the message
     * @param smallIconDrawableId the small icon drawable id
     * @return the notification builder
     */
    Notification.Builder getNotificationBuilder(Context context, String message, int smallIconDrawableId) {
        LogUtils.logI(TAG, "Displaying notification: " + message);
        return new Notification.Builder(context)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(smallIconDrawableId)
                .setTicker(message)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);
    }
}
