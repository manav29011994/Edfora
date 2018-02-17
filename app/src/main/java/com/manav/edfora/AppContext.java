package com.manav.edfora;

import android.app.Application;

/**
 * Created by manav on 16/12/17.
 */

public class AppContext extends Application {
    private static String TAG = AppContext.class.getSimpleName();
    private static AppContext appInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance=this;
    }

    public static AppContext getAppContext() {
        return appInstance;
    }
}
