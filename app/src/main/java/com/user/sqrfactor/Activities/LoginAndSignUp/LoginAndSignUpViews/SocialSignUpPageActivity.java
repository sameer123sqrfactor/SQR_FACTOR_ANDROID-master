package com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpViews;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.user.sqrfactor.Activities.HomeScreen;
import com.user.sqrfactor.Activities.OTP_Verify;
import com.user.sqrfactor.Activities.SocialFormActivity;
import com.user.sqrfactor.Constants.BundleConstants;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.IsOnline;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SocialSignUpPageActivity extends Activity {
    private EditText signupFirstName_text,signupLastName_text,signup_email_text,signupUserName_text,
            signupPassword_text,signupConfirmPassword_text;
    private SharedPreferences sp;
    private MySharedPreferences mSp;
    private SharedPreferences.Editor editor;
    private Button social_submit;
   private SharedPreferences mPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_sign_up_page);
        sp = getSharedPreferences("login",MODE_PRIVATE);
        mSp = MySharedPreferences.getInstance(this);
        mPrefs = getSharedPreferences("User", MODE_PRIVATE);

        signupFirstName_text=findViewById(R.id.signupFirstName_text);
        signupLastName_text=findViewById(R.id.signupLastName_text);
        signup_email_text=findViewById(R.id.signup_email_text);
        signupUserName_text=findViewById(R.id.signupUserName_text);
        signupPassword_text=findViewById(R.id.signupPassword_text);
        signupConfirmPassword_text=findViewById(R.id.signupConfirmPassword_text);
        social_submit=findViewById(R.id.social_submit_next);

        social_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(signupFirstName_text.getText().toString())) {
                    signupFirstName_text.setError("first name is required.");
                }else if(TextUtils.isEmpty(signupLastName_text.getText().toString())) {
                    signupLastName_text.setError("last name is required.");
                }else if(TextUtils.isEmpty(signup_email_text.getText().toString())) {
                    signup_email_text.setError("email is required.");
                }else if(TextUtils.isEmpty(signupUserName_text.getText().toString())){
                    signupUserName_text.setError("username is required.");
                }else if(TextUtils.isEmpty(signupPassword_text.getText().toString())){
                    signupPassword_text.setError("password is required.");
                }else if(TextUtils.isEmpty(signupConfirmPassword_text.getText().toString())){
                    signupConfirmPassword_text.setError("confirm password is required.");
                }else {
                    SubmitSocialSignUpFromToServer();
                }


//                if(getIntent()!=null && getIntent().hasExtra(BundleConstants.USER_TYPE)){
//                    if(getIntent().getStringExtra(BundleConstants.USER_TYPE).equals("work_individual")){
//
//
//                    }
//                    if(getIntent().getStringExtra(BundleConstants.USER_TYPE).equals("")){
//
//                    }
//                    if(getIntent().getStringExtra(BundleConstants.USER_TYPE).equals("")){
//
//                    }
//                }

            }
        });





    }

    private void SubmitSocialSignUpFromToServer() {
        RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();
        StringRequest myReq = new StringRequest(Request.Method.POST, "https://sqrfactor.com/api/add_account_details",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("signupApiReponse", response);
                        Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                                sp.edit().putBoolean("logged", true).apply();
                            }

                                UserClass userClass = new UserClass(jsonObject);
                                // notification listner for like and comment
                                FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications" + userClass.getUserId());
                                FirebaseMessaging.getInstance().subscribeToTopic("chats"+userClass.getUserId());


                                mSp.setKey(SPConstants.USER_ID, String.valueOf(userClass.getUserId()));
                                mSp.setKey(SPConstants.PROFILE_URL, userClass.getProfile());
                                mSp.setKey(SPConstants.EMAIL, userClass.getEmail());
                                mSp.setKey(SPConstants.USER_TYPE,userClass.getUserType());
                                mSp.setKey(SPConstants.NAME, UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));


                                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                Gson gson = new Gson();
                                String json = gson.toJson(userClass);
                                prefsEditor.putString("MyObject", json);
                                prefsEditor.commit();
                                //editor.commit();

                                Intent i = new Intent(getApplicationContext(), HomeScreen.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Authorization",  Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
//                params.put("Authorization",  "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjI3MDZiYmQ5MTllMzYwNGY0ODUyNTczMzQ0MTkwNDNmNjliNGYyZmRmZWUyNGNiZDQ1MDA3NzMxY2ZmNjcyMmVmZGI3MGUyNThjYzdmM2JiIn0.eyJhdWQiOiIxIiwianRpIjoiMjcwNmJiZDkxOWUzNjA0ZjQ4NTI1NzMzNDQxOTA0M2Y2OWI0ZjJmZGZlZTI0Y2JkNDUwMDc3MzFjZmY2NzIyZWZkYjcwZTI1OGNjN2YzYmIiLCJpYXQiOjE1NTIxMjQ1NzUsIm5iZiI6MTU1MjEyNDU3NSwiZXhwIjoxNTgzNzQ2OTc1LCJzdWIiOiIxNzU0NiIsInNjb3BlcyI6W119.M39LmFjfqZ_zhYtJ9Wpr_jZ3-Cmys1edErgJU6aDEpjjMESM1_UdBEUweadvY173PkjsG_o4DNVG2j5xKycA_JPrKn3jFx3tnXWX6YKFrNxA4DFGpAXpUHfqlbYCu2GSBvOWHnnCPBYjAZs6ToEmOUYHWtMmqvsdHql5izXDRY2OdMQXSYRye0icavJKEQvIr1AbvBNBURSGeks_pMmEEpPmAcWU0sXPi0ABYOIIiiTBXTsKrObXtxcGHymxkrXM9NCett5z1eFacoF4lbjCMbIbsV03AdxRmf-7sD43SHJTjVKyER1cEJ3Z_w9XbzfP0KtRZtb9U1goshqBdbAI3crFyxkHhaqWk8NXA-2vG_bzbfYFBnfaWei7NOO-8KyBql_aqnY0h1d5XcRhClBjUPrXbYYdpDLuehZX9NjlQ9FOJ9gkZFCxQPPJHH1xb9HP-3MUQ9O62r0HDYJ95__k1ZN0N0xKYZTDNaAgjeB7Zh299Hhex8mlrh4XVUtuicCYZrKZe_QydtrmkngdBH7FkBnBL9oByhBdzCbb-zVFsal_zDrTK14TQAsZFz4K9UC67HtSV0Q2AoauqS2e2_N2XCiCmX1JsZ1hi86aF4tBsZZZjT_trRCgsZW6rhYGU0LRYotJfEenN2BuTb9DPYV4UM9UjThoSGZTLdpey5DQCf4");
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("first_name",signupFirstName_text.getText().toString());
                params.put("last_name",signupLastName_text.getText().toString());
                params.put("email",signup_email_text.getText().toString());
                params.put("user_name",signupUserName_text.getText().toString());
                params.put("password",signupPassword_text.getText().toString());
                params.put("password_confirmation",signupConfirmPassword_text.getText().toString());
                //(post,mobile_number,user_type)
                return params;
            }

//+918876739082
        };

        requestQueue.add(myReq);


    }
}
