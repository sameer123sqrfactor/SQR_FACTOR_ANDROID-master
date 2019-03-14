package com.user.sqrfactor.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.internal.Objects;
import com.user.sqrfactor.Activities.CompetitionDetailActivity;
import com.user.sqrfactor.Adapters.SubmissionsAdapter;
import com.user.sqrfactor.Constants.BundleConstants;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.CompetitionClass;
import com.user.sqrfactor.Pojo.SubmissionClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Utils.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmissionsFragment extends Fragment {
    private static final String TAG = "SubmissionsFragment";

    private FloatingActionButton mFab;
    private RecyclerView mRecyclerView;
    private List<SubmissionClass> mSubmissions;
    private ArrayList<Integer> participationIdArray=new ArrayList<>();
    private SubmissionsAdapter mSubmissionsAdapter;
    private Button nextBtn,prevBtn;
    private Context context;

    private Spinner mSortSpinner;
    //LinearLayout mContentLayout;
    private MySharedPreferences mSp;
    private RequestQueue mRequestQueue;
    private String mSlug;
    //
    private String next_page_url="https://sqrfactor.com/api/competition/competitionsort";
    private String not_sorted_next_page_url=null;

    private boolean firstTimeLoading=true,firstTimeLoadingSort=true;

    private int mRequestCount = 1;
    private int mSortRequestCount = 1;

    String mType = "newest";
    private RecyclerView.LayoutManager layoutManager;

    private boolean mIsSortSpinnerSelected = false;
    private List<SubmissionClass> mSortedSubmissions;
    private SubmissionsAdapter mSubSortAdapter;
    private boolean isLoading =false;


    public SubmissionsFragment() {
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
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_submissions, container, false);
        Intent i = getActivity().getIntent();
        mSlug = i.getStringExtra(BundleConstants.SLUG);

        //mContentLayout = view.findViewById(R.id.content_layout);
        mSp = MySharedPreferences.getInstance(getActivity());
        mRequestQueue = MyVolley.getInstance().getRequestQueue();

        mRecyclerView = view.findViewById(R.id.submissions_rv);

        mSortSpinner = view.findViewById(R.id.submissions_sort);


        mRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(layoutManager);


//        mSubmissions = new ArrayList<>();
//        mSubmissionsAdapter = new SubmissionsAdapter(getActivity(), mSubmissions);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(5), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
       // mRecyclerView.setAdapter(mSubmissionsAdapter);

        mSortedSubmissions = new ArrayList<>();
        mSubSortAdapter = new SubmissionsAdapter(getActivity(), mSortedSubmissions);
        mRecyclerView.setAdapter(mSubSortAdapter);


        FetchCompetitionSubmitedDesign(next_page_url);

        mRecyclerView.setOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if (isLastItemDisplaying(recyclerView)) {
                            FetchCompetitionSubmitedDesignScroll(next_page_url);
                        }
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });



//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                isLoading=false;
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//
//                int lastId = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
////
//                if(dy>0 && lastId + 2 > layoutManager.getItemCount() && !isLoading) {
//                    FetchCompetitionSubmitedDesignScroll(next_page_url);
//                  //  FetchCompetitionSubmitedDesign(next_page_url);
//                }
//            }
//        });


        mSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    mType = adapterView.getItemAtPosition(position).toString();
                    FetchCompetitionSubmitedDesign("https://sqrfactor.com/api/competition/competitionsort");
                } else if (position == 1) {
                    mType = adapterView.getItemAtPosition(position).toString();
                    FetchCompetitionSubmitedDesign("https://sqrfactor.com/api/competition/competitionsort");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }


    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    private void FetchCompetitionSubmitedDesign(String url) {

        if(url!=null && !TextUtils.isEmpty(url)){
            final CompetitionDetailActivity compDetailActivity = (CompetitionDetailActivity) getActivity();
            final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                    //Log.v("submissionFragment",response);
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        JSONObject itemsObject = responseObject.getJSONObject("competitions_list");
                        next_page_url=itemsObject.getString("next_page_url");
                        JSONArray dataArray = itemsObject.getJSONArray("data");

                        if(mSortedSubmissions!=null && mSortedSubmissions.size()>0) {
                            mSortedSubmissions.clear();

                        }
                        if (dataArray.length() != 0) {
//                        ViewUtils.dismissProgressBar();
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject singleObject = dataArray.getJSONObject(i);
                                JSONArray commentsArray = singleObject.getJSONArray("comments");
                                JSONArray likesArray = singleObject.getJSONArray("likes");

                                JSONObject participationObject=singleObject.getJSONObject("participation");
                                String user_id=participationObject.getString("user_id");
                                String particiapationId1=participationObject.getString("participate_id1");
                                String particiapationId2=participationObject.getString("participate_id2");
                                String particiapationId3=participationObject.getString("participate_id3");
                                String particiapationId4=participationObject.getString("participate_id4");

                                if(participationIdArray!=null && participationIdArray.size()>0) {
                                    participationIdArray.clear();
                                }

                                if(particiapationId1!=null && !particiapationId1.equals("null")) {
                                    participationIdArray.add(Integer.parseInt(particiapationId1));
                                }
                                if(particiapationId2!=null && !particiapationId2.equals("null")) {
                                    participationIdArray.add(Integer.parseInt(particiapationId2));
                                }
                                if(particiapationId3!=null && !particiapationId3.equals("null")) {
                                    participationIdArray.add(Integer.parseInt(particiapationId3));
                                }
                                if(particiapationId4!=null && !particiapationId4.equals("null")) {
                                    participationIdArray.add(Integer.parseInt(particiapationId4));
                                }

                                String id = singleObject.getString("id");
                                String title = singleObject.getString("title");
                                String code = singleObject.getString("code");
                                String coverUrl = singleObject.getString("cover");
                                String slug = singleObject.getString("slug");

                                SubmissionClass submission = new SubmissionClass(user_id,id, title, code, coverUrl, commentsArray,likesArray,slug,participationIdArray);
                                mSortedSubmissions.add(submission);

                            }
                            mSubSortAdapter.notifyDataSetChanged();

                        } else {
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                ViewUtils.dismissProgressBar();
                    Activity activity = getActivity();
                    if(activity != null){
                        NetworkUtil.handleSimpleVolleyRequestError(error, activity);
                    }

                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(getActivity().getString(R.string.accept), getActivity().getString(R.string.application_json));
                    params.put(getActivity().getString(R.string.authorization), Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
                    return params;
                }


                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("id",compDetailActivity.mCompetitionId);
                    params.put("type", mType.toLowerCase());

                    return params;
                }
            };
            mRequestQueue.add(request);
        }

    }
 private void FetchCompetitionSubmitedDesignScroll(String url) {


        if(url!=null && !TextUtils.isEmpty(url)){
            final CompetitionDetailActivity compDetailActivity = (CompetitionDetailActivity) getActivity();
            final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                    //Log.v("submissionFragment",response);
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        JSONObject itemsObject = responseObject.getJSONObject("competitions_list");
                        next_page_url=itemsObject.getString("next_page_url");
                        JSONArray dataArray = itemsObject.getJSONArray("data");
//                        JSONArray commentsArray=null;
//                        JSONArray likesArray=null;
                        if (dataArray.length() != 0) {

                            new SubmissionFragmentParser(dataArray).execute();
//                        ViewUtils.dismissProgressBar();
//                            for (int i = 0; i < dataArray.length(); i++) {
//                                JSONObject singleObject = dataArray.getJSONObject(i);
//
//                              commentsArray=null;
//                              likesArray=null;
//                                if(singleObject.getJSONArray("comments")!=null && singleObject.getJSONArray("comments").length()>0)
//                                {
//                                    commentsArray = singleObject.getJSONArray("comments");
//                                   // Log.v("commentArray",commentsArray.toString());
//
////
//                                }
//
//                                if(singleObject.getJSONArray("likes")!=null && singleObject.getJSONArray("likes").length()>0)
//                                {
//                                    likesArray = singleObject.getJSONArray("likes");
//                                    //Log.v("likesArray",likesArray.toString());
//
//                                }
////
//
//                                JSONObject participationObject=singleObject.getJSONObject("participation");
//                                String user_id=participationObject.getString("user_id");
//                                String particiapationId1=participationObject.getString("participate_id1");
//                                String particiapationId2=participationObject.getString("participate_id2");
//                                String particiapationId3=participationObject.getString("participate_id3");
//                                String particiapationId4=participationObject.getString("participate_id4");
//
//                                if(participationIdArray!=null && participationIdArray.size()>0) {
//                                    participationIdArray.clear();
//                                }
//
//                                if(particiapationId1!=null && !particiapationId1.equals("null")) {
//                                    participationIdArray.add(Integer.parseInt(particiapationId1));
//                                }
//                                if(particiapationId2!=null && !particiapationId2.equals("null")) {
//                                    participationIdArray.add(Integer.parseInt(particiapationId2));
//                                }
//                                if(particiapationId3!=null && !particiapationId3.equals("null")) {
//                                    participationIdArray.add(Integer.parseInt(particiapationId3));
//                                }
//                                if(particiapationId4!=null && !particiapationId4.equals("null")) {
//                                    participationIdArray.add(Integer.parseInt(particiapationId4));
//                                }
//
//                                String id = singleObject.getString("id");
//                                String title = singleObject.getString("title");
//                                String code = singleObject.getString("code");
//                                String coverUrl = singleObject.getString("cover");
//                                String slug = singleObject.getString("slug");
//
//                                SubmissionClass submission = new SubmissionClass(user_id,id, title, code, coverUrl, commentsArray,likesArray,slug,participationIdArray);
//                                mSortedSubmissions.add(submission);
//                                mSubSortAdapter.notifyItemInserted(mSortedSubmissions.size()-1);
//
//
//                            }

                          //  mSubSortAdapter.notifyDataSetChanged();
                           // mSubSortAdapter.notifyItemRangeChanged(0,mSortedSubmissions.size());


                        }
                        else {
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                ViewUtils.dismissProgressBar();
                    Activity activity = getActivity();
                    if(activity != null){
                        NetworkUtil.handleSimpleVolleyRequestError(error, activity);
                    }

                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(getActivity().getString(R.string.accept), getActivity().getString(R.string.application_json));
                    params.put(getActivity().getString(R.string.authorization), Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
                    return params;
                }


                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("id",compDetailActivity.mCompetitionId);
                    params.put("type", mType.toLowerCase());

                    return params;
                }
            };
            mRequestQueue.add(request);
        }

    }

//    private void competitionSubmissionsApiOnScroll(final int requestCount) {
     //   final StringRequest request = new StringRequest(Request.Method.GET, ServerConstants.COMPETITION_DETAIL + mSlug + "/submissions" + "?page=" + requestCount, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//
//                try {
//                    JSONObject responseObject = new JSONObject(response);
//                    JSONObject itemsObject = responseObject.getJSONObject("items");
//                    JSONArray dataArray = itemsObject.getJSONArray("data");
//                    //Log.d(TAG, "dataArray: "+ dataArray.length() + " == " + requestCount);
//                    if (dataArray.length() != 0) {
////                        ViewUtils.dismissProgressBar();
//                        for (int i = 0; i < dataArray.length(); i++) {
//                            JSONObject singleObject = dataArray.getJSONObject(i);
//                            JSONArray commentsArray = singleObject.getJSONArray("comments");
//                            JSONArray likesArray = singleObject.getJSONArray("likes");
//
//
//
//                            JSONObject participationObject=singleObject.getJSONObject("participation");
//                            String user_id=participationObject.getString("user_id");
//                            String particiapationId1=participationObject.getString("participate_id1");
//                            String particiapationId2=participationObject.getString("participate_id2");
//                            String particiapationId3=participationObject.getString("participate_id3");
//                            String particiapationId4=participationObject.getString("participate_id4");
//
//                            if(participationIdArray!=null && participationIdArray.size()>0) {
//                                participationIdArray.clear();
//                            }
//
//                            if(particiapationId1!=null && !particiapationId1.equals("null")) {
//                                participationIdArray.add(Integer.parseInt(particiapationId1));
//                            }
//                            if(particiapationId2!=null && !particiapationId2.equals("null")) {
//                                participationIdArray.add(Integer.parseInt(particiapationId2));
//                            }
//                            if(particiapationId3!=null && !particiapationId3.equals("null")) {
//                                participationIdArray.add(Integer.parseInt(particiapationId3));
//                            }
//                            if(particiapationId4!=null && !particiapationId4.equals("null")) {
//                                participationIdArray.add(Integer.parseInt(particiapationId4));
//                            }
//
//                            String id = singleObject.getString("id");
//                            String title = singleObject.getString("title");
//                            String code = singleObject.getString("code");
//                            String coverUrl = singleObject.getString("cover");
//                            String slug = singleObject.getString("slug");
//
//                            SubmissionClass submission = new SubmissionClass(user_id,id, title, code, coverUrl, commentsArray,likesArray,slug,participationIdArray);
//                            mSubmissions.add(mSubmissions.size(),submission);
//                        }
//
//                        mSubmissionsAdapter.notifyDataSetChanged();
//
//                    } else {
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
////                ViewUtils.dismissProgressBar();
//                Activity activity = getActivity();
//                if(activity != null){
//                    NetworkUtil.handleSimpleVolleyRequestError(error, activity);
//                }
//
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> params = new HashMap<>();
//                params.put(getActivity().getString(R.string.accept), getActivity().getString(R.string.application_json));
//                params.put(getActivity().getString(R.string.authorization), Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
//                return params;
//            }
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> headers = new HashMap<>();
//                return headers;
//            }
//        };
//        mRequestQueue.add(request);
//    }
//
//    private void submissionsSortApi(int requestCount) {
//
//
////        Log.d(TAG, "submissionsSortApi: called");
//        final CompetitionDetailActivity compDetailActivity = (CompetitionDetailActivity) getActivity();
////        Log.d(TAG, "submissionsSortApi: competition id = " + compDetailActivity.mCompetitionId);
////        Log.d(TAG, "submissionsSortApi: type = " + mType);
//
//        final StringRequest request = new StringRequest(Request.Method.POST, ServerConstants.SUBMISSIONS_SORT + "?page=" + requestCount, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "submission sort response = " + response);
//                //Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();
//                try {
//
//                    JSONObject responseObject = new JSONObject(response);
//                    JSONObject compListObj = responseObject.getJSONObject("competitions_list");
//
//                    JSONArray dataArray = compListObj.getJSONArray("data");
//                    Log.v("participatiodatalength",dataArray.length() +"");
//
//
//                    if( mSortedSubmissions!=null && mSortedSubmissions.size()>0) {
//                        mSortedSubmissions.clear();
//
//                    }
//                    if (dataArray.length() != 0) {
//                        for (int i = 0; i < dataArray.length(); i++) {
//                            JSONObject singleObject = dataArray.getJSONObject(i);
//
//                            JSONArray commentsArray = singleObject.getJSONArray("comments");
//                            JSONArray likesArray = singleObject.getJSONArray("likes");
//
//                            JSONObject participationObject=singleObject.getJSONObject("participation");
//
//                            String user_id=participationObject.getString("user_id");
//                            String particiapationId1=participationObject.getString("participate_id1");
//                            String particiapationId2=participationObject.getString("participate_id2");
//                            String particiapationId3=participationObject.getString("participate_id3");
//                            String particiapationId4=participationObject.getString("participate_id4");
//
//
//                            if(participationIdArray!=null && participationIdArray.size()>0) {
//                                participationIdArray.clear();
//                            }
//
//                            if(particiapationId1!=null && !particiapationId1.equals("null")) {
//                                Log.v("participationCount1",particiapationId1+"");
//                                participationIdArray.add(Integer.parseInt(particiapationId1));
//
//                            }
//                            if(particiapationId2!=null && !particiapationId2.equals("null")) {
//                                Log.v("participationCount2",particiapationId2+"");
//                                participationIdArray.add(Integer.parseInt(particiapationId2));
//                            }
//                            if(particiapationId3!=null && !particiapationId3.equals("null")) {
//                                Log.v("participationCount3",particiapationId3+"");
//                                participationIdArray.add(Integer.parseInt(particiapationId3));
//                            }
//                            if(particiapationId4!=null && !particiapationId4.equals("null")) {
//                                Log.v("participationCount4",particiapationId4+"");
//                                participationIdArray.add(Integer.parseInt(particiapationId4));
//                            }
//
//
//                            String id = singleObject.getString("id");
//                            String title = singleObject.getString("title");
//                            String code = singleObject.getString("code");
//                            String coverUrl = singleObject.getString("cover");
//                            String slug = singleObject.getString("slug");
////                            String pdfUrl = singleObject.getString("pdf");
//                            SubmissionClass submission = new SubmissionClass(user_id,id, title, code, coverUrl, commentsArray,likesArray,slug,participationIdArray);
//                            mSortedSubmissions.add(submission);
//
//                        }
//                        mSubSortAdapter.notifyDataSetChanged();
//
//                    } else {
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "onErrorResponse: ", error);
//                Log.d(TAG, "onErrorResponse: error network response = " + error.networkResponse);
////                ViewUtils.dismissProgressBar();
//
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<>();
//                headers.put(getActivity().getString(R.string.accept), getActivity().getString(R.string.application_json));
//                headers.put(getString(R.string.authorization), Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
//                return headers;
//            }
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("id", compDetailActivity.mCompetitionId);
//                params.put("type", mType.toLowerCase());
//
//                return params;
//            }
//        };
//        mRequestQueue.add(request);
//    }
//
//    private void submissionsSortApiScorll(int requestCount) {
//        if(sorted_next_page_url!=null){
//
//            final StringRequest request = new StringRequest(Request.Method.POST, ServerConstants.SUBMISSIONS_SORT + "?page=" + requestCount, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Log.d(TAG, "submission sort response = " + response);
//                    //Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();
//                    try {
//
//                        JSONObject responseObject = new JSONObject(response);
//                        JSONObject compListObj = responseObject.getJSONObject("competitions_list");
//
//                        JSONArray dataArray = compListObj.getJSONArray("data");
//                        Log.v("participatiodatalength",dataArray.length() +"");
//
//                        if (dataArray.length() != 0) {
//                            for (int i = 0; i < dataArray.length(); i++) {
//                                JSONObject singleObject = dataArray.getJSONObject(i);
//
//                                JSONArray commentsArray = singleObject.getJSONArray("comments");
//                                JSONArray likesArray = singleObject.getJSONArray("likes");
//
//                                JSONObject participationObject=singleObject.getJSONObject("participation");
//
//                                String user_id=participationObject.getString("user_id");
//                                String particiapationId1=participationObject.getString("participate_id1");
//                                String particiapationId2=participationObject.getString("participate_id2");
//                                String particiapationId3=participationObject.getString("participate_id3");
//                                String particiapationId4=participationObject.getString("participate_id4");
//
//
//                                if(participationIdArray!=null && participationIdArray.size()>0) {
//                                    participationIdArray.clear();
//                                }
//
//                                if(particiapationId1!=null && !particiapationId1.equals("null")) {
//                                    Log.v("participationCount1",particiapationId1+"");
//                                    participationIdArray.add(Integer.parseInt(particiapationId1));
//
//                                }
//                                if(particiapationId2!=null && !particiapationId2.equals("null")) {
//                                    Log.v("participationCount2",particiapationId2+"");
//                                    participationIdArray.add(Integer.parseInt(particiapationId2));
//                                }
//                                if(particiapationId3!=null && !particiapationId3.equals("null")) {
//                                    Log.v("participationCount3",particiapationId3+"");
//                                    participationIdArray.add(Integer.parseInt(particiapationId3));
//                                }
//                                if(particiapationId4!=null && !particiapationId4.equals("null")) {
//                                    Log.v("participationCount4",particiapationId4+"");
//                                    participationIdArray.add(Integer.parseInt(particiapationId4));
//                                }
//
//
//                                String id = singleObject.getString("id");
//                                String title = singleObject.getString("title");
//                                String code = singleObject.getString("code");
//                                String coverUrl = singleObject.getString("cover");
//                                String slug = singleObject.getString("slug");
////                            String pdfUrl = singleObject.getString("pdf");
//                                SubmissionClass submission = new SubmissionClass(user_id,id, title, code, coverUrl, commentsArray,likesArray,slug,participationIdArray);
//                                mSortedSubmissions.add(mSortedSubmissions.size(),submission);
//
//                            }
//                            mSubSortAdapter.notifyDataSetChanged();
//
//                        } else {
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.e(TAG, "onErrorResponse: ", error);
//                    Log.d(TAG, "onErrorResponse: error network response = " + error.networkResponse);
////                ViewUtils.dismissProgressBar();
//
//                }
//            }) {
//                @Override
//                public Map<String, String> getHeaders() {
//                    Map<String, String> headers = new HashMap<>();
//                    headers.put(getActivity().getString(R.string.accept), getActivity().getString(R.string.application_json));
//                    headers.put(getString(R.string.authorization), Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
//                    return headers;
//                }
//
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("id", compDetailActivity.mCompetitionId);
//                    params.put("type", mType.toLowerCase());
//
//                    return params;
//                }
//            };
//            mRequestQueue.add(request);
//        }



    public class SubmissionFragmentParser extends AsyncTask {
        private JSONArray dataArray;

        public SubmissionFragmentParser(JSONArray dataArray) {
            this.dataArray = dataArray;
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

            JSONArray commentsArray=null;
            JSONArray likesArray=null;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject singleObject = null;
                try {
                    singleObject = dataArray.getJSONObject(i);
                    commentsArray=null;
                    likesArray=null;
                    if(singleObject.getJSONArray("comments")!=null && singleObject.getJSONArray("comments").length()>0) {
                        commentsArray = singleObject.getJSONArray("comments");
                        // Log.v("commentArray",commentsArray.toString());
                    }

                    if(singleObject.getJSONArray("likes")!=null && singleObject.getJSONArray("likes").length()>0) {
                        likesArray = singleObject.getJSONArray("likes");
                        //Log.v("likesArray",likesArray.toString());

                    }
                    JSONObject participationObject=singleObject.getJSONObject("participation");
                    String user_id=participationObject.getString("user_id");
                    String particiapationId1=participationObject.getString("participate_id1");
                    String particiapationId2=participationObject.getString("participate_id2");
                    String particiapationId3=participationObject.getString("participate_id3");
                    String particiapationId4=participationObject.getString("participate_id4");

                    if(participationIdArray!=null && participationIdArray.size()>0) {
                        participationIdArray.clear();
                    }

                    if(particiapationId1!=null && !particiapationId1.equals("null")) {
                        participationIdArray.add(Integer.parseInt(particiapationId1));
                    }
                    if(particiapationId2!=null && !particiapationId2.equals("null")) {
                        participationIdArray.add(Integer.parseInt(particiapationId2));
                    }
                    if(particiapationId3!=null && !particiapationId3.equals("null")) {
                        participationIdArray.add(Integer.parseInt(particiapationId3));
                    }
                    if(particiapationId4!=null && !particiapationId4.equals("null")) {
                        participationIdArray.add(Integer.parseInt(particiapationId4));
                    }

                    String id = singleObject.getString("id");
                    String title = singleObject.getString("title");
                    String code = singleObject.getString("code");
                    String coverUrl = singleObject.getString("cover");
                    String slug = singleObject.getString("slug");

                    SubmissionClass submission = new SubmissionClass(user_id,id, title, code, coverUrl, commentsArray,likesArray,slug,participationIdArray);
                    mSortedSubmissions.add(submission);
                   // mSubSortAdapter.notifyItemInserted(mSortedSubmissions.size()-1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mSubSortAdapter.notifyDataSetChanged();
        }


    }
}




