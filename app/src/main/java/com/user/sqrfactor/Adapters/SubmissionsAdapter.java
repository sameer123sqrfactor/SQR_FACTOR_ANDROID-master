package com.user.sqrfactor.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.user.sqrfactor.Activities.SubmissionDetailActivity;
import com.user.sqrfactor.Activities.SubmitActivity;
import com.user.sqrfactor.Activities.CommentsPage;
import com.user.sqrfactor.Constants.BundleConstants;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Activities.LikeListActivity;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.SubmissionClass;
import com.user.sqrfactor.Pojo.from_user;
import com.user.sqrfactor.Pojo.post;
import com.user.sqrfactor.Pojo.PushNotificationClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Utils.NetworkUtil;
import com.user.sqrfactor.Extras.UtilsClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class SubmissionsAdapter extends RecyclerView.Adapter<SubmissionsAdapter.MyViewHolder> {
    private static final String TAG = "SubmissionsAdapter";
    private Context mContext;
    private List<SubmissionClass> mSubmissions;
    private MySharedPreferences mSp;
    private ProgressDialog mPd;
    private RequestQueue mRequestQueue;
    private FirebaseDatabase database;
    private DatabaseReference ref;


    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView titleTV;
        TextView submission_likeList;
        TextView codeTV;
        ImageView coverIV;
        CheckBox likeButton;
        Button commentButton;
        ImageView submission_menu;

        MyViewHolder(View view) {
            super(view);
            titleTV = view.findViewById(R.id.submission_title);
            codeTV = view.findViewById(R.id.submission_code);
            coverIV = view.findViewById(R.id.submission_cover);
            likeButton = view.findViewById(R.id.submission_like);
            commentButton = view.findViewById(R.id.submission_comment);
            submission_likeList=view.findViewById(R.id.submission_likeList);
            submission_menu=view.findViewById(R.id.submission_menu);

            //likeButton.setOnClickListener(this);

            //view.setOnClickListener(this);

        }


    }



    public SubmissionsAdapter(Context context, List<SubmissionClass> submissions) {
        this.mContext = context;
        this.mSubmissions = submissions;
        mSp = MySharedPreferences.getInstance(mContext);

//        mPd = new ProgressDialog(mContext);
//        mPd.setMessage("Uploading Please Wait..");
//        mPd.setCancelable(false);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.submission_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final SubmissionClass submission = mSubmissions.get(position);
        int isAlreadyLiked = 0;
        int isAlreadyCommented = 0;

        SharedPreferences mPrefs = mContext.getSharedPreferences("User", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        final UserClass userClass = gson.fromJson(json, UserClass.class);

        holder.titleTV.setText(submission.getTitle());
        holder.codeTV.setText(submission.getCode());
        String coverUrl = submission.getCoverUrl();

        final JSONArray commentsArray = submission.getCommentsArray();
        final JSONArray likesArray = submission.getLikesArray();

        if (submission.getCommentsArray()!=null && submission.getCommentsArray().length() > 0) {
            holder.commentButton.setText(submission.getCommentsArray().length() + " comments");
           // Log.v("commentsCount"+position,submission.getCommentsArray().length()+"");
            for(int i=0;i<submission.getCommentsArray().length();i++){
                try {
                    JSONObject commentsObject=submission.getCommentsArray().getJSONObject(i);
                    if (userClass.getUserId() == commentsObject.getInt("user_id")) {
                        holder.commentButton.setTextColor(mContext.getResources().getColor(R.color.sqr));
                        // isAlreadyLiked = 1;
                        //  holder.commentButton.setChecked(true);
                        ///flag=1;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else {
            Log.v("commentsCount"+position,0+"");
        }



        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentsPage.class);
                intent.putExtra("IsSubmittedData",true);
                intent.putExtra("PostId",submission.getId()); //second param is Serializable
                mContext.startActivity(intent);
            }
        });


        holder.coverIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, SubmissionDetailActivity.class);
                i.putIntegerArrayListExtra(BundleConstants.PARTICIPATION_ID_ARRAY,submission.getParticipationIdArray());
                i.putExtra(BundleConstants.SLUG, submission.getSlug());
                i.putExtra(BundleConstants.SUBMISSION_ID, submission.getId());
                mContext.startActivity(i);
            }
        });




        if (submission.getLikesArray()!=null && submission.getLikesArray().length() > 0) {
            holder.submission_likeList.setText(submission.getLikesArray().length() + " likes");
            //Log.v("likesArray",)
            Log.v("LikesCountXX"+position,submission.getLikesArray().length()+"");

            for (int i = 0; i < submission.getLikesArray().length(); i++) {
                try {
                    JSONObject likeObject=submission.getLikesArray().getJSONObject(i);
                    if (userClass.getUserId() == likeObject.getInt("user_id")) {
                        holder.submission_likeList.setTextColor(mContext.getResources().getColor(R.color.sqr));
                        isAlreadyLiked = 1;
                        holder.likeButton.setChecked(true);
                        ///flag=1;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (coverUrl != null && !coverUrl.equals("null")) {
//            Picasso.get().load(ServerConstants.IMAGE_BASE_URL + submission.getCoverUrl()).
//                    resize(350, 200).placeholder(R.drawable.no_image_placeholder).
//                    into(holder.coverIV);

            Glide.with(mContext)
                    .load(UtilsClass.getParsedImageUrl(submission.getCoverUrl()))
                    .apply(new RequestOptions().override(350, 200))
                    .into(holder.coverIV);
        }





        final int isAlreadyLikedFinal = isAlreadyLiked;

        holder.likeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    int likeCount = 0;
                    if (submission.getLikesArray()!=null && submission.getLikesArray().length()!=0) {
                        likeCount=submission.getLikesArray().length();
                    }
                    holder.submission_likeList.setTextColor(mContext.getResources().getColor(R.color.sqr));
                    if (isAlreadyLikedFinal == 1)
                        holder.submission_likeList.setText(likeCount + " Like");
                    else {
                        likeCount = likeCount + 1;
                        holder.submission_likeList.setText(likeCount + " Like");
                    }

//                    database = FirebaseDatabase.getInstance();
//                    ref = database.getReference();

                    String name=UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name());
                    database = FirebaseDatabase.getInstance();
                    ref = database.getReference();


                   // Toast.makeText(mContext,submission.getParticipationIdArray().size(),Toast.LENGTH_LONG).show();

                    for(int i=0;i<submission.getParticipationIdArray().size();i++) {

                        Log.v("participationCount",submission.getParticipationIdArray().get(i)+"");
                        if (userClass.getUserId()!=submission.getParticipationIdArray().get(i)) {
                            PushNotificationClass pushNotificationClass;
                            from_user fromUser;

                            post post1 = new post("", "", "post Title", "", 0);
                            fromUser = new from_user(userClass.getEmail(), name, userClass.getUserId(), userClass.getUser_name(), userClass.getProfile());
                            pushNotificationClass = new PushNotificationClass( "liked your submitted design", new Date().getTime(), fromUser, post1, "like_post");
                            String key = ref.child("notification").child(submission.getParticipationIdArray().get(i) + "").child("all").push().getKey();


                            ref.child("notification").child(submission.getParticipationIdArray().get(i)+ "").child("all").child(key).setValue(pushNotificationClass);
                            Map<String, String> unred = new HashMap<>();
                            unred.put("unread", key);
                            ref.child("notification").child(submission.getParticipationIdArray().get(i) + "").child("unread").child(key).setValue(unred);

                        }
                    }
                } else {

                    if (isAlreadyLikedFinal == 1) {
                        holder.submission_likeList.setTextColor(mContext.getResources().getColor(R.color.gray));
                        if(submission.getLikesArray()!=null && submission.getLikesArray().length()>0){
                            int likeCount1 = submission.getLikesArray().length();
                            likeCount1 = likeCount1 - 1;
                            holder.submission_likeList.setText(likeCount1 + " Like");
                        }

                    } else {
                        if(submission.getLikesArray()!=null && submission.getLikesArray().length()>0) {
                            holder.submission_likeList.setTextColor(mContext.getResources().getColor(R.color.gray));
                            holder.submission_likeList.setText(submission.getLikesArray().length() + " Like");
                        }
                    }
                }

                submissionLikeApi(submission.getId());
            }
           //

        });
        holder.submission_likeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LikeListActivity.class);
                intent.putExtra("id", submission.getId());
                intent.putExtra(BundleConstants.IS_SUBMISSION,true);
                mContext.startActivity(intent);
            }

        });
        if(submission.getUser_id().equals(userClass.getUserId()+"")){
            holder.submission_menu.setVisibility(View.VISIBLE);
        }else {
            holder.submission_menu.setVisibility(View.GONE);
        }
        holder.submission_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu pop = new PopupMenu(mContext, v);
                pop.getMenuInflater().inflate(R.menu.all_submission_list_menu, pop.getMenu());
                pop.show();
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.fullView:
                                Intent i = new Intent(mContext, SubmissionDetailActivity.class);
                                i.putIntegerArrayListExtra(BundleConstants.PARTICIPATION_ID_ARRAY,submission.getParticipationIdArray());
                                i.putExtra(BundleConstants.SLUG, submission.getSlug());
                                i.putExtra(BundleConstants.SUBMISSION_ID, submission.getId());
                                mContext.startActivity(i);
                                break;
                            case R.id.editPost:
                                Intent intent = new Intent(mContext, SubmitActivity.class);
                                intent.putExtra(BundleConstants.SLUG, submission.getSlug());
                                intent.putExtra(BundleConstants.SUBMISSION_ID, submission.getId());
                                intent.putExtra(BundleConstants.IS_EDIT_SUBMISSION, true);
                                mContext.startActivity(intent);
                                break;
                            case R.id.deletePost:
                                showCommentDeleteDialog(submission.getId(),position);
                                break;

                        }
                        return true;
                    }
                });
            }
        });



    }

    @Override
    public int getItemCount() {
        return mSubmissions.size();
    }

    private void showCommentDeleteDialog(final String  submissionId, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle("Are you sure you want to delete this design?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteSubmission(submissionId,position);
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


    private void DeleteSubmission(final String submissionId, final int position) {
        //mPd.show();

        if (!NetworkUtil.isNetworkAvailable()) {
            Toast.makeText(mContext, "No internet", Toast.LENGTH_SHORT).show();
            //mPd.dismiss();
            return;
        }
        mRequestQueue = MyVolley.getInstance().getRequestQueue();

        StringRequest request = new StringRequest(Request.Method.POST, ServerConstants.DELETE_SUBMISSION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mPd.dismiss();

//                Toast.makeText(mContext,response,Toast.LENGTH_LONG).show();
//                Log.d(TAG, "onResponse: submission delete response = " + response);

                try {

                    JSONObject responseObject = new JSONObject(response);
                    String isDeletede =responseObject.getString("Return Response");
                    if(isDeletede!=null && isDeletede.equals("True")) {
                        mSubmissions.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeRemoved(position, 1);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPd.dismiss();
                NetworkUtil.handleSimpleVolleyRequestError(error, mContext);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
                headers.put(mContext.getString(R.string.accept), mContext.getString(R.string.application_json));
                headers.put(mContext.getString(R.string.authorization), Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));

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


    private void submissionLikeApi(final String submissionId) {
        //mPd.show();

//        if (!NetworkUtil.isNetworkAvailable()) {
//            Toast.makeText(mContext, "No internet", Toast.LENGTH_SHORT).show();
//            //mPd.dismiss();
//            return;
//        }
        mRequestQueue = MyVolley.getInstance().getRequestQueue();

        StringRequest request = new StringRequest(Request.Method.POST, ServerConstants.SUBMISSION_LIKE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               // mPd.dismiss();

                Toast.makeText(mContext,response,Toast.LENGTH_LONG).show();
                Log.d(TAG, "onResponse: submission like response = " + response);

                try {
                    JSONObject responseObject = new JSONObject(response);
                   // JSONObject respObject = responseObject.getJSONObject("Response");

                   // String message = respObject.getString("message");
//                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // mPd.dismiss();
                NetworkUtil.handleSimpleVolleyRequestError(error, mContext);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
                headers.put(mContext.getString(R.string.accept), mContext.getString(R.string.application_json));
                headers.put(mContext.getString(R.string.authorization), Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));

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




}