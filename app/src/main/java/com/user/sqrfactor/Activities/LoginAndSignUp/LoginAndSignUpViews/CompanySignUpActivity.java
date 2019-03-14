package com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpViews;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.user.sqrfactor.Activities.LoginAndSignUpWithPhoneNumber;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class CompanySignUpActivity extends AppCompatActivity {

   // private OnCompanySignUpFragmentInteractionListener mListener;

    private EditText signupCompanyUsename_text,signupCompanyName_text,signup_email_text,signup_password_text,signup_confirm_password_text;
    private Button next;
    private Context context;
    private CheckBox checkb;
    private SharedPreferences sp;
    private MySharedPreferences mSp;
    private SharedPreferences.Editor editor;
    private Button social_submit;
    private SharedPreferences mPrefs;
    public CompanySignUpActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_signup);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        mSp = MySharedPreferences.getInstance(this);
        mPrefs = getSharedPreferences("User", MODE_PRIVATE);


        SharedPreferences sharedPref = getSharedPreferences("PREF_NAME",MODE_PRIVATE);
        editor = sharedPref.edit();

        checkb=findViewById(R.id.checkb);
        signupCompanyName_text=findViewById(R.id.signupCompanyName_text);
        signupCompanyUsename_text=findViewById(R.id.signupCompanyUsename_text);
        signup_email_text=findViewById(R.id.signup_email_text);
        signup_password_text=findViewById(R.id.signup_password_text);
        signup_confirm_password_text=findViewById(R.id.signup_confirm_password_text);
        next=findViewById(R.id.company_next);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                SendCompanySignUpDataToServer();

                if(TextUtils.isEmpty(signupCompanyName_text.getText().toString())) {
                    signupCompanyName_text.setError("company name is required.");
                }
                else if(TextUtils.isEmpty(signupCompanyUsename_text.getText().toString())) {
                    signupCompanyUsename_text.setError("username is required.");
                }
                else if(TextUtils.isEmpty(signup_email_text.getText().toString())) {
                    signup_email_text.setError("email is required.");
                }
                else if(TextUtils.isEmpty(signup_password_text.getText().toString())) {
                    signup_password_text.setError("paswword is required.");
                }else if(TextUtils.isEmpty(signup_confirm_password_text.getText().toString())){
                    signup_confirm_password_text.setError("confirm password is required.");
                }else if(!signup_confirm_password_text.getText().toString().equals(signup_password_text.getText().toString())){
                    signup_confirm_password_text.setError("incorrect password");
                }else {

                    SendCompanySignUpDataToServer();

//                    Intent intent=new Intent(context, LoginAndSignUpWithPhoneNumber.class);
//                    intent.putExtra(Constants.SIGNUP_COMPANYNAME,signupCompanyName_text.getText().toString());
//                    // intent.putExtra(Constants.SIGNUP_LASTNAME,signup_email_text.getText().toString());
//                    intent.putExtra(Constants.SIGNUP_COMPANY_EMAIL,signup_email_text.getText().toString());
//                    intent.putExtra(Constants.SIGNUP_COMPANY_PASSWORD,signup_password_text.getText().toString());
//                    intent.putExtra(Constants.SIGNUP_COMPANY_CONFIRMPASSWORD,signup_confirm_password_text.getText().toString());
//                    // intent.putExtra("SignUp",true);
//                    context.startActivity(intent);
                }
            }
        });

        signup_confirm_password_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0 && signup_password_text.getText().toString().length() > 0) {
                    if(signup_confirm_password_text.getText().toString().equals(signup_password_text.getText().toString() )){
                        checkb.setChecked(true);
                    }else {
                        checkb.setChecked(false);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String passwrd = signup_password_text.getText().toString();
                if (editable.length() > 0 && passwrd.length() > 0) {
                    if(!signup_confirm_password_text.getText().toString().equals(passwrd )){
                        // give an error that password and confirm password not match
                    }

                }
            }
        });


    }

    private void SendCompanySignUpDataToServer() {

        final ProgressDialog progressDialog=ShowProgressDialog();
        progressDialog.show();

        //  Toast.makeText(getApplicationContext(),"calling server company", Toast.LENGTH_LONG).show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest myReq = new StringRequest(Request.Method.POST, "https://sqrfactor.com/api/register_with_emailid_others",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("signupApiReponseCompany", response);
                        Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            progressDialog.dismiss();

//                            if(FirebaseAuth.getInstance().getCurrentUser()!=null){
//                                sp.edit().putBoolean("logged", true).apply();
//                            }

//                            UserClass userClass = new UserClass(jsonObject);
//                            // notification listner for like and comment
//                            FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications" + userClass.getUserId());
//                            FirebaseMessaging.getInstance().subscribeToTopic("chats"+userClass.getUserId());
//
//
//                            JSONObject tokenObject = jsonObject.getJSONObject("success");
//                            String token = tokenObject.getString("token");
//                            editor.putString("TOKEN", token);
//
//
//                            mSp.setKey(SPConstants.API_KEY, token);
//                            //add token here also
//                            mSp.setKey(SPConstants.USER_ID, String.valueOf(userClass.getUserId()));
//                            mSp.setKey(SPConstants.PROFILE_URL, userClass.getProfile());
//                            mSp.setKey(SPConstants.EMAIL, userClass.getEmail());
//                            mSp.setKey(SPConstants.USER_TYPE,userClass.getUserType());
//                            mSp.setKey(SPConstants.NAME, UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
//
//
//                            SharedPreferences.Editor prefsEditor = mPrefs.edit();
//                            Gson gson = new Gson();
//                            String json = gson.toJson(userClass);
//                            prefsEditor.putString("MyObject", json);
//                            prefsEditor.commit();
//                            //editor.commit();
//
//                            Intent i = new Intent(getApplicationContext(), LoginAndSignUpWithPhoneNumber.class);
//                            i.putExtra("CompanySignUpActivity",true);
//                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(i);
//                            finish();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(context, "timeout",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(context, "auth failure",
                                    Toast.LENGTH_LONG).show();
                            //TODO
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(context, "server error",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            //TODO
                            Toast.makeText(context, "network error",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(context, "parse error",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                // params.put("Authorization",  Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
//                params.put("Authorization",  "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjI3MDZiYmQ5MTllMzYwNGY0ODUyNTczMzQ0MTkwNDNmNjliNGYyZmRmZWUyNGNiZDQ1MDA3NzMxY2ZmNjcyMmVmZGI3MGUyNThjYzdmM2JiIn0.eyJhdWQiOiIxIiwianRpIjoiMjcwNmJiZDkxOWUzNjA0ZjQ4NTI1NzMzNDQxOTA0M2Y2OWI0ZjJmZGZlZTI0Y2JkNDUwMDc3MzFjZmY2NzIyZWZkYjcwZTI1OGNjN2YzYmIiLCJpYXQiOjE1NTIxMjQ1NzUsIm5iZiI6MTU1MjEyNDU3NSwiZXhwIjoxNTgzNzQ2OTc1LCJzdWIiOiIxNzU0NiIsInNjb3BlcyI6W119.M39LmFjfqZ_zhYtJ9Wpr_jZ3-Cmys1edErgJU6aDEpjjMESM1_UdBEUweadvY173PkjsG_o4DNVG2j5xKycA_JPrKn3jFx3tnXWX6YKFrNxA4DFGpAXpUHfqlbYCu2GSBvOWHnnCPBYjAZs6ToEmOUYHWtMmqvsdHql5izXDRY2OdMQXSYRye0icavJKEQvIr1AbvBNBURSGeks_pMmEEpPmAcWU0sXPi0ABYOIIiiTBXTsKrObXtxcGHymxkrXM9NCett5z1eFacoF4lbjCMbIbsV03AdxRmf-7sD43SHJTjVKyER1cEJ3Z_w9XbzfP0KtRZtb9U1goshqBdbAI3crFyxkHhaqWk8NXA-2vG_bzbfYFBnfaWei7NOO-8KyBql_aqnY0h1d5XcRhClBjUPrXbYYdpDLuehZX9NjlQ9FOJ9gkZFCxQPPJHH1xb9HP-3MUQ9O62r0HDYJ95__k1ZN0N0xKYZTDNaAgjeB7Zh299Hhex8mlrh4XVUtuicCYZrKZe_QydtrmkngdBH7FkBnBL9oByhBdzCbb-zVFsal_zDrTK14TQAsZFz4K9UC67HtSV0Q2AoauqS2e2_N2XCiCmX1JsZ1hi86aF4tBsZZZjT_trRCgsZW6rhYGU0LRYotJfEenN2BuTb9DPYV4UM9UjThoSGZTLdpey5DQCf4");
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                params.put("user_name",signupCompanyUsename_text.getText().toString());
                params.put("name",signupCompanyName_text.getText().toString());

                params.put("email",signup_email_text.getText().toString());
                params.put("user_type","work_architecture_firm_company");
                params.put("password",signup_password_text.getText().toString());
                params.put("password_confirmation",signup_confirm_password_text.getText().toString());
                //(post,mobile_number,user_type)
                return params;
            }

//+918876739082
        };

        requestQueue.add(myReq);


    }

    private ProgressDialog ShowProgressDialog(){
        ProgressDialog dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

}
