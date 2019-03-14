package com.user.sqrfactor.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.user.sqrfactor.Activities.SettingModule.SettingViews.Activities.Settings;
import com.user.sqrfactor.Adapters.CreditsAdapter;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Pojo.CreditsClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Storage.MySharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Credits extends AppCompatActivity {

    Toolbar toolbar;
    private ImageView menu;
    //private String CurrentUrl=UtilsClass.baseurl+"profile/detail/sqrfactor/credits";
    private String CurrentUrl= ServerConstants.PROFILE_DETAIL_SQRFATOR_CREDITS;
    private String previousPageUrl=null;
    private String nextPageUrl=null;
    private CreditsAdapter creditsAdapter;
    private Button nextPage,prevPage;
    private RecyclerView recyclerView;
    private TextView profileName,followCnt,followingCnt,portfolioCnt,bluePrintCnt;
    private TextView bluePrint,portfolio,followers,following;
    private ImageButton editProfile;
    private ImageView userProfile,profileImage;
    private TextView Credits_shortBio,Credits_address;
    private MySharedPreferences mSp;
    private boolean isScrolling=false;
    private CoordinatorLayout mCLayout;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCToolbarLayout;
    int currentItems, totalItems, scrolledItems;
    ArrayList<CreditsClass> creditsClassArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credits);

        final UserClass userClass = UtilsClass.GetUserClassFromSharedPreferences(Credits.this);
        mSp = MySharedPreferences.getInstance(this);

        mCLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        mCToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        mToolbar.setTitleTextColor(getResources().getColor(R.color.White));
        setSupportActionBar(mToolbar);

        mSp = MySharedPreferences.getInstance(this);
        mCToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        mCToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.white));

        mToolbar.setNavigationIcon(R.drawable.back_arrow);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        profileName = (TextView) findViewById(R.id.profile_profile_name);
        profileImage = (ImageView) findViewById(R.id.profile_profile_image);
        followCnt = (TextView) findViewById(R.id.profile_followerscnt);
        followingCnt = (TextView) findViewById(R.id.profile_followingcnt);
        portfolioCnt = (TextView) findViewById(R.id.profile_portfoliocnt);
        bluePrintCnt = (TextView) findViewById(R.id.profile_blueprintcnt);

        bluePrint = (TextView)findViewById(R.id.profile_blueprintClick);
        portfolio = (TextView)findViewById(R.id.profile_portfolioClick);
        followers = (TextView)findViewById(R.id.profile_followersClick);
        following = (TextView)findViewById(R.id.profile_followingClick);
        followCnt.setText(userClass.getFollowerCount());
        followingCnt.setText(userClass.getFollowingCount());
        portfolioCnt.setText(userClass.getPortfolioCount());
        bluePrintCnt.setText(userClass.getBlueprintCount());

//        Credits_address=findViewById(R.id.Credits_address);
//        Credits_shortBio=findViewById(R.id.Credits_shortBio);
//
        profileName.setText(UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_credit);
        menu = (ImageView) findViewById(R.id.profile_about_morebtn);
        editProfile =(ImageButton)findViewById(R.id.profile_editprofile);


        final LinearLayoutManager layoutManager = new LinearLayoutManager(Credits.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        nextPage=(Button)findViewById(R.id.nextPage);
        prevPage=(Button)findViewById(R.id.prevPage);
        prevPage.setVisibility(View.GONE);
        creditsAdapter = new CreditsAdapter(this, creditsClassArrayList);
        recyclerView.setAdapter(creditsAdapter);
//
//        toolbar = (Toolbar) findViewById(R.id.toolbar1);
//        toolbar.setTitle("Credits");
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.back_arrow);


        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageRefersh(nextPageUrl);
                prevPage.setVisibility(View.VISIBLE);
            }
        });

        prevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageRefersh(previousPageUrl);
                prevPage.setVisibility(View.GONE);
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pop = new PopupMenu(getApplicationContext(), v);
                pop.getMenu().add(1,1,0,"About "+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
                pop.getMenuInflater().inflate(R.menu.more_menu, pop.getMenu());
                pop.show();

                pop.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){

                            case 1:
                                Intent i = new Intent(getApplicationContext(), About.class);
                                i.putExtra("UserID",userClass.getUserId());
                                i.putExtra("userType",userClass.getUserType());
                                startActivity(i);
                                return true;

                        }
                        return true;
                    }
                });

            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });

        bluePrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Credits.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        portfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Credits.this, PortfolioActivity.class);
                i.putExtra("UserName",userClass.getUser_name());
                startActivity(i);
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Credits.this, FollowersActivity.class);
                i.putExtra("UserName",userClass.getUser_name());
                startActivity(i);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Credits.this, FollowingActivity.class);
                i.putExtra("UserName",userClass.getUser_name());
                startActivity(i);
            }
        });

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
//                    isScrolling = true;
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                currentItems = layoutManager.getChildCount();
//                totalItems = layoutManager.getItemCount();
//                scrolledItems = layoutManager.findFirstVisibleItemPosition();
//                if (isScrolling && (currentItems + scrolledItems == totalItems)) {
//                    isScrolling = false;
//                    fetchData();
//                }
//            }
//        });

        if(userClass!=null){
            Glide.with(this).load(UtilsClass.getParsedImageUrl(userClass.getProfile()))
                    .into(profileImage);
//            if(userClass.getUser_address()!=null && !userClass.getUser_address().equals("null"))
//                Credits_address.setText(userClass.getUser_address());
//            if(userClass.getShort_bio()!=null && !userClass.getShort_bio().equals("null"))
//                Credits_shortBio.setText(userClass.getShort_bio());
        }


        PageRefersh(CurrentUrl);

    }
    public void PageRefersh( String currentUrl) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest myReq = new StringRequest(Request.Method.GET, currentUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("ReponseFeed", response);
//                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        creditsClassArrayList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject post = jsonObject.getJSONObject("posts");
                            nextPageUrl=post.getString("next_page_url");
                            previousPageUrl=post.getString("prev_page_url");
                            JSONArray jsonArrayData = post.getJSONArray("data");
                            for (int i = 0; i < jsonArrayData.length(); i++) {
                                CreditsClass creditsClass = new CreditsClass(jsonArrayData.getJSONObject(i));
                                creditsClassArrayList.add(creditsClass);
                            }
                            creditsAdapter.notifyDataSetChanged();

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

        };

        requestQueue.add(myReq);
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

}