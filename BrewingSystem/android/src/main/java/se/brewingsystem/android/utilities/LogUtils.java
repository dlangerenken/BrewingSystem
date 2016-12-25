/*
 * 
 */
package se.brewingsystem.android.utilities;

import android.util.Log;

import se.brewingsystem.android.BuildConfig;


/*
* Copyright 2012 Google Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/**
 * The Class LogUtils.
 */
public class LogUtils {

    /** The Constant LOG_PREFIX. */
    private static final String LOG_PREFIX = "brewing_";
    
    /** The Constant LOG_PREFIX_LENGTH. */
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    
    /** The Constant MAX_LOG_TAG_LENGTH. */
    private static final int MAX_LOG_TAG_LENGTH = 23;

    /**
     * Instantiates a new log utils.
     */
    private LogUtils() {
    }

    /**
     * Make log tag.
     *
     * @param str the str
     * @return the string
     */
    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }

        return LOG_PREFIX + str;
    }

    /**
     * Don't use this when obfuscating class names!.
     *
     * @param cls the cls
     * @return the string
     */
    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    /**
     * Logd.
     *
     * @param tag the tag
     * @param message the message
     */
    public static void logD(final String tag, String message) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG || Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }

    /**
     * Logd.
     *
     * @param tag the tag
     * @param message the message
     * @param cause the cause
     */
    public static void logD(final String tag, String message, Throwable cause) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG || Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message, cause);
        }
    }

    /**
     * Logv.
     *
     * @param tag the tag
     * @param message the message
     */
    public static void logV(final String tag, String message) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, message);
        }
    }

    /**
     * Logv.
     *
     * @param tag the tag
     * @param message the message
     * @param cause the cause
     */
    public static void logV(final String tag, String message, Throwable cause) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, message, cause);
        }
    }

    /**
     * Logi.
     *
     * @param tag the tag
     * @param message the message
     */
    public static void logI(final String tag, String message) {
        Log.i(tag, message);
    }

    /**
     * Logi.
     *
     * @param tag the tag
     * @param message the message
     * @param cause the cause
     */
    public static void logI(final String tag, String message, Throwable cause) {
        Log.i(tag, message, cause);
    }

    /**
     * Logw.
     *
     * @param tag the tag
     * @param message the message
     */
    public static void logW(final String tag, String message) {
        Log.w(tag, message);
    }

    /**
     * Logw.
     *
     * @param tag the tag
     * @param message the message
     * @param cause the cause
     */
    public static void logW(final String tag, String message, Throwable cause) {
        Log.w(tag, message, cause);
    }

    /**
     * Loge.
     *
     * @param tag the tag
     * @param message the message
     */
    public static void logE(final String tag, String message) {
        Log.e(tag, message);
    }

    /**
     * Loge.
     *
     * @param tag the tag
     * @param message the message
     * @param cause the cause
     */
    public static void logE(final String tag, String message, Throwable cause) {
        Log.e(tag, message, cause);
    }
}
