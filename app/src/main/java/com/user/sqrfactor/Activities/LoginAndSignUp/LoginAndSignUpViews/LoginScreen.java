package com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpViews;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.user.sqrfactor.R;

public class LoginScreen extends AppCompatActivity{

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private Toolbar toolbar;
    private SharedPreferences sp;
    private TextView sign_up_btn;
    private LinearLayout sign_up_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sign_up_btn=findViewById(R.id.sign_up_btn);
        //frag=findViewById(R.id.frag);
        sign_up_layout=findViewById(R.id.sign_up_layout);


        getFragmentManager().beginTransaction().replace(R.id.frag, new LoginFragment(), "login").commit();
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UsertypeActivity.class));
              //  frag.setGravity(Gravity.CENTER);
               // getFragmentManager().beginTransaction().replace(R.id.frag, new UsertypeActivity(), "signup").addToBackStack("signup").commit();
            }
        });

//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.signup_selected));
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.login));
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                //viewPager.setCurrentItem(tab.getPosition());
//                if (tab.getPosition() == 0) {
//
//                    tab.setIcon(R.drawable.signup_selected);
//                    getFragmentManager().beginTransaction().replace(R.id.frag, new SignUpFragment(), "stuff").commit();
//                }
//                if (tab.getPosition() == 1) {
//
//                    tab.setIcon(R.drawable.login_selected);
//                    getFragmentManager().beginTransaction().replace(R.id.frag, new LoginFragment(), "stuff1").commit();
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//                switch (tab.getPosition()){
//
//                    case 0:
//                        tab.setIcon(R.drawable.signup);
//                        break;
//
//                    case 1:
//                        tab.setIcon(R.drawable.login);
//                        break;
//
//                }
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        try {
            Fragment fragment = getFragmentManager().findFragmentByTag("login");
//            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
                Log.d("Activity Eoodr code", "ON RESULT CALLED");
//                Toast.makeText(this, "ON RESULT CALLED", Toast.LENGTH_SHORT).show();
//            }
        } catch (Exception e) {
            Log.d("ERROR code activity", e.toString());
            Toast.makeText(this, "ON RESULT Error", Toast.LENGTH_SHORT).show();
        }
    }



}