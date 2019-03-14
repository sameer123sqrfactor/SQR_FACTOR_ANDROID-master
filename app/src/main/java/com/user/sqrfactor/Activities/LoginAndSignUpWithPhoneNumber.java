package com.user.sqrfactor.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.auth.FirebaseAuth;

import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Constants.BundleConstants;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.CountryClass;
import com.user.sqrfactor.R;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginAndSignUpWithPhoneNumber extends AppCompatActivity {

    private EditText phoneNumberForLogin_text;
    private String country_val=null,userType;
    private int countryPhoneId;
    private ArrayList<CountryClass> countryClassArrayList=new ArrayList<>();
    private ArrayList<String> countryName=new ArrayList<>();
    private FirebaseAuth mAuth;
    private CountryCodePicker countryCodePicker;
    private String selectedCountyCode;
    private Spinner userTypeSpinner;
    private LinearLayout linearlayout;
    private String codeSent;

    private Tracker mTracker;

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("LoginAndSignUpWithPhoneNumber /");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_sign_up_with_phone_number);

        phoneNumberForLogin_text=findViewById(R.id.phoneNumberForLogin_text);

        mAuth = FirebaseAuth.getInstance();
        countryCodePicker=findViewById(R.id.ccp);
        countryPhoneId=countryCodePicker.getSelectedCountryCodeAsInt();

        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryPhoneId=countryCodePicker.getSelectedCountryCodeAsInt();

            }
        });


        //google anlytics
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();

//        userTypeSpinner=findViewById(R.id.userType);
//        linearlayout=findViewById(R.id.linearlayout);
//        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item,getResources().getStringArray(R.array.user_choices));
//        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
//
//
//        userTypeSpinner.setAdapter(spinnerArrayAdapter);
//
//        if(getIntent().hasExtra("SignUp"))
//        {
//            linearlayout.setVisibility(View.VISIBLE);
//        }else {
//            linearlayout.setVisibility(View.GONE);
//        }

//        userTypeSpinner.getSelectedView().setTe

//        userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                userType = parent.getItemAtPosition(position).toString(); //this is your selected item
//                if (userType.equals("Individual")) {
//                    userType = "work_individual";
//
//                } else if (userType.equals("Firm/Companies (Design Service Providers)")) {
//                    userType = "work_architecture_firm_companies";
//
//                } else if (userType.equals("Organisations, Companies, NGOs, Media")) {
//                    userType = "work_architecture_firm_organization";
//
//                } else if (userType.equals("College/University")) {
//                    userType = "work_architecture_firm_college";
//
//                }
//            }
//
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        if(getIntent().hasExtra("FacebookSignupActivity")){
            phoneNumberForLogin_text.setText(getIntent().getStringExtra("signup_facebok_number"));
        }

        findViewById(R.id.getVerificationcode).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendVerificationCode();
           }
        });
      //  LoadCountryFromServer();

    }



    private void sendVerificationCode(){

        String phone = phoneNumberForLogin_text.getText().toString();

        if(phone.isEmpty()){
            phoneNumberForLogin_text.setError("Phone number is required");
            phoneNumberForLogin_text.requestFocus();
            return;
        }

        if(phone.length() < 10 ){
            phoneNumberForLogin_text.setError("Please enter a valid phone");
            phoneNumberForLogin_text.requestFocus();
            return;
        }
        Intent intent=new Intent(getApplicationContext(),OTP_Verify.class);
        intent.putExtra(BundleConstants.PHONE_NUMBER,phone);
        intent.putExtra(BundleConstants.COUNTRY_CODE_PHONE,"+"+countryPhoneId);

        if(getIntent().hasExtra("indSignUpwithmobile")) {
            intent.putExtra(BundleConstants.SIGNUP_WITH_MOBILE_EBALED,true);
            intent.putExtra(BundleConstants.COUNTRY_CODE,country_val);
            intent.putExtra(BundleConstants.USER_TYPE,"work_individual");
            intent.putExtra(BundleConstants.SIGN_TYPE,"indSignUpwithmobile");
            CallServerForMobileExist(phone,intent);

        }else if(getIntent().hasExtra("IndiSignupWithEmail")) {
            intent.putExtra(BundleConstants.SIGNUP_WITH_MOBILE_EBALED,true);
            intent.putExtra(BundleConstants.COUNTRY_CODE,country_val);
            intent.putExtra(BundleConstants.USER_TYPE,"work_individual");
            intent.putExtra(BundleConstants.SIGN_TYPE,"IndiSignupWithEmail");
            CallServerForMobileExist(phone,intent);

        }
        else if(getIntent().hasExtra("CompanySignUpActivity")) {
            intent.putExtra(BundleConstants.SIGNUP_WITH_MOBILE_EBALED,true);
            intent.putExtra(BundleConstants.COUNTRY_CODE,country_val);
            intent.putExtra(BundleConstants.USER_TYPE,"work_architecture_firm_companies");
            intent.putExtra(BundleConstants.SIGN_TYPE,"CompanySignUpActivity");
            CallServerForMobileExist(phone,intent);

        }  else if(getIntent().hasExtra("OnCollegeSignUp")) {
            intent.putExtra(BundleConstants.SIGNUP_WITH_MOBILE_EBALED,true);
            intent.putExtra(BundleConstants.COUNTRY_CODE,country_val);
            intent.putExtra(BundleConstants.USER_TYPE,"work_architecture_firm_college");
            intent.putExtra(BundleConstants.SIGN_TYPE,"OnCollegeSignUp");
            CallServerForMobileExist(phone,intent);

        } else if(getIntent().hasExtra("OrganisationSignup")) {
            intent.putExtra(BundleConstants.SIGNUP_WITH_MOBILE_EBALED,true);
            intent.putExtra(BundleConstants.COUNTRY_CODE,country_val);
            intent.putExtra(BundleConstants.USER_TYPE,"work_architecture_firm_organization");
            intent.putExtra(BundleConstants.SIGN_TYPE,"OrganisationSignup");
            CallServerForMobileExist(phone,intent);

        }
        else if(getIntent().hasExtra("FacebookSignupActivity")) {
            intent.putExtra(BundleConstants.SIGNUP_WITH_MOBILE_EBALED,true);
            intent.putExtra(BundleConstants.COUNTRY_CODE,country_val);
            intent.putExtra(BundleConstants.USER_TYPE,"work_individual");
            intent.putExtra(BundleConstants.SIGN_TYPE,"FacebookSignupActivity");
            CallServerForMobileExist(phone,intent);

        }
        else
        {
            intent.putExtra(BundleConstants.LOGIN_WITH_MOBILE_EBALED,true);
            intent.putExtra(BundleConstants.COUNTRY_CODE,country_val);
            startActivity(intent);
        }





    }




    private void CallServerForMobileExist(final String phone, final Intent intent) {
            RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();
            StringRequest myReq = new StringRequest(Request.Method.POST, "https://sqrfactor.com/api/is_number_exist",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("signupApiReponse", response);
                            Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                            try {

                                JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.getString("status")==0+""){
                                    startActivity(intent);
                                }else {
                                    showCommentDeleteDialog();
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
                    params.put("mobile_number",phone);

                    return params;
                }

//+918876739082
            };

            requestQueue.add(myReq);


        }


    private void showCommentDeleteDialog() {
      //  final String[] item = {"Individual","Architecture firm companies","Architecture Organization","Architecture College"};
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("User with this number already exist. login to continue");


        b.show();

    }



    }
