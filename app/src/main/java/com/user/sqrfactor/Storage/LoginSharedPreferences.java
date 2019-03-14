package com.user.sqrfactor.Storage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class LoginSharedPreferences {

    private static LoginSharedPreferences mInstance= null;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefsEditor;

    private LoginSharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("loginPrefs", Activity.MODE_PRIVATE);
        prefsEditor = sharedPreferences.edit();
    }

    public static void init(Context context) {
        mInstance = new LoginSharedPreferences(context);

    }


    public static LoginSharedPreferences getInstance() {
        if (mInstance == null) {
            throw new RuntimeException(
                    "Must run init(Application application) before an instance can be obtained");
        }
        return mInstance;
    }

    /**
     * To get the Stored string value in Preference.
     *
     * @param key
     * @param defaultvalue
     * @return stored string value.
     */
    public String getStringValue(final String key, final String defaultvalue) {
        return sharedPreferences.getString(key, defaultvalue);
    }

    /**
     * To store the string value in prefernce.
     *
     * @param key
     * @param value
     */
    public void setStringValue(final String key, final String value) {
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public void setBooleanValue(final String savelogin, final boolean isChecked) {
        prefsEditor.putBoolean(savelogin, isChecked);
        prefsEditor.commit();
    }
    public void clearLoginCredentials(){
        prefsEditor.clear();
        prefsEditor.commit();
    }
}
