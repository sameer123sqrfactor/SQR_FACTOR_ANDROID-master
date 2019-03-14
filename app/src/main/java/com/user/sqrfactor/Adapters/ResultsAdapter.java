package com.user.sqrfactor.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
import com.user.sqrfactor.Activities.CommentsPage;
import com.user.sqrfactor.Activities.LikeListActivity;
import com.user.sqrfactor.Activities.SubmissionDetailActivity;
import com.user.sqrfactor.Constants.BundleConstants;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.PushNotificationClass;
import com.user.sqrfactor.Pojo.ResultClass;
import com.user.sqrfactor.Pojo.from_user;
import com.user.sqrfactor.Pojo.post;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.squareup.picasso.Picasso;
import com.user.sqrfactor.Utils.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ResultsAdapter";
    private Context mContext;
    private List<ResultClass> mResults;

    private RequestQueue mRequestQueue;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    private MySharedPreferences mSp;


    class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleTV;
        TextView codeTV;
        TextView prizeTV;
        ImageView coverIV;
        CheckBox likeButton;
        TextView likeCount;
        Button commentButton;
        Button shareButton;

        DataViewHolder(View view) {
            super(view);
            titleTV = view.findViewById(R.id.result_title);
            codeTV = view.findViewById(R.id.result_code);
            coverIV = view.findViewById(R.id.result_cover);
            likeCount=view.findViewById(R.id.result_likeList);
            likeButton = view.findViewById(R.id.result_like);
            commentButton = view.findViewById(R.id.result_comment);
            shareButton = view.findViewById(R.id.result_share);
            prizeTV = view.findViewById(R.id.result_prize);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: called");
            int pos = getAdapterPosition();
            ResultClass result = mResults.get(pos);

        }
    }

    class HeadingViewHolder extends RecyclerView.ViewHolder {
        TextView headingTV;

        HeadingViewHolder(View view) {
            super(view);
            headingTV = view.findViewById(R.id.result_heading);
        }
    }

    public ResultsAdapter(Context context, List<ResultClass> results) {
        this.mContext = context;
        this.mResults = results;

        mSp = MySharedPreferences.getInstance(mContext);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        switch (viewType) {
            case Constants.TYPE_DATA: {
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.result_item, parent, false);

                return new DataViewHolder(itemView);
            }
            case Constants.TYPE_HEADING: {
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.result_layout_heading, parent, false);

                return new HeadingViewHolder(itemView);
            }
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        switch (mResults.get(position).getType()) {
            case 0: {
                return Constants.TYPE_HEADING;
            }
            case 1: {
                return Constants.TYPE_DATA;
            }
            default: return -1;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final ResultClass result = mResults.get(position);
        int isAlreadyLiked = 0;
        final UserClass userClass= UtilsClass.GetUserClassFromSharedPreferences(mContext);
        if (holder instanceof DataViewHolder) {
            final DataViewHolder dataViewHolder = (DataViewHolder) holder;

            dataViewHolder.titleTV.setText(result.getTitle());
            dataViewHolder.codeTV.setText(result.getCode());
            dataViewHolder.prizeTV.setText(result.getPrizeTitle());


            dataViewHolder.commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CommentsPage.class);
                    intent.putExtra("IsCompetitionResultData",true);
                    intent.putExtra("PostId",result.getId()); //second param is Serializable
                    mContext.startActivity(intent);
                }
            });

            if (result.getCommentArray()!=null && result.getCommentArray().length() > 0) {
                dataViewHolder.commentButton.setText(result.getCommentArray().length()+ " comments");
                for(int i=0;i<result.getCommentArray().length();i++){
                    try {
                        JSONObject commentsObject=result.getCommentArray().getJSONObject(i);
                        if (userClass.getUserId() == commentsObject.getInt("user_id")) {
                            dataViewHolder.commentButton.setTextColor(mContext.getResources().getColor(R.color.sqr));
                            // isAlreadyLiked = 1;
                            //  holder.commentButton.setChecked(true);
                            ///flag=1;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (result.getLikesArray()!=null && result.getLikesArray().length() > 0) {
                dataViewHolder.likeCount.setText(result.getLikesArray().length() + " likes");
                for (int i = 0; i < result.getLikesArray().length(); i++) {
                    try {
                        JSONObject likeObject=result.getLikesArray().getJSONObject(i);
                        if (userClass.getUserId() == likeObject.getInt("user_id")) {
                            dataViewHolder.likeCount.setTextColor(mContext.getResources().getColor(R.color.sqr));
                            isAlreadyLiked = 1;
                            dataViewHolder.likeButton.setChecked(true);
                            ///flag=1;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }







            final int isAlreadyLikedFinal = isAlreadyLiked;


            dataViewHolder.coverIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, SubmissionDetailActivity.class);
                    i.putIntegerArrayListExtra(BundleConstants.PARTICIPATION_ID_ARRAY,result.getParticipationIdArray());
                    i.putExtra(BundleConstants.SLUG, result.getSlug());
                    i.putExtra(BundleConstants.SUBMISSION_ID, result.getId());
                    mContext.startActivity(i);
                }
            });
            String coverUrl = result.getCoverUrl();
            if (coverUrl != null && !coverUrl.equals("null")) {

                Glide.with(mContext)
                        .load(ServerConstants.IMAGE_BASE_URL + result.getCoverUrl())
                    .apply(new RequestOptions().override(300, 300).placeholder(R.drawable.no_image_placeholder).centerCrop())
                        .into(dataViewHolder.coverIV);
            }
            dataViewHolder.likeCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, LikeListActivity.class);
                    intent.putExtra("id", result.getId());
                    intent.putExtra(BundleConstants.IS_SUBMISSION,true);
                    mContext.startActivity(intent);
                }

            });

            dataViewHolder.likeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        int likeCount = 0;
                        if (result.getLikesArray()!=null && result.getLikesArray().length()!=0) {
                            likeCount=result.getLikesArray().length();
                        }
                        dataViewHolder.likeCount.setTextColor(mContext.getResources().getColor(R.color.sqr));
                        if (isAlreadyLikedFinal == 1)
                            dataViewHolder.likeCount.setText(likeCount + " Like");
                        else {
                            likeCount = likeCount + 1;
                            dataViewHolder.likeCount.setText(likeCount + " Like");
                        }

//                    database = FirebaseDatabase.getInstance();
//                    ref = database.getReference();

                        String name=UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name());
                        database = FirebaseDatabase.getInstance();
                        ref = database.getReference();


                        // Toast.makeText(mContext,submission.getParticipationIdArray().size(),Toast.LENGTH_LONG).show();

                        for(int i=0;i<result.getParticipationIdArray().size();i++) {

                            Log.v("participationCount",result.getParticipationIdArray().get(i)+"");
                            if (userClass.getUserId()!=result.getParticipationIdArray().get(i)) {
                                PushNotificationClass pushNotificationClass;
                                from_user fromUser;

                                post post1 = new post("", "", "post Title", "", 0);
                                fromUser = new from_user(userClass.getEmail(), name, userClass.getUserId(), userClass.getUser_name(), userClass.getProfile());
                                pushNotificationClass = new PushNotificationClass( "liked your submitted design", new Date().getTime(), fromUser, post1, "like_post");
                                String key = ref.child("notification").child(result.getParticipationIdArray().get(i) + "").child("all").push().getKey();


                                ref.child("notification").child(result.getParticipationIdArray().get(i)+ "").child("all").child(key).setValue(pushNotificationClass);
                                Map<String, String> unred = new HashMap<>();
                                unred.put("unread", key);
                                ref.child("notification").child(result.getParticipationIdArray().get(i) + "").child("unread").child(key).setValue(unred);

                            }
                        }
                    } else {

                        if (isAlreadyLikedFinal == 1) {
                            // Log.v("isAlreadyLiked1", isAlreadyLikedFinal + " ");
                            dataViewHolder.likeCount.setTextColor(mContext.getResources().getColor(R.color.gray));
                            int likeCount1 = result.getLikesArray().length();
                            //Toast.makeText(context, "Unchecked1", Toast.LENGTH_SHORT).show();
                            likeCount1 = likeCount1 - 1;
                            dataViewHolder.likeCount.setText(likeCount1 + " Like");
                        } else {
                            // Log.v("isAlreadyLiked2", isAlreadyLikedFinal + " ");
                            //Toast.makeText(context, "Unchecked2", Toast.LENGTH_SHORT).show();
                            dataViewHolder.likeCount.setTextColor(mContext.getResources().getColor(R.color.gray));
                            dataViewHolder.likeCount.setText(result.getLikesArray().length() + " Like");
                        }


                    }

                    submissionLikeApi(result.getId());
                }
                //

            });





        } else if (holder instanceof HeadingViewHolder) {
            ((HeadingViewHolder) holder).headingTV.setText(result.getHeading());
        }

    }

    @Override
    public int getItemCount() {
        return mResults.size();
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
//                     JSONObject respObject = responseObject.getJSONObject("Response");
//
//                     String message = respObject.getString("message");
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