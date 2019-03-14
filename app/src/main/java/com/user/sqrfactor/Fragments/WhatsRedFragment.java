package com.user.sqrfactor.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.widget.PullRefreshLayout;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.gms.analytics.Tracker;
import com.user.sqrfactor.Activities.ArticleActivity;
import com.user.sqrfactor.Activities.ContributorsActivity;
import com.user.sqrfactor.Activities.DesignActivity;
import com.user.sqrfactor.Activities.RedActivity;
import com.user.sqrfactor.Activities.StatusPostActivity;
import com.user.sqrfactor.Adapters.RedAdapter;
import com.user.sqrfactor.Adapters.SearchResultAdapter;
import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.NewsFeedStatus;
import com.user.sqrfactor.Pojo.SearchResultClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class WhatsRedFragment extends Fragment {
    Toolbar toolbar;
    private static ArrayList<NewsFeedStatus> newsstatus = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView recyclerView1;
    private boolean isScrolling=false;
    private boolean isLoading=false;
    int currentItems, totalItems, scrolledItems;
    private static RedAdapter redAdapter;
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
    Animation rotate_forward, rotate_Backward, fab_open, fab_close;

    private Tracker mTracker;
    private Context context;

    public WhatsRedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_whats_red, container, false);
        //google analytics code
//        com.user.sqrfactor.Application.MyApplication myApplication=context.getApplication();
//        mTracker=myApplication.getDefaultTracker();
        mSp = MySharedPreferences.getInstance(context);
//
//        toolbar = (Toolbar)view.findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        recyclerView = view.findViewById(R.id.red_recycler);


        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration decoration = new DividerItemDecoration(context, layoutManager.getOrientation());
        recyclerView.addItemDecoration(decoration);
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        redAdapter = new RedAdapter(context, newsstatus);
        recyclerView.setAdapter(redAdapter);
        //progress_bar=view.findViewById(R.id.progress_bar_red);

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
//
//        toolbar.setNavigationIcon(R.drawable.back_arrow);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               // finish();
//                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
//            }
//        });


        linearLayout = view.findViewById(R.id.linear_red);

//        searchEditText=(EditText)findViewById(R.id.user_search);
//        searchClose =(ImageButton) findViewById(R.id.search_close);
//
//        recyclerView1=(RecyclerView)findViewById(R.id.search_recycler1);
//        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
//
//
//
//        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView1.setLayoutManager(layoutManager1);
//        searchResultAdapter = new SearchResultAdapter(this,searchResultClasses);
//        recyclerView1.setAdapter(searchResultAdapter);
//
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
//        btn1 = view.findViewById(R.id.red_newsFeedbtn);
//        btn2 = view.findViewById(R.id.red_whatsRedbtn);
//        btn3 = view.findViewById(R.id.red_Topbtn);




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
                if(dy>0 && lastId + 2 > layoutManager.getItemCount() && !isLoading)
                {
                    isLoading=true;
                    Log.v("rolling",layoutManager.getChildCount()+" "+layoutManager.getItemCount()+" "+layoutManager.findLastVisibleItemPosition()+" "+
                            layoutManager.findLastVisibleItemPosition());

                    fetchDataFromServer();

                }
            }
        });


        layout = view.findViewById(R.id.red_pullToRefresh);

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

        PageRefersh();
//        recyclerView.setOnScrollListener(
//                new RecyclerView.OnScrollListener() {
//                    @Override
//                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                        if (isLastItemDisplaying(recyclerView)) {
//                            fetchDataFromServer();
//                        }
//                        super.onScrolled(recyclerView, dx, dy);
//                    }
//                });

        return view;
    }

    public void PageRefersh(){
        // if(NetworkUtil.checkNewtWorkSpeed(this)){
       // RequestQueue requestQueue = Volley.newRequestQueue(this);
         RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();

        // final int prevSize = newsstatus.size();
        if(newsstatus!=null && newsstatus.size()>0) {
            newsstatus.clear();
        }
        StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"whats-red",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // Log.v("RedFeed", response);
//                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            nextUrl = jsonObject.getString("nextPage");
                            JSONObject jsonPost = jsonObject.getJSONObject("posts");
                            JSONArray jsonArrayData = jsonPost.getJSONArray("data");
                            recyclerView.getRecycledViewPool().clear();
                            new WhatsRedParser(jsonArrayData).execute();
//                            for (int i = 0; i < jsonArrayData.length(); i++) {
//                                NewsFeedStatus newsFeedStatus1 = new NewsFeedStatus(jsonArrayData.getJSONObject(i));
//                                newsstatus.add(newsFeedStatus1);
//
//                            }
//                            recyclerView.getRecycledViewPool().clear();
//
////                            //  redAdapter.notifyItemRangeInserted(prevSize, newsstatus.size() -prevSize);
//                            redAdapter.notifyDataSetChanged();
//                           // progress_bar.setVisibility(View.GONE);


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
        //final ArrayList<NewsFeedStatus> Newnewsstatus = new ArrayList<>();

//        if(newsstatus!=null && newsstatus.size()>0) {
//            newsstatus.clear();
//        }
//        Toast.makeText(getApplicationContext(), "calling Pagination", Toast.LENGTH_LONG).show();
        if (nextUrl != null && !TextUtils.isEmpty(nextUrl)) {

            // RequestQueue requestQueue = Volley.newRequestQueue(this);
            RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();
            StringRequest myReq = new StringRequest(Request.Method.POST, nextUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                //progress_bar.setVisibility(View.GONE);
                                JSONObject jsonObject = new JSONObject(response);
                                nextUrl = jsonObject.getString("nextPage");
                                JSONObject jsonPost = jsonObject.getJSONObject("posts");
                                JSONArray jsonArrayData = jsonPost.getJSONArray("data");

                                new WhatsRedParser(jsonArrayData).execute();
//                                for (int i = 0; i < jsonArrayData.length(); i++) {
//                                    NewsFeedStatus newsFeedStatus1 = new NewsFeedStatus(jsonArrayData.getJSONObject(i));
//                                    newsstatus.add(newsFeedStatus1);
//                                    redAdapter.notifyItemInserted(newsstatus.size()-1);
//                                }
////                                // newsstatus.addAll(Newnewsstatus);
//                                 redAdapter.notifyDataSetChanged();
                                //progress_bar.setVisibility(View.GONE);


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
        }else {
            MDToast.makeText(getApplicationContext(), "No more post found", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
//            Toast.makeText(getApplicationContext(), "nothing", Toast.LENGTH_LONG).show();
        }
    }


//    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
//        if (recyclerView.getAdapter().getItemCount() != 0) {
//            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
//            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
//                return true;
//        }
//        return false;
//    }


    public static class WhatsRedParser extends AsyncTask {
        private JSONArray jsonArrayData;

        public WhatsRedParser(JSONArray jsonArrayData) {
            this.jsonArrayData = jsonArrayData;
        }

//        public NewsFeedParser(JSONArray jsonArrayData, ArrayList<NewsFeedStatus> newsstatus) {
//
//        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            for (int i = 0; i < jsonArrayData.length(); i++) {
                NewsFeedStatus newsFeedStatus1 = null;
                try {

                    newsFeedStatus1 = new NewsFeedStatus(jsonArrayData.getJSONObject(i));
                    //newsstatus.add(newsstatus.size(), newsFeedStatus1);
                    newsstatus.add(newsFeedStatus1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            redAdapter.notifyDataSetChanged();
        }


    }



}
