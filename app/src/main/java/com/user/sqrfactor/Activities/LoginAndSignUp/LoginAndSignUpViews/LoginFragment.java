
package com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpViews;

import android.app.Activity;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.user.sqrfactor.Activities.HomeScreen;
import com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpContractInterface;
import com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpPresenter.LoginPresenter;
import com.user.sqrfactor.Activities.LoginAndSignUpWithPhoneNumber;
import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Fragments.MDToast;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.IsOnline;
import com.user.sqrfactor.Pojo.TokenClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Activities.ResetPassword;
import com.user.sqrfactor.Activities.SocialFormActivity;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.android.volley.VolleyLog.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;

public class LoginFragment extends Fragment implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener, LoginAndSignUpContractInterface.loginView {

    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean isPasswordVisible=false;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    private ImageButton showPasswordIcon;
    private Boolean saveLogin;
    private String username, password;
    private String firstNameText="",lastNameText="";
    private Button login,email_login;
    private TextView forgot,info;
    private EditText loginEmail, loginPassword;
    private CheckBox loginRemberMe;
    private SharedPreferences.Editor editor;
    private SharedPreferences mPrefs;
    private  FirebaseDatabase database;
    private  DatabaseReference ref;

    private LoginButton facebookLoagin;

    private Button loginwithmobile;
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton login_eith_gmail_btn;
    private AccessToken accessToken;
    private SharedPreferences sp;
    private MySharedPreferences mSp;
    private LinearLayout login_with_email_view,login_with_mobile_view;


    private GoogleSignInClient mGoogleSignInClient;
    private Button fb_Button,Google_Button;
    private Tracker mTracker;

    private Context context;
    private CallbackManager mCallbackManager;

    private LoginAndSignUpContractInterface.loginPresenter loginPresenter=new LoginPresenter(this);

    public LoginFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity());
        mCallbackManager= CallbackManager.Factory.create();
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        callbackManager = CallbackManager.Factory.create();

       // FacebookSdk.sdkInitialize(getActivity());



        //google analytics code
        MyApplication application = (MyApplication)getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_login, container, false);



        try {
            PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo(
                    "com.user.sqrfactor",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:login", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
       // loginPresenter=new LoginPresenter(this);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("PREF_NAME", getActivity().MODE_PRIVATE);
        editor = sharedPref.edit();
        //private SharedPreferences mPrefs;
        mPrefs = getActivity().getSharedPreferences("User", MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();

      //  mSp = MySharedPreferences.getInstance(getActivity());


        sp = getActivity().getSharedPreferences("login",MODE_PRIVATE);


        database= FirebaseDatabase.getInstance();
        ref = database.getReference();

        initView(rootView);
        return rootView;


    }

    private void initView(ViewGroup rootView) {
        login_with_email_view=(LinearLayout)rootView.findViewById(R.id.login_with_email_view);
       // login_with_mobile_view=(LinearLayout)rootView.findViewById(R.id.login_with_mobile_view);
        login = (Button) rootView.findViewById(R.id.login);
        loginwithmobile = (Button) rootView.findViewById(R.id.mobile_login);
       // fb_Button = (Button) rootView.findViewById(R.id.facebook_login);
        login_eith_gmail_btn=rootView.findViewById(R.id.loginwithgoogle);
        email_login = (Button) rootView.findViewById(R.id.email_login);


        forgot = (TextView) rootView.findViewById(R.id.forgot);
        loginEmail = (EditText) rootView.findViewById(R.id.loginEmail);
        loginPassword = (EditText) rootView.findViewById(R.id.loginPassword);
        showPasswordIcon=rootView.findViewById(R.id.showPasswordIcon);

        loginRemberMe = (CheckBox) rootView.findViewById(R.id.rememberMeLoginCheckBox);


        showPasswordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPasswordVisible) {
                    loginPassword.setTransformationMethod(null);
                    isPasswordVisible=true;

                }else {
                    loginPassword.setTransformationMethod(new PasswordTransformationMethod());
                    isPasswordVisible=false;
                }
            }
        });
        loginRemberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked ) {
                    // perform logic
                    loginPresenter.loginRememberBtnClicked(true);
                }else {
                    loginPresenter.loginRememberBtnClicked(false);
                }

            }
        });

        loginPreferences = getActivity().getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();


        saveLogin = loginPreferences.getBoolean("saveLogin", false);


        if (saveLogin == true) {
            loginEmail.setText(loginPreferences.getString("username", ""));
            loginPassword.setText(loginPreferences.getString("password", ""));
            loginRemberMe.setChecked(true);
        }

        email_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // loginwithmobile.setBackgroundColor(getResources().getColor(R.color.gray));
                //email_login.setBackgroundColor(getResources().getColor(R.color.sqr));
                login_with_email_view.setVisibility(View.VISIBLE);
                //login_with_mobile_view.setVisibility(View.GONE);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UtilsClass.IsConnected(context)) {
                    username = loginEmail.getText().toString();
                    password = loginPassword.getText().toString();
                    loginPresenter.manualLoginBtnClick(username,password);


                    sp.edit().putBoolean("logged", true).apply();

                }
                else {
                    MDToast.makeText(getActivity(), "Check Your Internet Connection", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                }

            }


        });


        loginwithmobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  loginwithmobile.setBackgroundColor(getResources().getColor(R.color.sqr));
                //email_login.setBackgroundColor(getResources().getColor(R.color.gray));
                loginPresenter.loginWithMobileBtnClick();
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginPresenter.forgetPasswordBtnClicked();

            }
        });

//        fb_Button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v == fb_Button) {
//                    if(UtilsClass.IsConnected(context)) {
//                        loginPresenter.loginWithFacebookBtnClick();
////                        facebookLoagin.performClick();
//                    } else {
//                        MDToast.makeText(getActivity(), "Check Your Internet Connection", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
//                    }
//                }
//            }
//        });
        facebookLoagin = (LoginButton) rootView.findViewById(R.id.facebook_login);
       // mCallbackManager = CallbackManager.Factory.create();
        facebookLoagin.setReadPermissions("email","public_profile");
       // facebookLoagin.performClick();
        facebookLoagin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

               // Log.i(TAG,"Hello"+loginResult.getAccessToken().getToken());
                  Toast.makeText(getApplicationContext(), "Token:"+loginResult.getAccessToken(), Toast.LENGTH_SHORT).show();
                  handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                MDToast.makeText(getApplicationContext(), "facebook cnacel", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
            }

            @Override
            public void onError(FacebookException error) {
                MDToast.makeText(getApplicationContext(), "facebook error", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
            }
        });



        // facebookLoagin.setLoginBehavior(LoginBehavior.WEB_VIEW_ONLY);

//        facebookLoagin.performClick();
//        facebookLoagin.setReadPermissions(Arrays.asList(new String[]{"public_profile","email", "user_birthday"}));
//        facebookLoagin.setFragment(this);
//
//        facebookLoagin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                getProfileData();
//            }
//            @Override
//            public void onCancel() {
////                Toast.makeText(getActivity(), "Login canceled", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
////                Toast.makeText(getActivity(), "Login error", Toast.LENGTH_SHORT).show();
//            }
//        });
//        try {
//            PackageInfo info = getActivity().getPackageManager().getPackageInfo(
//                    "com.user.sqrfactor",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash", "KeyHash:" + Base64.encodeToString(md.digest(),
//                        Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).enableAutoManage((FragmentActivity) getActivity(),1,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso)
//                .build();
//
//
//        googleLogin = rootView.findViewById(R.id.google_login);
//        googleLogin.setSize(SignInButton.SIZE_STANDARD);
//        googleLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(UtilsClass.IsConnected(context)) {
//                    signIn();
//                } else {
//                    MDToast.makeText(getActivity(), "Check Your Internet Connection", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
//                }
//
//            }
//
//        });


    }

//    private void signIn() {
//        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN) {
//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            handleSignInResult(result);
//        }
//    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        mGoogleApiClient.stopAutoManage((FragmentActivity) getActivity());
//        mGoogleApiClient.disconnect();
//    }
//
//    private void handleSignInResult(GoogleSignInResult result) {
//
//        if(result.isSuccess()){
//            GoogleSignInAccount account = result.getSignInAccount();
//            final String name = account.getDisplayName();
//            final String givenName = account.getGivenName()+"   "+account.getFamilyName();
//            final String gmail_email = account.getEmail();
//            final String id = account.getId();
//            final String profile = String.valueOf(account.getPhotoUrl());
//            String[] nameArray=name.split(" ");
//
//            if(nameArray.length>1)
//            {
//                firstNameText=nameArray[0];
//                for(int i=1;i<nameArray.length;i++)
//                {
//                    if(!nameArray[i].equals("null")||nameArray[i]!=null)
//                    lastNameText+=nameArray[i];
//                }
//               // Log.v("data",firstNameText);
//            }
//            else {
//                firstNameText=nameArray[0];
//                lastNameText="";
//            }
//
//            //Toast.makeText(getApplicationContext(),id+" "+name+" "+gmail_email+" "+profile,Toast.LENGTH_LONG).show();
//            sp.edit().clear();
//            sp.edit().putBoolean("logged",true).commit();
//
//            RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();
//            //RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
//            StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"sociallogin",
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
////                            Log.v("ReponseGoogle", response);
////                            Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
//                            // TokenClass.Token=jsonObject.getJSONObject("success").getString("token");
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//                                String msg = jsonObject.getString("status");
//                                if(msg.equals("Already registered")){
//                                    // TokenClass.Token=jsonObject.getJSONObject("success").getString("token");
//                                    UserClass userClass = new UserClass(jsonObject);
//
//
//
//                                    SetUpSharedPreferencesAfterSuccessfullLogin(userClass,jsonObject);
//
//
//                                    Intent i = new Intent(getActivity(), HomeScreen.class);
//                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    getActivity().startActivity(i);
//                                    getActivity().finish();
//                                }
//                                else {
//                                    JSONObject user = jsonObject.getJSONObject("user");
//                                    JSONObject activationToken = user.getJSONObject("activation_token");
//                                    JSONObject userDeatil = activationToken.getJSONObject("user");
//
////
////                                    JSONObject TokenObject = jsonObject.getJSONObject("success");
////                                    String token = TokenObject.getString("token");
////                                    editor.putString("TOKEN", token);
////                                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
////                                    TokenClass.Token=token;
//
//
//                                    UserClass userClass = new UserClass("0","0","0","0",userDeatil.getString("user_name"),userDeatil.getString("first_name"),userDeatil.getString("last_name"),userDeatil.getString("profile"),
//                                            userDeatil.getInt("id"),userDeatil.getString("email"),userDeatil.getString("mobile_number"),userDeatil.getString("user_type"));
//                                    SetUpSharedPreferencesAfterSuccessfullLogin(userClass,jsonObject);
//
//                                    Intent i = new Intent(getActivity(), SocialFormActivity.class);
//                                   // i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    getActivity().startActivity(i);
//                                    getActivity().finish();
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    },
//                    new Response.ErrorListener() {
//
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            NetworkResponse response = error.networkResponse;
//                            if (error instanceof ServerError && response != null) {
//                                try {
//
//
//                                    String res = new String(response.data,
//                                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
//                                    Log.v("login 1",res);
//                                    Toast.makeText(getApplicationContext(),res,Toast.LENGTH_LONG).show();
//                                    // Now you can use any deserializer to make sense of data
//                                    JSONObject obj = new JSONObject(res);
//                                } catch (UnsupportedEncodingException e1) {
//                                    Toast.makeText(getApplicationContext(),e1.toString(),Toast.LENGTH_LONG).show();
//                                    // Couldn't properly decode data to string
//                                    e1.printStackTrace();
//                                } catch (JSONException e2) {
//                                    Toast.makeText(getApplicationContext(),e2.toString(),Toast.LENGTH_LONG).show();
//                                    // returned data is not JSONObject?
//                                    e2.printStackTrace();
//                                }
//                            }
//                        }
//                    }) {
//
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("Accept", "application/json");
//                    return params;
//                }
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("social_id",id);
//                    params.put("first_name",firstNameText);
//                    params.put("last_name",lastNameText);
//                    params.put("email", gmail_email);
//                    if(profile.equals("null")||profile==null)
//                    {
//                        params.put("profile_pic","");
//                    }
//                    else {
//                        params.put("profile_pic",profile);
//                    }
//                    params.put("service", "google");
//                    return params;
//                }
//
//            };
//           // requestQueue.add(myReq);
//            myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            requestQueue.add(myReq);
//        }
//
//    }
//    public void getProfileData() {
//        try {
//
//            accessToken = AccessToken.getCurrentAccessToken();
//            GraphRequest request = GraphRequest.newMeRequest(
//                    accessToken,
//                    new GraphRequest.GraphJSONObjectCallback() {
//                        @Override
//                        public void onCompleted(
//                                JSONObject object,
//                                GraphResponse response) {
//                            Log.d(TAG, "Graph Object :" + object);
//                            sp.edit().clear();
//                            sp.edit().putBoolean("logged",true).commit();
//                            try {
//                                String name = object.getString("name");
//                                String[] nameArray=name.split(" ");
//                                if(nameArray.length>1)
//                                {
//                                    firstNameText=nameArray[0];
//                                    for(int i=1;i<nameArray.length;i++)
//                                    {
//                                        if(!nameArray[i].equals("null")||nameArray[i]!=null)
//                                        lastNameText+=nameArray[i];
//                                    }
//                                    Log.v("data",firstNameText);
//                                }
//                                else {
//                                    firstNameText=nameArray[0];
//                                    lastNameText="";
//                                }
//
//
//                                final String email = object.getString("email");
//                                final String profileID = object.getString("id");
//                                JSONObject picture = object.getJSONObject("picture");
//                                JSONObject data = picture.getJSONObject("data");
//                                final String url = data.getString("url");
//
//                              //  RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
//                                RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();
//                                StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"sociallogin",
//                                        new Response.Listener<String>() {
//                                            @Override
//                                            public void onResponse(String response) {
//                                                Log.v("Reponse", response);
//                                                try {
//                                                    JSONObject jsonObject = new JSONObject(response);
//                                                    String msg = jsonObject.getString("status");
//                                                    if(msg.equals("Already registered")) {
//                                                        UserClass userClass = new UserClass(jsonObject);
//
////                                                        //JSONObject TokenObject = jsonObject.getJSONObject("success");
////                                                        String token = jsonObject.getJSONObject("success").getString("token");
////                                                        editor.putString("TOKEN", token);
////                                                        editor.commit();
////                                                        TokenClass.Token = token;
//
//                                                        SetUpSharedPreferencesAfterSuccessfullLogin(userClass,jsonObject);
//
////
//
//
//                                                        Intent i = new Intent(getActivity(), HomeScreen.class);
////                                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                        getActivity().startActivity(i);
//                                                        getActivity().finish();
//                                                    }else {
//                                                        JSONObject user = jsonObject.getJSONObject("user");
//                                                        JSONObject activationToken = user.getJSONObject("activation_token");
//
//
//                                                        JSONObject userDeatil = activationToken.getJSONObject("user");
//
//                                                        UserClass userClass = new UserClass("0","0","0","0",userDeatil.getString("user_name"),userDeatil.getString("first_name"),userDeatil.getString("last_name"),userDeatil.getString("profile"),
//                                                                userDeatil.getInt("id"),userDeatil.getString("email"),userDeatil.getString("mobile_number"),userDeatil.getString("user_type"));
////
////                                                        String token = jsonObject.getJSONObject("success").getString("token");
////                                                        editor.putString("TOKEN", token);
////                                                        editor.commit();
////                                                        TokenClass.Token = token;
//                                                        SetUpSharedPreferencesAfterSuccessfullLogin(userClass,jsonObject);
//
////
//
//                                                        Intent i = new Intent(getActivity(), SocialFormActivity.class);
//                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                        getActivity().startActivity(i);
//                                                        getActivity().finish();
//                                                    }
//                                                } catch (JSONException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        }, new Response.ErrorListener() {
//                                            @Override
//                                            public void onErrorResponse(VolleyError error) {
//                                                NetworkResponse response = error.networkResponse;
//                                                if (error instanceof ServerError && response != null) {
//                                                    try {
//
//
//                                                        String res = new String(response.data,
//                                                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
//                                                        Log.v("login 1",res);
//                                                        Toast.makeText(getApplicationContext(),res,Toast.LENGTH_LONG).show();
//                                                        // Now you can use any deserializer to make sense of data
//                                                        JSONObject obj = new JSONObject(res);
//                                                    } catch (UnsupportedEncodingException e1) {
//                                                        Toast.makeText(getApplicationContext(),e1.toString(),Toast.LENGTH_LONG).show();
//                                                        // Couldn't properly decode data to string
//                                                        e1.printStackTrace();
//                                                    } catch (JSONException e2) {
//                                                        Toast.makeText(getApplicationContext(),e2.toString(),Toast.LENGTH_LONG).show();
//                                                        // returned data is not JSONObject?
//                                                        e2.printStackTrace();
//                                                    }
//                                                }
//                                            }
//
//                            })
//                                {
//
//                                    @Override
//                                    public Map<String, String> getHeaders() throws AuthFailureError {
//                                        Map<String, String> params = new HashMap<String, String>();
//                                        params.put("Accept", "application/json");
//                                        return params;
//                                    }
//                                    @Override
//                                    protected Map<String, String> getParams() {
//                                        Map<String, String> params = new HashMap<String, String>();
//                                        params.put("social_id",profileID);
//                                        params.put("first_name",firstNameText);
//                                        params.put("last_name",lastNameText);
//                                        params.put("email", email);
//                                        if(url.equals("null")||url==null)
//                                        {
//                                            params.put("profile_pic","");
//                                        }
//                                        else {
//                                            params.put("profile_pic",url);
//                                        }
//                                        params.put("service", "facebook");
//                                        return params;
//                                    }
//
//                                };
//                                requestQueue.add(myReq);
//
//////                                info.setText("Welcome ," + name);
////
////                                Log.d(TAG, "Name :" + name);
////                                Log.d(TAG,"Email"+email);
////                                Log.d(TAG,"ProfileID"+profileID);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//            Bundle parameters = new Bundle();
//            parameters.putString("fields", "id,name,link,birthday,gender,email,picture");
//            request.setParameters(parameters);
//            request.executeAsync();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//
//    public void doSomethingElse() {
//
//       // RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
//        RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();
//        StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"login",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.v("ReponseLogin", response);
//
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//
//                            if(jsonObject.has("message")) {
//                                  MDToast.makeText(getApplicationContext(), "Check Your UserName or password field", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
//                               }
//                            else {
//                                UserClass userClass = new UserClass(jsonObject);
//
////                                String token = jsonObject.getJSONObject("success").getString("token");
////                                editor.putString("TOKEN", token);
////                                editor.commit();
////                                TokenClass.Token = token;
//                                SetUpSharedPreferencesAfterSuccessfullLogin(userClass,jsonObject);
//
//
//
//                                Intent i = new Intent(getApplicationContext(), HomeScreen.class);
//                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                //i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                                getActivity().startActivity(i);
//                                getActivity().finish();
//
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        MDToast.makeText(getApplicationContext(), "Your email or password not valid", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
//
//                    }
//                }) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Accept", "application/json");
//                return params;
//            }
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("email", loginEmail.getText().toString());
//                params.put("password", loginPassword.getText().toString());
//                return params;
//            }
//        };
//        requestQueue.add(myReq);
//    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onResume() {
        super.onResume();
        context = getApplicationContext();
        mTracker.setScreenName( "Loginfragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

//    private void SetUpSharedPreferencesAfterSuccessfullLogin(UserClass userClass,JSONObject jsonObject){
//
//        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications" + userClass.getUserId());
//        FirebaseMessaging.getInstance().subscribeToTopic("chats"+userClass.getUserId());
//        //code for user status
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date = new Date();
//
//        IsOnline isOnline=new IsOnline("True",formatter.format(date));
//        ref.child("Status").child(userClass.getUserId()+"").child("android").setValue(isOnline);
//        IsOnline isOnline1=new IsOnline("False",formatter.format(date));
//
//        ref.child("Status").child(userClass.getUserId()+"").child("web").setValue(isOnline1);
//        JSONObject TokenObject = null;
//        try {
//            TokenObject = jsonObject.getJSONObject("success");
//            String token = TokenObject.getString("token");
          //  editor.putString("TOKEN", token);
//            TokenClass.Token=token;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//
//        mSp.setKey(SPConstants.API_KEY, TokenClass.Token);
//        mSp.setKey(SPConstants.USER_ID, String.valueOf(userClass.getUserId()));
//        mSp.setKey(SPConstants.PROFILE_URL, userClass.getProfile());
//        mSp.setKey(SPConstants.EMAIL, userClass.getEmail());
//        mSp.setKey(SPConstants.USER_TYPE,userClass.getUserType());
//        mSp.setKey(SPConstants.NAME,UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
//
//        SharedPreferences.Editor prefsEditor = mPrefs.edit();
//        Gson gson = new Gson();
//
//        String json = gson.toJson(userClass);
//        prefsEditor.putString("MyObject", json);
//        prefsEditor.commit();
//        editor.commit();
//    }

    @Override
    public void showProgressBar() {

        MDToast.makeText(getActivity(), "loading", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
        //final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        //show loding progressbar here
    }

    @Override
    public void hideProgressBar() {
         //hide progressBarHere
    }

    @Override
    public void showErrorMessage(String errorMessage) {
     //show login failed message here
        MDToast.makeText(getActivity(), errorMessage, MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
    }

    @Override
    public void onSuceesFulLogin() {

        Intent i = new Intent(getApplicationContext(), HomeScreen.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        getActivity().startActivity(i);
        getActivity().finish();
    }

    @Override
    public void openLoginWithMobileActivity() {
        startActivity(new Intent(context,LoginAndSignUpWithPhoneNumber.class));
//        login_with_mobile_view.setVisibility(View.VISIBLE);
//        login_with_email_view.setVisibility(View.GONE);

    }

    @Override
    public void openResetPasswordActivity() {
        Intent i = new Intent(getActivity(), ResetPassword.class);
        startActivity(i);
    }

    @Override
    public void loginWithFacebook() {

//        facebookLoagin.performClick();
//        facebookLoagin.setReadPermissions(Arrays.asList(new String[]{"public_profile","email", "user_birthday"}));
//        facebookLoagin.setFragment(this);
//
//        facebookLoagin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                loginPresenter.onFacbookSuccessfulLogin();
//            }
//            @Override
//            public void onCancel() {
////                Toast.makeText(getActivity(), "Login canceled", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
////                Toast.makeText(getActivity(), "Login error", Toast.LENGTH_SHORT).show();
//            }
//        });
//        try {
//            PackageInfo info = getActivity().getPackageManager().getPackageInfo(
//                    "com.user.sqrfactor",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash", "KeyHash:" + Base64.encodeToString(md.digest(),
//                        Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }
//
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            loginPresenter.onFacbookSuccessfulLogin();
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
//        Log.d(TAG, "handleFacebookAccessToken:" + token);
//        Toast.makeText(getApplicationContext(), "handle facebook acesss  token", Toast.LENGTH_SHORT).show();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                             Toast.makeText(getApplicationContext(), "User Signed In"+user.getDisplayName()+user.getEmail()+user.getUid()+user.getPhotoUrl(), Toast.LENGTH_SHORT).show();
                            Log.w( "signInWithCredential", user.getDisplayName()+user.getEmail()+user.getUid()+user.getPhotoUrl());
                             loginPresenter.onFacbookSuccessfulLogin();
                           // updateUI(user);
                            // startActivity(new Intent(getApplicationContext(),SocialFormActivity.class));
                        }else{

                            Toast.makeText(getApplicationContext(), "Authentication error",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });
    }
    private void updateUI(FirebaseUser user) {
        //hideProgressDialog();

        if (user != null) {
            Toast.makeText(getApplicationContext(), "Authentication "+user.getDisplayName(),
                    Toast.LENGTH_SHORT).show();

//            mStatusTextView.setText(getString(R.string.facebook_status_fmt, user.getDisplayName()));
//            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//
//            findViewById(R.id.buttonFacebookLogin).setVisibility(View.GONE);
//            findViewById(R.id.buttonFacebookSignout).setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getApplicationContext(), "Authentication nothing",
                    Toast.LENGTH_SHORT).show();

//            mStatusTextView.setText(R.string.signed_out);
//            mDetailTextView.setText(null);
//
//            findViewById(R.id.buttonFacebookLogin).setVisibility(View.VISIBLE);
//            findViewById(R.id.buttonFacebookSignout).setVisibility(View.GONE);
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}