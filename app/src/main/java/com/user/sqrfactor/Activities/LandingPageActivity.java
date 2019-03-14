package com.user.sqrfactor.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpViews.LoginScreen;
import com.user.sqrfactor.Adapters.MyViewPagerAdapter;
import com.user.sqrfactor.Pojo.PrefManager;
import com.user.sqrfactor.R;

public class LandingPageActivity extends AppCompatActivity {


        private ViewPager viewPager;
        private MyViewPagerAdapter myViewPagerAdapter;
        private LinearLayout dotsLayout;
        private TextView[] dots;
        private int[] layouts;
        private Button btnSkip, btnNext;
        private PrefManager prefManager;
        Handler handler;
        private SharedPreferences sp;
        private SharedPreferences.Editor pref;
        private FirebaseAnalytics mFirebaseAnalytics;




    @Override
    protected void onStart() {
        super.onStart();

        sp = getSharedPreferences("login", MODE_PRIVATE);

        if (getIntent().getExtras() != null && getIntent().hasExtra("postSlug")) {
            // startFullPostActivity();
            if(sp.getString("isNotification","notification").equals(getIntent().getStringExtra("postSlug")) && sp.getBoolean("First",false))
            {
                //  Toast.makeText(getApplicationContext(),"activity1",Toast.LENGTH_LONG).show();
                pref=sp.edit();
                pref.putBoolean("First",false);
                pref.commit();
                Intent intent=new Intent(this,HomeScreen.class);
                intent.putExtra("FromNotification","True");
                startActivity(intent);
            }
            else {
                // Toast.makeText(getApplicationContext(),"activity2",Toast.LENGTH_LONG).show();
                pref=sp.edit();
                pref.putBoolean("First",true);
                pref.putString("isNotification",getIntent().getStringExtra("postSlug"));
                pref.commit();
                startFullPostActivity();
            }


        }

        else {

            if (sp.getBoolean("logged", false)) {
                goToHomeScreen();

            }
        }
    }
    public void goToHomeScreen(){

        Toast.makeText(getApplicationContext(),"goToHomeScreen",Toast.LENGTH_LONG).show();
        Intent i = new Intent(this,HomeScreen.class);
        startActivity(i);
    }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (Build.VERSION.SDK_INT >= 21) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }

            setContentView(R.layout.activity_landing_page);
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

            viewPager = (ViewPager) findViewById(R.id.view_pager);
            dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
            btnSkip = (Button) findViewById(R.id.btn_skip);
            btnNext = (Button) findViewById(R.id.btn_next);

            layouts = new int[]{
                    R.layout.slide1,
                    R.layout.slide2,
                    R.layout.slide3,
                    R.layout.slide4,};

            // adding bottom dots
            addBottomDots(0);

            // making notification bar transparent
            changeStatusBarColor();

            myViewPagerAdapter = new MyViewPagerAdapter(layouts,this);
            viewPager.setAdapter(myViewPagerAdapter);
            viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

            btnSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchHomeScreen();
                }
            });

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // checking for last page
                    // if last page home screen will be launched
                    int current = getItem(+1);
                    if (current < layouts.length) {
                        // move to next screen
                        viewPager.setCurrentItem(current);
                    } else {
                        launchHomeScreen();
                    }
                }
            });
        }

        private void addBottomDots(int currentPage) {
            dots = new TextView[layouts.length];

            int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
            int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

            dotsLayout.removeAllViews();
            for (int i = 0; i < dots.length; i++) {
                dots[i] = new TextView(this);
                dots[i].setText(Html.fromHtml("&#8226;"));
                dots[i].setTextSize(35);
                dots[i].setTextColor(colorsInactive[currentPage]);
                dotsLayout.addView(dots[i]);
            }

            if (dots.length > 0)
                dots[currentPage].setTextColor(colorsActive[currentPage]);
        }

        private int getItem(int i) {
            return viewPager.getCurrentItem() + i;
        }

        private void launchHomeScreen() {
          //  prefManager.setFirstTimeLaunch(false);
            startActivity(new Intent(LandingPageActivity.this, LoginScreen.class));
            finish();
        }

        //  viewpager change listener
        ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);

                // changing the next button text 'NEXT' / 'GOT IT'
                if (position == layouts.length - 1) {
                    // last page. make button text to GOT IT
                    btnNext.setText("Let's Go");
                    btnSkip.setVisibility(View.GONE);
                } else {
                    // still pages are left
                    btnNext.setText(getString(R.string.next));
                    btnSkip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        };

        /**
         * Making notification bar transparent
         */
        private void changeStatusBarColor() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        }

    private void startFullPostActivity()
    {
        String slug,userName,userProfile;
        int userId;
        for (String key : getIntent().getExtras().keySet()) {
            if(key.equals("postSlug")) {
                slug = getIntent().getStringExtra("postSlug");
                userId=Integer.parseInt(getIntent().getStringExtra("senderID"));
                userName=getIntent().getStringExtra("username");
                userProfile=getIntent().getStringExtra("notificationSenderUrl");
                Log.d("NotificationTag" , key+"____" + slug);

                if(slug.equals(""))
                {
                    Toast.makeText(getApplicationContext(),slug+"1",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, UserProfileActivity.class);
                    intent.putExtra("User_id", userId);
                    intent.putExtra("ProfileUserName", userName);
                    intent.putExtra("ProfileUrl",userProfile);
                    startActivity(intent);

                }
                else if(slug.equals("chat"))
                {
                    Toast.makeText(getApplicationContext(),slug+"2",Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(this, ChatWithAFriendActivity.class);
                    intent.putExtra("FriendId", userId);
                    intent.putExtra("FriendProfileUrl", userProfile);
                    intent.putExtra("FriendName",userName);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),slug+"3",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(this,FullPostActivity.class);
                    intent.putExtra("Post_Slug_ID",slug);
                    startActivity(intent);
                }

                break;

            }
        }
    }
    }
