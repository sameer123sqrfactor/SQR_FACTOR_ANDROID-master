package com.user.sqrfactor.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.user.sqrfactor.Activities.ArticleActivity;
import com.user.sqrfactor.Activities.DesignActivity;
import com.user.sqrfactor.Activities.HomeScreen;
import com.user.sqrfactor.Adapters.NewsFeedAdapter;
import com.user.sqrfactor.Application.ContainerHolderSingleton;
import com.user.sqrfactor.Application.ContainerLoadedCallback;
import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.NewsFeedStatus;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Activities.StatusPostActivity;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Utils.NetworkUtil;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
import static com.user.sqrfactor.Application.AppController.TAG;

public class StatusFragment extends Fragment {
    private static ArrayList<NewsFeedStatus> newsstatus = new ArrayList<>();
    private ImageView displayImage;
    private ImageView camera;// button
    public ImageButton mRemoveButton;
    private RecyclerView recyclerView;
    private NewsFeedStatus newsFeedStatus;
    private Button like, comment, share, like2;
    private String token;
    private SharedPreferences sharedPreferences;
    private ImageView profile_photo;
    private String message, encodedImage;
    private boolean isScrolling;
    int currentItems,totalItems,scrolledItems;
    private ProgressBar progressBar;
    private Button btnSubmit;
    private ImageView newsProfileImage;
    private EditText writePost;
    private Tracker mTracker;

    private ProgressDialog pDialog;
    public static String UPLOAD_URL = UtilsClass.baseurl+"post";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private int PICK_IMAGE_REQUEST = 1;
    Bitmap bitmap;
    LinearLayoutManager layoutManager;
    private static NewsFeedAdapter newsFeedAdapter;
    private ProgressDialog dialog = null;
    private JSONObject jsonObject;
    PullRefreshLayout layout;
    private Context context;
    private boolean isLoading=false;
    private static String nextPageUrl;
    private String oldUrl;
    public static DatabaseReference ref;
    public static FirebaseDatabase database;
    private LinearLayout textMsg;
    private UserClass userClass;
    private MySharedPreferences mSp;
    private FloatingActionButton fabView, fabStatus, fabDesign, fabArticle;
    private boolean fabExpanded = false;
    private LinearLayout layoutFabStatus;
    private LinearLayout layoutFabDesign;
    private LinearLayout layoutFabArticle;
    private static final long TIMEOUT_FOR_CONTAINER_OPEN_MILLISECONDS = 2000;
    private static final String CONTAINER_ID = "GTM-TXNTWP8";

    Animation rotate_forward, rotate_Backward, fab_open, fab_close;
//    private PopupWindow popupWindow;
//    private LinearLayout layoutOfPopup;
//    private ImageView popupclose, eventImage;
//    ShowEventPopUp();


    public StatusFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();
        context=getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fregment_status, container, false);

        recyclerView = rootView.findViewById(R.id.news_recycler);

        textMsg=rootView.findViewById(R.id.textMsg);
        final LinearLayout stausLinear = rootView.findViewById(R.id.status_linear);
        layoutManager = new LinearLayoutManager(this.getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        newsFeedAdapter = new NewsFeedAdapter(newsstatus, this.getActivity());
        recyclerView.setAdapter(newsFeedAdapter);
        database= FirebaseDatabase.getInstance();
        ref = database.getReference();
        mSp = MySharedPreferences.getInstance(context);


        //google analytics code
        MyApplication application = (MyApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();



        //google tag manager code

        //GoogleTagManagerCode(application);

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
//        {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (dy > 0 ||dy<0 && fabView.isShown()) {
//                    fabView.hide();
//                }
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    fabView.show();
//                }
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//        });


        final SkeletonScreen skeletonScreen = Skeleton.bind(recyclerView)
                .adapter(newsFeedAdapter)
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
        }, 1000);



        Log.v("status","hello");
        SharedPreferences mPrefs =getActivity().getSharedPreferences("User",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        userClass = gson.fromJson(json, UserClass.class);

//        fabView = rootView.findViewById(R.id.fab_view);
//        fabStatus = rootView.findViewById(R.id.fab_status);
//        fabDesign = rootView.findViewById(R.id.fab_design);
//        fabArticle = rootView.findViewById(R.id.fab_article);
//
//        layoutFabStatus = (LinearLayout) rootView.findViewById(R.id.layoutFabStatus);
//        layoutFabDesign = (LinearLayout) rootView.findViewById(R.id.layoutFabDesign);
//        layoutFabArticle = (LinearLayout) rootView.findViewById(R.id.layoutFabArticle);
//
//        rotate_forward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_forward);
//        rotate_Backward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_backward);
//        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
//        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
//
//        fabView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (fabExpanded == true){
//                    closeSubMenusFab();
//                } else {
//                    openSubMenusFab();
//                }
//            }
//        });
//        closeSubMenusFab();
//
//        fabStatus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity().getApplicationContext(), StatusPostActivity.class);
//                intent.putExtra("Fab",1);
//                getActivity().startActivity(intent);
//            }
//        });
//        fabDesign.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity().getApplicationContext(), DesignActivity.class);
//                intent.putExtra("Fab",1);
//                getActivity().startActivity(intent);
//            }
//        });
//        fabArticle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity().getApplicationContext(), ArticleActivity.class);
//                intent.putExtra("Fab",1);
//                getActivity().startActivity(intent);
//            }
//        });
//



        camera = rootView.findViewById(R.id.news_camera);
        displayImage = rootView.findViewById(R.id.news_upload_image);
        btnSubmit = rootView.findViewById(R.id.news_postButton);


        layout = rootView.findViewById(R.id.news_pullToRefresh);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //LoadNewsFeedDataFromServer();
                //layout.setRefreshing(false);
                layout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout.setRefreshing(false);
                        LoadNewsFeedDataFromServer();
                    }
                },2000);

            }
        });
        dialog = new ProgressDialog(this.getActivity());
        dialog.setMessage("Uploading Image...");
        dialog.setCancelable(false);
        final RelativeLayout relativeLayout = rootView.findViewById(R.id.rl);
//        relativeLayout.setVisibility(View.GONE);
        jsonObject = new JSONObject();
        sharedPreferences = this.getActivity().getSharedPreferences("PREF_NAME", this.getActivity().MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "sqr");
//        Log.v("TokenOnStatus", token);


//        recyclerView.setOnScrollListener(
//                new RecyclerView.OnScrollListener() {
//                    @Override
//                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                        if (isLastItemDisplaying(recyclerView)) {
//                            //FetchCompetitionSubmitedDesignScroll(next_page_url);
//                            FetchDataFromServer();
//                        }
//                        super.onScrolled(recyclerView, dx, dy);
//                    }
//                });

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
//                    Log.v("rolling",layoutManager.getChildCount()+" "+layoutManager.getItemCount()+" "+layoutManager.findLastVisibleItemPosition()+" "+
//                            layoutManager.findLastVisibleItemPosition());

                    FetchDataFromServer();

                }
            }
        });

        //RealTimeNotificationListner();
        if(userClass!=null){
            ref.child("notification").child(userClass.getUserId()+"").child("all").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    HomeScreen.getnotificationCount();

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            ref.child("Chats").child(userClass.getUserId()+"").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    HomeScreen.getUnReadMsgCount();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

//        showCommentDeleteDialog();
        LoadNewsFeedDataFromServer();
        return rootView;

    }

    private void GoogleTagManagerCode(MyApplication application) {
        PendingResult<ContainerHolder> pending =
                application.getTagManager().loadContainerPreferNonDefault(CONTAINER_ID,
                        R.raw.default_google_tag_manager_container);

        pending.setResultCallback(new ResultCallback<ContainerHolder>() {
            @Override
            public void onResult(ContainerHolder containerHolder) {
                ContainerHolderSingleton.setContainerHolder(containerHolder);
                Container container = containerHolder.getContainer();
                if (!containerHolder.getStatus().isSuccess()) {
                    Log.e("CuteAnimals", "failure loading container");
                    Toast.makeText(getActivity(),"failure loading container",Toast.LENGTH_LONG).show();
                    //displayErrorToUser(R.string.load_error);
                    return;
                }
                ContainerLoadedCallback.registerCallbacksForContainer(container);
                containerHolder.setContainerAvailableListener(new ContainerLoadedCallback());
               // startMainActivity();
            }
        }, TIMEOUT_FOR_CONTAINER_OPEN_MILLISECONDS, TimeUnit.MILLISECONDS);

        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        TagManager.getInstance(context).getDataLayer().push("SIGNUP LOGIN", "sucessful gtm tested");

    }


//    private void openSubMenusFab(){
//        layoutFabStatus.setVisibility(View.VISIBLE);
//        layoutFabDesign.setVisibility(View.VISIBLE);
//        layoutFabArticle.setVisibility(View.VISIBLE);
//        fabStatus.startAnimation(fab_open);
//        fabDesign.setAnimation(fab_open);
//        fabArticle.setAnimation(fab_open);
//        fabView.startAnimation(rotate_forward);
//        fabView.setImageResource(R.drawable.ic_add_black_24dp);
//        fabExpanded = true;
//    }
//    private void closeSubMenusFab(){
//        layoutFabStatus.setVisibility(View.GONE);
//        layoutFabDesign.setVisibility(View.GONE);
//        layoutFabArticle.setVisibility(View.GONE);
//        fabStatus.startAnimation(fab_close);
//        fabDesign.setAnimation(fab_close);
//        fabArticle.setAnimation(fab_close);
//        fabView.startAnimation(rotate_Backward);
//        fabExpanded = false;
//    }


    public void LoadNewsFeedDataFromServer()
    {
       // if(NetworkUtil.checkNewtWorkSpeed(getActivity())){
            RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();
            //newsstatus.clear();
            StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"news-feed",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject jsonPost = jsonObject.getJSONObject("posts");
                                nextPageUrl=jsonPost.getString("next_page_url");
                                JSONArray jsonArrayData = jsonPost.getJSONArray("data");
                                if(newsstatus!=null)
                                    newsstatus.clear();

                                recyclerView.getRecycledViewPool().clear();
                                new NewsFeedParser(jsonArrayData).execute();
//
//                                for (int i = 0; i < jsonArrayData.length(); i++) {
//                                    NewsFeedStatus newsFeedStatus1 = new NewsFeedStatus(jsonArrayData.getJSONObject(i));
//                                    newsstatus.add(newsFeedStatus1);
//                                }
//
//
//                                newsFeedAdapter.notifyDataSetChanged();


                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getActivity().getApplicationContext(), "Token" + error+"", Toast.LENGTH_SHORT).show();
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
//                        headerValue = response.headers.get("Date");
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
//            MDToast.makeText(getActivity(), "Bad network connection", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
//        }



       // RequestQueue requestQueue = Volley.newRequestQueue(getActivity());


//        final Handler handler=new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(newsstatus.size()==0)
//                {
//                    textMsg.setVisibility(View.VISIBLE);
//                }
//            }
//        }, 1500);

    }


    @Override
    public void onResume() {
        super.onResume();
        if(userClass!=null){
            Log.i(TAG, "Setting screen name: CompetitionDetailActivity");
            mTracker.setScreenName("newsfeed /"+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        }



    }

    public void FetchDataFromServer() {

        Toast.makeText(context, "loding pagination", Toast.LENGTH_LONG).show();
        final ArrayList<NewsFeedStatus> Newnewsstatus = new ArrayList<>();
        if (nextPageUrl != null && !TextUtils.isEmpty(nextPageUrl)) {

//
//            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();

            StringRequest myReq = new StringRequest(Request.Method.POST, nextPageUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            Log.v("MorenewsFeedFromServer", response);
                          //  Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject jsonPost = jsonObject.getJSONObject("posts");
                                nextPageUrl = jsonPost.getString("next_page_url");
                                JSONArray jsonArrayData = jsonPost.getJSONArray("data");
                                new NewsFeedParser(jsonArrayData).execute();
//                                for (int i = 0; i < jsonArrayData.length(); i++) {
//                                    NewsFeedStatus newsFeedStatus1 = new NewsFeedStatus(jsonArrayData.getJSONObject(i));
//                                    newsstatus.add(newsFeedStatus1);
//                                }
//
//
//                                newsFeedAdapter.notifyDataSetChanged();

//



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
                    params.put("Authorization",  Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));

                    return params;
                }

            };
            myReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(myReq);
//            requestQueue.add(myReq);
        }
    }


    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 2)
                return true;
        }
        return false;
    }


    public static class NewsFeedParser extends AsyncTask {
        private JSONArray jsonArrayData;

        public NewsFeedParser(JSONArray jsonArrayData) {
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
                    newsstatus.add(newsstatus.size(), newsFeedStatus1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            newsFeedAdapter.notifyDataSetChanged();
//            if(newsstatus.size()==0){
//                textMsg.setVisibility(View.VISIBLE);
//            }else {
//                textMsg.setVisibility(View.GONE);
//            }

        }


    }

//    private void showCommentDeleteDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//
//        builder.setTitle("want to take part in competition?")
//                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                       // DeleteSubmission(submissionId,position);
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
//
//    @Override
//    public void onSaveInstanceState(final Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putSerializable("list", (Serializable) newsstatus);
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        if (savedInstanceState != null) {
//            //probably orientation change
//            newsstatus = (ArrayList<NewsFeedStatus>) savedInstanceState.getSerializable("list");
//        } else {
//            if ( newsstatus!= null) {
//                Toast.makeText(getContext(),"Loading",Toast.LENGTH_LONG).show();
//                newsFeedAdapter.notifyDataSetChanged();
//                //returning from backstack, data is fine, do nothing
//            } else {
//                //newly created, compute data
//                LoadNewsFeedDataFromServer();
//            }
//        }
//    }

}

