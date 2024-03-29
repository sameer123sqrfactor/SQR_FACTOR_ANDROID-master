package com.user.sqrfactor.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpViews.LoginScreen;
import com.user.sqrfactor.Activities.SettingModule.SettingViews.Activities.Settings;
import com.user.sqrfactor.Fragments.ValentineEventFragment;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Utils.BadgeView;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Fragments.MenuFragment;
import com.user.sqrfactor.Fragments.MDToast;
import com.user.sqrfactor.Fragments.MessageFragment;
import com.user.sqrfactor.Fragments.NewsFeedFragment;
import com.user.sqrfactor.Fragments.NotificationsFragment;
import com.user.sqrfactor.Pojo.IsOnline;
import com.user.sqrfactor.Pojo.SearchResultClass;
import com.user.sqrfactor.Pojo.TokenClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Adapters.SearchResultAdapter;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeScreen extends ToolbarActivity {

    Toolbar toolbar;
    static TabLayout tabLayout;
    ImageView imageView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private static MySharedPreferences mSp;
    private ImageView profileImage;
    private boolean notificationVisible = false;
    private List<Integer> mBadgeCountList = new ArrayList<Integer>();
    private List<BadgeView> mBadgeViews;
    private int count = 0;
    private SearchResultAdapter searchResultAdapter;
    LinearLayout linearLayout;
    private EditText searchEditText;
    private ImageButton searchClose;
    private SharedPreferences sp;
    public static DatabaseReference ref;
    public static FirebaseDatabase database;
    private ArrayList<SearchResultClass> searchResultClasses=new ArrayList<>();
    private RecyclerView recyclerView;
    BadgeView badge7;
    private FragmentTabHost mTabHost;
    static int count1;
    private UserClass userClass;
    static Context context;
    private  View v,shadowView;
    private static Intent intent;
    private GoogleApiClient mGoogleApiClient;
    private FloatingActionButton fabView, fabStatus, fabDesign, fabArticle;
    private boolean fabExpanded = false;
    private LinearLayout layoutFabStatus;
    private LinearLayout layoutFabDesign;
    private LinearLayout layoutFabArticle;
    Animation rotate_forward, rotate_Backward, fab_open, fab_close;
//    private PopupWindow popupWindow;
//    private LinearLayout layoutOfPopup;
//    private ImageView popupclose, eventImage;
//    ShowEventPopUp();


    @Override
    protected void onResume() {
        super.onResume();
        //  Log.i(TAG, "Setting screen name: " + "CompetitionDetailActivity");
        UserClass userClass=UtilsClass.GetUserClassFromSharedPreferences(HomeScreen.this);
        if(userClass!=null){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            IsOnline isOnline = new IsOnline("True", formatter.format(date));
            ref.child("Status").child(userClass.getUserId() + "").child("android").setValue(isOnline);
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
//        toolbar.setNavigationIcon(R.drawable.profilepic);
        setSupportActionBar(toolbar);



        //ValentineDialog();

        mSp = MySharedPreferences.getInstance(this);
        ActionBar actionBar = getSupportActionBar();
//        actionBar.setLogo(R.drawable.profilepic);
        //
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        actionBar.setHomeAsUpIndicator(R.drawable.hamburger_icon);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitle("feeds");
//        actionBar.setTitle("feeds");
        //actionBar.setTitle();
//

        // If menuFragment is defined, then this activity was launched with a fragment selection






        mSp = MySharedPreferences.getInstance(this);
        sp = getSharedPreferences("login",MODE_PRIVATE);

        final SharedPreferences mPrefs = getSharedPreferences("User", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        userClass = gson.fromJson(json, UserClass.class);

        database= FirebaseDatabase.getInstance();
        ref = database.getReference();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(userClass!=null)
        ref.child("Status").child(userClass.getUserId() + "").child("android").onDisconnect().setValue(new IsOnline("False",formatter.format(new Date())));





//        profileImage = findViewById(R.id.home_profile_image);
//        Glide.with(HomeScreen.this).load(UtilsClass.getParsedImageUrl(userClass.getProfile()))
//                .into(profileImage);

        shadowView=findViewById(R.id.shadowView);

        drawerLayout = findViewById(R.id.drawer_layout);

        linearLayout=(LinearLayout)findViewById(R.id.mainfrag);
       // searchEditText=(EditText)findViewById(R.id.user_search);
       // searchClose =(ImageButton) findViewById(R.id.search_close);
        recyclerView=(RecyclerView)findViewById(R.id.search_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);



        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        searchResultAdapter = new SearchResultAdapter( this,searchResultClasses);
        recyclerView.setAdapter(searchResultAdapter);

//        searchEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                recyclerView.setVisibility(View.VISIBLE);
//                linearLayout.setVisibility(View.INVISIBLE);
//                searchClose.setVisibility(View.VISIBLE);
//                FetchSearchedDataFromServer(s+"");
//
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(s.length()==0)
//                {
//                    recyclerView.setVisibility(View.GONE);
//                    linearLayout.setVisibility(View.VISIBLE);
//
//                }
//            }
//        });
//            searchClose.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    searchEditText.setText("");
//                    recyclerView.setVisibility(View.GONE);
//                    linearLayout.setVisibility(View.VISIBLE);
//                   // searchClose.setVisibility(View.GONE);
//                }
//            });


        SharedPreferences sharedPreferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN", "sqr");
        TokenClass.Token = token;
        TokenClass tokenClass = new TokenClass(token);
        //Log.v("Token1", token);


//        profileImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                drawerLayout.openDrawer(GravityCompat.START);
//
//            }
//        });




        tabLayout = (TabLayout)findViewById(R.id.tabs);

        //tabLayout.setupWithViewPager(pager);

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.newsfeeed3color));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.chatmsg));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.notify4));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.toggle));




        getSupportFragmentManager().beginTransaction().replace(R.id.mainfrag, new NewsFeedFragment()).commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

               // searchEditText.setText("");
                recyclerView.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                //searchClose.setVisibility(View.GONE);

                switch (tab.getPosition()){

                    case 0:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainfrag, new NewsFeedFragment()).commit();
                        tab.setIcon(R.drawable.newsfeeed3color);
                        break;

                    case 1:
                        tab.setIcon(R.drawable.chatmsgcolor);
                        if(tab.getCustomView()!=null) {
                            v = tab.getCustomView().findViewById(R.id.badgeCotainer);
                        }
                        if(v != null) {
                            v.setVisibility(View.GONE);
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainfrag, new MessageFragment()).commit();
                        break;

                    case 2:
                        tab.setIcon(R.drawable.notifycolor1);
                        if(tab.getCustomView()!=null)
                        {
                            v = tab.getCustomView().findViewById(R.id.badgeCotainer);
                        }

                        if(v != null) {
                            v.setVisibility(View.GONE);
                        }
                        if(userClass!=null)
                        ref.child("notification").child(userClass.getUserId()+"").child("unread").removeValue();
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainfrag, new NotificationsFragment()).commit();
                        break;


                    case 3:
                        tab.setIcon(R.drawable.toggle1color);
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainfrag, new MenuFragment()).commit();
                        break;

                }
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                switch (tab.getPosition()){

                    case 0:
                        tab.setIcon(R.drawable.newsfeeed4);
                        break;

                    case 1:
                        tab.setIcon(R.drawable.chatmsg);
                        break;

                    case 2:
                        tab.setIcon(R.drawable.notify4);
                        break;

                    case 3:
                        tab.setIcon(R.drawable.toggle);

                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        for(int i=0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            tab.setBackgroundColor(getResources().getColor(R.color.White));
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            if(i==1){
                p.setMargins(0, 0, 60, 0);
            }else if(i==2){
                p.setMargins(60, 0, 0, 0);
            }

            tab.requestLayout();
        }


        intent=getIntent();
        navigationView = findViewById(R.id.navigation_view);


        View headerLayout = navigationView.inflateHeaderView(R.layout.navigation_drawer);
        ImageView profileImage = (ImageView) headerLayout.findViewById(R.id.profile_image);
        TextView menu_name=(TextView) headerLayout.findViewById(R.id.menu_name);
        TextView menu_designation=(TextView) headerLayout.findViewById(R.id.menu_designation);

        headerLayout.setVisibility(View.VISIBLE);

        if(userClass!=null)
        {
            Glide.with(this).load(UtilsClass.getParsedImageUrl(userClass.getProfile()))
                    .into(profileImage);
            menu_name.setText(UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
        }



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.navigation_profile) {
                    Intent i = new Intent(HomeScreen.this, ProfileActivity.class);
                    //i.putExtra("Activity", "1");
                    startActivity(i);

                }
                if (id == R.id.navigation_credits) {
                    Intent j = new Intent(HomeScreen.this, Credits.class);
                    //j.putExtra("Activity", "2");
                    startActivity(j);

                }
                if (id == R.id.navigation_settings) {
                    Intent intent = new Intent(HomeScreen.this, Settings.class);
                    //intent.putExtra("Activity", "3");
                    startActivity(intent);

                }
                if (id == R.id.navigation_logout) {
//                    if(NetworkUtil.checkNewtWorkSpeed(HomeScreen.this)){
                        sp.edit().putBoolean("logged",false).apply();
                        LoginManager.getInstance().logOut();
                        FirebaseAuth.getInstance().signOut();


                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(Status status) {
                                        //MDToast.makeText(HomeScreen.this, "Logout successfully", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                        Intent i=new Intent(getApplicationContext(), LoginScreen.class);
                                        startActivity(i);
                                    }
                                });
//                    //call api here for logout
//
                        SharedPreferences mPrefs = getSharedPreferences("User", MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = mPrefs.getString("MyObject", "");
                        final UserClass userClass = gson.fromJson(json, UserClass.class);
                        if(userClass!=null){
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("pushNotifications" + userClass.getUserId());
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("chats" + userClass.getUserId());
                        }



                        RequestQueue requestQueue = Volley.newRequestQueue(HomeScreen.this);

                        StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"logout",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.v("logout",response);
                                        MDToast.makeText(HomeScreen.this, response, MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        Date date = new Date();
                                        IsOnline isOnline=new IsOnline("False",formatter.format(date));

                                        ref.child("Status").child(userClass.getUserId()+"").child("android").setValue(isOnline);

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
                               // params.put("Authorization", "Bearer " + TokenClass.Token);
                                params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));

                                return params;
                            }

                        };

//                        requestQueue.add(myReq);
                    // requestQueue.add(myReq);
                    myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    requestQueue.add(myReq);

                    Intent intent = new Intent(HomeScreen.this, LoginScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
//                    }else {
//                        MDToast.makeText(HomeScreen.this, "Bad network connection", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
//
//                    }
                }
                menuItem.setChecked(false);
                drawerLayout.closeDrawers();
                return false;
            }
        });



        fabView = findViewById(R.id.fab_view);
        fabStatus = findViewById(R.id.fab_status);
        fabDesign =findViewById(R.id.fab_design);
        fabArticle = findViewById(R.id.fab_article);

        layoutFabStatus = (LinearLayout) findViewById(R.id.layoutFabStatus);
        layoutFabDesign = (LinearLayout) findViewById(R.id.layoutFabDesign);
        layoutFabArticle = (LinearLayout) findViewById(R.id.layoutFabArticle);

        rotate_forward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotate_Backward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
        fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        fabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabExpanded == true){
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });
        closeSubMenusFab();

        fabStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StatusPostActivity.class);
                intent.putExtra("Fab",1);
                startActivity(intent);
            }
        });
        fabDesign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DesignActivity.class);
                intent.putExtra("Fab",1);
                startActivity(intent);
            }
        });
        fabArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                intent.putExtra("Fab",1);
                startActivity(intent);
            }
        });


    }


    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            //navigationView.getMenu().getItem(0).setCheckable(false);
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
            moveTaskToBack(true);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.home_screen_menu,menu);

        MenuItem searchItem=menu.findItem(R.id.search_icon);
       // SearchView searchView=searchItem.getActionView();
        android.support.v7.widget.SearchView searchView=(android.support.v7.widget.SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length()==0)
                {
                    recyclerView.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()==0){
                    recyclerView.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);

                }else {
                    recyclerView.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.INVISIBLE);
                    // searchClose.setVisibility(View.VISIBLE);
                    FetchSearchedDataFromServer(newText+"");

                }

                return false;
            }
        });

//        searchEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                recyclerView.setVisibility(View.VISIBLE);
//                linearLayout.setVisibility(View.INVISIBLE);
//                searchClose.setVisibility(View.VISIBLE);
//                FetchSearchedDataFromServer(s+"");
//
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(s.length()==0)
//                {
//                    recyclerView.setVisibility(View.GONE);
//                    linearLayout.setVisibility(View.VISIBLE);
//
//                }
//            }
//        });
//        searchClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                searchEditText.setText("");
//                recyclerView.setVisibility(View.GONE);
//                linearLayout.setVisibility(View.VISIBLE);
//                searchClose.setVisibility(View.GONE);
//            }
//        });
//



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);

                return true;

        }

        return super.onOptionsItemSelected(item);
    }
    private void FetchSearchedDataFromServer(final String search) {

        RequestQueue requestQueue = Volley.newRequestQueue(HomeScreen.this);
        // "https://archsqr.in/api/profile/detail/
        StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"parse/search",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("ReponseFeed", response);
                        searchResultClasses.clear();
                        //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject users=jsonObject.getJSONObject("users");
                            JSONArray dataArray=users.getJSONArray("data");
                            for(int i=0;i<dataArray.length();i++)
                            {
                                SearchResultClass searchResultClass=new SearchResultClass(dataArray.getJSONObject(i));
                                searchResultClasses.add(searchResultClass);
                            }
                            searchResultAdapter.notifyDataSetChanged();

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
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("search",search);
                return params;
            }

        };
        requestQueue.add(myReq);

    }

    private void ValentineDialog() {
        new ValentineEventFragment().show(getSupportFragmentManager(), "");

    }


    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        this.context=getApplicationContext();
    }

    public static void  getnotificationCount(){

        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();
       // RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest myReq = new StringRequest(Request.Method.GET, UtilsClass.baseurl+"notificationcount",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("ReponseFeed", response);
//                        Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            count1 =jsonObject.getInt("count");
                            if(count1!=0)
                            {
                                CreateBadgeCount(count1);
                            }



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

        //requestQueue.add(myReq);
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);
    }



     private static void CreateBadgeCount(int count1) {

        if(intent!=null && intent.hasExtra("FromNotification"))
        {
            count1=count1-1;
        }
        if(count1!=0)
        {
            TabLayout.Tab tab1 = tabLayout.getTabAt(2);

            if (tab1.getCustomView() == null)
                tab1.setCustomView(R.layout.badged);

            if (tab1 != null && tab1.getCustomView() != null) {
                TextView b = (TextView) tab1.getCustomView().findViewById(R.id.badge);
                if (b != null) {
                    b.setText(count1 + "");
                }
                View v = tab1.getCustomView().findViewById(R.id.badgeCotainer);
                if (v != null) {
                    v.setVisibility(View.VISIBLE);
                }
            }

        }

    }


    private static void CreateBadgeCount1(int count1) {

        if(count1!=0)
        {
            TabLayout.Tab tab1 = tabLayout.getTabAt(1);

            if (tab1.getCustomView() == null)
                tab1.setCustomView(R.layout.badged);

            if (tab1 != null && tab1.getCustomView() != null) {
                TextView b = (TextView) tab1.getCustomView().findViewById(R.id.badge);
                if (b != null) {
                    b.setText(count1 + "");
                }
                View v = tab1.getCustomView().findViewById(R.id.badgeCotainer);
                if (v != null) {
                    v.setVisibility(View.VISIBLE);
                }
            }

        }

    }


    public static void getUnReadMsgCount(){
        //RequestQueue requestQueue = Volley.newRequestQueue(context);
        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();
        StringRequest myReq = new StringRequest(Request.Method.GET, UtilsClass.baseurl+"unread_counts",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Log.v("ReponseFeed", response);

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            count1 =jsonObject.getInt("Unread Messages");
//                            Toast.makeText(context, "count"+count1, Toast.LENGTH_LONG).show();
                            Log.v("count",count1+"");

                            CreateBadgeCount1(count1);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                        if(count1!=0) {
//                            TabLayout.Tab tab1 = tabLayout.getTabAt(1);
//                            if (tab1.getCustomView() == null)
//                                tab1.setCustomView(R.layout.badged);
//
//                            if (tab1 != null && tab1.getCustomView() != null) {
//                                TextView b = (TextView) tab1.getCustomView().findViewById(R.id.badge);
//                                if (b != null) {
//                                    b.setText(count1 + "");
//                                }
//                                View v = tab1.getCustomView().findViewById(R.id.badgeCotainer);
//                                if (v != null) {
//                                    v.setVisibility(View.VISIBLE);
//                                }
//                            }
//                        }
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

        //requestQueue.add(myReq);
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);
    }

    private void openSubMenusFab(){
        shadowView.setVisibility(View.VISIBLE);
        layoutFabStatus.setVisibility(View.VISIBLE);
        layoutFabDesign.setVisibility(View.VISIBLE);
        layoutFabArticle.setVisibility(View.VISIBLE);
        fabStatus.startAnimation(fab_open);
        fabDesign.setAnimation(fab_open);
        fabArticle.setAnimation(fab_open);
        fabView.startAnimation(rotate_forward);
        fabView.setImageResource(R.drawable.ic_add_black_24dp);
        fabExpanded = true;
    }
    private void closeSubMenusFab(){
        shadowView.setVisibility(View.GONE);
        layoutFabStatus.setVisibility(View.GONE);
        layoutFabDesign.setVisibility(View.GONE);
        layoutFabArticle.setVisibility(View.GONE);
        fabStatus.startAnimation(fab_close);
        fabDesign.setAnimation(fab_close);
        fabArticle.setAnimation(fab_close);
        fabView.startAnimation(rotate_Backward);
        fabExpanded = false;
    }

//    private void changeImageDrawable(){
//        ImageView profileImage = (ImageView) binding.navView.getHeaderView(0).findViewById(R.id.iv_profile_image);
//
//        final Target target = new Target() {
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                profileImage.setImageBitmap(bitmap);
//
//                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
//                binding.mapsContent.toolbar.setNavigationIcon(drawable);
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable errorDrawable) {
//
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//            }
//        };
//
//        profileImage.setTag(target);
////
////        Picasso.with(this)
////                .load("YOUR_IMAGE_URL_HERE")
////                .placeholder(R.drawable.placeholder_profile)
////                .error(R.drawable.placeholder_profile)
////                .into(target);
//    }
}