package com.user.sqrfactor.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
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
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Constants.BundleConstants;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Fragments.FillAllDetailsBeforeParticipatingInCompetitinDialog;
import com.user.sqrfactor.Fragments.InfoFragment;
import com.user.sqrfactor.Fragments.OwnCompetitionDialog;
import com.user.sqrfactor.Fragments.ParticipateFirstDialog;
import com.user.sqrfactor.Fragments.PayFirstDialog;
import com.user.sqrfactor.Fragments.RegistrationDialog;
import com.user.sqrfactor.Fragments.ResultsFragment;
import com.user.sqrfactor.Fragments.SubmissionsFragment;
import com.user.sqrfactor.Fragments.SubmitDesignDialog;
import com.user.sqrfactor.Fragments.WallFragment;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Utils.NetworkUtil;
import com.user.sqrfactor.Extras.UtilsClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class CompetitionDetailActivity extends AppCompatActivity {
    private static final String TAG = "CompetitionDetailActivi";

    Toolbar mToolbar;
    String paymentstatus;
    TabLayout mTabLayout;
    boolean isSubmitButtonClicked=false;
    ViewPager mPager;

    ImageView mCoverImageView;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private RequestQueue mRequestQueue;
    private MySharedPreferences mSp;

    public String mCompetitionId;
    private String mParticipateId;
    private String mCompUserId;
    private String mCompetitionTitle;

    String slug;
    private FloatingActionButton participateButton;
    public boolean mIsParticipateBtnSelected=true;
    private FloatingActionMenu mParticipateFabMenu;
    private UserClass userClass;
    private Tracker mTracker;

    //tracker code
    private GoogleAnalytics sAnalytics;
    private Tracker sTracker;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competition_detail);
        userClass = UtilsClass.GetUserClassFromSharedPreferences(getApplicationContext());
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.back_arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
        //sTracker = sAnalytics.newTracker(R.xml.global_tracker);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        mSp = MySharedPreferences.getInstance(this);

        mTabLayout = findViewById(R.id.participate_tablayout);
        mPager = findViewById(R.id.participate_pager);

        mCoverImageView = findViewById(R.id.participate_cover);

        mCollapsingToolbarLayout = findViewById(R.id.comp_detail_collapse);

        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mPager);


        if(getIntent()!=null && getIntent().hasExtra(BundleConstants.SLUG)) {
            slug = getIntent().getStringExtra(BundleConstants.SLUG);
            mCompetitionTitle=getIntent().getStringExtra(BundleConstants.COMPETITION_NAME);
            UtilsClass.slug = slug;
        } else {
            slug=UtilsClass.slug;
        }



        mParticipateFabMenu = findViewById(R.id.participate_fab_menu);
        participateButton = findViewById(R.id.participate_participate);
        FloatingActionButton submitDesignButton = findViewById(R.id.participate_submit);

        mParticipateFabMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mParticipateFabMenu.toggle(true);
            }
        });

        participateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)   {
                mIsParticipateBtnSelected = true;
                String myId = mSp.getKey(SPConstants.USER_ID);
                participateCheckApi();
            }
        });

        submitDesignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsParticipateBtnSelected = false;
                SubmissionCheckApi();
            }
        });

        competitionDetailApi(slug);

    }






    private void SubmissionCheckApi() {

        if (!NetworkUtil.isNetworkAvailable()) {
            mParticipateFabMenu.setVisibility(View.VISIBLE);
            return;
        }

        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, ServerConstants.CHECK_SUBMISSION_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject responseObject = new JSONObject(response);

                    if(responseObject.getString("payment_status").equals("1")){
                        Intent i = new Intent(CompetitionDetailActivity.this, SubmitActivity.class);
                        final String slug = getIntent().getStringExtra(BundleConstants.SLUG);
                        i.putExtra(BundleConstants.SLUG,slug);
                        startActivity(i);
                    }else {
                        participateFirstDialog(responseObject.getString("message"));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mParticipateFabMenu.setVisibility(View.VISIBLE);
                NetworkUtil.handleSimpleVolleyRequestError(error, CompetitionDetailActivity.this);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
                headers.put(getString(R.string.accept), getString(R.string.application_json));
                headers.put(getString(R.string.authorization), Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));

                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("competition_id",mCompetitionId);
                params.put("user_id", userClass.getUserId()+"");
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);


    }

    private void ownCompDialog() {
        new OwnCompetitionDialog().show(getSupportFragmentManager(), "");

    }

    public void participateCheckApi() {

        if (!NetworkUtil.isNetworkAvailable()) {
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
            return;
        }
        // Toast.makeText(getApplicationContext(),"calling",Toast.LENGTH_LONG).show();
        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        // mRequestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, ServerConstants.PARTICIPATE_CHECK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (mIsParticipateBtnSelected) {
                    try {
                        JSONObject response1 = new JSONObject(response);
                        if (response1.getInt("code")==4) { // user hasn't participated yet
                            ownCompDialog();

                        } else if(response1.getInt("code")==3) {
                            Intent i = new Intent(CompetitionDetailActivity.this, ParticipateActivity.class);
                            i.putExtra(BundleConstants.COMPETITION_ID, mCompetitionId);
                            startActivity(i);

                        } else if(response1.getInt("code")==5) {
                            Intent i = new Intent(getApplicationContext(), PaymentConfirmActivity.class);
                            i.putExtra(BundleConstants.COMPETITION_ID, mCompetitionId);
                            startActivity(i);


                        } else if(response1.getInt("code")==2) {
                            alreadyParticipatedDialog();
                        } else if(response1.getInt("code")==1) {
                            fillDetailsBeforeParticipatingDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        if (!responseObject.isNull("Message")) { // user hasn't participated yet

                            //participateFirstDialog();
                        } else {
                            JSONArray participantDataArray = responseObject.getJSONArray("Participant Data");
                            JSONObject participantDataObj = participantDataArray.getJSONObject(0);
                            String paymentStatus = participantDataObj.getString("Payment_Status");
                            if (paymentStatus.equals("0")) { // user hasn't paid yet
                                payFirstDialog();

                            } else if (paymentStatus.equals("success") || paymentStatus.equals("VERIFIED")) { // user has paid
                                Intent i = new Intent(CompetitionDetailActivity.this, SubmitActivity.class);
                                final String slug = getIntent().getStringExtra(BundleConstants.SLUG);
                                i.putExtra(BundleConstants.SLUG,slug);
                                startActivity(i);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        Log.v("chat",res);
                        Toast.makeText(getApplicationContext(),res,Toast.LENGTH_LONG).show();
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                    } catch (UnsupportedEncodingException e1) {
                        Toast.makeText(getApplicationContext(),e1.toString(),Toast.LENGTH_LONG).show();
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        Toast.makeText(getApplicationContext(),e2.toString(),Toast.LENGTH_LONG).show();
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
                headers.put(getString(R.string.accept), getString(R.string.application_json));
                headers.put(getString(R.string.authorization), Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
                return headers;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("competition_id",mCompetitionId);
                params.put("PaymentStatus",paymentstatus);
                return params;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }


    private void GoTOSubmitActivity() {
        Intent i = new Intent(CompetitionDetailActivity.this, SubmitActivity.class);
        final String slug = getIntent().getStringExtra(BundleConstants.SLUG);
        i.putExtra(BundleConstants.SLUG,slug);
        startActivity(i);
    }
    private void payFirstDialog() {
        new PayFirstDialog().show(getSupportFragmentManager(), "");
    }

    private void participateFirstDialog(String message) {
        ParticipateFirstDialog participateDialog = new ParticipateFirstDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString(BundleConstants.COMPETITION_ID, mCompetitionId);
        participateDialog.setArguments(args);
        participateDialog.show(getSupportFragmentManager(), "");

    }

    private void alreadyParticipatedDialog() {
        new RegistrationDialog().show(getSupportFragmentManager(), "");

    }
    private void fillDetailsBeforeParticipatingDialog() {
        new FillAllDetailsBeforeParticipatingInCompetitinDialog().show(getSupportFragmentManager(), "");

    }

    private void submitDesignDialog() {
        new SubmitDesignDialog().show(getSupportFragmentManager(), "");

    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new InfoFragment();
                case 1:
                    return new WallFragment();
                case 2:
                    return new SubmissionsFragment();
                case 3:
                    return new ResultsFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Info";
                case 1:
                    return "Wall";
                case 2:
                    return "Submissions";
                case 3:
                    return "Results";
            }
            return null;
        }
    }


    private void competitionDetailApi(String slug) {
        mParticipateFabMenu.setVisibility(View.GONE);
        if (!NetworkUtil.isNetworkAvailable()) {
            //Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
            mParticipateFabMenu.setVisibility(View.VISIBLE);
            return;
        }

        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        // Log.d(TAG, "SHIVANI: url = " + ServerConstants.COMPETITION_DETAIL + slug);
        StringRequest request = new StringRequest(Request.Method.GET, ServerConstants.COMPETITION_DETAIL + slug, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mParticipateFabMenu.setVisibility(View.VISIBLE);

                Log.d(TAG, "competition detail response = " + response);
//                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();

                try {
                    JSONObject responseObject = new JSONObject(response);
                    if(responseObject.has("paymentstatus"))
                    {

                        paymentstatus=responseObject.getJSONObject("paymentstatus").getString("Payment_Status");
                        // Toast.makeText(getApplicationContext(),"payment"+paymentstatus,Toast.LENGTH_LONG).show();

                    }
                    int wallcount = responseObject.getInt("wall_question_no");
                    mTabLayout.getTabAt(1).setText("Wall"+"("+wallcount+")");


                    Log.d(TAG, "onResponse: paymentStatus id = " + paymentstatus);
                    JSONObject competitionObj = responseObject.getJSONObject("competition");

                    mCompetitionId = competitionObj.getString("id");
                    Log.d(TAG, "onResponse: competition id = " + mCompetitionId);
                    // Toast.makeText(getApplicationContext(),"compet"+mCompetitionId,Toast.LENGTH_LONG).show();

                    Log.v("compilerandpyment",mCompetitionId+" "+paymentstatus);
                    mCompUserId = competitionObj.getString("user_id");
                    Log.d(TAG, "onResponse: competition user id = " + mCompUserId);

                    String competitionTitle = competitionObj.getString("competition_title");
                    mCompetitionTitle=competitionTitle;
                    Log.d(TAG, "onResponse: competition title = " + competitionTitle);
                    mCollapsingToolbarLayout.setTitle(competitionTitle);

                    String coverImageUrl = competitionObj.getString("cover_image");
                    // Toast.makeText(getApplicationContext(),coverImageUrl,Toast.LENGTH_LONG).show();
//                    Log.d(TAG, "onResponse: cover image url = " + coverImageUrl);

                    Glide.with(getApplicationContext()).load(UtilsClass.getParsedImageUrl(coverImageUrl))
                            .into(mCoverImageView);
                    // Picasso.get().load(ServerConstants.IMAGE_BASE_URL + coverImageUrl).into(mCoverImageView);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mParticipateFabMenu.setVisibility(View.VISIBLE);
                NetworkUtil.handleSimpleVolleyRequestError(error, CompetitionDetailActivity.this);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
                headers.put(getString(R.string.accept), getString(R.string.application_json));
                headers.put(getString(R.string.authorization), Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));

                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);

    }

    @Override
    protected void onResume() {
        super.onResume();

       // Log.i(TAG, "Setting screen name: " + name);
        if(mCompetitionTitle!=null && !TextUtils.isEmpty(mCompetitionTitle))
        mTracker.setScreenName("CompetitionDetailActivity /"+mCompetitionTitle+" / "+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
       else
            mTracker.setScreenName("CompetitionDetailActivity /"+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }
}
