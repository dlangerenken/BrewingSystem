/*
 * 
 */
package se.brewingsystem.android;

import android.app.Application;

import com.google.inject.Injector;

import roboguice.RoboGuice;
import se.brewingsystem.android.modules.MainModule;


/**
 * Created by Daniel on 29.11.2014.
 */
public class BaseApplication extends Application {

    /**
     * The injector which can inject objects later on
     */
    private static Injector injector;

    /**
     * On create.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        injector = RoboGuice.getOrCreateBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(this), new MainModule());
    }

    /**
     * Injects the object
     * @param object object which should be injected manually
     */
    public static void inject(Object object) {
        injector.injectMembers(object);
    }
}
