package com.user.sqrfactor.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Fragments.MDToast;
import com.user.sqrfactor.Pojo.NewsFeedStatus;
import com.user.sqrfactor.Pojo.PostDataClass;
import com.user.sqrfactor.Pojo.ProfileClass1;
import com.user.sqrfactor.Pojo.PushNotificationClass;
import com.user.sqrfactor.Pojo.TokenClass;
import com.user.sqrfactor.Pojo.UserFollowClass;
import com.user.sqrfactor.Pojo.UserProfileClass;
import com.user.sqrfactor.Pojo.from_user;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Adapters.UserProfileAdapter;
import com.user.sqrfactor.Extras.UtilsClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView morebtn, coverImage, userProfileImage;
    private TextView userName, followCnt, followingCnt, portfolioCnt, bluePrintCnt;
    private Button followbtn;
    private ImageButton messagebtn;
    private MySharedPreferences mSp;
    private static ArrayList<NewsFeedStatus> userProfileClassArrayList = new ArrayList<>();
    private ArrayList<PostDataClass> userFollowClassList = new ArrayList<>();
    private TextView userBlueprint, userPortfolio, userFollowers, userFollowing,user_short_bio;
    LinearLayoutManager layoutManager;
    static UserProfileAdapter userProfileAdapter;
    private static Context context;
    RecyclerView recyclerView;
    private String friendProfileUrl,userType;
    boolean flag = false,isFollowing=false;
    private String profileNameOfUser;
    private int user_id,followersCount;
    int count=0;
    private ProgressBar progressBar;
    public  String nextPageUrl;
    private boolean isLoading =false;
    private CoordinatorLayout mCLayout;
    private Toolbar mToolbar;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private CollapsingToolbarLayout mCToolbarLayout;
    private TextView noData;
    private AppBarLayout appBarLayout;

    @Override
    protected void onResume() {
        super.onResume();
       // mTracker.setScreenName("statusFragment/"+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
        //mTracker.send(new HitBuilders.ScreenViewBuilder().build());


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);



        context=getApplicationContext();

        mCLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout1);
        mCToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout1);
        mToolbar = (Toolbar) findViewById(R.id.toolbar1);
//        mToolbar.setTitleTextColor(getResources().getColor(R.color.White));
        setSupportActionBar(mToolbar);

        mSp = MySharedPreferences.getInstance(this);
        mCToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.black));
        mCToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.black));

        mToolbar.setNavigationIcon(R.drawable.ic_back_black);

//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });


        appBarLayout=(AppBarLayout) findViewById(R.id.profileAppBar);
        progressBar=findViewById(R.id.progress_bar_user_profile);
        noData = findViewById(R.id.user_noData);
        Intent intent = getIntent();

        user_id = intent.getIntExtra("User_id", 0);
        profileNameOfUser = intent.getStringExtra("ProfileUserName");
        friendProfileUrl=intent.getStringExtra("ProfileUrl");
        userType=intent.getStringExtra("UserType");
       // Toast.makeText(getApplicationContext(), user_id + " " + profileNameOfUser+" "+userType, Toast.LENGTH_LONG).show();

        SharedPreferences sharedPreferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN", "sqr");
        TokenClass.Token=token;
//        LoadData();

        user_short_bio=(TextView)findViewById(R.id.user_short_bio);
        messagebtn = (ImageButton) findViewById(R.id.user_messagebtn);
        coverImage = (ImageView) findViewById(R.id.user_coverImage);
        userName = (TextView) findViewById(R.id.user_name);
        followCnt = (TextView) findViewById(R.id.user_followers);
        followingCnt = (TextView) findViewById(R.id.user_following);
        portfolioCnt = (TextView) findViewById(R.id.user_portfolio);
        bluePrintCnt = (TextView) findViewById(R.id.user_blueprint);
        userProfileImage = (ImageView) findViewById(R.id.user_image);
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder imageDialog = new AlertDialog.Builder(UserProfileActivity.this);
                LayoutInflater inflater = (LayoutInflater) UserProfileActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);

                View layout = inflater.inflate(R.layout.custom_fullimage_dialog,
                        (ViewGroup) findViewById(R.id.layout_root));
                ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
                image.setImageDrawable(userProfileImage.getDrawable());
                imageDialog.setView(layout);
                imageDialog.create();
                imageDialog.show();
            }
        });

        recyclerView = findViewById(R.id.user_profile_recycler);
//        progressBar = rootView.findViewById(R.id.progress_bar_status);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        userProfileAdapter = new UserProfileAdapter(this, userProfileClassArrayList);
        recyclerView.setAdapter(userProfileAdapter);


        morebtn = (ImageView) findViewById(R.id.user_morebtn);

        userBlueprint = (TextView) findViewById(R.id.user_blueprintClick);
        userPortfolio = (TextView) findViewById(R.id.user_portfolioClick);
        userFollowers = (TextView) findViewById(R.id.user_followersClick);
        userFollowing = (TextView) findViewById(R.id.user_followingClick);


        userBlueprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(UserProfileActivity.this, BlueprintActivity.class);
                //startActivity(i);
                LoadData();

            }
        });

        userPortfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfileActivity.this, PortfolioActivity.class);
                i.putExtra("UserName", profileNameOfUser);
                startActivity(i);
            }
        });

        userFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfileActivity.this, FollowersActivity.class);
                i.putExtra("UserName", profileNameOfUser);
                startActivity(i);
            }
        });

        userFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfileActivity.this, FollowingActivity.class);
                i.putExtra("UserName", profileNameOfUser);
                startActivity(i);
            }
        });
        followbtn = (Button) findViewById(R.id.user_followButton);
        followbtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                FollowMethod();
            }


        });

        messagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context,ChatWithAFriendActivity.class);
                intent.putExtra("FriendId",user_id);
               // intent.putExtra("FriendName",profileNameOfUser);
                intent.putExtra("FriendName",userName.getText().toString());
                intent.putExtra("FriendProfileUrl",friendProfileUrl);
                intent.putExtra("isOnline","True");
                startActivity(intent);



            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                isLoading=false;
                //Toast.makeText(context,"moving down",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                int lastId=layoutManager.findLastVisibleItemPosition();
//                if(dy>0)
//                {
//                    Toast.makeText(context,"moving up",Toast.LENGTH_SHORT).show();
//                }
                if(dy>0 && lastId + 3 > layoutManager.getItemCount() && !isLoading)
                {
                    isLoading=true;
                    Log.v("rolling",layoutManager.getChildCount()+" "+layoutManager.getItemCount()+" "+layoutManager.findLastVisibleItemPosition()+" "+
                            layoutManager.findLastVisibleItemPosition());
                    LoadMoreDataFromServer();

                }
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(final AppBarLayout appBarLayout, int verticalOffset) {
                //Initialize the size of the scroll
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                //Check if the view is collapsed
                if (scrollRange + verticalOffset == 0) {
                    mCToolbarLayout.setTitle(userName.getText().toString());
                    mToolbar.setTitleTextColor(getResources().getColor(R.color.sqr));
                    mToolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                }else{
                    mCToolbarLayout.setTitle("");
                    mToolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.sqr_fade));
                }
            }
        });



        //FollowMethod();
        LoadData();

    }



    public void FollowMethod() {

        RequestQueue requestQueue1 = Volley.newRequestQueue(getApplicationContext());


        StringRequest stringRequest = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"follow_user",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
//                        Toast.makeText(UserProfileActivity.this, s, Toast.LENGTH_LONG).show();
//                        Log.v("ResponseFollow", s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            UserFollowClass userFollowClass = new UserFollowClass(jsonObject);
                            flag = userFollowClass.isReturnType();


                            if (flag == false && isFollowing==true) {

                                followersCount=followersCount-1;
                                followCnt.setText(followersCount+"");
                                followbtn.setText("Follow");
                                followbtn.setTextColor(getResources().getColor(R.color.black));
                                followbtn.setBackgroundResource(R.drawable.edit_profile);
                                //flag = true;
                            } else if(flag== true && isFollowing==true){
                                followersCount=followersCount+1;
                                followCnt.setText(followersCount+"");
                                followbtn.setText("Following");
                                followbtn.setTextColor(getResources().getColor(R.color.white));
                                followbtn.setBackgroundResource(R.drawable.following_button);
                                BuildNotification();
                                MDToast.makeText(UserProfileActivity.this, "You have started following "+profileNameOfUser, MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                //flag = false;
                            }
                            else if( flag==true && isFollowing==false)
                            {
                                followersCount=followersCount+1;
                                followCnt.setText(followersCount+"");
                                followbtn.setText("Following");
                                followbtn.setTextColor(getResources().getColor(R.color.white));
                                followbtn.setBackgroundResource(R.drawable.following_button);
                                BuildNotification();
                                MDToast.makeText(UserProfileActivity.this, "You have started following "+profileNameOfUser, MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();

                            }
                            else if( flag==false && isFollowing==false)
                            {
                                followersCount=followersCount-1;
                                followCnt.setText(followersCount+"");
                                followbtn.setText("Follow");
                                followbtn.setTextColor(getResources().getColor(R.color.black));
                                followbtn.setBackgroundResource(R.drawable.edit_profile);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences sharedPreferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
                String token = sharedPreferences.getString("TOKEN", "sqr");
                TokenClass.Token=token;
                params.put("Accept", "application/json");
                params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));

                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("to_user", user_id + "");
                return params;
            }
        };

        //Adding request to the queue
        requestQueue1.add(stringRequest);

    }

    public void BuildNotification()
    {
        PushNotificationClass pushNotificationClass;
        from_user fromUser;
        database= FirebaseDatabase.getInstance();
        ref = database.getReference();
        SharedPreferences mPrefs =context.getSharedPreferences("User",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        UserClass userClass = gson.fromJson(json, UserClass.class);
        //post post1=new post(""," "," "," ",1);
        if(userClass.getName()!="null")
        {
            fromUser=new from_user(userClass.getEmail(),userClass.getName(),userClass.getUserId(),userClass.getUser_name(),userClass.getProfile());

            pushNotificationClass=new PushNotificationClass(" started following you ",new Date().getTime(),fromUser,"follow");
        }
        else
        {
            fromUser=new from_user(userClass.getEmail(),userClass.getFirst_name()+" "+userClass.getLast_name(),userClass.getUserId(),userClass.getUser_name(),userClass.getProfile());
            pushNotificationClass=new PushNotificationClass(" started following you ",new Date().getTime(),fromUser,"follow");
        }

        String key =ref.child("notification").child(user_id+"").child("all").push().getKey();
        ref.child("notification").child(user_id+"").child("all").child(key).setValue(pushNotificationClass);
        Map<String,String> unred=new HashMap<>();
        unred.put("unread",key);
        ref.child("notification").child(user_id+"").child("unread").child(key).setValue(unred);


    }


    public void LoadData() {

        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        if(userProfileClassArrayList!=null)
            userProfileClassArrayList.clear();


        StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"profile/detail/" + profileNameOfUser,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // Toast.makeText(UserProfileActivity.this, response, Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        noData.setVisibility(View.GONE);
                       // Log.v("MorenewsFeedFromServer", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            followCnt.setText(jsonObject.getString("followerCnt"));
                            followingCnt.setText(jsonObject.getString("followingCnt"));
                            portfolioCnt.setText(jsonObject.getString("portfolioCnt"));
                            bluePrintCnt.setText(jsonObject.getString("blueprintCnt"));

                            followbtn.setText(jsonObject.getString("isfollowing"));

                            userName.setText(UtilsClass.getName(jsonObject.getJSONObject("user").getString("first_name"),jsonObject.getJSONObject("user").getString("last_name"),jsonObject.getJSONObject("user").getString("name")
                            ,jsonObject.getJSONObject("user").getString("user_name")));

                            BindNameToMoreButton(userName.getText().toString());

//                            mCToolbarLayout.setTitle(userName.getText().toString());
//                            mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
//
                            followersCount=Integer.parseInt(jsonObject.getString("followerCnt"));
                            if(jsonObject.getString("isfollowing").equals("Following"))
                            {
                                isFollowing=true;
                                followbtn.setTextColor(getResources().getColor(R.color.white));
                                followbtn.setBackgroundResource(R.drawable.following_button);

                            }
                            else {
                                isFollowing=false;
                                followbtn.setBackgroundResource(R.drawable.edit_profile);
                                followbtn.setTextColor(getResources().getColor(R.color.black));
                            }

                            Glide.with(getApplicationContext()).load( UtilsClass.getParsedImageUrl(jsonObject.getJSONObject("user").getString("profile")))
                                        .into(userProfileImage);

                            JSONObject jsonPost = jsonObject.getJSONObject("posts");

//                            UserProfileClass userProfileClass=null;
                            if(jsonPost!=null) {
                                nextPageUrl=jsonObject.getString("nextPage");
                                new UserProfileActivityParser(jsonObject).execute();
                               // userProfileClass= new UserProfileClass(jsonObject);
//                                userProfileClassArrayList.addAll(userProfileClass.getPostDataClassArrayList());
//
//                                recyclerView.getRecycledViewPool().clear();
//                                userProfileAdapter.notifyDataSetChanged();
                            }

//
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
                params.put("username", profileNameOfUser );
                return params;
            }

        };

        requestQueue2.add(myReq);
        final Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(userProfileClassArrayList.size()==0)
                {
                    progressBar.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);

                }else {
                    progressBar.setVisibility(View.GONE);
                    noData.setVisibility(View.GONE);
                }
            }
        }, 2000);

    }

    public void BindNameToMoreButton(final String name)
    {
        morebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pop = new PopupMenu(getApplicationContext(), v);
                pop.getMenu().add(1,1,0,"About "+name);
                pop.getMenuInflater().inflate(R.menu.more_menu, pop.getMenu());
                pop.show();

                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case 1:
                                Toast.makeText(getApplicationContext(),"morebtn"+user_id,Toast.LENGTH_LONG).show();
                                Intent i = new Intent(getApplicationContext(), About.class);
                                i.putExtra("UserID",user_id);
                                i.putExtra("userType",userType);
                                startActivity(i);
                                return true;

                        }
                        return true;
                    }
                });

            }
        });

    }

    public  void LoadMoreDataFromServer() {
        if (nextPageUrl != null && !TextUtils.isEmpty(nextPageUrl)) {
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            StringRequest myReq = new StringRequest(Request.Method.POST, nextPageUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            Log.v("MorenewsFeedFromServer", response);
                            //Toast.makeText(, response, Toast.LENGTH_LONG).show();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                nextPageUrl = jsonObject.getString("nextPage");
                                new UserProfileActivityParser(jsonObject).execute();

//                                UserProfileClass userProfileClass = new UserProfileClass(jsonObject);
//                                userProfileClassArrayList.addAll(userProfileClass.getPostDataClassArrayList());
//
//                                userProfileAdapter.notifyDataSetChanged();
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
                                    // Now you can use any deserializer to make sense of data
                                    JSONObject obj = new JSONObject(res);
                                } catch (UnsupportedEncodingException e1) {
                                    // Couldn't properly decode data to string
                                    e1.printStackTrace();
                                } catch (JSONException e2) {
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
                    params.put("username", profileNameOfUser );
                    return params;
                }

            };

            requestQueue.add(myReq);
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        //MyApplication.getInstance().trackScreenView("Profile Activity");
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

    public class UserProfileActivityParser extends AsyncTask {
        private JSONObject jsonObject;

        public UserProfileActivityParser(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            UserProfileClass userProfileClass= new UserProfileClass(jsonObject);
            userProfileClassArrayList.addAll(userProfileClass.getPostDataClassArrayList());


            // profileClassList.addAll(NewprofileClassList);

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            recyclerView.getRecycledViewPool().clear();
            userProfileAdapter.notifyDataSetChanged();
            if(userProfileClassArrayList.size()==0) {
                progressBar.setVisibility(View.GONE);
                noData.setVisibility(View.VISIBLE);

            }else {
                progressBar.setVisibility(View.GONE);
                noData.setVisibility(View.GONE);
            }

        }


    }

}