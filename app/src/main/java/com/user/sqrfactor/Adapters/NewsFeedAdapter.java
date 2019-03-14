package com.user.sqrfactor.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.user.sqrfactor.Activities.ArticleActivity;
import com.user.sqrfactor.Activities.DesignActivity;
import com.user.sqrfactor.Activities.FullPostActivity;
import com.user.sqrfactor.Activities.LikeListActivity;
import com.user.sqrfactor.Activities.CommentsPage;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Fragments.MDToast;
import com.user.sqrfactor.Pojo.NewsFeedStatus;
import com.user.sqrfactor.Pojo.comments_limited;
import com.user.sqrfactor.Pojo.from_user;
import com.user.sqrfactor.Pojo.post;
import com.user.sqrfactor.Activities.ProfileActivity;
import com.user.sqrfactor.Pojo.PushNotificationClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Activities.StatusPostActivity;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Activities.UserProfileActivity;
import com.user.sqrfactor.Extras.UtilsClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.MyViewHolder> {
    private ArrayList<NewsFeedStatus> newsFeedStatuses;
    private Context context;
    private PopupWindow popupWindow;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private String userName;
    private MySharedPreferences mSp;


    int commentsCount = 0, likeCount = 0;
    private UserClass userClass;


    public NewsFeedAdapter(ArrayList<NewsFeedStatus> newsFeedStatuses, Context context) {
        this.newsFeedStatuses = newsFeedStatuses;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_status_adapter, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final int[] commentId = new int[1];
        int isAlreadyLiked = 0;
        int isAlreadyCommented = 0;
        int isAlreadyLikedComment=0;
        boolean isStatus=false;


        mSp = MySharedPreferences.getInstance(context);
        SharedPreferences mPrefs = context.getSharedPreferences("User", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        userClass = gson.fromJson(json, UserClass.class);

        final NewsFeedStatus newsFeedStatus = newsFeedStatuses.get(position);
        holder.authName.setText(UtilsClass.getName(newsFeedStatus.getFirst_name(), newsFeedStatus.getLast_name(), newsFeedStatus.getAuthName(), newsFeedStatus.getUser_name_of_post()));

        if (newsFeedStatus.getType().equals("status")) {

            holder.postTitle.setVisibility(View.GONE);
            holder.postTag.setVisibility(View.GONE);

            userName = newsFeedStatus.getUser_name_of_post();
            isStatus=true;
            holder.shortDescription.setText(newsFeedStatus.getFullDescription());
            Glide.with(context).load(UtilsClass.baseurl1+ newsFeedStatus.getUserImageUrl())
                    .into(holder.postImage);
//
        } else if (newsFeedStatus.getType().equals("design")) {

            holder.postTitle.setVisibility(View.VISIBLE);
            //holder.postTag.setVisibility(View.VISIBLE);

            isStatus=false;
            userName = newsFeedStatus.getUser_name_of_post();
            holder.postTitle.setText(newsFeedStatus.getPostTitle());
            holder.shortDescription.setText(newsFeedStatus.getShortDescription());
            Glide.with(context).load(UtilsClass.baseurl1+newsFeedStatus.getPostImage())
                    .into(holder.postImage);
        } else if (newsFeedStatus.getType().equals("article")) {
            Log.v("status2", newsFeedStatus.getType());
            holder.postTitle.setVisibility(View.VISIBLE);
           // holder.postTag.setVisibility(View.VISIBLE);
            isStatus=false;
            holder.postTitle.setText(newsFeedStatus.getPostTitle());
            userName = newsFeedStatus.getUser_name_of_post();
            holder.shortDescription.setText(newsFeedStatus.getShortDescription());
            Glide.with(context).load(UtilsClass.baseurl1+newsFeedStatus.getPostImage())
                    .into(holder.postImage);

        }

        Glide.with(context).load(UtilsClass.getParsedImageUrl(newsFeedStatus.getAuthImageUrl()))
                .into(holder.authImageUrl);
        holder.authName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userClass.getUserId() == newsFeedStatus.getUserId()) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("User_id", newsFeedStatus.getUserId());
                    intent.putExtra("ProfileUserName", newsFeedStatus.getUser_name_of_post());
                    intent.putExtra("ProfileUrl", newsFeedStatus.getUserImageUrl());
                    intent.putExtra("UserType", newsFeedStatus.getUser_type_of_post());
                    context.startActivity(intent);
                }


            }
        });

        if(userClass!=null){
            Glide.with(context).load(UtilsClass.getParsedImageUrl(userClass.getProfile()))
                    .into(holder.news_user_imageProfile);
        }


        final boolean finalIsStatus = isStatus;
        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullPostActivity.class);
                intent.putExtra("Post_Slug_ID", newsFeedStatus.getSlug());
                intent.putExtra("post_type", finalIsStatus);
                context.startActivity(intent);

            }
        });

       // if (newsFeedStatus.getTime()!=null || !newsFeedStatus.getTime().equals("null"))
        holder.time.setText(UtilsClass.getTime(newsFeedStatus.getTime()));
//
        if (newsFeedStatus.getComments() != null)
        {
            commentsCount = Integer.parseInt(newsFeedStatus.getComments());
            holder.comments.setText(commentsCount + " Comment");

            for(int i=0;i<newsFeedStatus.getCommentsLimitedArrayList().size();i++){
                comments_limited commentLimited =newsFeedStatus.commentsLimitedArrayList.get(i);

                if(i==0){

                    holder.news_comment_card1.setVisibility(View.VISIBLE);
                    holder.commentTime1.setText(UtilsClass.getTime(commentLimited.getCreated_at()));
                    holder.commentDescription1.setText(commentLimited.getBody());
                    Glide.with(context).load(UtilsClass.getParsedImageUrl(commentLimited.getCommentUserPrfile()))
                            .into(holder.commentProfileImageUrl1);
                    String name=UtilsClass.getName(commentLimited.getCommenterFirstName(),commentLimited.getCommenterLastName(),"null",commentLimited.getCommentUserName());
                    holder.commentUserName1.setText(name);
                    if(userClass.getUserId()==commentLimited.getUser_id()){
                        holder.news_comment_menu1.setVisibility(View.VISIBLE);
                    }
                    isAlreadyLikedComment=0;
                    for(int j=0;j<commentLimited.getCommentLikesArray().length();j++){
                        try {
                            if (userClass.getUserId() == commentLimited.getCommentLikesArray().getJSONObject(i).getInt("user_id")) {
                                holder.news_comment_likeList1.setTextColor(context.getResources().getColor(R.color.sqr));
                                //isAlreadyLiked = 1;
                                isAlreadyLikedComment=1;
                                holder.news_commnent_like1.setChecked(true);
                                ///flag=1;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    final int finalIsAlreadyLikedComment = isAlreadyLikedComment;
                    holder.news_commnent_like1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            CommentLikeButtonClicked(isChecked,newsFeedStatus,holder, finalIsAlreadyLikedComment);
                        }
                    });


                }else if(i==1){
                   // comments_limited commentLimited =newsFeedStatus.commentsLimitedArrayList.get(i);
                    holder.news_comment_card2.setVisibility(View.VISIBLE);
                    holder.commentTime2.setText(UtilsClass.getTime(commentLimited.getCreated_at()));
                    holder.commentDescription2.setText(commentLimited.getBody());
                    Glide.with(context).load(UtilsClass.getParsedImageUrl(commentLimited.getCommentUserPrfile()))
                            .into(holder.commentProfileImageUrl2);
                    String name=UtilsClass.getName(commentLimited.getCommenterFirstName(),commentLimited.getCommenterLastName(),"null",commentLimited.getCommentUserName());
                    holder.commentUserName2.setText(name);
                    if(userClass.getUserId()==commentLimited.getUser_id()){
                        holder.news_comment_menu2.setVisibility(View.VISIBLE);
                    }
                    isAlreadyLikedComment=0;
                    for(int j=0;j<commentLimited.getCommentLikesArray().length();j++){
                        try {
                            if (userClass.getUserId() == commentLimited.getCommentLikesArray().getJSONObject(i).getInt("user_id")) {
                                holder.news_comment_likeList2.setTextColor(context.getResources().getColor(R.color.sqr));
                                //isAlreadyLiked = 1;
                                isAlreadyLikedComment=1;
                                holder.news_commnent_like2.setChecked(true);
                                ///flag=1;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    final int finalIsAlreadyLikedComment = isAlreadyLikedComment;
                    holder.news_commnent_like2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            CommentLikeButtonClicked1(isChecked,newsFeedStatus,holder, finalIsAlreadyLikedComment);
                        }
                    });


                }
            }

        }




        if (newsFeedStatus.getLike() != null) {
            likeCount = Integer.parseInt(newsFeedStatus.getLike());
            holder.likelist.setText(likeCount + " Like");
        }



        holder.comments.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentsPage.class);
                intent.putExtra("PostSharedId", newsFeedStatus.getSharedId());
                intent.putExtra("PostId", newsFeedStatus.getPostId()); //second param is Serializable
                context.startActivity(intent);

            }
        });


       // Log.v("userIdPosition","user"+position+" /"+newsFeedStatus.getUserId());
        if (userClass.getUserId() == newsFeedStatus.getUserId()) {
            holder.news_post_menu.setVisibility(View.VISIBLE);
        }
        else {
            holder.news_post_menu.setVisibility(View.GONE);
        }
        holder.news_post_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pop = new PopupMenu(context, v);
                pop.getMenuInflater().inflate(R.menu.delete_news_post_menu, pop.getMenu());
                pop.show();
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.fullView:
                                Intent intent = new Intent(context, FullPostActivity.class);
                                intent.putExtra("Post_Slug_ID", newsFeedStatus.getSlug());
                                intent.putExtra("Post_ID", newsFeedStatus.getPostId());
                                context.startActivity(intent);
                                break;
                            case R.id.editPost:
                                if (newsFeedStatus.getType().equals("design")) {
                                    Intent intent1 = new Intent(context, DesignActivity.class);
                                    intent1.putExtra("Post_Slug_ID", newsFeedStatus.getSlug());
                                    intent1.putExtra("Post_ID", newsFeedStatus.getPostId());
                                    context.startActivity(intent1);
                                } else if (newsFeedStatus.getType().equals("article")) {
                                    Intent intent1 = new Intent(context, ArticleActivity.class);
                                    intent1.putExtra("Post_Slug_ID", newsFeedStatus.getSlug());
                                    intent1.putExtra("Post_ID", newsFeedStatus.getPostId());
                                    context.startActivity(intent1);
                                } else if (newsFeedStatus.getType().equals("status")) {
                                    Intent intent1 = new Intent(context, StatusPostActivity.class);
                                    intent1.putExtra("Post_Slug_ID", newsFeedStatus.getSlug());
                                    intent1.putExtra("Post_ID", newsFeedStatus.getPostId());
                                    context.startActivity(intent1);
                                }
                                break;
                            case R.id.deletePost:
                                newsFeedStatuses.remove(position);
//                                    notifyItemRemoved(position);
                                notifyItemRangeRemoved(position, 1);
                                DeletePost(newsFeedStatus.getPostId() + "", newsFeedStatus.getSharedId() + "", newsFeedStatus.getIs_Shared());
                                break;
                            case R.id.selectAsFeaturedPost:
                                return true;

                        }
                        return true;
                    }
                });
            }
        });

        for (int i = 0; i < newsFeedStatus.getAllLikesId().size(); i++) {
            if (userClass.getUserId() == newsFeedStatus.getAllLikesId().get(i)) {
                holder.likelist.setTextColor(context.getResources().getColor(R.color.sqr));
                isAlreadyLiked = 1;
                holder.like.setChecked(true);
                ///flag=1;
            }

        }


        final int isAlreadyLikedFinal = isAlreadyLiked;

        for (int i = 0; i < newsFeedStatus.getAllCommentId().size(); i++) {
            if (userClass.getUserId() == newsFeedStatus.getAllCommentId().get(i)) {
                holder.comments.setTextColor(context.getResources().getColor(R.color.sqr));
                holder.commentCheckBox.setChecked(true);
                isAlreadyCommented = 1;
                //holder.co.setChecked(true);
            }
            else {
                holder.comments.setTextColor(context.getResources().getColor(R.color.gray));
                holder.commentCheckBox.setChecked(false);
            }
        }
        final int isAlreadyCommentedFinal = isAlreadyCommented;

        holder.likelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, LikeListActivity.class);
                intent.putExtra("id", newsFeedStatus.getPostId());
                context.startActivity(intent);
            }

        });

        holder.like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    //Toast.makeText(context, "checked", Toast.LENGTH_SHORT).show();
                    int likeCount = 0;
                    if (!newsFeedStatus.getLike().equals("null")) {
                        likeCount = Integer.parseInt(newsFeedStatus.getLike());
                    }
//                        DrawableCompat.setTint(like.getDrawable(), ContextCompat.getColor(context,R.color.sqr));
                    holder.likelist.setTextColor(context.getResources().getColor(R.color.sqr));
                    if (isAlreadyLikedFinal == 1)
                        holder.likelist.setText(likeCount + " Like");
                    else {
                        likeCount = likeCount + 1;
                        holder.likelist.setText(likeCount + " Like");
                    }

                    database = FirebaseDatabase.getInstance();
                    ref = database.getReference();

                    String name=UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name());
                    if (newsFeedStatus.getType().equals("status") && userClass.getUserId() != newsFeedStatus.getUserId()) {
                        PushNotificationClass pushNotificationClass;
                        from_user fromUser;
                        post post1 = new post(newsFeedStatus.getFullDescription(), newsFeedStatus.getSlug(), "post Title", newsFeedStatus.getType(), newsFeedStatus.getPostId());
                        fromUser = new from_user(userClass.getEmail(), name, userClass.getUserId(), userClass.getUser_name(), userClass.getProfile());
                        pushNotificationClass = new PushNotificationClass( " liked your status ", new Date().getTime(), fromUser, post1, "like_post");
                        String key = ref.child("notification").child(newsFeedStatus.getUserId() + "").child("all").push().getKey();
                        ref.child("notification").child(newsFeedStatus.getUserId() + "").child("all").child(key).setValue(pushNotificationClass);
                        Map<String, String> unred = new HashMap<>();
                        unred.put("unread", key);
                        ref.child("notification").child(newsFeedStatus.getUserId() + "").child("unread").child(key).setValue(unred);

                    } else if (userClass.getUserId() != newsFeedStatus.getUserId()) {

                        from_user fromUser = new from_user(userClass.getEmail(), name, userClass.getUserId(), userClass.getUser_name(), userClass.getProfile());
                        post post1 = new post(newsFeedStatus.getShortDescription(), newsFeedStatus.getSlug(), newsFeedStatus.getPostTitle(), newsFeedStatus.getType(), newsFeedStatus.getPostId());
                        PushNotificationClass pushNotificationClass  = new PushNotificationClass( " liked your article ", new Date().getTime(), fromUser, post1, "like_post");;
//
                        String key = ref.child("notification").child(newsFeedStatus.getUserId() + "").child("all").push().getKey();
                        ref.child("notification").child(newsFeedStatus.getUserId() + "").child("all").child(key).setValue(pushNotificationClass);
                        Map<String, String> unred = new HashMap<>();
                        unred.put("unread", key);
                        ref.child("notification").child(newsFeedStatus.getUserId() + "").child("unread").child(key).setValue(unred);

                    }
                } else {

                    if (isAlreadyLikedFinal == 1) {
                        // Log.v("isAlreadyLiked1", isAlreadyLikedFinal + " ");
                        holder.likelist.setTextColor(context.getResources().getColor(R.color.gray));
                        int likeCount1 = Integer.parseInt(newsFeedStatus.getLike());
                        //Toast.makeText(context, "Unchecked1", Toast.LENGTH_SHORT).show();
                        likeCount1 = likeCount1 - 1;
                        holder.likelist.setText(likeCount1 + " Like");
                    } else {
                        // Log.v("isAlreadyLiked2", isAlreadyLikedFinal + " ");
                        //Toast.makeText(context, "Unchecked2", Toast.LENGTH_SHORT).show();
                        holder.likelist.setTextColor(context.getResources().getColor(R.color.gray));
                        holder.likelist.setText(newsFeedStatus.getLike() + " Like");
                    }


                }
                RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"like_post",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                Log.v("ResponseLike", s);


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
                        params.put("Accept", "application/json");
                        params.put("Authorization",  Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));




                        return params;
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();

                        params.put("likeable_id", newsFeedStatus.getSharedId() + "");
                        params.put("likeable_type", "users_post_share");
//
                        return params;
                    }
                };

                requestQueue.add(stringRequest);
            }


        });




        holder.news_comment_menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pop = new PopupMenu(context, v);
                pop.getMenuInflater().inflate(R.menu.comment_delete, pop.getMenu());
                pop.show();

                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.deleteComment:
                                RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"delete_comment",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String s) {

                                                MDToast.makeText(context, "Your comment deleted successfully", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                                holder.news_comment_card1.setVisibility(View.GONE);
                                                if (newsFeedStatus.getComments() != null)
                                                    commentsCount = Integer.parseInt(newsFeedStatus.getComments());
                                                holder.comments.setText(commentsCount + " Comment");
                                                if (isAlreadyCommentedFinal == 0) {
                                                    holder.comments.setTextColor(context.getResources().getColor(R.color.gray));
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
                                        params.put("Accept", "application/json");
                                        params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));

                                        return params;
                                    }

                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("comment_id", commentId[0] + "");
                                        params.put("id", newsFeedStatus.getPostId() + "");
                                        return params;
                                    }
                                };

                                requestQueue.add(stringRequest);
                                break;


                        }
                        return true;
                    }
                });
            }
        });


        holder.commentPostbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());

                StringRequest stringRequest = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"comment",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                Log.v("ResponseLike", s);
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    commentId[0] = jsonObject.getJSONObject("comment").getInt("id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                holder.commentTime1.setText("0 minutes ago");
                                holder.commentDescription1.setText(holder.writeComment.getText().toString());
                                Glide.with(context).load(UtilsClass.getParsedImageUrl(userClass.getProfile()))
                                        .into(holder.commentProfileImageUrl1);
                                holder.news_comment_card1.setVisibility(View.VISIBLE);

                                database = FirebaseDatabase.getInstance();
                                ref = database.getReference();

                                String name=UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name());
                                holder.commentUserName1.setText(name);


                                if (userClass.getUserId() != newsFeedStatus.getUserId()) {

                                    post post1 = new post(newsFeedStatus.getFullDescription(), newsFeedStatus.getSlug(), newsFeedStatus.getPostTitle(), newsFeedStatus.getType(), newsFeedStatus.getPostId());
                                    from_user   fromUser = new from_user(userClass.getEmail(), name, userClass.getUserId(), userClass.getUser_name(), userClass.getProfile());
                                    PushNotificationClass pushNotificationClass = new PushNotificationClass( " commented on your post ", new Date().getTime(), fromUser, post1, "comment");


                                    String key = ref.child("notification").child(newsFeedStatus.getUserId() + "").child("all").push().getKey();
                                    ref.child("notification").child(newsFeedStatus.getUserId() + "").child("all").child(key).setValue(pushNotificationClass);
                                    Map<String, String> unred = new HashMap<>();
                                    unred.put("unread", key);
                                    ref.child("notification").child(newsFeedStatus.getUserId() + "").child("unread").child(key).setValue(unred);
                                }


                                holder.writeComment.setText("");
                                if (newsFeedStatus.getComments() != null)
                                    commentsCount = Integer.parseInt(newsFeedStatus.getComments());
                                commentsCount = commentsCount + 1;
                                holder.comments.setText(commentsCount + " Comment");
                                holder.comments.setTextColor(context.getResources().getColor(R.color.sqr));
                                holder.commentCheckBox.setChecked(true);

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
                        params.put("Accept", "application/json");
                        params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));

                        return params;
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();

                        params.put("commentable_id", newsFeedStatus.getSharedId() + "");
                        params.put("comment_text", holder.writeComment.getText().toString());

                        //returning parameters
                        return params;
                    }
                };

                requestQueue.add(stringRequest);
            }
        });
    }

    private void CommentLikeButtonClicked1(Boolean isChecked,final NewsFeedStatus newsFeedStatus, MyViewHolder holder,int isAlreadyLikedComment) {


        if (isChecked) {
            //  Toast.makeText(context, "checked", Toast.LENGTH_SHORT).show();
            int likeCount = newsFeedStatus.getCommentsLimitedArrayList().get(1).getCommentLikesArray().length();
//                        DrawableCompat.setTint(like.getDrawable(), ContextCompat.getColor(context,R.color.sqr));
            holder.news_comment_likeList2.setTextColor(context.getResources().getColor(R.color.sqr));
            if (isAlreadyLikedComment == 1)
                holder.news_comment_likeList2.setText(likeCount + " Like");
            else {
                likeCount = likeCount + 1;
                holder.news_comment_likeList2.setText(likeCount + " Like");
            }

            database = FirebaseDatabase.getInstance();
            ref = database.getReference();
            SharedPreferences mPrefs = context.getSharedPreferences("User", MODE_PRIVATE);
            Gson gson = new Gson();
            String json = mPrefs.getString("MyObject", "");
            UserClass userClass = gson.fromJson(json, UserClass.class);

            Log.v("daattataatat", userClass.getUserId() + " " + userClass.getProfile() + " ");
            if ( userClass.getUserId() != newsFeedStatus.getCommentsLimitedArrayList().get(1).getUser_id()) {
                PushNotificationClass pushNotificationClass;
                from_user fromUser;
                post post1 = new post("","", newsFeedStatus.getCommentsLimitedArrayList().get(1).getBody(), "App\\UsersPostShare", newsFeedStatus.getCommentsLimitedArrayList().get(1).getUser_id());
                String name=UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name());
                fromUser = new from_user(userClass.getEmail(), name, userClass.getUserId(), userClass.getUser_name(), userClass.getProfile());
                pushNotificationClass = new PushNotificationClass(" liked your comment ", new Date().getTime(), fromUser, post1, "comment_like");
                String key = ref.child("notification").child(newsFeedStatus.getCommentsLimitedArrayList().get(1).getUser_id() + "").child("all").push().getKey();
                ref.child("notification").child(newsFeedStatus.getCommentsLimitedArrayList().get(1).getUser_id() + "").child("all").child(key).setValue(pushNotificationClass);
                Map<String, String> unred = new HashMap<>();
                unred.put("unread", key);
                ref.child("notification").child(newsFeedStatus.getCommentsLimitedArrayList().get(1).getUser_id() + "").child("unread").child(key).setValue(unred);
            }
        } else {

            if (isAlreadyLikedComment == 1) {
                // Log.v("isAlreadyLiked1", isAlreadyLikedFinal + " ");
                holder.news_comment_likeList1.setTextColor(context.getResources().getColor(R.color.gray));
                int likeCount1 = newsFeedStatus.getCommentsLimitedArrayList().get(1).getCommentLikesArray().length();
                // Toast.makeText(context, "Unchecked1", Toast.LENGTH_SHORT).show();
                likeCount1 = likeCount1 - 1;
                holder.news_comment_likeList1.setText(likeCount1 + " Like");
            } else {
                // Log.v("isAlreadyLiked2", isAlreadyLikedFinal + " ");
                // Toast.makeText(context, "Unchecked2", Toast.LENGTH_SHORT).show();
                holder.news_comment_likeList1.setTextColor(context.getResources().getColor(R.color.gray));
                holder.news_comment_likeList1.setText( newsFeedStatus.getCommentsLimitedArrayList().get(1).getCommentLikesArray().length() + " Like");
            }


        }
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"like_post",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Toast.makeText(context, s, Toast.LENGTH_SHORT).show();


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
                params.put("Accept", "application/json");
                params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));


                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("likeable_id", newsFeedStatus.getCommentsLimitedArrayList().get(1).getId()+"");
                params.put("likeable_type", "comments");
//
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
    private void CommentLikeButtonClicked(Boolean isChecked,final NewsFeedStatus newsFeedStatus, MyViewHolder holder,int isAlreadyLikedComment) {


        if (isChecked) {
            //  Toast.makeText(context, "checked", Toast.LENGTH_SHORT).show();
            int likeCount = newsFeedStatus.getCommentsLimitedArrayList().get(0).getCommentLikesArray().length();
//                        DrawableCompat.setTint(like.getDrawable(), ContextCompat.getColor(context,R.color.sqr));
            holder.news_comment_likeList1.setTextColor(context.getResources().getColor(R.color.sqr));
            if (isAlreadyLikedComment == 1)
                holder.news_comment_likeList1.setText(likeCount + " Like");
            else {
                likeCount = likeCount + 1;
                holder.news_comment_likeList1.setText(likeCount + " Like");
            }

            database = FirebaseDatabase.getInstance();
            ref = database.getReference();
            SharedPreferences mPrefs = context.getSharedPreferences("User", MODE_PRIVATE);
            Gson gson = new Gson();
            String json = mPrefs.getString("MyObject", "");
            UserClass userClass = gson.fromJson(json, UserClass.class);

            Log.v("daattataatat", userClass.getUserId() + " " + userClass.getProfile() + " ");
            if ( userClass.getUserId() != newsFeedStatus.getCommentsLimitedArrayList().get(0).getUser_id()) {
                PushNotificationClass pushNotificationClass;
                from_user fromUser;
                post post1 = new post("","", newsFeedStatus.getCommentsLimitedArrayList().get(0).getBody(), "App\\UsersPostShare", newsFeedStatus.getCommentsLimitedArrayList().get(0).getUser_id());
                String name=UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name());
                fromUser = new from_user(userClass.getEmail(), name, userClass.getUserId(), userClass.getUser_name(), userClass.getProfile());
                pushNotificationClass = new PushNotificationClass(" liked your comment ", new Date().getTime(), fromUser, post1, "comment_like");
                String key = ref.child("notification").child(newsFeedStatus.getCommentsLimitedArrayList().get(0).getUser_id() + "").child("all").push().getKey();
                ref.child("notification").child(newsFeedStatus.getCommentsLimitedArrayList().get(0).getUser_id() + "").child("all").child(key).setValue(pushNotificationClass);
                Map<String, String> unred = new HashMap<>();
                unred.put("unread", key);
                ref.child("notification").child(newsFeedStatus.getCommentsLimitedArrayList().get(0).getUser_id() + "").child("unread").child(key).setValue(unred);
            }
        } else {

            if (isAlreadyLikedComment == 1) {
                // Log.v("isAlreadyLiked1", isAlreadyLikedFinal + " ");
                holder.news_comment_likeList1.setTextColor(context.getResources().getColor(R.color.gray));
                int likeCount1 = newsFeedStatus.getCommentsLimitedArrayList().get(0).getCommentLikesArray().length();
                // Toast.makeText(context, "Unchecked1", Toast.LENGTH_SHORT).show();
                likeCount1 = likeCount1 - 1;
                holder.news_comment_likeList1.setText(likeCount1 + " Like");
            } else {
                // Log.v("isAlreadyLiked2", isAlreadyLikedFinal + " ");
                // Toast.makeText(context, "Unchecked2", Toast.LENGTH_SHORT).show();
                holder.news_comment_likeList1.setTextColor(context.getResources().getColor(R.color.gray));
                holder.news_comment_likeList1.setText( newsFeedStatus.getCommentsLimitedArrayList().get(0).getCommentLikesArray().length() + " Like");
            }


        }
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"like_post",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Toast.makeText(context, s, Toast.LENGTH_SHORT).show();


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
                params.put("Accept", "application/json");
                params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));


                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("likeable_id", newsFeedStatus.getCommentsLimitedArrayList().get(0).getId()+"");
                params.put("likeable_type", "comments");
//
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }




    public void DeletePost(final String user_post_id, final String id, final String is_shared) {

        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"delete_post",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Log.v("ResponseLike",s);
                        //Toast.makeText(context, s , Toast.LENGTH_LONG).show();
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
                params.put("Accept", "application/json");
                params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));

                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("users_post_id", user_post_id + "");
                params.put("id", id + "");
                params.put("is_shared", is_shared + "");
//
                return params;
            }
        };

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }


    @Override
    public int getItemCount() {
        return newsFeedStatuses.size();
    }

    @Override
    public long getItemId(int position) {
        return  position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView userImageUrl, authImageUrl, postImage, commentProfileImageUrl, news_user_imageProfile, news_post_menu, news_comment_menu;
        TextView authName, time, postTitle, postTag, shortDescription, fullDescription, comments, share, commentPostbtn,
                commentUserName, commentTime, commentDescription, news_comment_likeList;

        ImageView commentProfileImageUrl1,news_comment_menu1,commentProfileImageUrl2,news_comment_menu2;
        TextView commentUserName1,commentTime1,
                commentDescription1,news_comment_likeList1,commentUserName2,commentTime2,
                commentDescription2,news_comment_likeList2;
        CheckBox news_commnent_like1,news_commnent_like2;

        TextView likelist;
        CheckBox like, commentCheckBox, news_commnent_like;
        EditText writeComment;
        View view;
        CardView news_comment_card1,news_comment_card2;
        TextView postId, userId;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            news_user_imageProfile = (ImageView) itemView.findViewById(R.id.news_user_imageProfile);
            userImageUrl = (ImageView) itemView.findViewById(R.id.newsProfileImage);
            authImageUrl = (ImageView) itemView.findViewById(R.id.news_auth_image);
            postImage = (ImageView) itemView.findViewById(R.id.news_post_image);
            news_post_menu = (ImageView) itemView.findViewById(R.id.news_post_menu);
            authName = (TextView) itemView.findViewById(R.id.news_auth_name);
            time = (TextView) itemView.findViewById(R.id.news_post_time);
            postTitle = (TextView) itemView.findViewById(R.id.news_post_title);
            postTag = (TextView) itemView.findViewById(R.id.news_post_tag);
            shortDescription = (TextView) itemView.findViewById(R.id.news_short_Descrip);


            commentCheckBox = itemView.findViewById(R.id.commentCheckBox);


            like = (CheckBox) itemView.findViewById(R.id.news_post_like);
            comments = (TextView) itemView.findViewById(R.id.news_comment);
            share = (TextView) itemView.findViewById(R.id.news_share);
            likelist = (TextView) itemView.findViewById(R.id.news_post_likeList);
            writeComment = (EditText) itemView.findViewById(R.id.news_user_commnentEdit);
            commentPostbtn = (TextView) itemView.findViewById(R.id.news_comment_post);

            news_comment_card1 = (CardView) itemView.findViewById(R.id.news_comment_card1);
            commentProfileImageUrl1 = (ImageView) itemView.findViewById(R.id.news_comment_image1);
            news_comment_menu1 = (ImageView) itemView.findViewById(R.id.news_comment_menu1);

//            fullDescription=(TextView)itemView.findViewById(R.id.news_full_Descrip);

            commentUserName1 = (TextView) itemView.findViewById(R.id.news_comment_name1);
            commentTime1 = (TextView) itemView.findViewById(R.id.news_comment_time1);
            commentDescription1 = (TextView) itemView.findViewById(R.id.news_comment_descrip1);
            news_commnent_like1 = (CheckBox) itemView.findViewById(R.id.news_commnent_like1);
            news_comment_likeList1 = (TextView) itemView.findViewById(R.id.news_comment_likeList1);

            news_comment_card2 = (CardView) itemView.findViewById(R.id.news_comment_card2);
            commentProfileImageUrl2 = (ImageView) itemView.findViewById(R.id.news_comment_image2);
            news_comment_menu2 = (ImageView) itemView.findViewById(R.id.news_comment_menu2);

//            fullDescription=(TextView)itemView.findViewById(R.id.news_full_Descrip);

            commentUserName2 = (TextView) itemView.findViewById(R.id.news_comment_name2);
            commentTime2 = (TextView) itemView.findViewById(R.id.news_comment_time2);
            commentDescription2 = (TextView) itemView.findViewById(R.id.news_comment_descrip2);
            news_commnent_like2 = (CheckBox) itemView.findViewById(R.id.news_commnent_like2);
            news_comment_likeList2 = (TextView) itemView.findViewById(R.id.news_comment_likeList2);



            share.setOnClickListener(new View.OnClickListener() {

                int flag = 0;
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    NewsFeedStatus newsFeedStatus= newsFeedStatuses.get(pos);
                    String slug = newsFeedStatus.getSlug();

                    String link = UtilsClass.baseurl1+"/post/post-detail/"+newsFeedStatus.getSlug();

                    if (flag == 0) {
                        share.setTextColor(context.getResources().getColor(R.color.sqr));
                        flag = 1;
                    } else {
                        share.setTextColor(context.getResources().getColor(R.color.gray));
                        flag = 0;
                    }
                    shareIt(link);
                }
            });

        }


        private void shareIt(String link) {
            //sharing implementation here
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "SqrFactor");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, link);
            context.startActivity(Intent.createChooser(sharingIntent, "Share via"));


        }

    }

}