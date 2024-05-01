package com.example.basekotlin;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static Context context;

    public static Context getAppContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

    }

    @Override
    protected void attachBaseContext(Context context2) {
        super.attachBaseContext(context2);
    }

}

