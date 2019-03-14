package com.user.sqrfactor.Application;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.tagmanager.TagManager;
import com.user.sqrfactor.BuildConfig;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.LoginSharedPreferences;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Storage.UserClassSharedPreferences;
import com.user.sqrfactor.Storage.UserDataSharedpreferences;

import net.gotev.uploadservice.UploadService;

public class MyApplication extends Application {
   // private static final String TAG = "MyApplication";
    private static MyApplication mInstance;
    private static MySharedPreferences mSp;

    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;
    private static TagManager tagManager ;


    public static final String TAG = MyApplication.class
            .getSimpleName();

    //private static MyApplication mInstance;


    @Override
    public void onCreate() {
        super.onCreate();

        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        mInstance = this;
        sAnalytics = GoogleAnalytics.getInstance(this);
        tagManager=TagManager.getInstance(this);


        //mSp = MySharedPreferences.getInstance(this);
        MySharedPreferences.init(this);
        LoginSharedPreferences.init(this);
        UserClassSharedPreferences.init(this);
        UserDataSharedpreferences.init(this);


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

    }

//    public static MyApplication getInstance() {
//        return mInstance;
//    }

    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }

    public TagManager getTagManager(){
         return tagManager;

    }
    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }
    public static MyApplication getInstance() {
        return mInstance;
    }



}
