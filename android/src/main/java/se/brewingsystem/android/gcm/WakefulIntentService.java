/*
 * 
 */
package se.brewingsystem.android.gcm;

/***
 Copyright (c) 2009-11 CommonsWare, LLC

 Licensed under the Apache License, Version 2.0 (the "License"); you may
 not use this file except in compliance with the License. You may obtain
 a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import roboguice.service.RoboIntentService;


/**
 * The Class WakefulIntentService.
 */
public abstract class WakefulIntentService extends RoboIntentService {

    /**
     * Do wakeful work.
     *
     * @param intent the intent
     */
    abstract protected void doWakefulWork(Intent intent);

    /** The Constant NAME. */
    private static final String NAME=
            "com.commonsware.cwac.wakeful.WakefulIntentService";

    /** The lock static. */
    private static volatile PowerManager.WakeLock lockStatic=null;

    /**
     * Gets the lock.
     *
     * @param context the context
     * @return the lock
     */
    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if (lockStatic == null) {
            PowerManager mgr=
                    (PowerManager)context.getSystemService(Context.POWER_SERVICE);

            lockStatic=mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, NAME);
            lockStatic.setReferenceCounted(true);
        }

        return(lockStatic);
    }

    /**
     * Send wakeful work.
     *
     * @param ctxt the ctxt
     * @param i the i
     */
    public static void sendWakefulWork(Context ctxt, Intent i) {
        getLock(ctxt.getApplicationContext()).acquire();
        ctxt.startService(i);
    }

    /**
     * Send wakeful work.
     *
     * @param ctxt the ctxt
     * @param clsService the cls service
     */
    public static void sendWakefulWork(Context ctxt, Class<?> clsService) {
        sendWakefulWork(ctxt, new Intent(ctxt, clsService));
    }


    /**
     * Instantiates a new wakeful intent service.
     *
     * @param name the name
     */
    public WakefulIntentService(String name) {
        super(name);
        setIntentRedelivery(true);
    }

    /**
     * On start command.
     *
     * @param intent the intent
     * @param flags the flags
     * @param startId the start id
     * @return the int
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PowerManager.WakeLock lock=getLock(this.getApplicationContext());

        if (!lock.isHeld() || (flags & START_FLAG_REDELIVERY) != 0) {
            lock.acquire();
        }

        super.onStartCommand(intent, flags, startId);

        return(START_REDELIVER_INTENT);
    }

    /**
     * On handle intent.
     *
     * @param intent the intent
     */
    @Override
    final protected void onHandleIntent(Intent intent) {
        try {
            doWakefulWork(intent);
        }
        finally {
            PowerManager.WakeLock lock=getLock(this.getApplicationContext());

            if (lock.isHeld()) {
                lock.release();
            }
        }
    }
}
