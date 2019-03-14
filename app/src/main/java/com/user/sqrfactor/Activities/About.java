package com.user.sqrfactor.Activities;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.user.sqrfactor.Adapters.AboutFirmPagerAdaptor;
import com.user.sqrfactor.Adapters.AboutIndividualPagerAdaptor;
import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Fragments.AboutBasicDetails;
import com.user.sqrfactor.Fragments.AboutCompanyFirmDetailsFragment;
import com.user.sqrfactor.Fragments.AboutDocumentFragment;
import com.user.sqrfactor.Fragments.AboutEmployeeDetails;
import com.user.sqrfactor.Fragments.AboutIndividualEducationFragment;
import com.user.sqrfactor.Fragments.AboutIndividualOtherDetails;
import com.user.sqrfactor.Fragments.AboutIndividualProfessionalFragment;
import com.user.sqrfactor.Fragments.AboutPersonalInfo;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.Work_Individuals_User_Details_Class;
import com.user.sqrfactor.Pojo.FirmDetails;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class About extends AppCompatActivity {


    private MySharedPreferences mSp;
    private Bundle bundle;

    public Work_Individuals_User_Details_Class work_individuals_user_details_class=null;
    public FirmDetails firmDetails1=null;



    TextView aboutName,aboutFollowers,aboutFollowing,about_city_text,about_shot_bio_text,about_company_type_text,about_company_name_text;
    TextView email,phoneNumber,about_nameofCompany_text,about_mobile_number_text,about_email_text,about_country_text,about_state_text,
            about_company_register_num_text;
    Toolbar toolbar;
    private int userId;
    private boolean isIndividual=false;


    private LinearLayout employee_member_details,ind_other_details,ind_professional_details,ind_basic_details,personal_info,basic_details,company_firm_details,basic_details_firm,ind_education;
    String usertype;
    TextView about_facebook_text,about_twitter_text,about_linkedin_text,about_instagram_text,employeemember_details,about_name_short_bio;



    private UserClass userClass;
    private ImageView profileImage;

    //textview for firm_basic_details
    private TextView basic_details_firm_instagram_text,basic_details_firm_linkedin_text,basic_details_firm_twitter_text,basic_details_firm_facebook_text,basic_details_firm_company_register_num_text,basic_details_firm_company_name_text
            ,basic_details_firm_company_type_text,basic_details_firm_shot_bio_text,basic_details_firm_email_text,basic_details_firm_mobile_number_text,basic_details_firm_nameofCompany_text;


    private Tracker mTracker;



    private OnAboutPersonalInfoDataReceivedListener aboutPersonalInfoDataReceivedListener;
    private OnAboutBasicDataReceivedListener onAboutBasicDataReceivedListener;
    private OnAboutEmployeeDataReceived onAboutEmployeeDataReceived;
    private OnAboutCompanyFirmDataReceived onAboutCompanyFirmDataReceived;
    private OnAboutIndividualEducationDataReceived onAboutIndividualEducationDataReceived;
    private OnAboutIndividualProffesionalDataReceived onAboutIndividualProffesionalDataReceived;
    private OnAboutIndividualOtherDetailsDataReceived onAboutIndividualOtherDetailsDataReceived;



    public interface OnAboutPersonalInfoDataReceivedListener {
        void onDataReceived(FirmDetails firmDetails,Work_Individuals_User_Details_Class work_individuals_user_details_class);
    }

    public void setOnAboutPersonalInfoDataReceivedListener(OnAboutPersonalInfoDataReceivedListener listener) {
        this.aboutPersonalInfoDataReceivedListener = listener;
    }


    public interface OnAboutBasicDataReceivedListener {
        void onDataReceived(FirmDetails firmDetails,Work_Individuals_User_Details_Class work_individuals_user_details_class);
    }

    public void setOnAboutBasicDataReceivedListener(OnAboutBasicDataReceivedListener listener) {
        this.onAboutBasicDataReceivedListener = listener;
    }


    public interface OnAboutEmployeeDataReceived {
        void onDataReceived(FirmDetails firmDetails,Work_Individuals_User_Details_Class work_individuals_user_details_class);
    }

    public void setAboutEmployeeDataListener(OnAboutEmployeeDataReceived listener) {
        this.onAboutEmployeeDataReceived = listener;
    }

    public interface OnAboutCompanyFirmDataReceived {
        void onDataReceived(FirmDetails firmDetails,Work_Individuals_User_Details_Class work_individuals_user_details_class);
    }

    public void setAboutCompanyFirmDataListener(OnAboutCompanyFirmDataReceived listener) {
        this.onAboutCompanyFirmDataReceived = listener;
    }

    public interface OnAboutIndividualEducationDataReceived {
        void onDataReceived(FirmDetails firmDetails,Work_Individuals_User_Details_Class work_individuals_user_details_class);
    }

    public void setAboutIndividualEducationDataListener(OnAboutIndividualEducationDataReceived listener) {
        this.onAboutIndividualEducationDataReceived = listener;
    }
    public interface OnAboutIndividualProffesionalDataReceived {
        void onDataReceived(FirmDetails firmDetails,Work_Individuals_User_Details_Class work_individuals_user_details_class);
    }

    public void setAboutIndividualProffesionalDataListener(OnAboutIndividualProffesionalDataReceived listener) {
        this.onAboutIndividualProffesionalDataReceived = listener;
    }

    public interface OnAboutIndividualOtherDetailsDataReceived {
        void onDataReceived(FirmDetails firmDetails,Work_Individuals_User_Details_Class work_individuals_user_details_class);
    }

    public void setAboutIndividualOtherDetailsDataListener(OnAboutIndividualOtherDetailsDataReceived listener) {
        this.onAboutIndividualOtherDetailsDataReceived = listener;
    }



    @Override
    protected void onResume() {
        super.onResume();
        if(userClass!=null){
            mTracker.setScreenName("AboutActivity /"+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //google analytics
        MyApplication application = (MyApplication)getApplication();
        mTracker = application.getDefaultTracker();

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        toolbar.setTitleTextColor(getResources().getColor(R.color.black));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        toolbar.setNavigationIcon(R.drawable.path);

       // profileImage=findViewById(R.id.about_profile_image);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSp = MySharedPreferences.getInstance(this);
        if(getIntent()!=null) {
            userId=getIntent().getIntExtra("UserID",0);
            usertype=getIntent().getStringExtra("userType");
            Toast.makeText(getApplicationContext(),userId+" "+usertype,Toast.LENGTH_LONG).show();

        }





        //getuser class from shared preferences
        userClass=UtilsClass.GetUserClassFromSharedPreferences(About.this);


//        Glide.with(About.this).load(UtilsClass.getParsedImageUrl(userClass.getProfile()))
//                .into(profileImage);

        initPaging();

        aboutName = findViewById(R.id.about_name);


        if(usertype.equals("work_individual")) {
            getIndividualUserDataFromServer(ServerConstants.GET_ABOUT_INDIVIDUAL_USER_DATA);
        } else if(usertype.equals("work_architecture_college")) {

        } else {
            getFirmDataFromServer(ServerConstants.GET_ABOUT_FIRM_USER_DATA);

        }

    }

    private void getFirmDataFromServer(String getAboutFirmUserData) {
        // RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        //Toast.makeText(getApplicationContext(), "firm data", Toast.LENGTH_LONG).show();
        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();

       // bundle=new Bundle();
        StringRequest myReq = new StringRequest(Request.Method.GET, getAboutFirmUserData+userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Log.v("ReponseFeed3", response);
                        //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            isIndividual=false;
                            firmDetails1=new FirmDetails(jsonObject.getJSONObject("firm_details"));
                            aboutPersonalInfoDataReceivedListener.onDataReceived(firmDetails1,work_individuals_user_details_class);
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
//
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
                params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
                return params;
            }

        };
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);
    }


    private void getIndividualUserDataFromServer(String getAboutUserData) {

        //Toast.makeText(getApplicationContext(), "userdata", Toast.LENGTH_LONG).show();
       // RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();


        StringRequest myReq = new StringRequest(Request.Method.GET, getAboutUserData+userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Log.v("ReponseFeed3", response);
                     //   Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            isIndividual = true;
                            work_individuals_user_details_class = new Work_Individuals_User_Details_Class(jsonObject.getJSONObject("about_work_individual"));
                            aboutPersonalInfoDataReceivedListener.onDataReceived(firmDetails1,work_individuals_user_details_class);

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
                               // Log.v("chat",res);
//
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
                params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
                return params;
            }

        };
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);

    }

    private void initPaging() {

        AboutBasicDetails aboutBasicDetails = new AboutBasicDetails();

        AboutPersonalInfo aboutPersonalInfo= new AboutPersonalInfo();
        // aboutPersonalInfo.setArguments(bundle);
        AboutEmployeeDetails aboutEmployeeDetails = new AboutEmployeeDetails();
        AboutCompanyFirmDetailsFragment aboutCompanyFirmDetailsFragment=new AboutCompanyFirmDetailsFragment();
        AboutDocumentFragment aboutDocumentFragment=new AboutDocumentFragment();


        AboutIndividualEducationFragment aboutIndividualEducationFragment=new AboutIndividualEducationFragment();
        AboutIndividualProfessionalFragment aboutIndividualProfessionalFragment=new AboutIndividualProfessionalFragment();
        AboutIndividualOtherDetails aboutIndividualOtherDetails=new AboutIndividualOtherDetails();

        ViewPager viewPager = (ViewPager) findViewById(R.id.about_view_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        AboutFirmPagerAdaptor aboutFirmPagerAdaptor = new AboutFirmPagerAdaptor(getSupportFragmentManager());
        AboutIndividualPagerAdaptor aboutIndividualPagerAdaptor = new AboutIndividualPagerAdaptor(getSupportFragmentManager());


        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FF0000"));
        tabLayout.setSelectedTabIndicatorHeight((int) (2 * getResources().getDisplayMetrics().density));
        tabLayout.setTabTextColors(Color.parseColor("#727272"), Color.parseColor("#ffea3e42"));



        if(usertype.equals("work_individual")) {

            aboutIndividualPagerAdaptor.addFragment(aboutPersonalInfo);
            aboutIndividualPagerAdaptor.addFragment(aboutBasicDetails);
            aboutIndividualPagerAdaptor.addFragment(aboutIndividualEducationFragment);
            aboutIndividualPagerAdaptor.addFragment(aboutIndividualProfessionalFragment);
            aboutIndividualPagerAdaptor.addFragment(aboutIndividualOtherDetails);
            aboutIndividualPagerAdaptor.addFragment(aboutDocumentFragment);
            viewPager.setAdapter(aboutIndividualPagerAdaptor);
            viewPager.setOffscreenPageLimit(6);
            tabLayout.setupWithViewPager(viewPager);




        } else if(usertype.equals("work_architecture_college")) {

//            aboutFirmPagerAdaptor.addFragment(aboutPersonalInfo);
//            aboutFirmPagerAdaptor.addFragment(aboutBasicDetails);
//            aboutFirmPagerAdaptor.addFragment(aboutEmployeeDetails);
//            aboutFirmPagerAdaptor.addFragment(aboutCompanyFirmDetailsFragment);
//            aboutFirmPagerAdaptor.addFragment(aboutDocumentFragment);
//            viewPager.setAdapter(aboutIndividualPagerAdaptor);
//            tabLayout.setupWithViewPager(viewPager);
        } else {
            aboutFirmPagerAdaptor.addFragment(aboutPersonalInfo);
            aboutFirmPagerAdaptor.addFragment(aboutBasicDetails);
            aboutFirmPagerAdaptor.addFragment(aboutCompanyFirmDetailsFragment);
            aboutFirmPagerAdaptor.addFragment(aboutEmployeeDetails);
            aboutFirmPagerAdaptor.addFragment(aboutDocumentFragment);
            viewPager.setAdapter(aboutFirmPagerAdaptor);
            viewPager.setOffscreenPageLimit(5);
            tabLayout.setupWithViewPager(viewPager);

        }


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                //  onAboutIndividualDataReceivedListener.onDataReceived(firmDetails1,work_individuals_user_details_class);
                // onAboutFirmDataReceivedListener.onDataReceived(firmDetails1,work_individuals_user_details_class,isIndividual);
//
//
                if(tab.getText().toString().toLowerCase().equals("personal info")){
                    aboutPersonalInfoDataReceivedListener.onDataReceived(firmDetails1,work_individuals_user_details_class);
                    // onAboutIndividualDataReceivedListener.onDataReceived(firmDetails1,work_individuals_user_details_class);
                    // onAboutFirmDataReceivedListener.onDataReceived(firmDetails1,work_individuals_user_details_class,isIndividual);
                   // Toast.makeText(getApplicationContext(),tab.getText().toString(),Toast.LENGTH_LONG).show();
                }
                else if(tab.getText().toString().toLowerCase().equals("basic details")){
                    onAboutBasicDataReceivedListener.onDataReceived(firmDetails1,work_individuals_user_details_class);
                    // aboutBasicDetails.setArguments(bundle);
                    //  onAboutIndividualDataReceivedListener.onDataReceived(firmDetails1,work_individuals_user_details_class);
                    //onAboutFirmDataReceivedListener.onDataReceived(firmDetails1,work_individuals_user_details_class,isIndividual);
                    //Toast.makeText(getApplicationContext(),tab.getText().toString(),Toast.LENGTH_LONG).show();
                }
                else if(tab.getText().toString().toLowerCase().equals("employee details")){
                    //aboutEmployeeDetails.setArguments(bundle);
                    // onAboutIndividualDataReceivedListener.onDataReceived(firmDetails1,work_individuals_user_details_class);
                    //onAboutFirmDataReceivedListener.onDataReceived(firmDetails1,work_individuals_user_details_class,isIndividual);
                    Toast.makeText(getApplicationContext(),tab.getText().toString(),Toast.LENGTH_LONG).show();
                }
                else if(tab.getText().toString().equals("Company/Firm Details")){
                    onAboutCompanyFirmDataReceived.onDataReceived(firmDetails1,work_individuals_user_details_class);
                    //Toast.makeText(getApplicationContext(),tab.getText().toString(),Toast.LENGTH_LONG).show();
                }
                else if(tab.getText().toString().equals("Employee/Member Details")){
                    onAboutEmployeeDataReceived.onDataReceived(firmDetails1,work_individuals_user_details_class);
                   // Toast.makeText(getApplicationContext(),tab.getText().toString(),Toast.LENGTH_LONG).show();

                }
                else if(tab.getText().toString().equals("Education Details ")){
                    onAboutIndividualEducationDataReceived.onDataReceived(firmDetails1,work_individuals_user_details_class);
                   // Toast.makeText(getApplicationContext(),tab.getText().toString(),Toast.LENGTH_LONG).show();

                }
                else if(tab.getText().toString().equals("Professional Details ")){
                    onAboutIndividualProffesionalDataReceived.onDataReceived(firmDetails1,work_individuals_user_details_class);
                   // Toast.makeText(getApplicationContext(),tab.getText().toString(),Toast.LENGTH_LONG).show();

                }
                else if(tab.getText().toString().equals("Other Details")){
                    onAboutIndividualOtherDetailsDataReceived.onDataReceived(firmDetails1,work_individuals_user_details_class);
                   // Toast.makeText(getApplicationContext(),tab.getText().toString(),Toast.LENGTH_LONG).show();

                }



            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }
}
