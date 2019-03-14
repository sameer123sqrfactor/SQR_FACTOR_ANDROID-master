package com.user.sqrfactor.Activities;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Constants.BundleConstants;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Pojo.CountryClass;
import com.user.sqrfactor.Pojo.TokenClass;
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

public class SocialFormActivity extends AppCompatActivity {
    private EditText userName,mobileNo,firstName,lastName,user_backup_email_text,user_backup_password_text;
    private Button submit,skip;
    private Spinner country;
    private String country_val=null;
    private ArrayList<CountryClass> countryClassArrayList=new ArrayList<>();
    private ArrayList<String> countryName=new ArrayList<>();
    private int countryId=1;
    private UserClass userClass;
    private SharedPreferences mPrefs;
    private SharedPreferences mPrefEditor;

    private MySharedPreferences mSp;
    private Intent camIntent,gallIntent;
    public static DatabaseReference ref;
    public static FirebaseDatabase database;
    private ImageView uploadProfile;
    private Bitmap bitmap;
    private boolean isSignupWithMobileEnalbled=false;
    private TextInputLayout user_backup_password,user_backup_email,user_mobile_number,userName_textinputlayout;
    private Uri uri;
    public  static final int RequestPermissionCode  = 1 ;
    final int PIC_CROP = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    private Tracker mTracker;

    @Override
    protected void onResume() {
        super.onResume();

        if(userClass!=null){
            mTracker.setScreenName("SocialFormActivity /");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_form);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        //google anlytics
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();

        SharedPreferences mPrefs = getSharedPreferences("User", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        userClass = gson.fromJson(json, UserClass.class);
        database= FirebaseDatabase.getInstance();
        ref = database.getReference();

        mPrefEditor =getSharedPreferences("User", MODE_PRIVATE);
        SharedPreferences sharedPreferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN", "sqr");
        TokenClass.Token = token;
        TokenClass tokenClass = new TokenClass(token);

        mSp = MySharedPreferences.getInstance(this);
        userName = findViewById(R.id.userName_text);
        mobileNo = findViewById(R.id.user_mobile_number_text);
        firstName=findViewById(R.id.userFirstName_text);
        lastName=findViewById(R.id.userLastName_text);
        userName_textinputlayout=findViewById(R.id.userName_textinputlayout);
        user_backup_email=findViewById(R.id.user_backup_email);
        user_backup_password=findViewById(R.id.user_backup_password);
        user_backup_email_text=findViewById(R.id.user_backup_email_text);
        user_backup_password_text=findViewById(R.id.user_backup_password_text);
        user_mobile_number=findViewById(R.id.user_mobile_number);


        country = findViewById(R.id.social_country_spinner);
        submit = findViewById(R.id.social_submit);

        uploadProfile = findViewById(R.id.upload_profile);

        if(userClass!=null) {
            uploadProfile.setImageResource(0);
            Glide.with(this).load(UtilsClass.getParsedImageUrl(userClass.getProfile()))
                    .into(uploadProfile);
        }


//        uploadProfile.setOnClickListener(new View.OnClickListener() {
//                                             @Override
//                                             public void onClick(View v) {
//                                                 if (ContextCompat.checkSelfPermission(SocialFormActivity.this,
//                                                         android.Manifest.permission.READ_EXTERNAL_STORAGE)
//                                                         != PackageManager.PERMISSION_GRANTED) {
//
//                                                     ActivityCompat.requestPermissions(SocialFormActivity.this,
//                                                             new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
//                                                             MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//                                                 }
//
//                                                 if (ActivityCompat.shouldShowRequestPermissionRationale(SocialFormActivity.this,
//                                                         android.Manifest.permission.CAMERA)) {
//
//                                                     Toast.makeText(SocialFormActivity.this, "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();
//
//                                                 } else {
//
//                                                     ActivityCompat.requestPermissions(SocialFormActivity.this, new String[]{
//                                                             Manifest.permission.CAMERA}, RequestPermissionCode);
//
//                                                 }
//                                                 selectImage();
//
//                                             }
//                                         });


                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isSignupWithMobileEnalbled){
                            submitDetails(ServerConstants.SUBMIT_MOBILE_BASED_REG_DETAILS);
                        }else {
                            submitDetails(UtilsClass.baseurl+"updatesocialreg");
                        }

                    }
                });

                LoadCountryFromServer();

                if(getIntent().hasExtra(BundleConstants.SIGNUP_WITH_MOBILE_EBALED)){
                    isSignupWithMobileEnalbled=true;
                    user_backup_password.setVisibility(View.VISIBLE);
                    user_backup_email.setVisibility(View.VISIBLE);
                    userName_textinputlayout.setVisibility(View.GONE);
                    user_mobile_number.setVisibility(View.GONE);
                    country.setVisibility(View.GONE);
                }else {
                    user_backup_password.setVisibility(View.GONE);
                    user_backup_email.setVisibility(View.GONE);
                    userName_textinputlayout.setVisibility(View.VISIBLE);
                    user_mobile_number.setVisibility(View.VISIBLE);
                    country.setVisibility(View.VISIBLE);
                }

                country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,

                                               int position, long id) {

                        if (countryName.size() > 0) {
                            //country_val = countryName.get(position);
                            country_val = position + "";

                            countryId = countryClassArrayList.get(position).getId();
                        }


                    }

                    @Override

                    public void onNothingSelected(AdapterView<?> arg0) {

                    }

                });

            }

            public void LoadCountryFromServer() {
                RequestQueue requestQueue1 = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.GET, UtilsClass.baseurl+"public_country",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                Log.v("ResponseLike", s);
//                                Toast.makeText(SocialFormActivity.this, "res" + s, Toast.LENGTH_LONG).show();
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    JSONArray countries = jsonObject.getJSONArray("countries");
                                    countryName.clear();
                                    countryClassArrayList.clear();
                                    for (int i = 0; i < countries.length(); i++) {
                                        CountryClass countryClass = new CountryClass(countries.getJSONObject(i));
                                        countryClassArrayList.add(countryClass);
                                        countryName.add(countryClass.getName());

                                    }


                                    ArrayAdapter<String> spin_adapter1 = new ArrayAdapter<String>(SocialFormActivity.this, android.R.layout.simple_list_item_1, countryName);
                                    country.setAdapter(spin_adapter1);
                                    country.setSelection(100);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {

                                //Showing toast
//                        Toast.makeText(getActivity(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Accept", "application/json");


                        return params;
                    }

                };

                //Adding request to the queue
                requestQueue1.add(stringRequest);

            }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

public void submitDetails(String url){
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    StringRequest myReq = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                   // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                    Log.v("Reponse", response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if(jsonObject.has("profile_data")){

                            SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
                            Gson gson = new Gson();
                            String json = mPrefs.getString("MyObject", "");
                            UserClass userClass = gson.fromJson(json, UserClass.class);
                            TokenClass.Token=mSp.getKey(SPConstants.API_KEY);

                            JSONObject profile_data=jsonObject.getJSONObject("profile_data");
                            userClass.setProfile(profile_data.getString("profile"));
                            userClass.setUser_name(profile_data.getString("user_name"));
                            userClass.setFirst_name(profile_data.getString("first_name"));
                            userClass.setLast_name(profile_data.getString("last_name"));
                            userClass.setUserType("work_individual");

                            firstName.setText(profile_data.getString("first_name"));
                            lastName.setText(profile_data.getString("last_name"));



                            SharedPreferences.Editor prefsEditor = mPrefs.edit();
                            json = gson.toJson(userClass);
                            prefsEditor.putString("MyObject", json);
                            prefsEditor.apply();

                        }

                        else {

                           UserClass userClass=new UserClass();

                            userClass.setBlueprintCount("0");
                            userClass.setPortfolioCount("0");
                            userClass.setFollowingCount("0");
                            userClass.setFollowerCount("0");
                            userClass.setProfile(jsonObject.getString("user_profile"));
                            userClass.setUser_name(jsonObject.getString("user_username"));
                            userClass.setFirst_name(jsonObject.getString("user_firstname"));
                            userClass.setLast_name(jsonObject.getString("user_lastname"));
                            userClass.setName(jsonObject.getString("user_name"));
                            userClass.setEmail(jsonObject.getString("user_email"));
                            userClass.setMobile(jsonObject.getString("user_mobile"));
                            userClass.setUserId(Integer.parseInt(jsonObject.getString("user_id")));
                            userClass.setUserType("work_individual");

                            SharedPreferences.Editor prefsEditor = mPrefEditor.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(userClass);
                            prefsEditor.putString("MyObject", json);
                            prefsEditor.commit();

                            firstName.setText(jsonObject.getString("user_firstname"));
                            lastName.setText(jsonObject.getString("user_lastname"));
                        }



                        //userName.setText("");
                        mobileNo.setText("");
                        firstName.setText("");
                        lastName.setText("");
                        TokenClass.Token=mSp.getKey(SPConstants.API_KEY);
                        Intent i = new Intent(SocialFormActivity.this, HomeScreen.class);
                        startActivity(i);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
//                    MDToast.makeText(SocialFormActivity.this, "Your Mobile No. or Usename should be unique", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
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
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();

            if(isSignupWithMobileEnalbled){
                params.put("password",user_backup_password_text.getText().toString());
                params.put("email",user_backup_email_text.getText().toString());
                params.put("first_name",firstName.getText().toString());
                params.put("last_name",lastName.getText().toString());
            }else {
                params.put("mobile_number",mobileNo.getText().toString());
                params.put("country_id",country_val+"");
                params.put("username",userName.getText().toString());
                params.put("first_name",firstName.getText().toString());
                params.put("last_name",lastName.getText().toString());
            }


            return params;
        }


    };

    requestQueue.add(myReq);

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

                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    uri=Uri.fromFile(f);
                    Log.v("uriCamara",uri+"");
                    camIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    camIntent.putExtra("return-data", true);

                    startActivityForResult(camIntent, 1);

                }

                else if (options[item].equals("Choose from Gallery"))

                {


                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

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
                    uploadProfile.setImageBitmap(bitmap);
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
//            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
//            toast.show();
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
//            String errorMessage = "Whoops - your device doesn't support the crop action!";
//            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
//            toast.show();
        }
    }
    public void ChangeProfile(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest myReq = new StringRequest(Request.Method.POST,  UtilsClass.baseurl+"parse/change_profile",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("ReponseFeed", response);
//                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

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


        requestQueue.add(myReq);
    }
}
