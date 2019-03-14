package com.user.sqrfactor.Activities.SettingModule.SettingViews.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.user.sqrfactor.Activities.BasicFirmDetails;
import com.user.sqrfactor.Activities.SettingModule.SettingInterface;
import com.user.sqrfactor.Activities.SettingModule.SettingPresenters.SettingPresenter;
import com.user.sqrfactor.Activities.SettingModule.SettingViews.Fragments.SettingBasicFirmDetails;
import com.user.sqrfactor.Activities.SettingModule.SettingViews.Fragments.SettingChangePassword;
import com.user.sqrfactor.Activities.SettingModule.SettingViews.Fragments.SettingCollegeBasicDetails;
import com.user.sqrfactor.Activities.SettingModule.SettingViews.Fragments.SettingCollegeDetails;
import com.user.sqrfactor.Activities.SettingModule.SettingViews.Fragments.SettingCompanyFirmDetails;
import com.user.sqrfactor.Activities.SettingModule.SettingViews.Fragments.SettingEmployeeMemberDetails;
import com.user.sqrfactor.Activities.SettingModule.SettingViews.Fragments.SettingIndividualBasicDetails;
import com.user.sqrfactor.Activities.SettingModule.SettingViews.Fragments.SettingIndividualEducationDetails;
import com.user.sqrfactor.Activities.SettingModule.SettingViews.Fragments.SettingIndividualOtherDetails;
import com.user.sqrfactor.Activities.SettingModule.SettingViews.Fragments.SettingIndividualProfessionalDetails;
import com.user.sqrfactor.Activities.SettingModule.SettingViews.Fragments.SettingUploadDocuments;
import com.user.sqrfactor.Adapters.SettingCollegeAdapter;
import com.user.sqrfactor.Adapters.SettingFirmAdapter;
import com.user.sqrfactor.Adapters.SettingIndividualsAdapter;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.TokenClass;
import com.user.sqrfactor.Pojo.UserData;
import com.user.sqrfactor.Pojo.User_basic_detail;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Storage.MySharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Settings extends AppCompatActivity implements SettingIndividualBasicDetails.OnFragmentInteractionListener, SettingEmployeeMemberDetails.OnFragmentInteractionListener,
        SettingCompanyFirmDetails.OnFragmentInteractionListener, SettingBasicFirmDetails.OnFragmentInteractionListener,
        SettingCollegeDetails.OnFragmentInteractionListener, SettingCollegeBasicDetails.OnFragmentInteractionListener,
        SettingChangePassword.OnFragmentInteractionListener, SettingUploadDocuments.OnFragmentInteractionListener,
        SettingIndividualEducationDetails.OnFragmentInteractionListener, SettingIndividualProfessionalDetails.OnFragmentInteractionListener,
        SettingIndividualOtherDetails.OnFragmentInteractionListener,SettingInterface.settingView
{

    private Toolbar toolbar;
    private MySharedPreferences mSp;
    private ImageView menu,profilePic;
    private TextView text1,text2,text3,text4,text5,text6,profileUserName;
    private TextView bluePrint1,portfolio1,followers1,following1;
    private TextView bluePrint,portfolio,followers,following;
    private TextView profileName,followCnt,followingCnt,portfolioCnt,bluePrintCnt;
    private ArrayList<UserData> userDataArrayList = new ArrayList<>();
    private SharedPreferences.Editor editor;
    private SharedPreferences mPrefs;
    private UserData userData;
    private UserClass userClass;
    private TextView Setting_ShortBio,Setting_Address;
    public static DatabaseReference ref;
    public static FirebaseDatabase database;
    private SettingInterface.settingPresenter settingPresenter=new SettingPresenter(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        toolbar.setTitleTextColor(getResources().getColor(R.color.black));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        toolbar.setNavigationIcon(R.drawable.path);
        mSp = MySharedPreferences.getInstance(getApplicationContext());

        // profileImage=findViewById(R.id.about_profile_image);
//
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });

//        profilePic=(ImageView)findViewById(R.id.profile_profile_image);
//
//        profileUserName=(TextView)findViewById(R.id.profile_profile_name);
//        bluePrint =(TextView)findViewById(R.id.profile_blueprintClick);
//        portfolio =(TextView)findViewById(R.id.profile_portfolioClick);
//        followers =(TextView)findViewById(R.id.profile_followersClick);
//        following =(TextView)findViewById(R.id.profile_followingClick);
//        Setting_ShortBio=(TextView)findViewById(R.id.Setting_ShortBio);
//        Setting_Address=(TextView)findViewById(R.id.Setting_Address);
//        //getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new SettingsFragment()).commit();

        database= FirebaseDatabase.getInstance();
        ref = database.getReference();

//
//        SharedPreferences sharedPreferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
//        String token = sharedPreferences.getString("TOKEN", "sqr");
//        TokenClass.Token = token;
//        TokenClass tokenClass = new TokenClass(token);
//        Log.v("Token1", token);

        SharedPreferences mPrefs =getSharedPreferences("User",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        userClass = gson.fromJson(json, UserClass.class);





        //profileUserName.setText(UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));

        initPaging();


//        if(userClass.getShort_bio()!=null && !userClass.getShort_bio().equals("null"))
//        {
//            Setting_ShortBio.setText(userClass.getShort_bio());
//        }
//        if(userClass.getUser_address()!=null && !userClass.getUser_address().equals("null"))
//        {
//            Setting_Address.setText(userClass.getUser_address());
//        }
//
//
//        Glide.with(this).load(UtilsClass.getParsedImageUrl(userClass.getProfile()))
//                .into(profilePic);
//
////        toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
////        toolbar.setTitle("Settings");
////        setSupportActionBar(toolbar);
////        toolbar.setNavigationIcon(R.drawable.back_arrow);
//
//        menu = (ImageView)findViewById(R.id.profile_about_morebtn);
//        menu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupMenu pop = new PopupMenu(getApplicationContext(), v);
//                pop.getMenu().add(1,1,0,"About "+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
//                pop.getMenuInflater().inflate(R.menu.more_menu, pop.getMenu());
//                pop.show();
//
//                pop.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//
//                        switch (item.getItemId()){
//
//                            case 1:
//                                Intent i = new Intent(getApplicationContext(), About.class);
//                                i.putExtra("UserID",userClass.getUserId());
//                                i.putExtra("userType",userClass.getUserType());
//                                startActivity(i);
//                                return true;
//
//                        }
//                        return true;
//                    }
//                });
//
//            }
//        });
//
//        bluePrint1 = (TextView)findViewById(R.id.profile_blueprintcnt);
//        bluePrint1.setText(userClass.getBlueprintCount());
//
//        bluePrint.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(Settings.this, ProfileActivity.class);
//                i.putExtra("UserName",userClass.getUser_name());
//                startActivity(i);
//                //getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new Portfolio())
//                //.addToBackStack(null).commit();
//
//            }
//        });
//
//        portfolio1 = (TextView)findViewById(R.id.profile_portfoliocnt);
//        portfolio1.setText(userClass.getPortfolioCount());
//
//
//        portfolio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent j = new Intent(Settings.this, PortfolioActivity.class);
//                j.putExtra("UserName",userClass.getUser_name());
//                startActivity(j);
//                //getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new Portfolio())
//                //.addToBackStack(null).commit();
//            }
//        });
//
//        followers1 = (TextView)findViewById(R.id.profile_followerscnt);
//        followers1.setText(userClass.getFollowerCount());
//
//
//        followers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent k = new Intent(Settings.this, FollowersActivity.class);
//                k.putExtra("UserName",userClass.getUser_name());
//                startActivity(k);
//                //getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new Portfolio())
//                //.addToBackStack(null).commit();
//
//            }
//        });
//        following1 = (TextView)findViewById(R.id.profile_followingcnt);
//        following1.setText(userClass.getFollowingCount());
//
//        following.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent l = new Intent(Settings.this, FollowingActivity.class);
//                l.putExtra("UserName",userClass.getUser_name());
//                startActivity(l);
//
//            }
//        });
//
//        text1 = (TextView)findViewById(R.id.basic_details);
//        text2 = (TextView)findViewById(R.id.edu_details);
//        text3 = (TextView)findViewById(R.id.prof_details);
//        text4 = (TextView)findViewById(R.id.other_details);
//        text4.setVisibility(View.GONE);
//
//        text5 = (TextView)findViewById(R.id.change_password);
//
//        text6=(TextView)findViewById(R.id.upload_document);
//
//        //Toast.makeText(this,"type"+userClass.getUserType(),Toast.LENGTH_LONG).show();
//



        }

    private void initPaging() {

        ViewPager viewPager = (ViewPager) findViewById(R.id.about_view_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        SettingUploadDocuments settingUploadDocuments=new SettingUploadDocuments();
        SettingChangePassword settingChangePassword=new SettingChangePassword();


        if(userClass.getUserType().equals("work_individual"))
        {

            SettingIndividualBasicDetails settingIndividualBasicDetails=new SettingIndividualBasicDetails();
            SettingIndividualEducationDetails settingIndividualEducationDetails=new SettingIndividualEducationDetails();
            SettingIndividualProfessionalDetails settingIndividualProfessionalDetails=new SettingIndividualProfessionalDetails();
            SettingIndividualOtherDetails settingIndividualOtherDetails=new SettingIndividualOtherDetails();

            SettingIndividualsAdapter settingIndividualsAdapter = new SettingIndividualsAdapter(getSupportFragmentManager());
            //AboutIndividualPagerAdaptor aboutIndividualPagerAdaptor = new AboutIndividualPagerAdaptor(getSupportFragmentManager());



            tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FF0000"));
            tabLayout.setSelectedTabIndicatorHeight((int) (2 * getResources().getDisplayMetrics().density));
            tabLayout.setTabTextColors(Color.parseColor("#727272"), Color.parseColor("#ffea3e42"));


            settingIndividualsAdapter.addFragment(settingIndividualBasicDetails);
            settingIndividualsAdapter.addFragment(settingIndividualEducationDetails);
            settingIndividualsAdapter.addFragment(settingIndividualProfessionalDetails);
            settingIndividualsAdapter.addFragment(settingIndividualOtherDetails);
            settingIndividualsAdapter.addFragment(settingUploadDocuments);
            settingIndividualsAdapter.addFragment(settingChangePassword);
            viewPager.setAdapter(settingIndividualsAdapter);
            viewPager.setOffscreenPageLimit(6);
            tabLayout.setupWithViewPager(viewPager);

            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                if(i==0) {
                    tabLayout.getTabAt(i).setIcon(R.drawable.setting_basic_details_red_icon);
                }if(i==1) {
                    tabLayout.getTabAt(i).setIcon(R.drawable.ic_education_grey);
                }if(i==2) {
                    tabLayout.getTabAt(i).setIcon(R.drawable.setting_employee_icon);
                }if(i==3) {
                    tabLayout.getTabAt(i).setIcon(R.drawable.setting_basic_details_icon);
                }if(i==4) {
                    tabLayout.getTabAt(i).setIcon(R.drawable.ic_doc_grey);
                }if(i==5) {
                    tabLayout.getTabAt(i).setIcon(R.drawable.setting_change_password_icon);
                }
            }
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    if(tab.getPosition()==0){
                        tab.setIcon(R.drawable.setting_basic_details_red_icon);
                    }
                    if(tab.getPosition()==2){
                        tab.setIcon(R.drawable.set_emp_red_icon);
                    }
                    if(tab.getPosition()==4){
                        tab.setIcon(R.drawable.ic_doc_red);
                    }
                    if(tab.getPosition()==5){
                        tab.setIcon(R.drawable.setting_change_password_red_icon);
                    }

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab){
                    if(tab.getPosition()==0){
                        tab.setIcon(R.drawable.setting_basic_details_icon);
                    }
                    if(tab.getPosition()==2){
                        tab.setIcon(R.drawable.setting_employee_icon);
                    }

                    if(tab.getPosition()==4){
                        tab.setIcon(R.drawable.ic_doc_grey);
                    }

                    if(tab.getPosition()==5){
                        tab.setIcon(R.drawable.setting_change_password_icon);
                    }

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });


//            text1.setText("Basic Details");
//            text1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(Settings.this,BasicDetails.class);
//                    startActivity(i);
//                }
//            });
//
//            text2.setText("Education Details");
//            text2.setOnClickListener(new View.OnClickListener() {
//                @SuppressLint("ResourceAsColor")
//                @Override
//                public void onClick(View v) {
//                    Intent j = new Intent(Settings.this,EducationDetailsActivity.class);
//                    startActivity(j);
//                }
//            });
//
//            text3.setText("Professional Details");
//            text3.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent k = new Intent(Settings.this,ProfessionalActivity.class);
//                    startActivity(k);
//                }
//            });
//
//            text4.setVisibility(View.VISIBLE);
//            text4.setText("Other Details");
//            text4.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent l = new Intent(Settings.this,OtherDetailsActivity.class);
//                    startActivity(l);
//                }
//            });
//            text5.setText("Change Password");
//            text5.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent l = new Intent(Settings.this,ChangePassword.class);
//                    startActivity(l);
//                }
//            });
//            text6.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent l = new Intent(Settings.this,UploadDocumentActivity.class);
//                    l.putExtra("isIndividuals",true);
//                    startActivity(l);
//                }
//            });

        }

        else if(userClass.getUserType().equals("work_architecture_college")) {


            SettingCollegeBasicDetails settingCollegeBasicDetails=new SettingCollegeBasicDetails();
            SettingCollegeDetails settingCollegeDetails=new SettingCollegeDetails();
            SettingEmployeeMemberDetails employeeMemberDetails=new SettingEmployeeMemberDetails();


            SettingCollegeAdapter settingCollegeAdapter = new SettingCollegeAdapter(getSupportFragmentManager());
            //AboutIndividualPagerAdaptor aboutIndividualPagerAdaptor = new AboutIndividualPagerAdaptor(getSupportFragmentManager());



            tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FF0000"));
            tabLayout.setSelectedTabIndicatorHeight((int) (2 * getResources().getDisplayMetrics().density));
            tabLayout.setTabTextColors(Color.parseColor("#727272"), Color.parseColor("#ffea3e42"));


            settingCollegeAdapter.addFragment(settingCollegeBasicDetails);
            settingCollegeAdapter.addFragment(settingCollegeDetails);
            settingCollegeAdapter.addFragment(employeeMemberDetails);

            settingCollegeAdapter.addFragment(settingUploadDocuments);
            settingCollegeAdapter.addFragment(settingChangePassword);
            viewPager.setAdapter(settingCollegeAdapter);
            viewPager.setOffscreenPageLimit(5);
            tabLayout.setupWithViewPager(viewPager);
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                if(i==0) {
                    tabLayout.getTabAt(i).setIcon(R.drawable.setting_basic_details_red_icon);
                }if(i==1) {
                    tabLayout.getTabAt(i).setIcon(R.drawable.setting_basic_details_icon);
                }if(i==2) {
                    tabLayout.getTabAt(i).setIcon(R.drawable.setting_employee_icon);
                }if(i==3) {
                    tabLayout.getTabAt(i).setIcon(R.drawable.setting_basic_details_icon);
                }if(i==4) {
                    tabLayout.getTabAt(i).setIcon(R.drawable.setting_change_password_icon);
                }
            }


            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    if(tab.getPosition()==0){
                        tab.setIcon(R.drawable.setting_basic_details_red_icon);
                    }
                    if(tab.getPosition()==2){
                        tab.setIcon(R.drawable.set_emp_red_icon);
                    }
                    if(tab.getPosition()==4){
                        tab.setIcon(R.drawable.setting_change_password_red_icon);
                    }

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab){
                    if(tab.getPosition()==0){
                        tab.setIcon(R.drawable.setting_basic_details_icon);
                    }
                    if(tab.getPosition()==2){
                        tab.setIcon(R.drawable.setting_employee_icon);
                    }
                    if(tab.getPosition()==4){
                        tab.setIcon(R.drawable.setting_change_password_icon);
                    }

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
//
//            text1.setText("Basic Details");
//            text1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(Settings.this, CollegeBasicDetails.class);
//                    startActivity(i);
//                }
//            });
//
//            text2.setText("College/University Details");
//            text2.setOnClickListener(new View.OnClickListener() {
//                @SuppressLint("ResourceAsColor")
//                @Override
//                public void onClick(View v) {
//                    Intent j = new Intent(Settings.this, CollegeDetails.class);
//                    startActivity(j);
//                }
//            });
//
//            text3.setText("Faculty Details");
//            text3.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent k = new Intent(Settings.this, EmployeeMemberDetails.class);
//                    startActivity(k);
//                }
//            });
//
//            text4.setVisibility(View.GONE);
//
//            text5.setText("Change Password");
//            text5.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent l = new Intent(Settings.this, ChangePassword.class);
//                    startActivity(l);
//                }
//            });
//
//            text6.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent l = new Intent(Settings.this,UploadDocumentActivity.class);
//                    l.putExtra("isIndividuals",false);
//                    startActivity(l);
//                }
//            });
        }
        else
        {

            SettingBasicFirmDetails settingBasicFirmDetails=new SettingBasicFirmDetails();
            SettingCompanyFirmDetails settingCompanyFirmDetails=new SettingCompanyFirmDetails();
            SettingEmployeeMemberDetails settingEmployeeMemberDetails=new SettingEmployeeMemberDetails();
            SettingFirmAdapter settingFirmAdapter=new SettingFirmAdapter(getSupportFragmentManager());

            settingFirmAdapter.addFragment(settingBasicFirmDetails);
            settingFirmAdapter.addFragment(settingCompanyFirmDetails);
            settingFirmAdapter.addFragment(settingEmployeeMemberDetails);

            tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FF0000"));
            tabLayout.setSelectedTabIndicatorHeight((int) (2 * getResources().getDisplayMetrics().density));
            tabLayout.setTabTextColors(Color.parseColor("#727272"), Color.parseColor("#ffea3e42"));


            settingFirmAdapter.addFragment(settingUploadDocuments);
            settingFirmAdapter.addFragment(settingChangePassword);
            viewPager.setAdapter(settingFirmAdapter);
            viewPager.setOffscreenPageLimit(5);
            tabLayout.setupWithViewPager(viewPager);
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                if(i==0) {
                    tabLayout.getTabAt(i).setIcon(R.drawable.setting_basic_details_red_icon);
                }if(i==1) {
                    tabLayout.getTabAt(i).setIcon(R.drawable.setting_basic_details_icon);
                }if(i==2) {
                    tabLayout.getTabAt(i).setIcon(R.drawable.setting_employee_icon);
                }if(i==3) {
                    tabLayout.getTabAt(i).setIcon(R.drawable.ic_doc_grey);
                }if(i==4) {
                    tabLayout.getTabAt(i).setIcon(R.drawable.setting_change_password_icon);
                }
            }

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    if(tab.getPosition()==0){
                        tab.setIcon(R.drawable.setting_basic_details_red_icon);
                    }if(tab.getPosition()==2){
                        tab.setIcon(R.drawable.set_emp_red_icon);
                    }if(tab.getPosition()==3) {
                        tab.setIcon(R.drawable.ic_doc_red);
                    }if(tab.getPosition()==4){
                        tab.setIcon(R.drawable.setting_change_password_red_icon);
                    }

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab){
                    if(tab.getPosition()==0){
                        tab.setIcon(R.drawable.setting_basic_details_icon);
                    }
                    if(tab.getPosition()==2){
                        tab.setIcon(R.drawable.setting_employee_icon);
                    }
                    if(tab.getPosition()==4){
                        tab.setIcon(R.drawable.setting_change_password_icon);
                    }

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });




//            text1.setText("Basic Details");
//            text1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
                  //  Intent i = new Intent(Settings.this, BasicFirmDetails.class);
//                    startActivity(i);
//                }
//            });
//
//            text2.setText("Company Firm Details");
//            text2.setOnClickListener(new View.OnClickListener() {
//                @SuppressLint("ResourceAsColor")
//                @Override
//                public void onClick(View v) {
//                    Intent j = new Intent(Settings.this,CompanyFirmDetails.class);
//                    startActivity(j);
//                }
//            });
//
//            text3.setText("Employee/Member Details");
//            text3.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent k = new Intent(Settings.this,EmployeeMemberDetails.class);
//                    startActivity(k);
//                }
//            });
//
//            text4.setVisibility(View.GONE);
//
//            text5.setText("Change Password");
//            text5.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent l = new Intent(Settings.this,ChangePassword.class);
//                    startActivity(l);
//                }
//            });
//            text6.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent l = new Intent(Settings.this,UploadDocumentActivity.class);
//                    l.putExtra("isIndividuals",false);
//                    startActivity(l);
//                }
//            });

        }

        getUserDataFromServer();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void getUserDataFromServer()
    {
//        RequestQueue requestQueue = Volley.newRequestQueue(this);

        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();
        StringRequest myReq = new StringRequest(Request.Method.GET, UtilsClass.baseurl+"profile/edit",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("settings", response);
                       // Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("User Data");
                            UserData  userData = new UserData(jsonArray.getJSONObject(0));

                            User_basic_detail user_basic_detail=new User_basic_detail(jsonObject.getJSONObject("User_basic_detail"));
                            userData.setUser_basic_detail(user_basic_detail);

                            mPrefs = getSharedPreferences("UserData", MODE_PRIVATE);
                            SharedPreferences.Editor prefsEditor = mPrefs.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(userData);
                            prefsEditor.putString("UserData", json);
                            prefsEditor.commit();


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
               // params.put("Authorization", "Bearer " + TokenClass.Token);
                params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
                return params;
            }

        };
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
