package com.user.sqrfactor.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.widget.PullRefreshLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.user.sqrfactor.Adapters.NotificationsAdapter;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.NewsFeedStatus;
import com.user.sqrfactor.Pojo.NotificationClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Utils.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class NotificationsFragment extends Fragment {

    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<NotificationClass> notificationsClassArrayList = new ArrayList<>();
    private NotificationsAdapter notificationsAdapter;
    private Button send;
    private MySharedPreferences mSp;
    private boolean isLoading = false;
    private Context context;
    PullRefreshLayout layout;
    private ProgressBar progressBar;
    public static DatabaseReference ref;
    public static FirebaseDatabase database;
    private String nextPageUrl;
    LinearLayout commentMsg;

    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_notifications, container, false);

        SharedPreferences mPrefs =getActivity().getSharedPreferences("User",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        final UserClass userClass = gson.fromJson(json, UserClass.class);



        database= FirebaseDatabase.getInstance();
        ref = database.getReference();
        mSp = MySharedPreferences.getInstance(context);


        commentMsg=view.findViewById(R.id.commentMsg);
        progressBar=view.findViewById(R.id.progress_bar);
        recycler = (RecyclerView) view.findViewById(R.id.recycler);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(linearLayoutManager);

        notificationsAdapter = new NotificationsAdapter(notificationsClassArrayList, getActivity());
        recycler.setAdapter(notificationsAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(recycler.getContext(), linearLayoutManager.getOrientation());
        recycler.addItemDecoration(decoration);

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                isLoading=false;
                //Toast.makeText(context,"moving down",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                int lastId=linearLayoutManager.findLastVisibleItemPosition();
//                if(dy>0)
//                {
//                    Toast.makeText(context,"moving up",Toast.LENGTH_SHORT).show();
//                }
                if(dy>0 && lastId + 2 > linearLayoutManager.getItemCount() && !isLoading)
                {
                    isLoading=true;
//                    Log.v("rolling",layoutManager.getChildCount()+" "+layoutManager.getItemCount()+" "+layoutManager.findLastVisibleItemPosition()+" "+
//                            layoutManager.findLastVisibleItemPosition());

                    LoadMoreNotification();

                }
            }
        });
//

        layout = view.findViewById(R.id.notification_pullRefresh);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //LoadNewsFeedDataFromServer();
                //layout.setRefreshing(false);
                layout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout.setRefreshing(false);
                       NotificationLoad();
                    }
                },1500);

            }
        });
        NotificationLoad();
        return view;

    }




    public void NotificationLoad(){


//        if(NetworkUtil.checkNewtWorkSpeed(getActivity())){
            if(notificationsClassArrayList!=null && notificationsClassArrayList.size()>0)
                notificationsClassArrayList.clear();

            //RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();

            StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"notifications",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("ReponseFeed", response);
                            commentMsg.setVisibility(View.GONE);
//                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                nextPageUrl = jsonObject.getString("nextpage");
                                JSONArray jsonArrayData = jsonObject.getJSONArray("notifications");
                                 new NotificationParser(jsonArrayData).execute();
                                progressBar.setVisibility(View.GONE);
//                                for (int i = 0; i < jsonArrayData.length(); i++) {
//                                    Log.v("Response", response);
//                                    NotificationClass notificationsClass = new NotificationClass(jsonArrayData.getJSONObject(i));
//                                    notificationsClassArrayList.add(notificationsClass);
//                                }
//                                progressBar.setVisibility(View.GONE);
//                                notificationsAdapter.notifyDataSetChanged();


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

            };
            myReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(myReq);

//        }else {
//            MDToast.makeText(getActivity(), "Bad network connection", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
//        }




//        final Handler handler=new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(notificationsClassArrayList.size()==0)
//                {
//                    commentMsg.setVisibility(View.VISIBLE);
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//        }, 1000);

    }
    public void LoadMoreNotification() {
        if (nextPageUrl != null && !TextUtils.isEmpty(nextPageUrl)) {


          //  RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();

            StringRequest myReq = new StringRequest(Request.Method.POST,nextPageUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("ReponseFeed", response);
//                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArrayData = jsonObject.getJSONArray("notifications");
                                new NotificationParser(jsonArrayData).execute();

//                                for (int i = 0; i < jsonArrayData.length(); i++) {
//                                    Log.v("Response", response);
//                                    NotificationClass notificationsClass = new NotificationClass(jsonArrayData.getJSONObject(i));
//                                    notificationsClassArrayList.add(notificationsClass);
//                                    notificationsAdapter.notifyItemInserted(notificationsClassArrayList.size()-1);
//
//                                }
                                //notificationsAdapter.notifyDataSetChanged();


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

           // requestQueue.add(myReq);
            myReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(myReq);

        }
    }


    public class NotificationParser extends AsyncTask {
        private JSONArray jsonArrayData;

        public NotificationParser(JSONArray jsonArrayData) {
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
                //Log.v("Response", response);
                NotificationClass notificationsClass = null;
                try {
                    notificationsClass = new NotificationClass(jsonArrayData.getJSONObject(i));
                    notificationsClassArrayList.add(notificationsClass);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            notificationsAdapter.notifyDataSetChanged();

        }


    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }
}