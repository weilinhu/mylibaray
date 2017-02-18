package com.zhanshow.mylibrary.network;

import android.app.Activity;
import android.app.Application;
import android.content.Context;


/**
 * @author weilinhu
 */
public class LibraryConfig {
    private static final String TAG = "LibraryConfig";
    private Application application;

    private LibraryConfig() {
    }



    private static final class Singleton {
        private static final LibraryConfig INSTANCE = new LibraryConfig();
    }


    public static LibraryConfig getInstance() {
        return Singleton.INSTANCE;
    }


    public void initApplication(Activity activity) {
        this.application =activity.getApplication();
    }

    public Context getContext() {
        return application;
    }




}
