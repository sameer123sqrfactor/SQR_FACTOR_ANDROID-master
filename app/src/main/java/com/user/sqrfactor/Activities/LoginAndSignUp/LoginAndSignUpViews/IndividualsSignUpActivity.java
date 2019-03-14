package com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpViews;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.user.sqrfactor.Activities.LoginAndSignUpWithPhoneNumber;
import com.user.sqrfactor.Activities.SocialFormActivity;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Fragments.MDToast;
import com.user.sqrfactor.Pojo.IsOnline;
import com.user.sqrfactor.Pojo.TokenClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class IndividualsSignUpActivity extends AppCompatActivity {

  //  private OnIndividualSignUpFragmentInteractionListener mListener;
    private Button indSignUpWithEmail,indSignUpwithmobile,indSignUpWithGmail;
    private Context context;
    private LoginButton indSignUpWithFacebook;
    private SharedPreferences mPrefs;
    private SharedPreferences mPrefEditor;
    private SharedPreferences.Editor editor;

    private MySharedPreferences mSp;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private Intent camIntent,gallIntent;
    public static DatabaseReference ref;
    public static FirebaseDatabase database;
    public IndividualsSignUpActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(this);
        mCallbackManager= CallbackManager.Factory.create();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        mAuth = FirebaseAuth.getInstance();
        mPrefs = getSharedPreferences("User", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
      //  userClass = gson.fromJson(json, UserClass.class);
        database= FirebaseDatabase.getInstance();
        ref = database.getReference();
        SharedPreferences sharedPref = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        editor = sharedPref.edit();


        //mSp = MySharedPreferences.getInstance(this);

        indSignUpwithmobile=findViewById(R.id.indSignUpwithmobile);
        indSignUpWithFacebook=findViewById(R.id.indSignUpWithFacebook);
        indSignUpWithEmail=findViewById(R.id.indSignUpWithEmail);



        indSignUpWithFacebook.setReadPermissions("email","public_profile");
        indSignUpWithFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                // Log.i(TAG,"Hello"+loginResult.getAccessToken().getToken());
                Toast.makeText(getApplicationContext(), "Token:"+loginResult.getAccessToken(), Toast.LENGTH_SHORT).show();
               // final ProgressDialog loading = ProgressDialog.show(getApplicationContext(),"Getting data from facebook...","Please wait...",false,false);

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



        indSignUpwithmobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), LoginAndSignUpWithPhoneNumber.class);
                intent.putExtra("indSignUpwithmobile",true);
                startActivity(intent);
            }
        });
        indSignUpWithFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });
        indSignUpWithEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), IndiSignupWithEmail.class);
               // intent.putExtra("SignUp",true);
                startActivity(intent);
            }
        });

    }

    private void handleFacebookAccessToken(AccessToken token) {
//        Log.d(TAG, "handleFacebookAccessToken:" + token);
//        Toast.makeText(getApplicationContext(), "handle facebook acesss  token", Toast.LENGTH_SHORT).show();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Log.v("facebook data", "User Signed In"+user.getDisplayName()+user.getEmail()+user.getUid()+user.getPhotoUrl()+user.getPhoneNumber());
                            submitFacebookDetails("https://sqrfactor.com/api/social_registration",user);
                        }else{

                            Toast.makeText(getApplicationContext(), "Authentication error",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view= inflater.inflate(R.layout.signup, container, false);
//        indSignUpwithmobile=view.findViewById(R.id.indSignUpwithmobile);
//        indSignUpWithFacebook=view.findViewById(R.id.indSignUpWithFacebook);
//        indSignUpWithEmail=view.findViewById(R.id.indSignUpWithEmail);
//
//        indSignUpwithmobile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(context, LoginAndSignUpWithPhoneNumber.class);
//                intent.putExtra("SignUp",true);
//                startActivity(intent);
//            }
//        });
//        indSignUpWithFacebook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        indSignUpWithEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent=new Intent(context, LoginAndSignUpWithPhoneNumber.class);
////                intent.putExtra("SignUp",true);
////                startActivity(intent);
//            }
//        });
//
//        return view;
//    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onIndividualFragmentInteraction();
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        this.context=context;
//        if (context instanceof OnIndividualSignUpFragmentInteractionListener) {
//            mListener = (OnIndividualSignUpFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnIndividualSignUpFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onIndividualFragmentInteraction();
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void submitFacebookDetails(String url, final FirebaseUser firebaseUser){

//        String[] parsedName=firebaseUser.getDisplayName().split(" ");
//        final String first_name,last_name;
//        if(parsedName.length>1){
//            first_name=parsedName[0];
//            last_name=parsedName[1];
////            signupFirstName_text.setText(parsedName[0]);
////            signupLastName_text.setText(parsedName[1]);
//        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest myReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                         Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                         Log.v("Reponse facebook", response);
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            UserClass userClass=new UserClass(jsonObject);

                            if(jsonObject.getString("status").equals("Already registered")){
                                //JSONObject successToken=jsonObject.getJSONObject("success");



                                SetUpSharedPreferencesAfterSuccessfullLogin(userClass,jsonObject);
                                Intent i = new Intent(getApplicationContext(), HomeScreen.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                //i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(i);
                                finish();
//

                            }else {

                                SetUpSharedPreferencesAfterSuccessfullLogin(userClass,jsonObject);
                                Intent intent=new Intent(getApplicationContext(),FacebookSignupActivity.class);
                                intent.putExtra("user_image",firebaseUser.getPhotoUrl().toString());
                                intent.putExtra("signupFirstName_text",firebaseUser.getDisplayName());
                                intent.putExtra("signup_email_text",firebaseUser.getEmail());
                                intent.putExtra("signup_facebok_number",firebaseUser.getPhoneNumber());
                                startActivity(intent);



                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


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
                params.put("Accept", "application/json");
              //  params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("social_id",firebaseUser.getUid());
                params.put("service","facebook");
                params.put("email",firebaseUser.getEmail());
                params.put("fullname",firebaseUser.getDisplayName());
                params.put("profile_pic",firebaseUser.getPhotoUrl().toString());


                return params;
            }


        };

        requestQueue.add(myReq);
    }


    private void SetUpSharedPreferencesAfterSuccessfullLogin(UserClass userClass,JSONObject jsonObject){

        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications" + userClass.getUserId());
        FirebaseMessaging.getInstance().subscribeToTopic("chats"+userClass.getUserId());
        //code for user status
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        IsOnline isOnline=new IsOnline("True",formatter.format(date));
        ref.child("Status").child(userClass.getUserId()+"").child("android").setValue(isOnline);
        IsOnline isOnline1=new IsOnline("False",formatter.format(date));

        ref.child("Status").child(userClass.getUserId()+"").child("web").setValue(isOnline1);
        JSONObject TokenObject = null;
        try {
            TokenObject = jsonObject.getJSONObject("success");
            String token = TokenObject.getString("token");
            editor.putString("TOKEN", token);
            TokenClass.Token=token;
            MySharedPreferences mSp= MySharedPreferences.getInstance();
            mSp.setKey(SPConstants.API_KEY, token);
            mSp.setKey(SPConstants.USER_ID, String.valueOf(userClass.getUserId()));
            mSp.setKey(SPConstants.PROFILE_URL, userClass.getProfile());
            mSp.setKey(SPConstants.EMAIL, userClass.getEmail());
            mSp.setKey(SPConstants.USER_TYPE,userClass.getUserType());
            mSp.setKey(SPConstants.NAME, UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));

        } catch (JSONException e) {
            e.printStackTrace();
        }



        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(userClass);
        prefsEditor.putString("MyObject", json);
        prefsEditor.commit();
        editor.commit();
    }

    private void showCommentDeleteDialog(final String  submissionId, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Are you sure you want to delete this design?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       // DeleteSubmission(submissionId,position);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }




}
