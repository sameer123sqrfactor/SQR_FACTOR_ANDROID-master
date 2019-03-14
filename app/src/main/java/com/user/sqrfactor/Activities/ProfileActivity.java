package com.user.sqrfactor.Activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.user.sqrfactor.Activities.SettingModule.SettingViews.Activities.Settings;
import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.NewsFeedStatus;
import com.user.sqrfactor.Pojo.ProfileClass1;
import com.user.sqrfactor.Pojo.TokenClass;
import com.user.sqrfactor.Adapters.ProfileAdapter;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends ToolbarActivity {
    private ArrayList<ProfileClass1> profileClassList = new ArrayList<>();
    private ImageView displayImage, camera;
    public ImageButton mRemoveButton;
    private ProfileAdapter profileAdapter;
    private MySharedPreferences mSp;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ImageView morebtn, btn,coverImage,profileImage,profileStatusImage,profile_status_image;
    private TextView writePost,profileName,followCnt,followingCnt,portfolioCnt,bluePrintCnt;
    private Button btnSubmit;
    private ImageButton editProfile;
    private Bitmap bitmap;
    private Uri uri;
    boolean flag = false;
    private LinearLayoutManager layoutManager;
    private TextView blueprint, portfolio, followers, following,profile_profile_address,profile_short_bio;
    private UserClass userClass;
    private static String nextPageUrl;
    private boolean isLoading=false;
    private static Context context;
    public  static final int RequestPermissionCode  = 1 ;
    final int PIC_CROP = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    private Intent camIntent,gallIntent;
    private AppBarLayout appBarLayout;

    private CoordinatorLayout mCLayout;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCToolbarLayout;
    private TextView noData;

    FloatingActionButton fabView,fabStatus,fabDesign, fabArticle;
    private boolean fabExpanded = false;
    private LinearLayout layoutFabStatus;
    private LinearLayout layoutFabDesign;
    private LinearLayout layoutFabArticle;
    private ProgressBar progressBar;
    Animation rotate_forward, rotate_Backward, fab_open, fab_close;

    //tracker code
    //private GoogleAnalytics sAnalytics;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        SharedPreferences mPrefs =getSharedPreferences("User",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        userClass = gson.fromJson(json, UserClass.class);



        mSp = MySharedPreferences.getInstance(this);

        progressBar=findViewById(R.id.progress_bar_profile);
        noData= findViewById(R.id.noData);
        mCLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        mCToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        mToolbar.setTitleTextColor(getResources().getColor(R.color.White));
        setSupportActionBar(mToolbar);
        //mCToolbarLayout.setTitle("Profile");
        mCToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.black));
        mCToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.black));

        mToolbar.setNavigationIcon(R.drawable.ic_back_black);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });

        recyclerView = findViewById(R.id.profile_recycler);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        profileAdapter = new ProfileAdapter(profileClassList,this);
        recyclerView.setAdapter(profileAdapter);

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        profile_profile_address=findViewById(R.id.profile_profile_address);
        profile_short_bio=findViewById(R.id.profile_short_bio);

        editProfile = findViewById(R.id.profile_editprofile);
        coverImage = (ImageView) findViewById(R.id.profile_cover_image);
        profileImage = (ImageView) findViewById(R.id.profile_profile_image);


//        profileStatusImage = (ImageView) findViewById(R.id.profile_status_image);
        followCnt = (TextView) findViewById(R.id.profile_followerscnt);
        followingCnt = (TextView) findViewById(R.id.profile_followingcnt);
        portfolioCnt = (TextView) findViewById(R.id.profile_portfoliocnt);
        bluePrintCnt = (TextView) findViewById(R.id.profile_blueprintcnt);
        appBarLayout=(AppBarLayout) findViewById(R.id.profileAppBar);

        followCnt.setText(userClass.getFollowerCount());
        followingCnt.setText(userClass.getFollowingCount());
        bluePrintCnt.setText(userClass.getBlueprintCount());
        portfolioCnt.setText(userClass.getPortfolioCount());

//        fabView = findViewById(R.id.fab_view);
//        fabStatus = findViewById(R.id.fab_status);
//        fabDesign = findViewById(R.id.fab_design);
//        fabArticle = findViewById(R.id.fab_article);
//
//        layoutFabStatus = (LinearLayout)findViewById(R.id.layoutFabStatus);
//        layoutFabDesign = (LinearLayout) findViewById(R.id.layoutFabDesign);
//        layoutFabArticle = (LinearLayout) findViewById(R.id.layoutFabArticle);

        rotate_forward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotate_Backward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

//        fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open);
//        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        morebtn = (ImageView)findViewById(R.id.profile_about_morebtn);


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        android.app.AlertDialog.Builder imageDialog = new android.app.AlertDialog.Builder(ProfileActivity.this);
                        LayoutInflater inflater = (LayoutInflater) ProfileActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);

                        View layout = inflater.inflate(R.layout.custom_fullimage_dialog,
                                (ViewGroup) findViewById(R.id.layout_root));
                        ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
                        image.setImageDrawable(profileImage.getDrawable());
                        imageDialog.setView(layout);
                        TextView edittext = (TextView)layout.findViewById(R.id.custom_fullimage_placename);
                        edittext.setVisibility(View.VISIBLE);
                        imageDialog.setPositiveButton(getResources().getString(R.string.Edit), new DialogInterface.OnClickListener(){

                            public void onClick(DialogInterface dialog, int which) {
                                if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                                        Manifest.permission.READ_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED) {

                                    ActivityCompat.requestPermissions(ProfileActivity.this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                                }

                                if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                                        Manifest.permission.CAMERA))
                                {

//                    Toast.makeText(ProfileActivity.this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

                                } else {

                                    ActivityCompat.requestPermissions(ProfileActivity.this,new String[]{
                                            Manifest.permission.CAMERA}, RequestPermissionCode);

                                }
                                selectImage();

                            }

                        });

                        imageDialog.create();
                        imageDialog.show();
                    }
                });

        profileName =(TextView)findViewById(R.id.profile_profile_name);


        if(userClass!=null){
            if(userClass.getUser_address()!=null && !userClass.getUser_address().equals("null"))
                profile_profile_address.setText(userClass.getUser_address());

            if(userClass.getShort_bio()!=null && !userClass.getShort_bio().equals("null"))
                profile_short_bio.setText(userClass.getShort_bio());

            Glide.with(this).load(UtilsClass.getParsedImageUrl(userClass.getProfile()))
                    .into(profileImage);

            profileName.setText(UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));

        }




        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(final AppBarLayout appBarLayout, int verticalOffset) {
                //Initialize the size of the scroll
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                //Check if the view is collapsed
                if (scrollRange + verticalOffset == 0) {
                    mCToolbarLayout.setTitle("Profile");
                    mToolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                }else{
                    mCToolbarLayout.setTitle("");
                    mToolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.sqr_fade));
                }
            }
        });

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
                    LoadMoreDataFromServer();


                }
            }
        });




        morebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(getApplicationContext(),"about more icon",Toast.LENGTH_LONG).show();

                PopupMenu pop = new PopupMenu(getApplicationContext(), v);
                pop.getMenu().add(1,1,0,"About "+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
                pop.getMenuInflater().inflate(R.menu.more_menu, pop.getMenu());
                pop.show();

                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){

                            case 1:
                                Intent i = new Intent(ProfileActivity.this, About.class);
                                i.putExtra("UserID",userClass.getUserId());
                                i.putExtra("userType",userClass.getUserType());
                                startActivity(i);
                                return true;

                        }
                        return true;
                    }
                });

            }
        });

        blueprint = (TextView)findViewById(R.id.profile_blueprintClick);
        portfolio = (TextView)findViewById(R.id.profile_portfolioClick);
        followers = (TextView)findViewById(R.id.profile_followersClick);
        following = (TextView)findViewById(R.id.profile_followingClick);



        blueprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(ProfileActivity.this, BlueprintActivity.class);
//                startActivity(i);
                LoadData();
            }
        });

        portfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, PortfolioActivity.class);
                i.putExtra("UserName",userClass.getUser_name());
                startActivity(i);
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, FollowersActivity.class);
                i.putExtra("UserName",userClass.getUser_name());
                startActivity(i);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, FollowingActivity.class);
                i.putExtra("UserName",userClass.getUser_name());
                startActivity(i);
            }
        });

        LoadData();
//
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                isLoading=false;
//
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//
//                int lastId=layoutManager.findLastVisibleItemPosition();
//                if(dy>0 && lastId + 2 > layoutManager.getItemCount() && !isLoading)
//                {
//                    isLoading=true;
//                    LoadMoreDataFromServer();
//
//                }
//            }
//        });


        //google analytics code
        com.user.sqrfactor.Application.MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();

    }

    private void openSubMenusFab(){
        layoutFabStatus.setVisibility(View.VISIBLE);
        layoutFabDesign.setVisibility(View.VISIBLE);
        layoutFabArticle.setVisibility(View.VISIBLE);
        fabStatus.startAnimation(fab_open);
        fabDesign.setAnimation(fab_open);
        fabArticle.setAnimation(fab_open);
        fabView.startAnimation(rotate_forward);
        fabView.setImageResource(R.drawable.ic_add_black_24dp);
        fabExpanded = true;
    }
    private void closeSubMenusFab(){
        layoutFabStatus.setVisibility(View.GONE);
        layoutFabDesign.setVisibility(View.GONE);
        layoutFabArticle.setVisibility(View.GONE);
        fabStatus.startAnimation(fab_close);
        fabDesign.setAnimation(fab_close);
        fabArticle.setAnimation(fab_close);
        fabView.startAnimation(rotate_Backward);
        fabExpanded = false;
    }
    public void LoadData(){


        if(profileClassList!= null){
            profileClassList.clear();
        }
       // RequestQueue requestQueue = Volley.newRequestQueue(this);
        RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();
        StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"profile/detail/"+userClass.getUser_name(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        noData.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            nextPageUrl = jsonObject.getString("nextPage");

                            JSONObject user=jsonObject.getJSONObject("user");

                            JSONObject jsonPost = jsonObject.getJSONObject("posts");
                            if (jsonPost!=null)
                            {
                                JSONArray jsonArrayData = jsonPost.getJSONArray("data");
                                new ProfileActivityParser(jsonArrayData).execute();
//                                for (int i = 0; i < jsonArrayData.length(); i++) {
//                                    ProfileClass1 profileClass1 = new ProfileClass1(jsonArrayData.getJSONObject(i));
//                                    profileClassList.add(profileClass1);
//                                }
//                                profileAdapter.notifyDataSetChanged();
                            }



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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username",userClass.getUser_name());
                return params;
            }

        };


        //requestQueue.add(myReq);
        myReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(myReq);


        final Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(profileClassList.size()==0)
                {
                    progressBar.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);

                }else {
                    progressBar.setVisibility(View.GONE);
                    noData.setVisibility(View.GONE);
                }
            }
        }, 2000);


    }


    public void LoadMoreDataFromServer(){

        final ArrayList<ProfileClass1> NewprofileClassList = new ArrayList<>();
        SharedPreferences mPrefs =getSharedPreferences("User",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        final UserClass userClass = gson.fromJson(json, UserClass.class);

        if(nextPageUrl!=null && TextUtils.isEmpty(nextPageUrl)) {
           // RequestQueue requestQueue = Volley.newRequestQueue(this);
            RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();
            StringRequest myReq = new StringRequest(Request.Method.POST, nextPageUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            Log.v("ReponseFeed", response);
//                            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                nextPageUrl = jsonObject.getString("nextPage");
                                JSONObject jsonPost = jsonObject.getJSONObject("posts");
                                if (jsonPost != null) {
                                    JSONArray jsonArrayData = jsonPost.getJSONArray("data");
                                    new ProfileActivityParser(jsonArrayData).execute();
//                                    for (int i = 0; i < jsonArrayData.length(); i++) {
//                                        ProfileClass1 profileClass1 = new ProfileClass1(jsonArrayData.getJSONObject(i));
//                                        NewprofileClassList.add(profileClass1);
//                                    }
//                                    profileClassList.addAll(NewprofileClassList);
//                                    profileAdapter.notifyDataSetChanged();
                                }


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
                    params.put("Authorization", "Bearer " + TokenClass.Token);

                    return params;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", userClass.getUser_name() );
                    return params;
                }
            };


           // requestQueue.add(myReq);
            myReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(myReq);
        }


    }

    private void selectImage() {



        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };



        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo"))

                {

                    camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                    uri=Uri.fromFile(f);
//                   uri = FileProvider.getUriForFile( ProfileActivity.this,ProfileActivity.this.getPackageName() + ".provider", f);
                    Log.v("uriCamara",uri+"");
                    camIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    camIntent.putExtra("return-data", true);

                    startActivityForResult(camIntent, 1);

                }

                else if (options[item].equals("Choose from Gallery"))

                {


                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i, 2);


                }

                else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                performCrop();

            }


            else if (requestCode == 2) {


                if(data!=null)
                {

                    uri = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(uri,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    //uri=picturePath;
                    Log.v("uriGallary",picturePath+"   *****  "+uri);
                    cursor.close();
                    performCropFromGallary(picturePath);
                    //ImageView imageView = (ImageView) findViewById(R.id.imgView);
                    //profileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                }



            }
            else if(requestCode == 6)
            {

                if(data!=null)
                {
                    Bundle extras = data.getExtras();
                    bitmap  = extras.getParcelable("data");
                    profileImage.setImageBitmap(bitmap);
                    ChangeProfile();
                }

            }
        }

    }
    public String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte,Base64.DEFAULT);
        return encode;
    }
    private void performCrop(){
        try {

            Log.v("uri1",uri+"");
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(uri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 640);
            cropIntent.putExtra("outputY", 640);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, 6);

        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    private void performCropFromGallary(String picUri){
        try {

            Log.v("uri1",uri+"");
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);
            cropIntent.setDataAndType(contentUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 640);
            cropIntent.putExtra("outputY", 640);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, 6);

        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    public void ChangeProfile(){

            //RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            RequestQueue requestQueue = MyVolley.getInstance().getRequestQueue();
            StringRequest myReq = new StringRequest(Request.Method.POST,  UtilsClass.baseurl+"parse/change_profile",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                           // Log.v("ReponseFeed", response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
                                Gson gson = new Gson();
                                String json = mPrefs.getString("MyObject", "");
                                UserClass userClass = gson.fromJson(json, UserClass.class);


                                userClass.setProfile(jsonObject.getJSONObject("profilepic").getString("profile"));
                                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                json = gson.toJson(userClass);

                                prefsEditor.putString("MyObject", json);
                                prefsEditor.apply();

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
                    params.put("Authorization", "Bearer " + TokenClass.Token);

                    return params;
                }
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    String image = getStringImage(bitmap);
                    params.put("profile_image","data:image/jpeg;base64,"+image );
                    return params;
                }

            };

        myReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(myReq);
            //requestQueue.add(myReq);
        }


    @Override
    protected void onResume() {
        super.onResume();
      //  Log.i(TAG, "Setting screen name: " + "CompetitionDetailActivity");
        mTracker.setScreenName("ProfileActivity /"+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());


    }

//    @Override
//    public boolean onSupportNavigateUp(){
//        finish();
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class ProfileActivityParser extends AsyncTask {
        private JSONArray jsonArrayData;

        public ProfileActivityParser(JSONArray jsonArrayData) {
            this.jsonArrayData = jsonArrayData;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            for (int i = 0; i < jsonArrayData.length(); i++) {
                ProfileClass1 profileClass1 = null;
                try {
                    profileClass1 = new ProfileClass1(jsonArrayData.getJSONObject(i));
                    profileClassList.add(profileClass1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
           // profileClassList.addAll(NewprofileClassList);

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            profileAdapter.notifyDataSetChanged();
            if(profileClassList.size()==0) {
                progressBar.setVisibility(View.GONE);
                noData.setVisibility(View.VISIBLE);

            }else {
                progressBar.setVisibility(View.GONE);
                noData.setVisibility(View.GONE);
            }

        }


    }

}