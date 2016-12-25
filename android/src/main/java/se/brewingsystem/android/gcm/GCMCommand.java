/*
 * Copyright 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package se.brewingsystem.android.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import com.google.gson.Gson;

import se.brewingsystem.android.MainActivity;
import se.brewingsystem.android.R;
import gson.Serializer;
import se.brewingsystem.android.utilities.LogUtils;

import static se.brewingsystem.android.utilities.LogUtils.makeLogTag;



/**
 * The Class GCMCommand.
 */
public abstract class GCMCommand {
  
  /** The Constant TAG. */
  private static final String TAG = makeLogTag("AnnouncementCommand");
  
  /** The gson. */
  protected final Gson gson = Serializer.getInstance();

  /**
   * Execute.
   *
   * @param context the context
   * @param extraData the extra data
   */
  public abstract void execute(Context context, String extraData);

  /**
   * Display notification.
   *
   * @param context the context
   * @param message the message
   * @param intent the intent
   * @param notificationId id of the notification
   * @param smallIconDrawableId the small icon drawable id
   */
  protected void displayNotification(Context context, String message, PendingIntent intent,
      int smallIconDrawableId, int notificationId) {
    LogUtils.logI(TAG, "Displaying notification: " + message);
    Notification.Builder builder =
        new Notification.Builder(context).setWhen(System.currentTimeMillis())
            .setSmallIcon(smallIconDrawableId).setTicker(message)
            .setContentTitle(context.getString(R.string.app_name)).setContentText(message)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true);
    if (intent != null) {
      builder.setContentIntent(PendingIntent.getActivity(
          context,
          0,
          new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
              | Intent.FLAG_ACTIVITY_SINGLE_TOP), 0));
    }
    ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(notificationId,
        builder.build());
  }

}
