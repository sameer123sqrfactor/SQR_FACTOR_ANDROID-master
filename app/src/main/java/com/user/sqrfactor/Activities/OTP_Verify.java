package com.user.sqrfactor.Activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alahammad.otp_view.OTPListener;
import com.alahammad.otp_view.OtpView;
import com.alahammad.otp_view.smsCatcher.OnSmsCatchListener;
import com.alahammad.otp_view.smsCatcher.SmsVerifyCatcher;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpViews.SocialSignUpPageActivity;
import com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpViews.UploadProfileImageActivity;
import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Constants.BundleConstants;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Fragments.MDToast;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.IsOnline;
import com.user.sqrfactor.Pojo.TokenClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Utils.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class OTP_Verify extends AppCompatActivity implements OTPListener, OnSmsCatchListener<String> {
    private OtpView otpView;
    private Button validate,skip;
    private String otp;
    private TextView textMobile;
    private SmsVerifyCatcher smsVerifyCatcher;
    private TextView resendOTP;
    private UserClass userClass;
    private SharedPreferences mPrefs;
    private String COUNTRY_ID,USERTYPE,mobile,countryCode,userType=null;
    private SharedPreferences.Editor editor;
    private String mVerificationId;
    private TextView otpTime;
    private FirebaseAuth mAuth;
    private boolean isLoginWithPhone=false,indSignUpwithmobile=false,IndiSignupWithEmail=false,
            CompanySignUpActivity=false,OnCollegeSignUp=false,OrganisationSignup=false,FacebookSignupActivity=false;
    public static DatabaseReference ref;
    public static FirebaseDatabase database;
    private SharedPreferences sp;
    private Spinner userTypeSpinner;
    private MySharedPreferences mSp;
    private PhoneAuthProvider.ForceResendingToken resendCode;
    private ProgressBar otp_progress_bar;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp__verifiy);


        //google anlytics
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();


//        if (checkAndRequestPermissions()) {
            mPrefs = getSharedPreferences("User", MODE_PRIVATE);
            Gson gson = new Gson();
            mAuth = FirebaseAuth.getInstance();
            SharedPreferences sharedPref = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
            editor = sharedPref.edit();
            mSp = MySharedPreferences.getInstance(this);
            sp = getSharedPreferences("login",MODE_PRIVATE);




            String json = mPrefs.getString("MyObject", "");
            userClass = gson.fromJson(json, UserClass.class);
            database= FirebaseDatabase.getInstance();
            ref = database.getReference();
            smsVerifyCatcher = new SmsVerifyCatcher(this, this);

            SharedPreferences sharedPreferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
            String token = sharedPreferences.getString("TOKEN", "sqr");
            TokenClass.Token = mSp.getKey(SPConstants.API_KEY);
            TokenClass tokenClass = new TokenClass(token);
            Log.v("Token1", token);

            otp_progress_bar=findViewById(R.id.otp_progress_bar);
            textMobile=(TextView)findViewById(R.id.textmobile);
            validate = (Button)findViewById(R.id.validate_button);
            skip = findViewById(R.id.skip_button);
            otpView = (OtpView) findViewById(R.id.otpview);
            resendOTP = (TextView) findViewById(R.id.resend_otp);
            if(getIntent()!=null) {
                if (getIntent().hasExtra(BundleConstants.LOGIN_WITH_MOBILE_EBALED)) {
                    isLoginWithPhone = true;
                    PrepareAndCallFirebaseServer();

                } else if (getIntent().hasExtra(BundleConstants.SIGN_TYPE)) {
                    if (getIntent().getStringExtra(BundleConstants.SIGN_TYPE).equals("indSignUpwithmobile"))
                    //  Toast.makeText(getApplicationContext(),"indSignUpwithmobile",Toast.LENGTH_LONG).show();
                    {
                        indSignUpwithmobile = true;
                        PrepareAndCallFirebaseServer();
                    } else if (getIntent().getStringExtra(BundleConstants.SIGN_TYPE).equals("IndiSignupWithEmail")) {
                        IndiSignupWithEmail = true;
                        //  Toast.makeText(getApplicationContext(),"IndiSignupWithEmail",Toast.LENGTH_LONG).show();
                        PrepareAndCallFirebaseServer();
                    } else if (getIntent().getStringExtra(BundleConstants.SIGN_TYPE).equals("CompanySignUpActivity")) {
                        // Toast.makeText(getApplicationContext(),"CompanySignUpActivity",Toast.LENGTH_LONG).show();
                        CompanySignUpActivity = true;
                        PrepareAndCallFirebaseServer();
                    } else if (getIntent().getStringExtra(BundleConstants.SIGN_TYPE).equals("OnCollegeSignUp")) {
                        //  Toast.makeText(getApplicationContext(),"OnCollegeSignUp",Toast.LENGTH_LONG).show();
                        OnCollegeSignUp = true;
                        PrepareAndCallFirebaseServer();
                    } else if (getIntent().getStringExtra(BundleConstants.SIGN_TYPE).equals("OrganisationSignup")) {
                        // Toast.makeText(getApplicationContext(),"OrganisationSignup",Toast.LENGTH_LONG).show();
                        OrganisationSignup = true;
                        PrepareAndCallFirebaseServer();
                    } else if (getIntent().getStringExtra(BundleConstants.SIGN_TYPE).equals("FacebookSignupActivity")) {
                        // Toast.makeText(getApplicationContext(),"OrganisationSignup",Toast.LENGTH_LONG).show();
                        FacebookSignupActivity = true;
                        PrepareAndCallFirebaseServer();
                    }
                    mobile = getIntent().getStringExtra("mobile");
                    skip.setVisibility(View.VISIBLE);
                }

                textMobile.setText("Please type the verification code sent to " + mobile);
            }
//                }else if(getIntent().hasExtra(BundleConstants.SIGN_TYPE)){
//
//
//
//                }else if(getIntent().hasExtra(BundleConstants.SIGN_TYPE)){
//
//
//
//                }else if(getIntent().hasExtra(BundleConstants.SIGN_TYPE)){
//
//
//                }else if(getIntent().hasExtra(BundleConstants.SIGN_TYPE)){
//
//
//                }
//                else {
//
//            }

            resendOTP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isLoginWithPhone ||indSignUpwithmobile){
                        ResendCode();
                    }else {
                        ResendOTP();
                    }

                }
            });

            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OTP_Verify.this,HomeScreen.class);
                    startActivity(intent);
                }
            });


//           otpView.setPinViewEventListener(new Pinview.PinViewEventListener() {
//                @Override
//                public void onDataEntered(Pinview pinview, boolean fromUser) {
////                Toast.makeText(OTP_Verify.this, pinview.getValue(), Toast.LENGTH_SHORT).show();
//                    otp = otpView.getOTP();
//                }
//            });
        otpTime = (TextView)findViewById(R.id.otpTime);
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                otpTime.setText("Please wait   " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                otpTime.setText("done!");
            }

        }.start();



            validate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    otp = otpView.getOTP();
                    Log.v("otp",otp);
//                    Toast.makeText(getApplicationContext(),
//                            "Login Successfull"+otp, Toast.LENGTH_LONG).show();
                    validate.setEnabled(false);
                    otp_progress_bar.setVisibility(View.VISIBLE);
                    verifySignInCode(otp);
//                    if(indSignUpwithmobile||isLoginWithPhone){
//
//                    }
//                    }else {
//                        ValidateCodeWithServer();
//                    }

                }
            });


        }

//    }


    private void PrepareAndCallFirebaseServer(){
        COUNTRY_ID=getIntent().getStringExtra(BundleConstants.COUNTRY_CODE);
        mobile=getIntent().getStringExtra(BundleConstants.PHONE_NUMBER);
        countryCode=getIntent().getStringExtra(BundleConstants.COUNTRY_CODE_PHONE);
        PrepareRequest();
    }
    private void ValidateCodeWithServer() {
       // RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();

        StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"verify_otp",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("Reponse", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            validate.setEnabled(true);
                            Intent i = new Intent(getApplicationContext(), HomeScreen.class);
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
                params.put("Authorization", "Bearer " + TokenClass.Token);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("otp",otp);
                return params;
            }
        };

        requestQueue.add(myReq);

    }

    @Override
    public void otpFinished(String otp) {

    }

    @Override
    public void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onSmsCatch(String message) {
        if(message!=null && !TextUtils.isEmpty(message))
        otpView.setOTP(message);
    }

    private void PrepareRequest(){
        skip.setVisibility(View.GONE);
        otpView.setFocusable(false);
        sendVerificationCode(countryCode,mobile);

    }
    private void sendVerificationCode(String countryCode,String mobile) {
        SetUpVerificationCallback();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                countryCode + mobile,
                20,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }
    public void ResendCode() {
        String phoneNumber = mobile;
        SetUpVerificationCallback();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                countryCode+phoneNumber,
                20,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                resendCode);
    }

    private void SetUpVerificationCallback()
    {
         mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
             @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();

                //sometime the code is not detected automatically
                //in this case the code will be null
                //so user has to manually enter the code
                if (code != null) {
                    otpView.setOTP(code);
                    //verifying the code
                    validate.setEnabled(true);
                    otp_progress_bar.setVisibility(View.VISIBLE);
                    verifySignInCode(code);
                }

            }
            @Override
            public void onVerificationFailed(FirebaseException e) {

                if(e instanceof FirebaseAuthInvalidCredentialsException){
//                    Toast.makeText(getApplicationContext(),
//                            "error"+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                if(e instanceof FirebaseTooManyRequestsException){
//                    Toast.makeText(getApplicationContext(),
//                            "sms quota exceeded", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCodeSent(String codeFromServer, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(codeFromServer, forceResendingToken);
                mVerificationId = codeFromServer;
                resendCode=forceResendingToken;

            }
        };
    }




    private void verifySignInCode(String code){

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }


    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //here you can open new activity
                            Toast.makeText(getApplicationContext(),
                                    "Login Successfull"+credential.getSmsCode(), Toast.LENGTH_LONG).show();
                            if(isLoginWithPhone){
                                CallLoginApiAfterSucess();
                            }else if(indSignUpwithmobile) {
                                CallIndSignUpWithMobileApi();
                            }else if(IndiSignupWithEmail){
                                goToHomeScreen();
                            }else if(CompanySignUpActivity){
                                goToHomeScreen();
                            }else if(OnCollegeSignUp){
                                goToHomeScreen();
                            }else if(OrganisationSignup){
                                goToHomeScreen();
                            }else if(FacebookSignupActivity){
                                goToHomeScreen();
                            }




                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),
                                        "Incorrect Verification Code ", Toast.LENGTH_LONG).show();
                                otp_progress_bar.setVisibility(View.GONE);
                            }
                        }
                    }
                });

    }

    public void goToHomeScreen(){

        CallServerForMobileNumberValAfterFirebaseOtpSucess();
//        Intent i = new Intent(getApplicationContext(), UploadProfileImageActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(i);
//        finish();
    }

    private void CallIndSignUpWithMobileApi() {
        RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();
        StringRequest myReq = new StringRequest(Request.Method.POST, "https://sqrfactor.com/api/register_with_mobile_firebase",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("signupApiReponse", response);
                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        try {

                            if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                                sp.edit().putBoolean("logged", true).apply();
                            }
////                            validate.setEnabled(true);
//                            otp_progress_bar.setVisibility(View.GONE);
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getString("successRegistration")==1+""){
                                JSONObject tokenObject = jsonObject.getJSONObject("success");
                                String token = tokenObject.getString("token");
                                editor.putString("TOKEN", token);
                                mSp.setKey(SPConstants.API_KEY, token);
                                Intent i = new Intent(getApplicationContext(), SocialSignUpPageActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.putExtra(BundleConstants.USER_TYPE,"work_individual");
                                startActivity(i);
                                finish();
                            }else {
                                MDToast.makeText(getApplicationContext(), "Error in SignUp", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                            }



                        }
                        catch (JSONException e) {
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
               // params.put("Authorization",  Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile_number",mobile);
                params.put("user_type",userType);
                params.put("country_id",COUNTRY_ID+"");
                //(post,mobile_number,user_type)
                return params;
            }

//+918876739082
        };

        requestQueue.add(myReq);


    }

//
//    private void showCommentDeleteDialog() {
//        final String[] item = {"Individual","Architecture firm companies","Architecture Organization","Architecture College"};
//        AlertDialog.Builder b = new AlertDialog.Builder(this);
//        b.setTitle("User not found. To continue select user type");
//
//        b.setItems(item, new DialogInterface.OnClickListener(){
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                dialog.dismiss();
//                switch(which){
//                    case 0:
//                        userType = "work_individual";
//                        CallSignUpApiAfterSucess();
//
//                        break;
//                    case 1:
//                        userType = "work_architecture_firm_companies";
//                        CallSignUpApiAfterSucess();
//                        break;
//                        case 2:
//                            userType = "work_architecture_firm_organization";
//                            CallSignUpApiAfterSucess();
//                        break;
//                        case 3:
//                            userType = "work_architecture_firm_college";
//                            CallSignUpApiAfterSucess();
//                        break;
//                }
//            }
//
//        });
//
//        b.show();
//
//    }


    private void CallServerForMobileNumberValAfterFirebaseOtpSucess(){
        RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();
        StringRequest myReq = new StringRequest(Request.Method.POST, "https://sqrfactor.com/api/add_mobile_for_validation",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("signupApiReponse", response);
                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        try {

                            validate.setEnabled(true);
                            otp_progress_bar.setVisibility(View.GONE);
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getString("message").equals("Mobile Number and Country added successfully")){
                                Intent i = new Intent(getApplicationContext(), UploadProfileImageActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                            }else {
                                MDToast.makeText(getApplicationContext(), "Somethisng went wrong.Please try again", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                            }



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
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile_number",mobile);
                params.put("country_id",COUNTRY_ID);
                //(post,mobile_number,user_type)
                return params;
            }

//+918876739082
        };

        requestQueue.add(myReq);



    }



    private void CallSignUpApiAfterSucess() {

     //   RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();
        StringRequest myReq = new StringRequest(Request.Method.POST, ServerConstants.SIGNUP_API_FOR_MOBILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("signupApiReponse", response);
//                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        try {

                            validate.setEnabled(true);
                            otp_progress_bar.setVisibility(View.GONE);
                            JSONObject jsonObject = new JSONObject(response);

                            if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                                sp.edit().putBoolean("logged", true).apply();
                            }
                            if(jsonObject.has("status") && jsonObject.getString("status").equals("New user added")){

                                JSONObject TokenObject = jsonObject.getJSONObject("success");
                                String Token = TokenObject.getString("token");
                                editor.putString("TOKEN", Token);

                                mSp.setKey(SPConstants.API_KEY, Token);

                                Intent intent=new Intent(OTP_Verify.this,SocialFormActivity.class);
                                intent.putExtra(BundleConstants.SIGNUP_WITH_MOBILE_EBALED,true);
                                startActivity(intent);

                            }else {
                                UserClass userClass = new UserClass(jsonObject);
                                // notification listner for like and comment
                                FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications" + userClass.getUserId());
                                FirebaseMessaging.getInstance().subscribeToTopic("chats"+userClass.getUserId());
                                //code for user status
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = new Date();

                                IsOnline isOnline=new IsOnline("True",formatter.format(date));
                                ref.child("Status").child(userClass.getUserId()+"").child("android").setValue(isOnline);
                                IsOnline isOnline1=new IsOnline("False",formatter.format(date));
                                ref.child("Status").child(userClass.getUserId()+"").child("web").setValue(isOnline1);

                                JSONObject TokenObject = jsonObject.getJSONObject("success");
                                String Token = TokenObject.getString("token");
                                editor.putString("TOKEN", Token);

                                mSp.setKey(SPConstants.API_KEY, Token);
                                mSp.setKey(SPConstants.USER_ID, String.valueOf(userClass.getUserId()));
                                mSp.setKey(SPConstants.PROFILE_URL, userClass.getProfile());
                                mSp.setKey(SPConstants.EMAIL, userClass.getEmail());
                                mSp.setKey(SPConstants.USER_TYPE,userClass.getUserType());
                                mSp.setKey(SPConstants.NAME,UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));


                                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                Gson gson = new Gson();
                                String json = gson.toJson(userClass);
                                prefsEditor.putString("MyObject", json);
                                prefsEditor.commit();
                                editor.commit();

                                Intent i = new Intent(getApplicationContext(), HomeScreen.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();

                            }
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
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile_number",mobile);
                params.put("user_type",userType);
                params.put("country_id",COUNTRY_ID);
                //(post,mobile_number,user_type)
                return params;
            }

//+918876739082
        };

        requestQueue.add(myReq);

    }

    private void CallLoginApiAfterSucess() {
      //  RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();
        StringRequest myReq = new StringRequest(Request.Method.POST, ServerConstants.LOGIN_API_FOR_MOBILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("Reponse", response);
                        try {
                            validate.setEnabled(true);
                            otp_progress_bar.setVisibility(View.GONE);
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("message")){
                               // showCommentDeleteDialog();

                            }else {
                                UserClass userClass = new UserClass(jsonObject);
                                // notification listner for like and comment
                                FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications" + userClass.getUserId());
                                FirebaseMessaging.getInstance().subscribeToTopic("chats"+userClass.getUserId());
                                //code for user status
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = new Date();

                                IsOnline isOnline=new IsOnline("True",formatter.format(date));
                                ref.child("Status").child(userClass.getUserId()+"").child("android").setValue(isOnline);
                                IsOnline isOnline1=new IsOnline("False",formatter.format(date));
                                ref.child("Status").child(userClass.getUserId()+"").child("web").setValue(isOnline1);

                                JSONObject TokenObject = jsonObject.getJSONObject("success");
                                String Token = TokenObject.getString("token");

                                if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                                    sp.edit().putBoolean("logged", true).apply();
                                }
                                editor.putString("TOKEN", Token);

                                mSp.setKey(SPConstants.API_KEY, Token);
                                mSp.setKey(SPConstants.USER_ID, String.valueOf(userClass.getUserId()));
                                mSp.setKey(SPConstants.PROFILE_URL, userClass.getProfile());
                                mSp.setKey(SPConstants.EMAIL, userClass.getEmail());
                                mSp.setKey(SPConstants.USER_TYPE,userClass.getUserType());
                                mSp.setKey(SPConstants.NAME,UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));


                                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                Gson gson = new Gson();
                                String json = gson.toJson(userClass);
                                prefsEditor.putString("MyObject", json);
                                prefsEditor.commit();
                                editor.commit();

                                Intent i = new Intent(getApplicationContext(), HomeScreen.class);
                                startActivity(i);
                                finish();


                            }
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
                params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("mobile_number",mobile);
                return params;
            }


        };

        requestQueue.add(myReq);

    }

    public void ResendOTP(){
      // RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();
       StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"resendotp",
               new Response.Listener<String>() {
                   @Override
                   public void onResponse(String response) {
                       Log.v("Reponse", response);
                       try {
                           JSONObject jsonObject = new JSONObject(response);
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
               params.put("Authorization", "Bearer " + TokenClass.Token);
               return params;
           }

           @Override
           protected Map<String, String> getParams() {
               Map<String, String> params = new HashMap<String, String>();
               return params;
           }


       };

       requestQueue.add(myReq);
   }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");

//                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                //tv.setText(message);
            }
        }
    };

    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int receiveSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int readSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_MMS);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
        mTracker.setScreenName("OTP_Verify /");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }



}