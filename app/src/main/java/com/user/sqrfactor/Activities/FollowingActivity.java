package com.user.sqrfactor.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.user.sqrfactor.Adapters.FollowingAdapter;
import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Pojo.FollowingClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Extras.UtilsClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FollowingActivity extends AppCompatActivity {

    private FollowingAdapter followingAdapter;
    private Toolbar toolbar;
    private RecyclerView recyclerView1;
    private ArrayList<FollowingClass> followingClassArrayList = new ArrayList<>();
    boolean isLoading = false;
    private static String nextPageUrl;
    private ProgressBar progressBar;
    private TextView noFollowing;
    private MySharedPreferences mSp;
    private String userName;
    private LinearLayoutManager layoutManager;
    private Tracker mTracker;

    @Override
    protected void onResume() {
        super.onResume();
        UserClass userClass=UtilsClass.GetUserClassFromSharedPreferences(FollowingActivity.this);
        if(userClass!=null){
            mTracker.setScreenName("FollowingActivity /"+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        //google anlytics
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        toolbar.setNavigationIcon(R.drawable.ic_back_black);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//
        mSp=MySharedPreferences.getInstance(this);
        Intent intent=getIntent();
        if(intent!=null)
        {
            userName=intent.getStringExtra("UserName");
        }


        progressBar=findViewById(R.id.progress_bar_following);
        noFollowing = findViewById(R.id.noFollowing);
        recyclerView1 = findViewById(R.id.recyclerView_following);
        layoutManager = new LinearLayoutManager(FollowingActivity.this);
        recyclerView1.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        DividerItemDecoration decoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView1.addItemDecoration(decoration);

        followingAdapter = new FollowingAdapter(followingClassArrayList,this);
        recyclerView1.setAdapter(followingAdapter);

        recyclerView1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                isLoading=false;

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastId=layoutManager.findLastVisibleItemPosition();

                if(dy>0 && lastId + 3 > layoutManager.getItemCount() && !isLoading)
                {
                    isLoading=true;
                    loadmoreData();

                }
            }
        });

        LoadData();


    }

    public void LoadData()
    {

        RequestQueue requestQueue = Volley.newRequestQueue(FollowingActivity.this);
        StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"profile/follow/"+userName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("FollowingFromServer", response);
                        progressBar.setVisibility(View.GONE);
                        noFollowing.setVisibility(View.GONE);
                      //  Toast.makeText(FollowingActivity.this, response, Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            nextPageUrl = jsonObject.getString("nextPage");
                           // Toast.makeText(FollowingActivity.this, nextPageUrl, Toast.LENGTH_LONG).show();
                            JSONArray follow=jsonObject.getJSONArray("follows");
                            for(int i=0;i<follow.length();i++)
                            {
                                FollowingClass followingClass=new FollowingClass(follow.getJSONObject(i));
                                followingClassArrayList.add(followingClass);
                            }

                            followingAdapter.notifyDataSetChanged();
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
                params.put("Authorization",Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));

                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username",userName);
                params.put("action","following");

                return params;
            }

        };

       // requestQueue.add(myReq);
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(myReq);

        final Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(followingClassArrayList.size()==0)
                {
                    progressBar.setVisibility(View.GONE);
                    noFollowing.setVisibility(View.VISIBLE);
                }else {
                    progressBar.setVisibility(View.GONE);
                    noFollowing.setVisibility(View.GONE);
                }
            }
        }, 1500);

    }

    public void loadmoreData(){

         final ArrayList<FollowingClass> newfollowingClassArrayList = new ArrayList<>();
        if(nextPageUrl!=null) {
            RequestQueue requestQueue = Volley.newRequestQueue(FollowingActivity.this);


            StringRequest myReq = new StringRequest(Request.Method.POST,nextPageUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                nextPageUrl = jsonObject.getString("nextPage");
                                //Toast.makeText(FollowingActivity.this, nextPageUrl, Toast.LENGTH_LONG).show();
                                JSONArray follow = jsonObject.getJSONArray("follows");
                                for (int i = 0; i < follow.length(); i++) {
                                    FollowingClass followingClass = new FollowingClass(follow.getJSONObject(i));
                                    newfollowingClassArrayList.add(followingClass);
                                }

                                followingClassArrayList.addAll(newfollowingClassArrayList);
                                followingAdapter.notifyDataSetChanged();

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
                    params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));

                    return params;
                }


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username",userName);
                    params.put("action","following");

                    return params;
                }

            };

            myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(myReq);
            //requestQueue.add(myReq);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



}