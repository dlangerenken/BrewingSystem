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
package se.brewingsystem.android.gcm;

import android.content.Intent;
import com.google.android.gms.gcm.GoogleCloudMessaging;


/**
 * The Class GCMBaseIntentServiceCompat.
 */
abstract public class GCMBaseIntentServiceCompat extends
        WakefulIntentService {
    
    /**
     * On message.
     *
     * @param message the message
     */
    abstract protected void onMessage(Intent message);

    /**
     * On error.
     *
     * @param message the message
     */
    abstract protected void onError(Intent message);

    /**
     * On deleted.
     *
     * @param message the message
     */
    abstract protected void onDeleted(Intent message);

    /**
     * Instantiates a new GCM base intent service compat.
     *
     * @param name the name
     */
    public GCMBaseIntentServiceCompat(String name) {
        super(name);
    }

    /* (non-Javadoc)
     * @see java.se.brewingsystem.android.gcm.WakefulIntentService#doWakefulWork(Intent)
     */
    @Override
    protected void doWakefulWork(Intent i) {
        GoogleCloudMessaging gcm=GoogleCloudMessaging.getInstance(this);
        String messageType=gcm.getMessageType(i);

        switch (messageType) {
            case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:
                onError(i);
                break;
            case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:
                onDeleted(i);
                break;
            default:
                onMessage(i);
                break;
        }
    }
}