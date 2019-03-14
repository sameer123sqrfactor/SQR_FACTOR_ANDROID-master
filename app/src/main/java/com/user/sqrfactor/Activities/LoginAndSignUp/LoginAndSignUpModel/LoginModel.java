package com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpModel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.user.sqrfactor.Activities.HomeScreen;
import com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpContractInterface;
import com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpPresenter.LoginPresenter;
import com.user.sqrfactor.Activities.SocialFormActivity;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Fragments.MDToast;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.IsOnline;
import com.user.sqrfactor.Pojo.TokenClass;
import com.user.sqrfactor.Storage.LoginSharedPreferences;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Storage.UserClassSharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import static android.content.Context.MODE_PRIVATE;
import static com.android.volley.VolleyLog.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;

public class LoginModel implements LoginAndSignUpContractInterface.loginModel {

    private SharedPreferences sp;
    private  FirebaseDatabase database;
    private DatabaseReference ref;
    private AccessToken accessToken;
    private String firstNameText,lastNameText;

    LoginAndSignUpContractInterface.loginPresenter loginPresenter;


    public LoginModel(LoginAndSignUpContractInterface.loginPresenter loginPresenter ) {
        this.loginPresenter=loginPresenter;
        sp = getApplicationContext().getSharedPreferences("login",MODE_PRIVATE);

    }


    @Override
    public void sendloginData(final String userName, final String password) {


        RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();

        StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("ReponseLogin", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.has("message")) {

                                loginPresenter.onLoginFailure("Check Your UserName or password field");
                            }
                            else {
                                UserClass userClass = new UserClass(jsonObject);
                                SetUpSharedPreferencesAfterSuccessfullLogin(userClass,jsonObject);
                                loginPresenter.onSuccessFulLogin();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        MDToast.makeText(getApplicationContext(), "Your email or password not valid", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", userName);
                params.put("password", password);
                return params;
            }
        };
        requestQueue.add(myReq);


    }

    @Override
    public void getUserDataAfterFacebookSucessFullLogin() {
        try {

            accessToken = AccessToken.getCurrentAccessToken();

            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {

                            sp.edit().clear();
                            sp.edit().putBoolean("logged",true).commit();

                            try {
                                String name = object.getString("name");
                                String[] nameArray=name.split(" ");
                                if(nameArray.length>1) {
                                    firstNameText=nameArray[0];
                                    for(int i=1;i<nameArray.length;i++) {
                                        if(!nameArray[i].equals("null")||nameArray[i]!=null)
                                            lastNameText+=nameArray[i];
                                    }
                                    Log.v("data",firstNameText);
                                }
                                else {
                                    firstNameText=nameArray[0];
                                    lastNameText="";
                                }


                                final String email = object.getString("email");
                                final String profileID = object.getString("id");
                                JSONObject picture = object.getJSONObject("picture");
                                JSONObject data = picture.getJSONObject("data");
                                final String url = data.getString("url");

                                //  RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                                RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();
                                StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"sociallogin",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Log.v("Reponse", response);
                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    String msg = jsonObject.getString("status");
                                                    if(msg.equals("Already registered")) {
                                                        UserClass userClass = new UserClass(jsonObject);
                                                        SetUpSharedPreferencesAfterSuccessfullLogin(userClass,jsonObject);
                                                        loginPresenter.onSuccessFulLogin();

                                                    }else {
                                                        JSONObject user = jsonObject.getJSONObject("user");
                                                        JSONObject activationToken = user.getJSONObject("activation_token");

                                                        JSONObject userDeatil = activationToken.getJSONObject("user");
                                                        UserClass userClass = new UserClass("0","0","0","0",userDeatil.getString("user_name"),userDeatil.getString("first_name"),userDeatil.getString("last_name"),userDeatil.getString("profile"),
                                                                userDeatil.getInt("id"),userDeatil.getString("email"),userDeatil.getString("mobile_number"),userDeatil.getString("user_type"));
                                                        SetUpSharedPreferencesAfterSuccessfullLogin(userClass,jsonObject);
                                                        loginPresenter.onSuccessFulLogin();

                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        NetworkResponse response = error.networkResponse;
                                        if (error instanceof ServerError && response != null) {
                                            try {


                                                String res = new String(response.data,
                                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                                Log.v("login 1",res);
                                                Toast.makeText(getApplicationContext(),res,Toast.LENGTH_LONG).show();
                                                // Now you can use any deserializer to make sense of data
                                                JSONObject obj = new JSONObject(res);
                                            } catch (UnsupportedEncodingException e1) {
                                                Toast.makeText(getApplicationContext(),e1.toString(),Toast.LENGTH_LONG).show();
                                                // Couldn't properly decode data to string
                                                e1.printStackTrace();
                                            } catch (JSONException e2) {
                                                Toast.makeText(getApplicationContext(),e2.toString(),Toast.LENGTH_LONG).show();
                                                // returned data is not JSONObject?
                                                e2.printStackTrace();
                                            }
                                        }
                                    }

                                })
                                {

                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("Accept", "application/json");
                                        return params;
                                    }
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("social_id",profileID);
                                        params.put("first_name",firstNameText);
                                        params.put("last_name",lastNameText);
                                        params.put("email", email);
                                        if(url.equals("null")||url==null) {
                                            params.put("profile_pic","");
                                        }
                                        else {
                                            params.put("profile_pic",url);
                                        }
                                        params.put("service", "facebook");
                                        return params;
                                    }

                                };
                                myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                requestQueue.add(myReq);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link,birthday,gender,email,picture");
            request.setParameters(parameters);
            request.executeAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void SetUpSharedPreferencesAfterSuccessfullLogin(UserClass userClass,JSONObject jsonObject){

        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications" + userClass.getUserId());
        FirebaseMessaging.getInstance().subscribeToTopic("chats"+userClass.getUserId());
        //code for user status
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();



        database= FirebaseDatabase.getInstance();
        ref = database.getReference();


        IsOnline isOnline=new IsOnline("True",formatter.format(date));
        ref.child("Status").child(userClass.getUserId()+"").child("android").setValue(isOnline);
        IsOnline isOnline1=new IsOnline("False",formatter.format(date));

        ref.child("Status").child(userClass.getUserId()+"").child("web").setValue(isOnline1);
        JSONObject TokenObject = null;


        try {
            TokenObject = jsonObject.getJSONObject("success");
            String token = TokenObject.getString("token");
           // editor.putString("TOKEN", token);
            TokenClass.Token=token;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //setting user credentials details to shred preferences
        MySharedPreferences mSp= MySharedPreferences.getInstance();
        mSp.setKey(SPConstants.API_KEY, TokenClass.Token);
        mSp.setKey(SPConstants.USER_ID, String.valueOf(userClass.getUserId()));
        mSp.setKey(SPConstants.PROFILE_URL, userClass.getProfile());
        mSp.setKey(SPConstants.EMAIL, userClass.getEmail());
        mSp.setKey(SPConstants.USER_TYPE,userClass.getUserType());
        mSp.setKey(SPConstants.NAME,UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));


        //setting userclass in preference editor
        UserClassSharedPreferences userClassSharedPreferences=UserClassSharedPreferences.getInstance();
        Gson gson = new Gson();
        String json = gson.toJson(userClass);
        userClassSharedPreferences.setStringValue("MyObject", json);

    }






    @Override
    public void rememberMeLoginCredentials(String username,String password,boolean isChecked) {


        //SharedPrefsManager.getInstance().setStringValue("key", "value");
        LoginSharedPreferences loginSharedPreferences= LoginSharedPreferences.getInstance();
        loginSharedPreferences.setBooleanValue(SPConstants.SAVELOGIN, isChecked);
        loginSharedPreferences.setStringValue(SPConstants.USERNAME, username);
        loginSharedPreferences.setStringValue(SPConstants.PASSWORD, password);


    }

    @Override
    public void clearLoginCredentials() {
       LoginSharedPreferences.getInstance().clearLoginCredentials();
    }
}
