package com.user.sqrfactor.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Parser.JsonParser;
import com.user.sqrfactor.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class NetworkUtil {

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }


    public static boolean checkNewtWorkSpeed(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to mobile data
                if(activeNetwork.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS){
                    // Bandwidth between 100 kbps and below
                    return true;
                } else if(activeNetwork.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE){
                    // Bandwidth between 50-100 kbps
                    return true;
                } else if(activeNetwork.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_0){
                    // Bandwidth between 400-1000 kbps
                    return true;
                } else if(activeNetwork.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_A){
                    // Bandwidth between 600-1400 kbps
                    return true;
                }
            }
        }
       return false;

    }


    public static void handleSimpleVolleyRequestError(VolleyError error, Context context) {
        if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            try {
                String body = new String(error.networkResponse.data, "UTF-8");
                Log.d("handleSimpleVolley: ", body);
                if (statusCode == 400 || statusCode == 401) {
                    //server error
                    String errorMsg = JsonParser.SimpleParser(body);
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();

                } else if (statusCode == 422) {
                    JSONObject object = new JSONObject(body);
                    String key = object.getJSONArray("error_keys").getString(0);
                    String message = object.getJSONObject("errors").getJSONArray(key).getString(0);
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                } else {
                    String errorString = MyVolley.handleVolleyError(error);
                    Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show();
                }
            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
                NetworkUtil.showParsingErrorAlert(context);
            }
        } else {
            String errorString = MyVolley.handleVolleyError(error);
            Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showParsingErrorAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.oops))
                .setMessage(context.getString(R.string.data_not_found))
                .setNegativeButton(context.getString(R.string.report_issue), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO:: take user to report issue area
                    }
                })
                .setPositiveButton(context.getString(R.string.try_again), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
