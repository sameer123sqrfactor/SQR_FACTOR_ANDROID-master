package com.user.sqrfactor.Extras;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Storage.MySharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class UtilsClass {

    private static MySharedPreferences mSp;
    public static String baseurl="https://sqrfactor.com/api/";
    public static String baseurl1="https://sqrfactor.com/";
    public static String slug;

    public static String getName(String firstName,String lastName,String name,String userName)
    {
        if(firstName!=null && !firstName.equals("null") && lastName!=null&& !lastName.equals("null"))
        {
            return firstName+" "+lastName;
        }
        else if(name!=null && !name.equals("null"))
        {
            return name;
        }
        else {
            return userName;
        }
    }

    public static String getTime(String time)
    {
        String dtc = time;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            Date past = format.parse(dtc);
            Date now = new Date();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days1 = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if (seconds < 60) {
                return  (seconds + " sec ago");

            } else if (minutes < 60) {
                return (minutes + " min ago");
            } else if (hours < 24) {
                return (hours + " hours ago");
            } else {
                return (days1 + " days ago");
            }
        } catch (Exception j) {
            j.printStackTrace();
        }
      return "";
    }

    public static String getParsedImageUrl(String url)
    {
        String[] parsedUrl=null;
        if(url!=null)
        {
            parsedUrl=url.split("/");
        }


        if(parsedUrl!=null && parsedUrl.length>2 && (parsedUrl[2].equals("graph.facebook.com")||parsedUrl[2].contains("googleusercontent.com")||parsedUrl[2].equals("platform-lookaside.fbsbx.com")))
        {
            return url;
        }
        else {
            return  "https://sqrfactor.com/" + url;

        }

    }

    public static boolean IsConnected(Context context)
    {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        return connected;
    }

    public static UserClass GetUserClassFromSharedPreferences(Context context)
    {

        final SharedPreferences mPrefs = context.getSharedPreferences("User", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        UserClass userClass = gson.fromJson(json, UserClass.class);
        return userClass;

    }

    public static String GetToken(Context context){
        mSp = MySharedPreferences.getInstance(context);
       return Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY);
    }

   public static void GoogleAnalytics(Tracker tracker,String screenName,String userName){
        tracker.setScreenName(screenName+" /"+userName);

   }


}
