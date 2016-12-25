/***
 Copyright (c) 2013 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 http://commonsware.com/Android
 */

package se.brewingsystem.android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import se.brewingsystem.android.gcm.GCMBaseIntentServiceCompat;
import se.brewingsystem.android.gcm.GCMCommand;
import se.brewingsystem.android.gcm.command.AlarmCommand;
import se.brewingsystem.android.gcm.command.AnnouncementCommand;
import se.brewingsystem.android.gcm.command.MessageCommand;
import se.brewingsystem.android.gcm.command.TestCommand;
import se.brewingsystem.android.utilities.CommonUtilities;
import push.PushType;
import se.brewingsystem.android.utilities.LogUtils;

import static se.brewingsystem.android.utilities.LogUtils.makeLogTag;



/**
 * The Class GCMIntentService.
 */
public class GCMIntentService extends GCMBaseIntentServiceCompat {
    
    /** The Constant TAG. */
    private static final String TAG = makeLogTag("GCM");

    /** The Constant MESSAGE_RECEIVERS. */
    private static final Map<String, GCMCommand> MESSAGE_RECEIVERS;

    static {
        // Known messages and their GCM message receivers
        Map<String, GCMCommand> receivers = new HashMap<>();
        receivers.put(PushType.TEST.toString(), new TestCommand());
        receivers.put(PushType.INFO.toString(), new AnnouncementCommand());
        receivers.put(PushType.ALARM.toString(), new AlarmCommand());
        receivers.put(PushType.MESSAGE.toString(), new MessageCommand());
        MESSAGE_RECEIVERS = Collections.unmodifiableMap(receivers);
    }

    /**
     * Instantiates a new GCM intent service.
     */
    public GCMIntentService() {
        super(CommonUtilities.SENDER_ID);
    }

    /**
     * On message.
     *
     * @param message the message
     */
    @Override
    protected void onMessage(Intent message) {
        String action = message.getStringExtra("action");
        String extraData = message.getStringExtra("extraData");
        if (action == null) {
            LogUtils.logE(TAG, "Message received without command action");
            return;
        }
        action = action.toLowerCase();
        GCMCommand command = MESSAGE_RECEIVERS.get(action);
        if (command == null) {
            LogUtils.logE(TAG, "Unknown command received: " + action);
        } else {
            command.execute(this, extraData);
        }
    }

    /**
     * On error.
     *
     * @param message the message
     */
    @Override
    protected void onError(Intent message) {
        error("onDeleted", message);
    }

    /**
     * On deleted.
     *
     * @param message the message
     */
    @Override
    protected void onDeleted(Intent message) {
        error("onDeleted", message);
    }

    /**
     * Error.
     *
     * @param event the event
     * @param message the message
     */
    private void error(String event, Intent message) {
        Bundle extras=message.getExtras();

        for (String key : extras.keySet()) {
            Log.d(getClass().getSimpleName(),
                    String.format("%s: %s=%s", event, key,
                            extras.getString(key)));
        }
    }
}

