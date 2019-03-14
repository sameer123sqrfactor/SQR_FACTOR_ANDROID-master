package com.user.sqrfactor.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.user.sqrfactor.Adapters.SubmissionCommentsAdapter;
import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Constants.BundleConstants;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Fragments.MDToast;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.QuestionCommentClass;
import com.user.sqrfactor.Pojo.from_user;
import com.user.sqrfactor.Pojo.post;
import com.user.sqrfactor.Utils.PostContentHandlerForSubmissionData;
import com.user.sqrfactor.Pojo.PushNotificationClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Utils.NetworkUtil;
import com.user.sqrfactor.Extras.UtilsClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmissionDetailActivity extends AppCompatActivity  {
    private static final String TAG = "SubmissionDetailActivit";

    private TextView fullView_Submission_Title,team_code,submission_time,competition_title,sub_detail_post_comment,
            sub_detail_comment_count,submission_fullpost_likeList,submission_fullpost_comment;
    private EditText sub_detail_comment;
    private ImageView mSubImageView,sub_detail_image,full_view_submission_menu;
    private CheckBox submission_fullpost_like;
    private WebView mPdfWebView;
    private MySharedPreferences mSp;
    private RequestQueue mRequestQueue;
    private Toolbar mToolbar;
    private  String submissionId;
    private int isAlreadyLiked = 0;
    private LinearLayout linearLayoutWebView;
    private ArrayList<Integer> participationIDArrayList=new ArrayList<>();
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private JSONArray mCommentsArray;
    private UserClass userClass;

    private TextView mCommentsCountTV,submission_fullPostDescription;

    private ProgressBar progressBar;
    private List<QuestionCommentClass> mComments;
    private SubmissionCommentsAdapter mCommentsAdapter;

    private RecyclerView mCommentsRecyclerView;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission_detail);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        userClass=UtilsClass.GetUserClassFromSharedPreferences(SubmissionDetailActivity.this);

        progressBar=findViewById(R.id.pb);

        fullView_Submission_Title=findViewById(R.id.fullView_Submission_Title);
        team_code=findViewById(R.id.team_code);
        submission_time=findViewById(R.id.submission_time);
        competition_title=findViewById(R.id.competition_title);
        sub_detail_post_comment=findViewById(R.id.sub_detail_post_comment);
        sub_detail_comment_count=findViewById(R.id.sub_detail_comment_count);
        submission_fullpost_likeList=findViewById(R.id.submission_fullpost_likeList);
        submission_fullpost_comment=findViewById(R.id.submission_fullpost_comment);
        sub_detail_comment=findViewById(R.id.sub_detail_comment);
       // sub_detail_image=findViewById(R.id.sub_detail_image);
        mPdfWebView=findViewById(R.id.webViewPdf);
        submission_fullpost_like=findViewById(R.id.submission_fullpost_like);
        full_view_submission_menu=findViewById(R.id.submission_menu);
        linearLayoutWebView=findViewById(R.id.submission_webView);
        submission_fullPostDescription=findViewById(R.id.submission_fullPostDescription);


        //code for google analytics
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();

        mToolbar.setNavigationIcon(R.drawable.back_arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent intent = getIntent();

        if(intent!=null && intent.hasExtra(BundleConstants.SLUG)) {
            GetFullPostDataFromServer(intent.getStringExtra(BundleConstants.SLUG));
            submissionId = intent.getStringExtra(BundleConstants.SUBMISSION_ID);
            participationIDArrayList=intent.getIntegerArrayListExtra(BundleConstants.PARTICIPATION_ID_ARRAY);
        }
        mCommentsRecyclerView = findViewById(R.id.sub_detail_comments_rv);
        mCommentsRecyclerView.setHasFixedSize(true);
        mCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mComments = new ArrayList<>();
        mCommentsAdapter = new SubmissionCommentsAdapter(this, mComments, submissionId);
        mCommentsRecyclerView.setAdapter(mCommentsAdapter);

        sub_detail_post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(sub_detail_comment.getText().toString())) {
                    PostSubmissionCommentOnServer(sub_detail_comment.getText().toString());
                    sub_detail_comment.setText("");
                }else {
                    MDToast.makeText(SubmissionDetailActivity.this, "write comment body", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                }
            }
        });

    }



    public void GetFullPostDataFromServer(String slug) {

        mSp = MySharedPreferences.getInstance(SubmissionDetailActivity.this);
        mRequestQueue = MyVolley.getInstance().getRequestQueue();

        final StringRequest request = new StringRequest(Request.Method.GET, ServerConstants.SUBMISSIONS_FULL_VIEW+slug , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
               // Toast.makeText(SubmissionDetailActivity.this,response,Toast.LENGTH_LONG).show();
                try {

                    JSONObject responseObject = new JSONObject(response);
                    JSONObject competitionName = responseObject.getJSONObject("competitionName");
                    String competition_title_string=competitionName.getString("competition_title");
                    String cover_image=competitionName.getString("cover_image");
                    final String slug=competitionName.getString("slug");

                    competition_title.setText(competition_title_string);
                    JSONObject usersSubmissiondetail = responseObject.getJSONObject("usersSubmissiondetail");

                    final int submissionId=usersSubmissiondetail.getInt("id");
                    int user_id=usersSubmissiondetail.getInt("user_id");
                    if(userClass.getUserId()==user_id){
                        full_view_submission_menu.setVisibility(View.VISIBLE);
                    }else {
                        full_view_submission_menu.setVisibility(View.GONE);
                    }

                    int competition_id=usersSubmissiondetail.getInt("competition_id");
                    int competition_participation_id=usersSubmissiondetail.getInt("competition_participation_id");
                    String code=usersSubmissiondetail.getString("code");
                    String title=usersSubmissiondetail.getString("title");
                    String cover=usersSubmissiondetail.getString("cover");

                    final String body=usersSubmissiondetail.getString("body");
                    String pdf=usersSubmissiondetail.getString("pdf");
                    Toast.makeText(getApplicationContext(),title,Toast.LENGTH_LONG).show();

                    if(body!=null && !body.equals("null")) {
                        LoadEditorBody(body);
                    }
                    if(pdf!=null && !pdf.equals("null")) {
                        LoadPdfBody(pdf);
                    }
                  //  pdf": "storage/competition_submission/37693cfc748049e45d87b8c7d8b9aacd/pdf/Gruq9Czh8XF3nwR6XJTa.pdf",
                    //storage/competition_submission/37693cfc748049e45d87b8c7d8b9aacd/cover_image/kC1iU767xaCr6iYKOELi.jpg
                    String created_at=usersSubmissiondetail.getString("created_at");
                    String updated_at=usersSubmissiondetail.getString("updated_at");

                    final JSONArray likesArray = usersSubmissiondetail.getJSONArray("likes");
                    int likesLength=likesArray.length();
                    for (int i = 0; i < likesArray.length(); i++) {
                        try {
                            JSONObject likeObject=likesArray.getJSONObject(i);
                            if (userClass.getUserId() == likeObject.getInt("user_id")) {
                                submission_fullpost_likeList.setTextColor(getResources().getColor(R.color.sqr));
                                isAlreadyLiked = 1;
                                submission_fullpost_like.setChecked(true);
                                ///flag=1;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    final int isAlreadyLikedFinal = isAlreadyLiked;
                    submission_fullpost_like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                int likeCount = 0;
                                if (likesArray.length()!=0) {
                                    likeCount=likesArray.length();
                                }
                                submission_fullpost_likeList.setTextColor(getResources().getColor(R.color.sqr));
                                if (isAlreadyLikedFinal == 1)
                                    submission_fullpost_likeList.setText(likeCount + " Like");
                                else {
                                    likeCount = likeCount + 1;
                                    submission_fullpost_likeList.setText(likeCount + " Like");
                                }


                                String name=UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name());

                                for(int i=0;i<participationIDArrayList.size();i++) {
                                    if (userClass.getUserId()!=participationIDArrayList.get(i)) {
                                        PushNotificationClass pushNotificationClass;
                                        from_user fromUser;
                                        post post1 = new post("", "", "post Title", "", 0);
                                        fromUser = new from_user(userClass.getEmail(), name, userClass.getUserId(), userClass.getUser_name(), userClass.getProfile());
                                        pushNotificationClass = new PushNotificationClass( "liked your submitted design", new Date().getTime(), fromUser, post1, "like_post");
                                        String key = ref.child("notification").child(participationIDArrayList.get(i) + "").child("all").push().getKey();
                                        ref.child("notification").child(participationIDArrayList.get(i)+ "").child("all").child(key).setValue(pushNotificationClass);
                                        Map<String, String> unred = new HashMap<>();
                                        unred.put("unread", key);
                                        ref.child("notification").child(participationIDArrayList.get(i) + "").child("unread").child(key).setValue(unred);

                                    }
                                }
                            } else {

                                if (isAlreadyLikedFinal == 1) {
                                    // Log.v("isAlreadyLiked1", isAlreadyLikedFinal + " ");
                                    submission_fullpost_likeList.setTextColor(getResources().getColor(R.color.gray));
                                    int likeCount1 = likesArray.length();
                                    //Toast.makeText(context, "Unchecked1", Toast.LENGTH_SHORT).show();
                                    likeCount1 = likeCount1 - 1;
                                    submission_fullpost_likeList.setText(likeCount1 + " Like");
                                } else {
                                    // Log.v("isAlreadyLiked2", isAlreadyLikedFinal + " ");
                                    //Toast.makeText(context, "Unchecked2", Toast.LENGTH_SHORT).show();
                                    submission_fullpost_likeList.setTextColor(getResources().getColor(R.color.gray));
                                    submission_fullpost_likeList.setText(likesArray.length() + " Like");
                                }
                            }

                            submissionLikeApi(submissionId+"");
                        }
                    });

                    submission_fullpost_likeList.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), LikeListActivity.class);
                            intent.putExtra("id", submissionId+"");
                            intent.putExtra(BundleConstants.IS_SUBMISSION,true);
                            startActivity(intent);
                        }

                    });

                    full_view_submission_menu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupMenu pop = new PopupMenu(getApplicationContext(), v);
                            pop.getMenuInflater().inflate(R.menu.single_submission_menu, pop.getMenu());
                            pop.show();
                            pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {

                                    switch (item.getItemId()) {
                                        case R.id.editPost:
                                            Intent intent = new Intent(getApplicationContext(), SubmitActivity.class);
                                            intent.putExtra(BundleConstants.SLUG, slug);
                                            intent.putExtra(BundleConstants.SUBMISSION_ID, submissionId+"");
                                            intent.putExtra(BundleConstants.IS_EDIT_SUBMISSION, true);
                                            startActivity(intent);
                                            break;
                                        case R.id.deletePost:
                                            showCommentDeleteDialog(submissionId+"",slug);
                                            break;

                                    }
                                    return true;
                                }
                            });
                        }
                    });


                    JSONArray commentsArray = usersSubmissiondetail.getJSONArray("comments");
                    loadComments(commentsArray);
                    int commentLength=commentsArray.length();
                    sub_detail_comment_count.setText(commentLength+" comments");
                    fullView_Submission_Title.setText("Title : "+title);
                    team_code.setText(code);
                    submission_time.setText(UtilsClass.getTime(updated_at));
                    submission_fullpost_likeList.setText(likesLength+" likes");
                    submission_fullpost_comment.setText(commentLength+" comments");
                    for(int i=0;i<commentsArray.length();i++){
                        try {
                            JSONObject commentsObject=commentsArray.getJSONObject(i);
                            if (userClass.getUserId() == commentsObject.getInt("user_id")) {
                                submission_fullpost_comment.setTextColor(getResources().getColor(R.color.sqr));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    submission_fullpost_comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SubmissionDetailActivity.this, CommentsPage.class);
                            intent.putExtra("IsSubmittedData",true);
                            intent.putExtra("PostId",submissionId+""); //second param is Serializable
                            startActivity(intent);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: ", error);
                Log.d(TAG, "onErrorResponse: error network response = " + error.networkResponse);
//                ViewUtils.dismissProgressBar();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put(getResources().getString(R.string.accept),getResources().getString(R.string.application_json));
                headers.put(getString(R.string.authorization), Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
                return headers;
            }


        };
        mRequestQueue.add(request);

    }

    private void LoadPdfBody(String pdf)
    {
        pdf="https://sqrfactor.com/"+pdf;

        WebView myWebView=findViewById(R.id.webViewPdf);
        myWebView.setVisibility(View.VISIBLE);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.setWebChromeClient(new WebChromeClient(){});
        myWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + pdf);
    }

    private void LoadEditorBody(String body) {
        final String finalHtml="   <html>\n" +
                "  <head>\n" +
                "    <title>Combined</title>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div id=\"page1\">\n" +
                body +
                "    </div>\n" +
                "  </body>\n" +
                "</html>";
        Thread thread = new Thread() {
            @Override
            public void run() {
                try { Thread.sleep(100); }
                catch (InterruptedException e) {}
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PostContentHandlerForSubmissionData postContentHandlerForSubmissionData=new PostContentHandlerForSubmissionData(getApplicationContext(),finalHtml,linearLayoutWebView,submission_fullPostDescription);
                        postContentHandlerForSubmissionData.setContentToView();
                    }
                });
            }
        };
        thread.start();
    }

    private void PostSubmissionCommentOnServer(final String commentBody) {
        //  Toast.makeText(SubmissionDetailActivity.this,commentBody,Toast.LENGTH_LONG).show();
        mSp = MySharedPreferences.getInstance(SubmissionDetailActivity.this);
        mRequestQueue = MyVolley.getInstance().getRequestQueue();

        final StringRequest request = new StringRequest(Request.Method.POST, ServerConstants.POST_SUBMISSIONS_COMMENT_TO_SERVER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.d(TAG, "submission sort response = " + response);
                Toast.makeText(SubmissionDetailActivity.this,response,Toast.LENGTH_LONG).show();
                try {

                    JSONObject responseObject = new JSONObject(response);
                    if(responseObject.getBoolean("return")) {

                        JSONObject userObject = responseObject.getJSONObject("from_user");
                        JSONObject commentObject = responseObject.getJSONObject("comment");
                        String id = commentObject.getString("id");
                        String userId = commentObject.getString("user_id");
                        String description = commentObject.getString("body");

                        String askedBy = UtilsClass.getName(userObject.getString("first_name"),userObject.getString("last_name"),
                                userObject.getString("name"),userObject.getString("user_name"));

                        String dateAsked = commentObject.getString("updated_at");
                        String imageUrl =UtilsClass.getParsedImageUrl(userObject.getString("profile"));

                        QuestionCommentClass comment = new QuestionCommentClass(id, userId, description, askedBy, dateAsked, imageUrl,submissionId);
                        mComments.add(comment);
                        mCommentsAdapter.notifyDataSetChanged();
                        sub_detail_comment_count.setText(mComments.size()+" comments");
                        submission_fullpost_comment.setText(mComments.size()+" comments");
                        SendNotificationOnComment();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put(getResources().getString(R.string.accept),getResources().getString(R.string.application_json));
                headers.put(getString(R.string.authorization), Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
                return headers;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("commentable_id",submissionId+"");
                params.put("body",commentBody);
                return params;
            }


        };
        mRequestQueue.add(request);
    }


    private void loadComments(JSONArray mCommentsArray) {
        for (int i = 0; i < mCommentsArray.length(); i++) {
            try {
                JSONObject singleObject = mCommentsArray.getJSONObject(i);
                JSONObject userObj = singleObject.getJSONObject("user");

                String id = singleObject.getString("id");
                String userId = singleObject.getString("user_id");
                String description = singleObject.getString("body");
                String dateAsked = singleObject.getString("updated_at");
                String askedBy = UtilsClass.getName(userObj.getString("first_name"),userObj.getString("last_name"),
                        userObj.getString("name"),userObj.getString("user_name"));
                String imageUrl = UtilsClass.getParsedImageUrl(userObj.getString("profile"));
                QuestionCommentClass comment = new QuestionCommentClass(id, userId, description, askedBy, dateAsked, imageUrl,submissionId);
                mComments.add(comment);
                mCommentsAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void SendNotificationOnComment() {
        String name=UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name());
        for(int i=0;i<participationIDArrayList.size();i++) {
            if (userClass.getUserId() != participationIDArrayList.get(i)) {
                post post1 = new post("", "", "", "", 0);
                from_user   fromUser = new from_user(userClass.getEmail(), name, userClass.getUserId(), userClass.getUser_name(), userClass.getProfile());
                PushNotificationClass pushNotificationClass = new PushNotificationClass( " commented on your post ", new Date().getTime(), fromUser, post1, "comment");
                String key = ref.child("notification").child(participationIDArrayList.get(i) + "").child("all").push().getKey();
                ref.child("notification").child(participationIDArrayList.get(i) + "").child("all").child(key).setValue(pushNotificationClass);
                Map<String, String> unred = new HashMap<>();
                unred.put("unread", key);
                ref.child("notification").child(participationIDArrayList.get(i) + "").child("unread").child(key).setValue(unred);
            }
        }

    }


    private void submissionLikeApi(final String submissionId) {
        //mPd.show();

        if (!NetworkUtil.isNetworkAvailable()) {
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
            return;
        }
        mRequestQueue = MyVolley.getInstance().getRequestQueue();

        StringRequest request = new StringRequest(Request.Method.POST, ServerConstants.SUBMISSION_LIKE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // mPd.dismiss();
                NetworkUtil.handleSimpleVolleyRequestError(error, getApplicationContext());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
                headers.put(getString(R.string.accept), getString(R.string.application_json));
                headers.put(getString(R.string.authorization), Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));

                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("like_id", submissionId);

                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }

    private void showCommentDeleteDialog(final String  submissionId,final String competitionSlug) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SubmissionDetailActivity.this);
        builder.setTitle("Are you sure you want to delete this design?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteSubmission(submissionId,competitionSlug);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void DeleteSubmission(final String submissionId,final String competitionSlug) {
        //mPd.show();

        if (!NetworkUtil.isNetworkAvailable()) {
            Toast.makeText(SubmissionDetailActivity.this, "No internet", Toast.LENGTH_SHORT).show();
            //mPd.dismiss();
            return;
        }
        mRequestQueue = MyVolley.getInstance().getRequestQueue();

        StringRequest request = new StringRequest(Request.Method.POST, ServerConstants.DELETE_SUBMISSION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(SubmissionDetailActivity.this,response,Toast.LENGTH_LONG).show();
                Log.d(TAG, "onResponse: submission delete response = " + response);

                try {

                    JSONObject responseObject = new JSONObject(response);
                    String isDeletede =responseObject.getString("Return Response");
                    if(isDeletede!=null && isDeletede.equals("True")) {
                        Intent intent=new Intent(SubmissionDetailActivity.this,CompetitionDetailActivity.class);
                        intent.putExtra(BundleConstants.SLUG,competitionSlug);
                        startActivity(intent);
                        finish();
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPd.dismiss();
                NetworkUtil.handleSimpleVolleyRequestError(error, SubmissionDetailActivity.this);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
                headers.put(getString(R.string.accept),getString(R.string.application_json));
                headers.put(getString(R.string.authorization), Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));

                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("submission_id", submissionId);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }



    public void SetCommentCount(int commentCount)
    {
        sub_detail_comment_count.setText(commentCount+" comments");
//        submission_fullpost_comment.setText(commentCount+" comments");
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Log.i(TAG, "Setting screen name: " + name);
        if(competition_title!=null && !TextUtils.isEmpty(competition_title.getText().toString()))
        mTracker.setScreenName("SubmissionDetailActivity /"+competition_title.getText().toString()+" /"+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
       else
            mTracker.setScreenName("SubmissionDetailActivity /"+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


}
