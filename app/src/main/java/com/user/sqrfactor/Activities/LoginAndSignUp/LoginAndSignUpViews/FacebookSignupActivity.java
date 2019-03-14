package com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpViews;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.user.sqrfactor.Activities.Design2Activity;
import com.user.sqrfactor.Activities.HomeScreen;
import com.user.sqrfactor.Activities.LoginAndSignUpWithPhoneNumber;
import com.user.sqrfactor.Activities.SocialFormActivity;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Fragments.MDToast;
import com.user.sqrfactor.Pojo.TokenClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FacebookSignupActivity extends Activity {

    private ImageView user_image;
    private EditText signupFirstName_text,signupLastName_text,signup_email_text,signup_username_text;
    private Button facebook_signup_activity_next;
    private String signup_facebok_number;
    private MySharedPreferences mSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_signup);

        mSp = MySharedPreferences.getInstance(this);
        user_image=findViewById(R.id.user_image);
        signupFirstName_text=findViewById(R.id.signupFirstName_text);
        signupLastName_text=findViewById(R.id.signupLastName_text);
        signup_email_text=findViewById(R.id.signup_email_text);
        signup_username_text=findViewById(R.id.signup_username_text);
        facebook_signup_activity_next=findViewById(R.id.facebook_signup_activity_next);
        facebook_signup_activity_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(TextUtils.isEmpty(signupFirstName_text.getText().toString())){
                    signupFirstName_text.setError("first name requird");

                }else if(TextUtils.isEmpty(signupLastName_text.getText().toString())){
                    signupLastName_text.setError("last name requird");

                }else if(TextUtils.isEmpty(signup_email_text.getText().toString())){
                    signup_email_text.setError("email requird");
                }else if(TextUtils.isEmpty(signup_username_text.getText().toString())){
                    signup_username_text.setError("username requird");
                }
                else {
                    CallServerForUpdatingFacebookData();

                }



            }
        });

        if(getIntent()!=null){
            UpdateUI(getIntent());
        }


    }

    private void CallServerForUpdatingFacebookData() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest myReq = new StringRequest(Request.Method.POST, "https://sqrfactor.com/api/updatesocialreg",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        Log.v("Reponse facebook", response);

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            if(jsonObject.has("profile_data")){

                                SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
                                Gson gson = new Gson();
                                String json = mPrefs.getString("MyObject", "");
                                UserClass userClass = gson.fromJson(json, UserClass.class);
                                TokenClass.Token=mSp.getKey(SPConstants.API_KEY);

                                JSONObject profile_data=jsonObject.getJSONObject("profile_data");
                                userClass.setProfile(profile_data.getString("profile"));
                                userClass.setUser_name(profile_data.getString("user_name"));
                                userClass.setFirst_name(profile_data.getString("first_name"));
                                userClass.setLast_name(profile_data.getString("last_name"));
                                userClass.setEmail(profile_data.getString("email"));
                                userClass.setUserType("work_individual");


                                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                json = gson.toJson(userClass);
                                prefsEditor.putString("MyObject", json);
                                prefsEditor.apply();


// start mobile verification process after successfule facebook signup
                               Intent intent=new Intent(getApplicationContext(), LoginAndSignUpWithPhoneNumber.class);
                               intent.putExtra("signup_facebok_number",signup_facebok_number);
                               intent.putExtra("FacebookSignupActivity",true);
                               startActivity(intent);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        Log.v("token on signup",TokenClass.Token);
//                        Toast.makeText(getApplicationContext(),TokenClass.Token,Toast.LENGTH_LONG).show();





                    }},
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
//                    MDToast.makeText(SocialFormActivity.this, "Your Mobile No. or Usename should be unique", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                Log.v("Token",Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
                params.put("Accept", "application/json");

                  //params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
                  params.put("Authorization", TokenClass.Token);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("first_name",signupFirstName_text.getText().toString());
                params.put("last_mname",signupLastName_text.getText().toString());
                params.put("email",signup_email_text.getText().toString());
                params.put("user_name",signup_username_text.getText().toString());


                return params;
            }


        };

        requestQueue.add(myReq);
    }

    private void UpdateUI(Intent intent) {

        Glide.with(this).load(UtilsClass.getParsedImageUrl(intent.getStringExtra("user_image")))
                .into(user_image);

//
//        ArrayList<Uri> fileUriList = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
//        Log.e("urilist", fileUriList.toString());

        String[] parsedName=intent.getStringExtra("signupFirstName_text").split(" ");
        if(parsedName.length>1){
            signupFirstName_text.setText(parsedName[0]);
            signupLastName_text.setText(parsedName[1]);
        }

        signup_email_text.setText(intent.getStringExtra("signup_email_text"));
        signup_facebok_number=intent.getStringExtra("signup_facebok_number");


    }

}
