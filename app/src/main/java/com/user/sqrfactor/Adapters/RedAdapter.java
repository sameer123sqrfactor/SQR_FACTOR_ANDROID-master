package com.user.sqrfactor.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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
import com.user.sqrfactor.Pojo.PostCommentClass;
import com.user.sqrfactor.Pojo.PushNotificationClass;
import com.user.sqrfactor.Pojo.TokenClass;
import com.user.sqrfactor.Pojo.comments_limited;
import com.user.sqrfactor.Pojo.from_user;
import com.user.sqrfactor.Pojo.post;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Activities.StatusPostActivity;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Activities.UserProfileActivity;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Storage.MySharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class RedAdapter extends RecyclerView.Adapter<RedAdapter.MyViewHolder> {
    private Context context;
    int flag = 0;
    int flag1 = 0;
    private PopupWindow popupWindow;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private ArrayList<NewsFeedStatus> whatsRed;
    private int result=0;
    private MySharedPreferences mSp;
    private int commentsCount=0,likeCount=0;
    public RedAdapter(Context context,ArrayList<NewsFeedStatus> whatsRed) {

        this.context = context;
        this.whatsRed=whatsRed;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.red_adapter,parent,false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        int isAlreadyLiked=0;
        int isAlreadyCommented=0;
        final int[] commentId = new int[1];
        boolean isStatus=false;
        final NewsFeedStatus newsFeedStatus=whatsRed.get(position);
        SharedPreferences mPrefs =context.getSharedPreferences("User",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        final UserClass userClass = gson.fromJson(json, UserClass.class);
        mSp = MySharedPreferences.getInstance(context);

        if(newsFeedStatus.getComments()!=null){
            commentsCount = Integer.parseInt(newsFeedStatus.getComments());
            holder.buttonComment.setText(commentsCount+" Comment");


            for(int i=0;i<newsFeedStatus.getCommentsLimitedArrayList().size();i++){
              //  Toast.makeText(context,"coment"+i,Toast.LENGTH_LONG).show();

                comments_limited commentLimited =newsFeedStatus.commentsLimitedArrayList.get(i);
                if(i==0){
                    holder.commentCardView1.setVisibility(View.VISIBLE);
                    holder.commentTime1.setText(UtilsClass.getTime(commentLimited.getCreated_at()));
                    holder.commentMessage1.setText(commentLimited.getBody());
                    Glide.with(context).load(UtilsClass.getParsedImageUrl(commentLimited.getCommentUserPrfile()))
                            .into(holder.commentProfile1);
                    String name=UtilsClass.getName(commentLimited.getCommenterFirstName(),commentLimited.getCommenterLastName(),"null",commentLimited.getCommentUserName());
                    holder.commentUserName1.setText(name);
                    if(userClass.getUserId()==commentLimited.getUser_id()){
                        holder.red_comment_menu1.setVisibility(View.VISIBLE);
                    }
                }else if(i==1){
                    // comments_limited commentLimited =newsFeedStatus.commentsLimitedArrayList.get(i);
                    holder.commentCardView2.setVisibility(View.VISIBLE);
                    holder.commentTime2.setText(UtilsClass.getTime(commentLimited.getCreated_at()));
                    holder.commentMessage2.setText(commentLimited.getBody());
                    Glide.with(context).load(UtilsClass.getParsedImageUrl(commentLimited.getCommentUserPrfile()))
                            .into(holder.commentProfile2);
                    String name=UtilsClass.getName(commentLimited.getCommenterFirstName(),commentLimited.getCommenterLastName(),"null",commentLimited.getCommentUserName());
                    holder.commentUserName2.setText(name);
                    if(userClass.getUserId()==commentLimited.getUser_id()){
                        holder.red_comment_menu2.setVisibility(View.VISIBLE);
                    }

                }
            }

        }

        if(newsFeedStatus.getLike()!=null)
            likeCount = Integer.parseInt(newsFeedStatus.getLike());
        holder.buttonLikeList.setText(likeCount+" Like");

        Glide.with(context).load(UtilsClass.getParsedImageUrl(userClass.getProfile()))
                .into(holder.userProfile);

        if(userClass.getUserId()==newsFeedStatus.getUserId())
        {
            holder.red_menu.setVisibility(View.VISIBLE);
        }
        else {
            holder.red_menu.setVisibility(View.GONE);
        }


        holder.authName.setText(UtilsClass.getName(newsFeedStatus.getFirst_name(),newsFeedStatus.getLast_name(),newsFeedStatus.getAuthName(),newsFeedStatus.getUser_name_of_post()));
        if(newsFeedStatus.getType().equals("status")) {

             isStatus = true;
            holder.postTitle.setVisibility(View.GONE);
            holder.postViews.setVisibility(View.GONE);
            holder.postDescription.setText(newsFeedStatus.getFullDescription());
            Glide.with(context).load(UtilsClass.baseurl1+newsFeedStatus.getUserImageUrl())
                    .into(holder.postBannerImage);

        }

        else if(newsFeedStatus.getType().equals("design")) {
            isStatus = false;
            holder.postTitle.setText(newsFeedStatus.getPostTitle());
            holder.postDescription.setText(newsFeedStatus.getShortDescription());
            Glide.with(context).load(UtilsClass.baseurl1+newsFeedStatus.getPostImage())
                    .into(holder.postBannerImage);

        }

        else if(newsFeedStatus.getType().equals("article"))
        {

            isStatus = false;
            holder.postTitle.setText(newsFeedStatus.getPostTitle());
            holder.postDescription.setText(newsFeedStatus.getShortDescription());
            Glide.with(context).load(UtilsClass.baseurl1+newsFeedStatus.getPostImage())
                    .into(holder.postBannerImage);


        }
        Glide.with(context).load(UtilsClass.getParsedImageUrl(newsFeedStatus.getAuthImageUrl()))
                .into(holder.authProfile);

        holder.authName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,UserProfileActivity.class);
                intent.putExtra("User_id",newsFeedStatus.getUserId());
                intent.putExtra("ProfileUserName",newsFeedStatus.getUser_name_of_post());
                intent.putExtra("UserType", newsFeedStatus.getUser_type_of_post());
                context.startActivity(intent);

            }
        });

        final boolean finalIsStatus = isStatus;
        holder.postBannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, FullPostActivity.class);
                intent.putExtra("post_type", finalIsStatus);
                intent.putExtra("Post_Slug_ID", newsFeedStatus.getSlug());
                    context.startActivity(intent);


            }
        });
        holder.red_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pop = new PopupMenu(context, v);
                pop.getMenuInflater().inflate(R.menu.delete_news_post_menu, pop.getMenu());
                pop.show();

                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){

                            case R.id.fullView:
                                Intent intent=new Intent(context,FullPostActivity.class);
                                intent.putExtra("Post_Slug_ID",newsFeedStatus.getSlug());
                                context.startActivity(intent);
                                break;
                            case R.id.editPost:
                                if(newsFeedStatus.getType().equals("design"))
                                {
                                    context.startActivity(new Intent(context,DesignActivity.class));
                                }
                                else if(newsFeedStatus.getType().equals("article"))
                                {
                                    context.startActivity(new Intent(context,ArticleActivity.class));
                                }
                                else if(newsFeedStatus.getType().equals("status"))
                                {
                                    context.startActivity(new Intent(context,StatusPostActivity.class));
                                }
                                break;
                            case R.id.deletePost:
                                whatsRed.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeRemoved(position, 1);
                                // DeletePost(newsFeedStatus.getPostId()+"",newsFeedStatus.getSharedId()+"",newsFeedStatus.getIs_Shared());
                                break;
                            case R.id.selectAsFeaturedPost:
                                return true;

                        }
                        return true;
                    }
                });
            }
        });

        holder.postTime.setText(UtilsClass.getTime(newsFeedStatus.getTime()));

        if(newsFeedStatus.getTotal_views()!=null || !newsFeedStatus.getTotal_views().equals("null"))
        {
            holder.postViews.setText(newsFeedStatus.getTotal_views());
        }


        holder.buttonLikeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, LikeListActivity.class);
                intent.putExtra("id",newsFeedStatus.getPostId());
                context.startActivity(intent);

            }

        });

        holder.buttonComment.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, CommentsPage.class);
                intent.putExtra("PostSharedId",newsFeedStatus.getSharedId()); //second param is Serializable
                context.startActivity(intent);
            }
        });


        for(int i=0;i<newsFeedStatus.getAllLikesId().size();i++) {
            if(userClass.getUserId() == newsFeedStatus.getAllLikesId().get(i)) {

               // Log.v("LikeID",position+"/ "+newsFeedStatus.getAllLikesId().get(i));
                holder.buttonLikeList.setTextColor(ContextCompat.getColor(context,R.color.sqr));
                isAlreadyLiked=1;
                holder.buttonLike.setChecked(true);
            }
            else {
               // holder.buttonLikeList.setTextColor(ContextCompat.getColor(context,R.color.gray));
                isAlreadyLiked=0;

            }
        }
        final int isAlreadyLikedFinal=isAlreadyLiked;

        for(int i=0;i<newsFeedStatus.getAllCommentId().size();i++)
        {
            if(userClass.getUserId()==newsFeedStatus.getAllCommentId().get(i))
            {
                isAlreadyCommented=1;
               // Log.v("commetnId",position+"/ "+newsFeedStatus.getAllCommentId().get(i));
                holder.buttonComment.setTextColor(ContextCompat.getColor(context,R.color.sqr));
            }
            else {
                isAlreadyCommented=0;
                holder.buttonComment.setTextColor(ContextCompat.getColor(context,R.color.gray));
            }
        }

        final int isAlreadyCommentedFinal = isAlreadyCommented;

        holder.commentpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"comment",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {

                                try {
                                    JSONObject jsonObject=new JSONObject(s);
                                    commentId[0] = jsonObject.getJSONObject("comment").getInt("id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = new Date();
                                SharedPreferences sharedPreferences = context.getSharedPreferences("User",MODE_PRIVATE);
                                Gson gson = new Gson();
                                String json = sharedPreferences.getString("MyObject","");
                                UserClass userClass = gson.fromJson(json,UserClass.class);
                                String name=UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name());


                                PostCommentClass postCommentClass=new PostCommentClass(userClass.getProfile(),userClass.getUser_name()
                                        ,formatter.format(date),holder.userComment.getText().toString(),"0");


                               // holder.commentCardView1.setVisibility(View.VISIBLE);
                                holder.commentUserName1.setText(name);
                                holder.commentMessage1.setText(holder.userComment.getText().toString());
                                holder.commentTime1.setText("0 seconds ago");

                                Toast.makeText(context,"commenting",Toast.LENGTH_LONG).show();


                                Glide.with(context).load(UtilsClass.getParsedImageUrl(postCommentClass.getProfileImage()))
                                        .into(holder.commentProfile1);

                                holder.commentCardView1.setVisibility(View.VISIBLE);
                                database= FirebaseDatabase.getInstance();
                                ref = database.getReference();
//                                database= FirebaseDatabase.getInstance();
//                                ref = database.getReference();


                                if(userClass.getUserId()!=newsFeedStatus.getUserId()) {

                                    post post1=new post(newsFeedStatus.getFullDescription(),newsFeedStatus.getSlug(),newsFeedStatus.getPostTitle(),newsFeedStatus.getType(),newsFeedStatus.getPostId());
                                    from_user fromUser=new from_user(userClass.getEmail(),name,userClass.getUserId(),userClass.getUser_name(),userClass.getProfile());
                                    PushNotificationClass pushNotificationClass=new PushNotificationClass(" commented on your post ",new Date().getTime(),fromUser,post1,"comment");
                                    String key =ref.child("notification").child(newsFeedStatus.getUserId()+"").child("all").push().getKey();
                                    ref.child("notification").child(newsFeedStatus.getUserId()+"").child("all").child(key).setValue(pushNotificationClass);
                                    Map<String,String> unred=new HashMap<>();
                                    unred.put("unread",key);
                                    ref.child("notification").child(newsFeedStatus.getUserId()+"").child("unread").child(key).setValue(unred);
                                }


                                if(newsFeedStatus.getComments()!=null)
                                    commentsCount=Integer.parseInt(newsFeedStatus.getComments());
                                commentsCount=commentsCount+1;
                                holder.buttonComment.setText(commentsCount+" Comment");
                                holder.buttonComment.setTextColor(ContextCompat.getColor(context,R.color.sqr));

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {

                                //Showing toast
//                        Toast.makeText(getActivity(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                            }
                        }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Accept", "application/json");
//                        params.put("Authorization", "Bearer " +TokenClass.Token);
                        params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));

                        return params;
                    }
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();

                        params.put("commentable_id",newsFeedStatus.getSharedId()+"");

                        params.put("comment_text",holder.userComment.getText().toString());

                        //returning parameters
                        return params;
                    }
                };

                //Adding request to the queue
                requestQueue.add(stringRequest);
            }
        });

        holder.red_comment_menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pop = new PopupMenu(context, v);
                pop.getMenuInflater().inflate(R.menu.comment_delete, pop.getMenu());
                pop.show();

                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.deleteComment:
                                RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"delete_comment",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String s) {

                                                MDToast.makeText(context, "Your comment deleted successfully", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                                holder.commentCardView.setVisibility(View.GONE);
                                                if(newsFeedStatus.getComments()!=null)
                                                    commentsCount=Integer.parseInt(newsFeedStatus.getComments());
                                                holder.buttonComment.setText(commentsCount+" Comment");
                                                if(isAlreadyCommentedFinal==0)
                                                {
                                                    holder.buttonComment.setTextColor(context.getResources().getColor(R.color.gray));
                                                }

                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError volleyError) {
                                            }
                                        }){
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("Accept", "application/json");
                                        params.put("Authorization", "Bearer " +TokenClass.Token);

                                        return params;
                                    }
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String,String> params = new HashMap<>();
                                        params.put("comment_id",commentId[0]+"");
                                        params.put("id",newsFeedStatus.getPostId()+"");
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

        holder.buttonLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    holder.buttonLikeList.setTextColor(ContextCompat.getColor(context,R.color.sqr));
                    int likeCount=Integer.parseInt(newsFeedStatus.getLike());
//                        DrawableCompat.setTint(like.getDrawable(), ContextCompat.getColor(context,R.color.sqr));
                    holder.buttonLikeList.setTextColor(ContextCompat.getColor(context,R.color.sqr));
                    if(isAlreadyLikedFinal==1)
                        holder.buttonLikeList.setText(likeCount+" Like");
                    else
                    {
                        likeCount=likeCount+1;
                        holder.buttonLikeList.setText(likeCount+" Like");
                    }

                    //  buttonLikeList.setText(result+" Like");
                    database= FirebaseDatabase.getInstance();
                    ref = database.getReference();
                    SharedPreferences mPrefs =context.getSharedPreferences("User",MODE_PRIVATE);
                    Gson gson = new Gson();
                    String json = mPrefs.getString("MyObject", "");
                    UserClass userClass = gson.fromJson(json, UserClass.class);

                    String name=UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name());
                    if(newsFeedStatus.getType().equals("status")&& userClass.getUserId()!=newsFeedStatus.getUserId())
                    {
                        post post1=new post(newsFeedStatus.getFullDescription(),newsFeedStatus.getSlug(),"post Title",newsFeedStatus.getType(),newsFeedStatus.getPostId());
                        from_user fromUser=new from_user(userClass.getEmail(),name,userClass.getUserId(),userClass.getUser_name(),userClass.getProfile());
                        PushNotificationClass   pushNotificationClass=new PushNotificationClass(" liked your status ",new Date().getTime(),fromUser,post1,"like_post");
                        String key =ref.child("notification").child(newsFeedStatus.getUserId()+"").child("all").push().getKey();
                        ref.child("notification").child(newsFeedStatus.getUserId()+"").child("all").child(key).setValue(pushNotificationClass);
                        Map<String,String> unred=new HashMap<>();
                        unred.put("unread",key);
                        ref.child("notification").child(newsFeedStatus.getUserId()+"").child("unread").child(key).setValue(unred);
                    }
                    else if(userClass.getUserId()!=newsFeedStatus.getUserId())
                    {
                        from_user fromUser=new from_user(userClass.getEmail(),name,userClass.getUserId(),userClass.getUser_name(),userClass.getProfile());
                        post post1=new post(newsFeedStatus.getShortDescription(),newsFeedStatus.getSlug(),newsFeedStatus.getPostTitle(),newsFeedStatus.getType(),newsFeedStatus.getPostId());
                        PushNotificationClass pushNotificationClass =new PushNotificationClass(" liked your post ",new Date().getTime(),fromUser,post1,"like_post");;
                        String key =ref.child("notification").child(newsFeedStatus.getUserId()+"").child("all").push().getKey();
                        ref.child("notification").child(newsFeedStatus.getUserId()+"").child("all").child(key).setValue(pushNotificationClass);
                        Map<String,String> unred=new HashMap<>();
                        unred.put("unread",key);
                        ref.child("notification").child(newsFeedStatus.getUserId()+"").child("unread").child(key).setValue(unred);

                    }

                    flag = 1;
                }
                else {
                    if(isAlreadyLikedFinal==1)
                    {
                       // Log.v("isAlreadyLiked1",isAlreadyLikedFinal+" ");
                        holder.buttonLikeList.setTextColor(context.getColor(R.color.gray));
                        int likeCount1=Integer.parseInt(newsFeedStatus.getLike());
                        //Toast.makeText(context, "Unchecked1", Toast.LENGTH_SHORT).show();
                        likeCount1=likeCount1-1;
                        holder.buttonLikeList.setText(likeCount1+" Like");
                    }
                    else
                    {

                        holder.buttonLikeList.setTextColor(context.getColor(R.color.gray));
                        holder.buttonLikeList.setText(newsFeedStatus.getLike()+" Like");
                    }
                }
                RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"like_post",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                Log.v("ResponseLike",s);

//
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {

                                //Showing toast
//                        Toast.makeText(getActivity(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                            }
                        }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Accept", "application/json");
                        params.put("Authorization", "Bearer " +TokenClass.Token);

                        return params;
                    }
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();

                        params.put("likeable_id",newsFeedStatus.getSharedId()+"");
                        params.put("likeable_type","users_post_share");
                        return params;
                    }
                };

                requestQueue.add(stringRequest);
            }


        });


    }


    @Override
    public long getItemId(int position) {
        return  position;
    }


    @Override
    public int getItemCount() {
        return whatsRed.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView authName,postTime,postTitle,postDescription, postViews,commentpost,buttonLikeList;
        EditText userComment;
        ImageView authProfile,userProfile,postBannerImage,red_comment_menu;
        Button buttonComment,buttonShare,commentLike;
        CheckBox buttonLike;
        CardView commentCardView;
        TextView commentUserName,commentTime,commentMessage;
        ImageView commentProfile,red_menu;

        CardView commentCardView1,commentCardView2;
        ImageView  commentProfile1,commentProfile2,red_comment_menu1,red_comment_menu2;
        TextView commentMessage1,commentMessage2,commentTime1,commentTime2,commentUserName1,commentUserName2;

        public MyViewHolder(View itemView) {
            super(itemView);

            postBannerImage=(ImageView) itemView.findViewById(R.id.red_postImage);
            authName=(TextView)itemView.findViewById(R.id.red_authName);
            postTime=(TextView)itemView.findViewById(R.id.red_postTime);
            postTitle =(TextView)itemView.findViewById(R.id.red_postTitle);
            postDescription=(TextView)itemView.findViewById(R.id.red_postDescription);
            authProfile=(ImageView)itemView.findViewById(R.id.red_authImage);
            postViews = (TextView) itemView.findViewById(R.id.red_postViews);
            buttonLike=(CheckBox) itemView.findViewById(R.id.red_like);


            buttonLikeList=(TextView) itemView.findViewById(R.id.red_likeList);
            buttonComment=(Button)itemView.findViewById(R.id.red_comment);
            buttonShare=(Button)itemView.findViewById(R.id.red_share);
            userComment = (EditText)itemView.findViewById(R.id.red_userComment);
            red_menu=(ImageView)itemView.findViewById(R.id.red_menu);

            userProfile =(ImageView)itemView.findViewById(R.id.red_userProfile);

            commentpost=(TextView)itemView.findViewById(R.id.red_commentPostbtn);


            commentCardView1=(CardView)itemView.findViewById(R.id.red_commentListCard1);
            commentProfile1=(ImageView)itemView.findViewById(R.id.red_commenterProfile1);
            commentMessage1=itemView.findViewById(R.id.red_commentMsg1);
            commentTime1=itemView.findViewById(R.id.red_commentTime1);
            commentUserName1=itemView.findViewById(R.id.red_commentUserName1);
            red_comment_menu1=(ImageView)itemView.findViewById(R.id.red_comment_menu1);

            commentCardView2=(CardView)itemView.findViewById(R.id.red_commentListCard2);
            commentProfile2=(ImageView)itemView.findViewById(R.id.red_commenterProfile2);
            commentMessage2=itemView.findViewById(R.id.red_commentMsg2);
            commentTime2=itemView.findViewById(R.id.red_commentTime2);
            commentUserName2=itemView.findViewById(R.id.red_commentUserName2);
            red_comment_menu2=(ImageView)itemView.findViewById(R.id.red_comment_menu2);

            buttonShare.setOnClickListener(new View.OnClickListener() {

                int flag = 0;
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    NewsFeedStatus newsFeedStatus=whatsRed.get(pos);
                    String slug = newsFeedStatus.getSlug();

                    String link = UtilsClass.baseurl1+"/post/post-detail/"+newsFeedStatus.getSlug();
                    if (flag == 0) {
                        buttonShare.setTextColor(ContextCompat.getColor(context,R.color.sqr));
                        flag = 1;
                    }
                    else {
                        buttonShare.setTextColor(ContextCompat.getColor(context,R.color.gray));
                        flag = 0;
                    }
                    shareIt(link);
                }
            });
        }
    }

    private void shareIt(String link) {
        //sharing implementation here
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "SqrFactor");
        sharingIntent.putExtra(Intent.EXTRA_TEXT,link);
//        sharingIntent.putExtra(Intent.EXTRA_TEXT, "professional network for the architecture community visit https://sqrfactor.com");
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

}
