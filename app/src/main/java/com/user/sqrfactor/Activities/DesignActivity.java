package com.user.sqrfactor.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.irshulx.Editor;
import com.github.irshulx.EditorListener;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Constants.BundleConstants;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.ArticleEditClass;
import com.user.sqrfactor.Pojo.SecondPageDesignData;
import com.user.sqrfactor.Pojo.TokenClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Storage.MySharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DesignActivity extends ToolbarActivity {

    private Uri uri;
    private int designId;
    Toolbar toolbar;
    private ImageView profileImage;
    private TextView profileName;
    private Editor editor;
    private ArticleEditClass designEditClass;
    private PlaceAutocompleteFragment autocompleteFragment;
    private SecondPageDesignData secondPageDesignData;
    private String imageString;
    private String slug;
    private MySharedPreferences mSp;
    private String userLocation;
    private boolean isEdit=false;
    private int PLACE_PICKER_REQUEST = 1;
    private  String finalHtml,html;
    private FrameLayout videoFrameLayout;
    private Button nextButton,video_post_close;
    private ImageButton design_insert_video,design_insert_image,design_insert_link;
    private EditText designTitle,designShortDescription,designLocation;
    private Tracker mTracker;

    @Override
    protected void onResume() {
        super.onResume();

        UserClass userClass=UtilsClass.GetUserClassFromSharedPreferences(DesignActivity.this);
        if(userClass!=null){
            mTracker.setScreenName("DesignActivity /"+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design);

        final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
                new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

        //google anayltics code
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();


        mSp = MySharedPreferences.getInstance(DesignActivity.this);
        designTitle = findViewById(R.id.designTitle);
        designTitle.setFocusable(true);
        designShortDescription = findViewById(R.id.designShortDescription);

      //  profileImage = findViewById(R.id.design_profile);
       // profileName = findViewById(R.id.design_profileName);

        nextButton = findViewById(R.id.next_design);

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
                Log.i("DesignActivity", "Place: " + place.getName()+" "+place.getAttributions()+" "+place
                .toString()+" "+place.getAddress()+" "+place.getPhoneNumber());
//                Toast.makeText(getApplicationContext(),"Place: " + place.getName(),Toast.LENGTH_LONG).show();
                userLocation=place.getName().toString();
                autocompleteFragment.setText(userLocation);
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("DesignActivity", "An error occurred: " + status);
            }
        });

        autocompleteFragment.setBoundsBias(new LatLngBounds(
                new LatLng(-33.880490, 151.184363),
                new LatLng(-33.858754, 151.229596)));



        ImageView searchIcon = (ImageView)((LinearLayout)autocompleteFragment.getView()).getChildAt(0);
        searchIcon.setVisibility(View.GONE);

//        SharedPreferences mPrefs =getSharedPreferences("User",MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = mPrefs.getString("MyObject", "");
//
//
//        UserClass userClass = gson.fromJson(json, UserClass.class);
        UserClass userClass = UtilsClass.GetUserClassFromSharedPreferences(DesignActivity.this);


        toolbar = (Toolbar)findViewById(R.id.toolbar);
       // toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        //toolbar.setNavigationIcon(R.drawable.ic_back_black);
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        toolbar.setNavigationIcon(R.drawable.ic_back_black);

//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });


        Intent intent1=getIntent();
        if(intent1!=null && intent1.hasExtra("Post_Slug_ID")) {
//            Toast.makeText(this,intent1.getStringExtra("Post_Slug_ID"),Toast.LENGTH_LONG).show();
            isEdit=true;
            slug=intent1.getStringExtra("Post_Slug_ID");
            designId=intent1.getIntExtra("Post_ID",0);
            FetchDataFromServerAndBindToViews(intent1.getStringExtra("Post_Slug_ID"));
        }


//        if(userClass!=null){
//            Glide.with(this).load(UtilsClass.getParsedImageUrl(userClass.getProfile()))
//                    .into(profileImage);
//            profileName.setText(UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
//        }
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Log.v("Design",editor.getContentAsHTML());

                if(isEdit) {
                    Intent intent=new Intent(getApplicationContext(),EditorActivity.class);
                    intent.putExtra(BundleConstants.IS_EDIT_DESIGN_DATA,true);
                    intent.putExtra("ArticleEditClass",designEditClass);
                    intent.putExtra("SecondPageDesignData", secondPageDesignData);
                    //Intent intent=new Intent(getApplicationContext(),Design2Activity.class);
                    intent.putExtra("Title",designTitle.getText().toString());
                    intent.putExtra("slug",slug);
                    intent.putExtra("designId",designId);
                    intent.putExtra("ShortDescription",designShortDescription.getText().toString());

                    if(userLocation!=null) {
                        intent.putExtra("Location",userLocation);
                    } else {
                        intent.putExtra("Location",secondPageDesignData.getLocation());
                    }

                    startActivity(intent);

                }
                else if (!TextUtils.isEmpty(designTitle.getText()) && !TextUtils.isEmpty(designShortDescription.getText())) {
                    Intent intent=new Intent(getApplicationContext(),EditorActivity.class);
                    intent.putExtra(BundleConstants.DATA_FROM_DESIGN_PAGE,true);
                    intent.putExtra("Title",designTitle.getText().toString());
                    intent.putExtra("DesignShortDescription",designShortDescription.getText().toString());
                    intent.putExtra("userLocation",userLocation);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(),"All fields are required",Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    public String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte,Base64.DEFAULT);
        return encode;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void FetchDataFromServerAndBindToViews(String post_slug_id) {

//        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // "https://archsqr.in/api/profile/detail/


        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();
        StringRequest myReq = new StringRequest(Request.Method.GET, ServerConstants.BASE_URL +"post/design/edit/"+post_slug_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("ReponseFeed", response);
//                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObjectFullPost = jsonObject.getJSONObject("designPostEdit");
                            JSONObject jsonObjectSecondPageData = jsonObject.getJSONObject("secondpage_data");
                            designEditClass = new ArticleEditClass(jsonObjectFullPost);
                            secondPageDesignData = new SecondPageDesignData(jsonObjectSecondPageData);

                            designTitle.setText(designEditClass.getTitle());
                            designShortDescription.setText(designEditClass.getShort_description());
                            //designLocation.setText(secondPageDesignData.getLocation());
                            autocompleteFragment.setText(secondPageDesignData.getLocation());

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
        //requestQueue.add(myReq);
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);
    }

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