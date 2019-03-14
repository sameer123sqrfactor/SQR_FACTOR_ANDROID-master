package com.user.sqrfactor.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.widget.PullRefreshLayout;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Fragments.MDToast;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.NewsFeedStatus;
import com.user.sqrfactor.Pojo.SearchResultClass;
import com.user.sqrfactor.Pojo.TokenClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Adapters.RedAdapter;
import com.user.sqrfactor.Adapters.SearchResultAdapter;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Utils.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RedActivity extends AppCompatActivity {
    Toolbar toolbar;
    private ArrayList<NewsFeedStatus> newsstatus = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView recyclerView1;
    private boolean isScrolling=false;
    private boolean isLoading=false;
    int currentItems, totalItems, scrolledItems;
    private RedAdapter redAdapter;
    private LinearLayoutManager layoutManager,layoutManager1;
    private Button btn1,btn2,btn3;
    private String nextUrl;
    PullRefreshLayout layout;
    LinearLayout linearLayout;
    private ImageButton searchClose;
    private EditText searchEditText;
    private SearchResultAdapter searchResultAdapter;
    private ArrayList<SearchResultClass> searchResultClasses=new ArrayList<>();
    private MySharedPreferences mSp;
    FloatingActionButton fabView, fabStatus, fabDesign, fabArticle;
    private boolean fabExpanded = false;
    private LinearLayout layoutFabStatus;
    private LinearLayout layoutFabDesign;
    private LinearLayout layoutFabArticle;
    private ProgressBar progress_bar;
    Animation rotate_forward, rotate_Backward, fab_open, fab_close;

    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red);
        //google analytics code
        com.user.sqrfactor.Application.MyApplication myApplication=(MyApplication) getApplication();
        mTracker=myApplication.getDefaultTracker();
        mSp = MySharedPreferences.getInstance(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.red_recycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration decoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(decoration);
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        redAdapter = new RedAdapter(this, newsstatus);
        recyclerView.setAdapter(redAdapter);
        progress_bar=findViewById(R.id.progress_bar_red);

        final SkeletonScreen skeletonScreen = Skeleton.bind(recyclerView)
                .adapter(redAdapter)
                .shimmer(true)
                .angle(20)
                .frozen(false)
                .duration(1200)
                .count(10)
                .load(R.layout.item_skeleton_news)
                .show(); //default count is 10
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                skeletonScreen.hide();
            }
        }, 1500);

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               // finish();
//                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
//            }
//        });


        linearLayout = findViewById(R.id.linear_red);
        //searchEditText=(EditText)findViewById(R.id.user_search);
        //searchClose =(ImageButton) findViewById(R.id.search_close);

        recyclerView1=(RecyclerView)findViewById(R.id.search_recycler1);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);



        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView1.setLayoutManager(layoutManager1);
        searchResultAdapter = new SearchResultAdapter(this,searchResultClasses);
        recyclerView1.setAdapter(searchResultAdapter);

//        searchEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                //Toast.makeText(getApplicationContext(),s+"",Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                //Toast.makeText(getApplicationContext(),s+"",Toast.LENGTH_SHORT).show();
//                recyclerView1.setVisibility(View.VISIBLE);
//                linearLayout.setVisibility(View.INVISIBLE);
//                searchClose.setVisibility(View.VISIBLE);
//                FetchSearchedDataFromServer(s+"");
//
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(s.length()==0) {
//                    recyclerView1.setVisibility(View.GONE);
//                    linearLayout.setVisibility(View.VISIBLE);
//
//                }
//            }
//        });
//        searchClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                searchEditText.setText("");
//                recyclerView1.setVisibility(View.GONE);
//                linearLayout.setVisibility(View.VISIBLE);
//                searchClose.setVisibility(View.GONE);
//            }
//        });
//

        btn1 = findViewById(R.id.red_newsFeedbtn);
        btn2 = findViewById(R.id.red_whatsRedbtn);
        btn3 = findViewById(R.id.red_Topbtn);

        layout = findViewById(R.id.red_pullToRefresh);

        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //LoadNewsFeedDataFromServer();
                //layout.setRefreshing(false);
                layout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout.setRefreshing(false);
                        //layout.setColor(getResources().getColor(R.color.sqr));
                       // layout.setRefreshStyle(PullRefreshLayout.STYLE_WATER_DROP);
                        PageRefersh();
                    }
                },2000);

            }
        });

       // layout.setRefreshing(false);


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RedActivity.this,ContributorsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);

            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 ||dy<0 && fabView.isShown()) {
                    fabView.hide();
                }
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fabView.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        fabView = findViewById(R.id.fab_view);
        fabStatus = findViewById(R.id.fab_status);
        fabDesign = findViewById(R.id.fab_design);
        fabArticle = findViewById(R.id.fab_article);

        layoutFabStatus = (LinearLayout)findViewById(R.id.layoutFabStatus);
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
                Intent intent = new Intent(RedActivity.this, StatusPostActivity.class);
                intent.putExtra("Fab",1);
                startActivity(intent);
            }
        });
        fabDesign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RedActivity.this, DesignActivity.class);
                intent.putExtra("Fab",1);
                startActivity(intent);
            }
        });

        fabArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RedActivity.this, ArticleActivity.class);
                intent.putExtra("Fab",1);
                startActivity(intent);
            }
        });

        recyclerView.setOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if (isLastItemDisplaying(recyclerView)) {
                            fetchDataFromServer();
                        }
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                isLoading=false;
//
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int lastId=layoutManager.findLastVisibleItemPosition();
//                if(dy>0 && lastId + 3 > layoutManager.getItemCount() && !isLoading) {
//                    isLoading=true;
////                    Log.v("rolling",layoutManager.getChildCount()+" "+layoutManager.getItemCount()+" "+layoutManager.findLastVisibleItemPosition()+" "+
////                            layoutManager.findLastVisibleItemPosition());
//                    fetchDataFromServer();
//                }
//            }
//        });
        PageRefersh();
    }
    private void openSubMenusFab(){
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
        layoutFabStatus.setVisibility(View.GONE);
        layoutFabDesign.setVisibility(View.GONE);
        layoutFabArticle.setVisibility(View.GONE);
        fabStatus.startAnimation(fab_close);
        fabDesign.setAnimation(fab_close);
        fabArticle.setAnimation(fab_close);
        fabView.startAnimation(rotate_Backward);
        fabExpanded = false;
    }

    public void PageRefersh(){
       // if(NetworkUtil.checkNewtWorkSpeed(this)){
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            // RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();

            // final int prevSize = newsstatus.size();
            if(newsstatus!=null && newsstatus.size()>0) {
                newsstatus.clear();
            }
            StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"whats-red",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("RedFeed", response);
//                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                nextUrl = jsonObject.getString("nextPage");
                                JSONObject jsonPost = jsonObject.getJSONObject("posts");
                                JSONArray jsonArrayData = jsonPost.getJSONArray("data");
                                for (int i = 0; i < jsonArrayData.length(); i++) {
                                    NewsFeedStatus newsFeedStatus1 = new NewsFeedStatus(jsonArrayData.getJSONObject(i));
                                    newsstatus.add(newsFeedStatus1);

                                }
                                recyclerView.getRecycledViewPool().clear();

                                //  redAdapter.notifyItemRangeInserted(prevSize, newsstatus.size() -prevSize);
                                redAdapter.notifyDataSetChanged();
                                progress_bar.setVisibility(View.GONE);


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

//                @Override
//                protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                    try {
//                        Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
//                        if (cacheEntry == null) {
//                            cacheEntry = new Cache.Entry();
//                        }
//                        final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
//                        final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
//                        long now = System.currentTimeMillis();
//                        final long softExpire = now + cacheHitButRefreshed;
//                        final long ttl = now + cacheExpired;
//                        cacheEntry.data = response.data;
//                        cacheEntry.softTtl = softExpire;
//                        cacheEntry.ttl = ttl;
//                        String headerValue;
//                        headerValue = response.headers.get("Red");
//                        if (headerValue != null) {
//                            cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
//                        }
//                        headerValue = response.headers.get("Last-Modified");
//                        if (headerValue != null) {
//                            cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
//                        }
//                        cacheEntry.responseHeaders = response.headers;
//                        final String jsonString = new String(response.data,
//                                HttpHeaderParser.parseCharset(response.headers));
//                        return Response.success(new String(jsonString), cacheEntry);
//                    } catch (UnsupportedEncodingException e) {
//                        return Response.error(new ParseError(e));
//                    }
//                }
//
//                @Override
//                protected void deliverResponse(String response) {
//                    super.deliverResponse(response);
//                }
//
//                @Override
//                public void deliverError(VolleyError error) {
//                    super.deliverError(error);
//                }
//
//                @Override
//                protected VolleyError parseNetworkError(VolleyError volleyError) {
//                    return super.parseNetworkError(volleyError);
//                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Accept", "application/json");
                    params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));

                    return params;
                }

            };
            myReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(myReq);
//        }else {
//            MDToast.makeText(this, "Bad network connection", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
//
//        }

    }
    public void fetchDataFromServer() {
       final ArrayList<NewsFeedStatus> Newnewsstatus = new ArrayList<>();

        if (nextUrl != null) {
           // RequestQueue requestQueue = Volley.newRequestQueue(this);
            RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();
            StringRequest myReq = new StringRequest(Request.Method.POST, nextUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progress_bar.setVisibility(View.GONE);
                                JSONObject jsonObject = new JSONObject(response);
                                nextUrl = jsonObject.getString("nextPage");
                                JSONObject jsonPost = jsonObject.getJSONObject("posts");
                                JSONArray jsonArrayData = jsonPost.getJSONArray("data");
                                for (int i = 0; i < jsonArrayData.length(); i++) {
                                    NewsFeedStatus newsFeedStatus1 = new NewsFeedStatus(jsonArrayData.getJSONObject(i));
                                    newsstatus.add(newsFeedStatus1);
                                    redAdapter.notifyItemInserted(newsstatus.size()-1);
                                }
                                // newsstatus.addAll(Newnewsstatus);
                               // redAdapter.notifyDataSetChanged();
                                progress_bar.setVisibility(View.GONE);


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
                //    params.put("Authorization", "Bearer " + TokenClass.Token);
                    params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));

                    return params;
                }

            };

            //requestQueue.add(myReq);
            myReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(myReq);
        }
    }
    private void FetchSearchedDataFromServer(final String search) {

       // RequestQueue requestQueue = Volley.newRequestQueue(this);
        RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();
        // "https://archsqr.in/api/profile/detail/
        StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"parse/search",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Log.v("ReponseFeed", response);
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
                params.put("Authorization", "Bearer " + TokenClass.Token);
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  Log.i(TAG, "Setting screen name: " + "CompetitionDetailActivity");
        UserClass userClass=UtilsClass.GetUserClassFromSharedPreferences(RedActivity.this);
        mTracker.setScreenName("What's Red /"+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());


    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}