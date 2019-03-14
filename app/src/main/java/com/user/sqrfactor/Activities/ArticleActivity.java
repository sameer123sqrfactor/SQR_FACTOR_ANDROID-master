package com.user.sqrfactor.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
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
import com.github.irshulx.Editor;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Constants.BundleConstants;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Fragments.MDToast;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.ArticleEditClass;
import com.user.sqrfactor.Pojo.TokenClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import io.github.angebagui.mediumtextview.MediumTextView;


public class ArticleActivity extends ToolbarActivity{

    private Toolbar toolbar;
    private Editor editor;
    private MultiAutoCompleteTextView multiAutoCompleteTextView;
    private Uri uri;
    private String finalHtml=null,content=null;
    private String cropedImage;
    private ImageView mRemoveButton;
    private boolean isEdit=false;
    private WebView videoView;
    private MySharedPreferences mSp;
    private int postId;
    private String imageString,html,Slug=null;
    private Button saveArticleButton,video_post_close;
    private EditText articleTitle,articleShortDescription,articleTag;
    private ImageView articleSelectBannerImage,articleUserName;
    // TextInputLayout articleSelectBannerImage;
    private ImageView articleCustomBaneerImage,cropFinalImage,profileImage,selectBanerImageIcon,banner_attachment_image;
    private CropImageView cropImageView;
    private FrameLayout videoFrameLayout,frameLayout;
    private ImageButton article_insert_video;
    Bitmap bitmap;
    private Tracker mTracker;
    public  static final int RequestPermissionCode  = 1 ;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2,PIC_CROP = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        mSp = MySharedPreferences.getInstance(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        ContinueAfterPermission();
    }


    public void ContinueAfterPermission() {

        //adding text editor
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        toolbar.setNavigationIcon(R.drawable.ic_back_black);
//
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                handleOnBackPress();
//            }
//        });

        //google analytics code
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();

        articleTitle = findViewById(R.id.articleTitle);
        articleTitle.setFocusable(true);
        articleShortDescription=findViewById(R.id.artcileShortDescription);

//        articleShortDescription = findViewById(R.id.articleShortDescription);
        articleSelectBannerImage = findViewById(R.id.articleSelectBaneerImage);
        //selectBanerImageIcon=findViewById(R.id.selectBanerImageIcon);
//
//
//        banner_attachment_image=(ImageView)findViewById(R.id.banner_attachment_image);

        videoFrameLayout=(FrameLayout)findViewById(R.id.videoFrameLayout);
        video_post_close=(Button)findViewById(R.id.video_post_close);
        //articleTag = findViewById(R.id.articleTags);
        saveArticleButton = findViewById(R.id.nextArticle);
        frameLayout = findViewById(R.id.article_rl);
//        frameLayout.setVisibility(View.GONE);

        mRemoveButton = findViewById(R.id.article_banner_remove);
        mRemoveButton.setVisibility(View.GONE);

//        profileImage=findViewById(R.id.article_profile);
//        articleUserName=findViewById(R.id.article_userName);


        cropFinalImage = findViewById(R.id.cropFinalImage);
        cropImageView = findViewById(R.id.cropImageView);
        multiAutoCompleteTextView = findViewById(R.id.articleTags);


        //call method for getting tags from server
        //GetTagFromServer();

        Intent intent=getIntent();
        if(intent!=null && intent.hasExtra("Post_Slug_ID")) {
           Toast.makeText(this,intent.getStringExtra("Post_Slug_ID"),Toast.LENGTH_LONG).show();
            isEdit=true;
            Slug=intent.getStringExtra("Post_Slug_ID");
            postId=intent.getIntExtra("Post_ID",0);
            FetchDataFromServerAndBindToViews(intent.getStringExtra("Post_Slug_ID"));
        }


        mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.GONE);
                cropImageView.setImageBitmap(null);
                cropImageView.setVisibility(View.GONE);
                mRemoveButton.setVisibility(View.GONE);


            }

        });


        //getuser class from shared preferences
        UserClass userClass=UtilsClass.GetUserClassFromSharedPreferences(ArticleActivity.this);

//        if(userClass!=null){
//            Glide.with(this).load(UtilsClass.getParsedImageUrl(userClass.getProfile()))
//                    .into(profileImage);
//            articleUserName.setText(UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
//
//        }

        articleSelectBannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.VISIBLE);
                cropImageView.setVisibility(View.VISIBLE);
                mRemoveButton.setVisibility(View.VISIBLE);
                if (ContextCompat.checkSelfPermission(ArticleActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(ArticleActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }

                if (ActivityCompat.shouldShowRequestPermissionRationale(ArticleActivity.this,
                        Manifest.permission.CAMERA)) {

//                    Toast.makeText(ProfileActivity.this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

                } else {

                    ActivityCompat.requestPermissions(ArticleActivity.this,new String[]{
                            Manifest.permission.CAMERA}, RequestPermissionCode);

                }
//                selectImage();
                CropImage.activity().setAspectRatio(16,9).setAutoZoomEnabled(false).
                        setGuidelines(CropImageView.Guidelines.OFF)
                        .start(ArticleActivity.this);


            }


        });
//        selectBanerImageIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                frameLayout.setVisibility(View.VISIBLE);
//                cropImageView.setVisibility(View.VISIBLE);
//                mRemoveButton.setVisibility(View.VISIBLE);
//
//                CropImage.activity().setAspectRatio(16,9).setAutoZoomEnabled(false).
//                        setGuidelines(CropImageView.Guidelines.OFF)
//                        .start(ArticleActivity.this);
//            }
//
//        });



        saveArticleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save the data and send it to server
                String image;

                if(TextUtils.isEmpty(articleTitle.getText().toString())) {
                    articleTitle.setError("article title is required.");
                    //MDToast.makeText(getApplicationContext(), "article title is required.", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                } else if(TextUtils.isEmpty(articleShortDescription.getText().toString())) {
                    articleShortDescription.setError("article short description is required.");
                    //MDToast.makeText(getApplicationContext(), "article short description is required.", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                } else if(TextUtils.isEmpty(multiAutoCompleteTextView.getText().toString())) {
                    multiAutoCompleteTextView.setError("atleast one tag is required.");
                  //  MDToast.makeText(getApplicationContext(), " atleast one tag is required.", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                } else {
                    if(isEdit) {
                        BitmapDrawable drawable = (BitmapDrawable)cropFinalImage.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();

                        image=getStringImage(bitmap);
                        //SendArticleDataToServer1(image,UtilsClass.baseurl+"article-edit");
                        Intent intent=new Intent(getApplicationContext(),EditorActivity.class);
                        intent.putExtra("id",postId+"");
                        intent.putExtra("slug",Slug);
                        intent.putExtra("content",content);
                        intent.putExtra("isEdit",isEdit);

                        intent.putExtra("title",articleTitle.getText().toString());
                        intent.putExtra("tags",multiAutoCompleteTextView.getText().toString());
                        intent.putExtra("description_short",articleShortDescription.getText().toString());
                        intent.putExtra("banner_image","data:image/jpeg;base64,"+image);
                        startActivity(intent);

                        multiAutoCompleteTextView.setText("");
                        articleTitle.setText("");
                        articleShortDescription.setText("");
                        cropImageView.setVisibility(View.GONE);


                    } else {
                        cropImageView.invalidate();
                        image=getStringImage(cropImageView.getCroppedImage());
                        if(image==null){
                            MDToast.makeText(getApplicationContext(), "Select A Baner Image", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                        }else {
                            Intent intent=new Intent(getApplicationContext(),EditorActivity.class);
                            intent.putExtra(BundleConstants.DATA_FROM_ARTICLE_PAGE,true);
                            intent.putExtra("articleTitle",articleTitle.getText().toString());
                            intent.putExtra("articleTags",multiAutoCompleteTextView.getText().toString());
                            intent.putExtra("articleDescriptionShort",articleShortDescription.getText().toString());
                            intent.putExtra("articleBannerImage","data:image/jpeg;base64,"+image);
                            startActivity(intent);

                            multiAutoCompleteTextView.setText("");
                            articleTitle.setText("");
                            articleShortDescription.setText("");
                            cropImageView.setVisibility(View.GONE);
                        }

                    }

                }

            }
        });


        TextWatcher watcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //YOUR CODE
                GetTagFromServer(s.toString());

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //YOUR CODE
            }

            @Override
            public void afterTextChanged(Editable s) {
                String outputedText = s.toString();

               // mOutputText.setText(outputedText);

            }
        };

        multiAutoCompleteTextView.addTextChangedListener(watcher);


        multiAutoCompleteTextView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                multiAutoCompleteTextView.setText(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    //convert bitmap to string
    public String getStringImage(Bitmap bitmap){
        if(bitmap==null){

          return null;
        }
        else {
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,ba);
            byte[] imagebyte = ba.toByteArray();
            String encode = Base64.encodeToString(imagebyte,Base64.DEFAULT);
            return encode;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                cropImageView.setImageUriAsync(resultUri);
                bitmap = cropImageView.getCroppedImage();
                cropFinalImage.setImageBitmap(bitmap);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        if (resultCode == RESULT_OK && requestCode == 3) {

        }
        else if (resultCode == RESULT_OK && requestCode == 2) {
            if(data!=null) {
                uri = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(uri,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
            }
        }
        else if(resultCode == RESULT_OK && requestCode == 6) {
            if(data!=null) {
                Bundle extras = data.getExtras();
                bitmap  = extras.getParcelable("data");
                cropFinalImage.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode== RequestPermissionCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ContinueAfterPermission();
            } else {
                // Permission Denied
                MDToast.makeText(getApplicationContext(), "permission denied", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    //get tags from server
    private void GetTagFromServer(final String tags) {

        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();

        StringRequest myReq = new StringRequest(Request.Method.POST, ServerConstants.BASE_URL+"searchtags",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject tagsObject=jsonObject.getJSONObject("searched_tags");

                            JSONArray tagsArray=tagsObject.getJSONArray("data");
                            final ArrayList<String> tagName=new ArrayList<>();

                            for(int i=0;i<tagsArray.length();i++) {
                                //tagName.add(tags.getJSONObject(i).getString("name"));
                                tagName.add(tagsArray.getJSONObject(i).getString("name"));
                            }

                            // String[] languages = { "C","C++","Java","C#","PHP","JavaScript","jQuery","AJAX","JSON" };
                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ArticleActivity.this, android.R.layout.simple_spinner_dropdown_item, tagName);
                            multiAutoCompleteTextView.setAdapter(adapter);
                            multiAutoCompleteTextView.setThreshold(1);
                            multiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

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

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("search",tags);
                return params;
            }


        };
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);

    }

    //get data from server fro editing article
    private void FetchDataFromServerAndBindToViews(String post_slug_id) {

        //RequestQueue mRequestQueue = Volley.newRequestQueue(ArticleActivity.this);
        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();

        Toast.makeText(ArticleActivity.this,"calling server"+post_slug_id,Toast.LENGTH_LONG).show();
       // RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();
        // "https://archsqr.in/api/profile/detail/

        StringRequest myReq = new StringRequest(Request.Method.GET,ServerConstants.FETCH_ARTICLE_DATA_FOR_EDIT+post_slug_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("response",response);
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObjectFullPost = jsonObject.getJSONObject("articlePostEdit");
                            final ArticleEditClass articleEditClass = new ArticleEditClass(jsonObjectFullPost);
                            articleTitle.setText(articleEditClass.getTitle());
                            articleShortDescription.setText(articleEditClass.getShort_description());
                            // setContentToView(articleEditClass.getDescription());
                            content=articleEditClass.getDescription();

                            if(articleEditClass.getBanner_image()!=null) {
                                Glide.with(getApplicationContext()).load(UtilsClass.baseurl1+articleEditClass.getBanner_image())
                                        .into(cropFinalImage);
                                cropFinalImage.setVisibility(View.VISIBLE);
                                frameLayout.setVisibility(View.VISIBLE);
                                mRemoveButton.setVisibility(View.VISIBLE);
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

        };
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);
       // requestQueue.add(myReq);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserClass userClass=UtilsClass.GetUserClassFromSharedPreferences(ArticleActivity.this);
        if(userClass!=null){
            mTracker.setScreenName("ArticlePost /"+" /"+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }


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



}