package com.user.sqrfactor.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.user.sqrfactor.Constants.BundleConstants;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Interfaces.DragListener;
import com.user.sqrfactor.Extras.DragToClose;
import com.user.sqrfactor.Adapters.LikeListAdapter;
import com.user.sqrfactor.Pojo.LikeClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Extras.UtilsClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LikeListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private LikeListAdapter likeListAdapter;
    private TextView likeMsg;
    private ArrayList<LikeClass> likeClassArrayList = new ArrayList<>();
    private static final String TAG = LikeListActivity.class.getSimpleName();
    private int user_id;
    private MySharedPreferences mSp;


    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_list);
        mProgressBar = findViewById(R.id.like_progress_bar);
        recyclerView = findViewById(R.id.likeList_recycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSp = MySharedPreferences.getInstance(this);
        likeMsg=findViewById(R.id.likeMsg);
        DividerItemDecoration decoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(decoration);
        //adapter for likelist
        likeListAdapter = new LikeListAdapter(likeClassArrayList,this);
        recyclerView.setAdapter(likeListAdapter);

        //getting data from previous activity from intent
        Intent intent = getIntent();
        if(intent!=null && intent.hasExtra("isComment")) {
            user_id = intent.getIntExtra("id",0);
            likelistofComment(user_id);
        }else if(intent!=null && intent.hasExtra(BundleConstants.IS_SUBMISSION)) {

            user_id =Integer.parseInt(intent.getStringExtra("id"));

          //  Toast.makeText(getApplicationContext(),user_id+"",Toast.LENGTH_LONG).show();
            likelistOfSubmission(user_id);
        } else {
            user_id = intent.getIntExtra("id",0);
            likelist(user_id);
        }

        //drag to close animation
        final DragToClose dragToClose = findViewById(R.id.drag_to_close);
        dragToClose.setDragListener(new DragListener() {
            @Override
            public void onStartDraggingView() {
                Log.d(TAG, "onStartDraggingView()");
            }

            @Override
            public void onViewCosed() {
                Log.d(TAG, "onViewCosed()");
            }
        });

    }

    //getting like list of commment data from server
    private void likelistofComment(int user_id) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest myReq = new StringRequest(Request.Method.GET, UtilsClass.baseurl+"commentlikelist/"+user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressBar.setVisibility(View.GONE);
                        likeMsg.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray userslist=jsonObject.getJSONArray("users_list");
                            if(likeClassArrayList!=null) {
                                likeClassArrayList.clear();
                            }
                            for(int i=0;i<userslist.length();i++) {
                                LikeClass likeClass=new LikeClass(userslist.getJSONObject(i));
                                likeClassArrayList.add(likeClass);
                            }
                            likeListAdapter.notifyDataSetChanged();
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

        requestQueue.add(myReq);
        final Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(likeClassArrayList.size()==0)
                {
                    mProgressBar.setVisibility(View.GONE);
                    likeMsg.setVisibility(View.VISIBLE);
                }else{
                    mProgressBar.setVisibility(View.GONE);
                    likeMsg.setVisibility(View.GONE);

                }
            }
        }, 800);
    }


    //getting like list data of individuals post from server
    public void likelist(int user_id){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest myReq = new StringRequest(Request.Method.GET, UtilsClass.baseurl+"fetchlikelist/"+user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("likelist", response);
                        mProgressBar.setVisibility(View.GONE);
                        likeMsg.setVisibility(View.GONE);
                       // Toast.makeText(LikeListActivity.this, response, Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("message")) {
                                    likeMsg.setVisibility(View.VISIBLE);
                            }
                            JSONArray userslist=jsonObject.getJSONArray("userslist");
                            if(likeClassArrayList!=null) {
                                likeClassArrayList.clear();
                            }
                            for(int i=0;i<userslist.length();i++) {
                                LikeClass likeClass=new LikeClass(userslist.getJSONObject(i));
                                likeClassArrayList.add(likeClass);
                            }
                            likeListAdapter.notifyDataSetChanged();
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
        final Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(likeClassArrayList.size()==0) {
                    mProgressBar.setVisibility(View.GONE);
                    likeMsg.setVisibility(View.VISIBLE);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    likeMsg.setVisibility(View.GONE);
                }
            }
        }, 800);

    }

//getting full like list data of individuals submission from  from server
    public void likelistOfSubmission(int user_id){
        //Toast.makeText(LikeListActivity.this,"submissionId"+user_id,Toast.LENGTH_LONG).show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest myReq = new StringRequest(Request.Method.GET, ServerConstants.FETCH_LIKELIST_OF_EACH_SUBMISSION+user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // Log.v("likelist", response);
                        Toast.makeText(getApplicationContext(), response,Toast.LENGTH_LONG).show();
                        mProgressBar.setVisibility(View.GONE);
                        likeMsg.setVisibility(View.GONE);
                        //Toast.makeText(LikeListActivity.this,"SubmissionLike"+response, Toast.LENGTH_LONG).show();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("message")) {
                                likeMsg.setVisibility(View.VISIBLE);
                            }
                            JSONArray userslist=jsonObject.getJSONArray("userslist");
                            if(likeClassArrayList!=null) {
                                likeClassArrayList.clear();
                            }
                            for(int i=0;i<userslist.length();i++) {
                                LikeClass likeClass=new LikeClass(userslist.getJSONObject(i));
                                likeClassArrayList.add(likeClass);
                            }
                            likeListAdapter.notifyDataSetChanged();
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
        final Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(likeClassArrayList.size()==0)
                {
                    mProgressBar.setVisibility(View.GONE);
                    likeMsg.setVisibility(View.VISIBLE);
                }
                else {
                    mProgressBar.setVisibility(View.GONE);
                    likeMsg.setVisibility(View.GONE);
                }
            }
        }, 800);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}