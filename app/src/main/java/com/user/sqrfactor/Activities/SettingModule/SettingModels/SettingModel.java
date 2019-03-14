package com.user.sqrfactor.Activities.SettingModule.SettingModels;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.user.sqrfactor.Activities.SettingModule.SettingInterface;
import com.user.sqrfactor.Activities.SettingModule.SettingViews.Activities.Settings;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.TokenClass;
import com.user.sqrfactor.Pojo.UserData;
import com.user.sqrfactor.Pojo.User_basic_detail;
import com.user.sqrfactor.Storage.UserDataSharedpreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SettingModel implements SettingInterface.settingModel {

    private SettingInterface.settingPresenter settingPresenter;
    private SharedPreferences mPrefs;



    public SettingModel(SettingInterface.settingPresenter settingPresenter) {
        this.settingPresenter = settingPresenter;
    }

    @Override
    public void getUserSettingDataFromServerAndUpdateLocalDb() {

        //RequestQueue requestQueue = Volley.newRequestQueue(this);

        RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();

        StringRequest myReq = new StringRequest(Request.Method.GET, UtilsClass.baseurl+"profile/edit",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("settings", response);
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("User Data");
                            UserData userData = new UserData(jsonArray.getJSONObject(0));

                            User_basic_detail user_basic_detail=new User_basic_detail(jsonObject.getJSONObject("User_basic_detail"));
                            userData.setUser_basic_detail(user_basic_detail);
//
//                            mPrefs = getSharedPreferences("UserData", MODE_PRIVATE);
                           // SharedPreferences.Editor prefsEditor = mPrefs.edit();


                            UserDataSharedpreferences userDataSharedpreferences=UserDataSharedpreferences.getInstance();

                            Gson gson = new Gson();
                            String json = gson.toJson(userData);
                            userDataSharedpreferences.setStringValue("UserData",json);


//                            prefsEditor.putString("UserData", json);
//                            prefsEditor.commit();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {


                                String res = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                Log.v("chat",res);
//                                Toast.makeText(getApplicationContext(),res,Toast.LENGTH_LONG).show();
                                // Now you can use any deserializer to make sense of data
                                JSONObject obj = new JSONObject(res);
                            } catch (UnsupportedEncodingException e1) {
//                                Toast.makeText(getApplicationContext(),e1.toString(),Toast.LENGTH_LONG).show();
                                // Couldn't properly decode data to string
                                e1.printStackTrace();
                            } catch (JSONException e2) {
//                                Toast.makeText(getApplicationContext(),e2.toString(),Toast.LENGTH_LONG).show();
                                // returned data is not JSONObject?
                                e2.printStackTrace();
                            }
                        }
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + TokenClass.Token);

                return params;
            }

        };
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(myReq);
    }
}
