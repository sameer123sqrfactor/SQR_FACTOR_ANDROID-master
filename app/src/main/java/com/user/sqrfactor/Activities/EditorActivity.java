package com.user.sqrfactor.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.irshulx.Editor;
import com.github.irshulx.EditorListener;
import com.github.irshulx.models.EditorTextStyle;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Constants.BundleConstants;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Extras.EditorExtendClass;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Fragments.MDToast;
import com.user.sqrfactor.Pojo.ArticleEditClass;
import com.user.sqrfactor.Pojo.SecondPageDesignData;
import com.user.sqrfactor.Pojo.TokenClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Utils.NetworkUtil;
import com.user.sqrfactor.Extras.UtilsClass;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EditorActivity extends ToolbarActivity {
    private Editor editor;
    private String finalHtml=null,imageString,html,Slug=null;
    private FrameLayout videoFrameLayout,frameLayout;
    private ImageButton article_insert_video;
    private Button submitArticleBtn,video_post_close;
    private Toolbar toolbar;
    private Uri uri;
    private Boolean isEditedEnabled=false,isCompetitionEditedData=false;
    private static final String TAG = "SubmitActivity";
    private static final int RESULT_COVER_IMAGE = 1,RESULT_PDF = 2;
    private Integer pageNumber = 0;
    private Toolbar mToolbar;
    private RelativeLayout mCoverImageButton;
    private TextView mCoverImagePathTV;
    private RelativeLayout mDesignPdfButton;
    private TextView mPdfPathTV;
    private Button mSubmitButton,sqrfactor_editor_btn,submit_pdf_btn;
    private ArticleEditClass designEditClass=null;
    private SecondPageDesignData secondPageDesignData=null;

    private String articleTitle,articleTags,articleBannerImage,articleDescriptionShort;
    private boolean designData=false,isEditedArticleEnabled=false,articleData=false,isEditedDesignEnabled=false;
    private ProgressBar mPb;

    private MySharedPreferences mSp;

    private ScrollView mContentLayout;
    private Tracker mTracker;

    private String designTitle,designSubmissionLocation,designShortDescritpion;

    private String slug,title,coverImagePath,pdfPath,competitionId,participationId;


    @Override
    protected void onResume() {
        super.onResume();
        UserClass userClass=UtilsClass.GetUserClassFromSharedPreferences(EditorActivity.this);
        if(userClass!=null){
            mTracker.setScreenName("EditorActivity /"+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSp = MySharedPreferences.getInstance(this);

        mPb = findViewById(R.id.pb);
        mContentLayout = findViewById(R.id.fullContentView);


        //google anlytics
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        submitArticleBtn=findViewById(R.id.submitArticleBtn);
        videoFrameLayout=(FrameLayout)findViewById(R.id.videoFrameLayout);
        video_post_close=(Button)findViewById(R.id.video_post_close);
        article_insert_video=(ImageButton)findViewById(R.id.article_insert_video);
        article_insert_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();

            }
        });
        editor = (Editor) findViewById(R.id.editor);
        editor.setFocusable(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            editor.setFocusable(View.NOT_FOCUSABLE);
        }

        findViewById(R.id.article_text_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.BOLD);
            }
        });

        findViewById(R.id.article_text_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.ITALIC);
            }
        });

        findViewById(R.id.article_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.openImagePicker();
            }
        });

//        findViewById(R.id.article_insert_link).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editor.insertLink();
//            }
//        });

        findViewById(R.id.article_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertLink();
            }
        });


        editor.setDividerLayout(R.layout.tmpl_divider_layout);
        editor.setEditorImageLayout(R.layout.tmpl_image_view);
        editor.setListItemLayout(R.layout.tmpl_list_item);
        //editor.StartEditor();
        editor.setEditorListener(new EditorListener() {
            @Override
            public void onTextChanged(EditText editText, Editable text) {
                //Toast.makeText(ArticleActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUpload(Bitmap image, String uuid) {
              //  Toast.makeText(EditorActivity.this, uuid, Toast.LENGTH_LONG).show();
                uploadEditorImageToServer(uuid);
            }

        });
        editor.render();


        Intent intent1=getIntent();


        if(intent1!=null && intent1.hasExtra("isEdit")) {
            isEditedArticleEnabled=true;
            slug = getIntent().getStringExtra("slug");
            setContentToView1(intent1.getStringExtra("content"));

        }else if(intent1!=null && intent1.hasExtra(BundleConstants.DATA_FROM_ARTICLE_PAGE)){
            articleData=true;
            articleTitle=intent1.getStringExtra("articleTitle");
            articleTags=intent1.getStringExtra("articleTags");
            articleDescriptionShort=intent1.getStringExtra("articleDescriptionShort");
            articleBannerImage=intent1.getStringExtra("articleBannerImage");
        }
        else if(intent1!=null && intent1.hasExtra("isEditor")) {

            isCompetitionEditedData=true;
            title=getIntent().getStringExtra("design_title");
            coverImagePath=getIntent().getStringExtra("design_cover_image");
            competitionId=getIntent().getStringExtra("competition_id");
            participationId=getIntent().getStringExtra("competition_participation_id");
            slug = getIntent().getStringExtra(BundleConstants.SLUG);


        }else if(intent1!=null && intent1.hasExtra(BundleConstants.DATA_FROM_DESIGN_PAGE)){
            designData=true;
            submitArticleBtn.setText("next");
            designTitle=intent1.getStringExtra("Title");
            designShortDescritpion=intent1.getStringExtra("DesignShortDescription");
            designSubmissionLocation=intent1.getStringExtra("userLocation");


        }else if(intent1!=null && intent1.hasExtra(BundleConstants.IS_EDIT_DESIGN_DATA)){
            submitArticleBtn.setText("next");
            isEditedDesignEnabled=true;
            designEditClass= (ArticleEditClass) intent1.getSerializableExtra("ArticleEditClass");
            secondPageDesignData=(SecondPageDesignData)intent1.getSerializableExtra("SecondPageDesignData");

            setContentToView1(designEditClass.getDescription());


        }


        submitArticleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(isEditedArticleEnabled) {
                    SendEditedArticleDataToServer();

                }else if(isCompetitionEditedData){
                    submissionDesignSaveApi();

                }
                else if(articleData){
                    if(finalHtml!=null){
                        SendArticleDataToServer(articleTitle,articleTags,articleDescriptionShort,articleBannerImage,finalHtml);
                    } else{
                        SendArticleDataToServer(articleTitle,articleTags,articleDescriptionShort,articleBannerImage,editor.getContentAsHTML());
                    }
                }
                else if(designData){
                    if(finalHtml!=null){
                        SendDesignDataToServer(designTitle,designShortDescritpion,designSubmissionLocation,finalHtml);
                    } else{
                        SendDesignDataToServer(designTitle,designShortDescritpion,designSubmissionLocation,editor.getContentAsHTML());
                    }
                }else if(isEditedDesignEnabled){
                    String Title,slug,designId,ShortDescription,Location,Description;
                    Intent intent1=getIntent();
                    if(intent1!=null && intent1.hasExtra(BundleConstants.IS_EDIT_DESIGN_DATA)){
                        designEditClass= (ArticleEditClass) intent1.getSerializableExtra("ArticleEditClass");
                        secondPageDesignData=(SecondPageDesignData)intent1.getSerializableExtra("SecondPageDesignData");
                        Title=intent1.getStringExtra("Title");
                        slug=intent1.getStringExtra("slug");
                        designId=intent1.getStringExtra("designId");
                        ShortDescription=intent1.getStringExtra("ShortDescription");
                        Location=intent1.getStringExtra("Location");
                        Description=intent1.getStringExtra("Description");


                        Intent intent=new Intent(getApplicationContext(),Design2Activity.class);
                        intent.putExtra(BundleConstants.IS_EDIT_DESIGN_DATA,true);
                        intent.putExtra("ArticleEditClass",designEditClass);
                        intent.putExtra("SecondPageDesignData", secondPageDesignData);
                        intent.putExtra("Title",Title);
                        intent.putExtra("slug",slug);
                        intent.putExtra("designId",designId);
                        intent.putExtra("ShortDescription",ShortDescription);
                        intent.putExtra("Location",Location);
                        if(finalHtml!=null){
                            intent.putExtra("Description",finalHtml);
                        } else{
                            intent.putExtra("Description",editor.getContentAsHTML());
                        }
                        //intent.putExtra("Description",Description);
                        startActivity(intent);
                    }


                    //SendDesignEditedDataToServer(designTitle,designShortDescritpion,designSubmissionLocation,);
                }
            }
        });

    }




    public void uploadEditorImageToServer(final String uuid)
    {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"upload-medium-image",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String Imgurl=jsonObject.getString("asset_image");
                            editor.onImageUploadComplete(Imgurl, uuid);
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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("image","data:image/jpeg;base64,"+imageString);
                return params;
            }
        };

        requestQueue.add(myReq);


    }


    public void showPopup(){
        LayoutInflater li = LayoutInflater.from(this);
        final View promptsView = li.inflate(R.layout.post_video_link_popup, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.post_video_link);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                String videoLink = userInput.getText().toString();
                                showVideo(videoLink);
                                //result.setText(userInput.getText());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        // AlertDialog alertDialog = alertDialogBuilder.create();


    }

    private void showVideo(String videoLink)
    {

        final WebView myWebView = (WebView) findViewById(R.id.articleVideoView);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.setWebChromeClient(new WebChromeClient(){});

        myWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setLoadWithOverviewMode(true);

        String[] stringId=videoLink.split("/");

        String id=stringId[stringId.length-1];
        Log.v("id",id);
        String src="src="+'"'+"https://www.youtube.com/embed/"+id+'"';




        html="<iframe width=\"100%\" height=\"250\" "+src+"frameborder=\"0\" allowfullscreen=\"\"></iframe>";
        //String html1 = "<iframe width=\"100%\" height=\"600\" src=\"www.youtube.com/embed/cffcUX_aHe0\" frameborder=\"0\" allowfullscreen=\"\"></iframe>";

        myWebView.loadDataWithBaseURL("https://www.youtube.com/embed/"+id+'"', html, "text/html","UTF-8",null);
        //myWebView.loadUrl(videoLink);

        finalHtml="   <html>\n" +
                "  <head>\n" +
                "    <title>Combined</title>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div id=\"page1\">\n" +
                editor.getContentAsHTML() +
                "    </div>\n" +
                "    <div id=\"page2\">\n" +
                html +
                "    </div>\n" +
                "  </body>\n" +
                "</html>";

        videoFrameLayout.setVisibility(View.VISIBLE);
//
        video_post_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //myWebView.setVisibility(View.GONE);
                videoFrameLayout.setVisibility(View.GONE);
            }
        });
    }
    public void SendDesignDataToServer(final String designTitle, final String designShortDescritpion, final String designSubmissionLocation, final String designEditorContent)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"design-parse",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Intent intent=new Intent(getApplicationContext(),Design2Activity.class);
                        intent.putExtra("Title",designTitle);
                        intent.putExtra("Location",designSubmissionLocation);
                        intent.putExtra("ShortDescription",designShortDescritpion);
                        intent.putExtra("Description",designEditorContent);
                        startActivity(intent);
                        editor.clearAllContents();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            editor.setFocusable(View.NOT_FOCUSABLE);
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
                params.put("Authorization", "Bearer "+TokenClass.Token);
                return params;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("title",designTitle);
                params.put("formatted_address",designSubmissionLocation);
                params.put("description_short", designShortDescritpion);
                params.put("description", designEditorContent);
                params.put("post_type","design");
                return params;
            }
        };

        requestQueue.add(myReq);
    }


    public void SendArticleDataToServer(final String articleTitle, final String articleTags, final String articleDescriptionShort, final String articleBannerImage, final String articleEditorContent) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest myReq = new StringRequest(Request.Method.POST,ServerConstants.SUBMIT_ARTICLE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Log.v("Reponse", response);
//                        Toast.makeText(EditorActivity.this,response,Toast.LENGTH_LONG).show();

                        MDToast.makeText(EditorActivity.this, "Article posted successfully", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                        editor.clearAllContents();
                        Intent intent=new Intent(EditorActivity.this,HomeScreen.class);
                        startActivity(intent);
                        finish();

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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("post_type","article");
                params.put("title",articleTitle);
                params.put("tags",articleTags);
                params.put("description_short",articleDescriptionShort);
                params.put("banner_image",articleBannerImage);
                params.put("description",articleEditorContent);
                return params;
            }
        };

        requestQueue.add(myReq);
    }



    private void submissionDesignSaveApi() {
        mPb.setVisibility(View.VISIBLE);
        mContentLayout.setVisibility(View.GONE);

        if (!NetworkUtil.isNetworkAvailable()) {
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT);
            mPb.setVisibility(View.GONE);
            mContentLayout.setVisibility(View.VISIBLE);
            return;
        }

        try {
            String description="";
            if(finalHtml!=null)
                description=finalHtml;
            else
                description=editor.getContentAsHTML();

            final String uploadId = UUID.randomUUID().toString();
            //initializeSSLContext(getCallingActivity());
            final MultipartUploadRequest request = new MultipartUploadRequest(this, uploadId, ServerConstants.SUBMISSION_DESIGN_SAVE);
            request.addParameter("design_title",title)
                    .addParameter("competition_id",competitionId)
                    .addParameter("competition_participation_id", participationId)
                    .addParameter("design_body", description)
                    .addFileToUpload(coverImagePath,"design_cover_image")
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .setDelegate(new UploadStatusDelegate() {

                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {
                            Log.i(TAG, "onProgress: ");
                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
                            mPb.setVisibility(View.GONE);
                            mContentLayout.setVisibility(View.VISIBLE);
                            Log.d(TAG, "onError: server response = " + serverResponse.getBodyAsString());
                            if(exception != null)
                                Log.d(TAG, "onError: exception = " + exception.toString());
                            else
                                MDToast.makeText(EditorActivity.this, "Error uploading documents", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                            mPb.setVisibility(View.GONE);
                            mContentLayout.setVisibility(View.VISIBLE);
                            Log.d(TAG, "onCompleted: upload successful");
                            Log.d(TAG, "onCompleted: " + serverResponse.getBodyAsString());
                            try {
                                JSONObject responseObject = new JSONObject(serverResponse.getBodyAsString());
                                String sl = responseObject.getString("message");
                                if(sl.equals("success")) {
                                    MDToast.makeText(EditorActivity.this, "Design Upload Successful", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                    startActivity(new Intent(EditorActivity.this,CompetitionDetailActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            .putExtra(BundleConstants.SLUG,slug));
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {
                            mPb.setVisibility(View.GONE);
                            mContentLayout.setVisibility(View.VISIBLE);
                            Log.i(TAG, "onCancelled: ");
                        }
                    });
            request.addHeader("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY))
                    .addHeader("Accept", "application/json")
                    .startUpload();

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == editor.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK&& data != null && data.getData() != null) {
            uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap,
                        (int) (bitmap.getWidth() * 0.5), (int) (bitmap.getHeight() * 0.5), false);
                imageString=getStringImage(bitmap);
                editor.insertImage(bitmapResized);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (resultCode == RESULT_OK && requestCode == 3) {


        } else if (resultCode == RESULT_OK && requestCode == 2) {
            if(data!=null) {
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

                //ImageView imageView = (ImageView) findViewById(R.id.imgView);
                //profileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
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

    public void SendEditedArticleDataToServer() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest myReq = new StringRequest(Request.Method.POST,ServerConstants.SUBMIT_EDITED_ARTICLE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MDToast.makeText(EditorActivity.this, "Article Edited successfully", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                        editor.clearAllContents();
                        frameLayout.setVisibility(View.GONE);
                        Intent intent=new Intent(EditorActivity.this,HomeScreen.class);
                        startActivity(intent);
                        finish();
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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//                Log.v("article1",getIntent().getStringExtra("id")+" "+getIntent().getStringExtra("title")+" "+getIntent().getStringExtra("tags")+
//                        " "+getIntent().getStringExtra("description_short")+" "+finalHtml+" "+Slug +" "+getIntent().getStringExtra("banner_image")+ " ");

                Log.v("artcile1123",slug);
                params.put("id",getIntent().getStringExtra("id"));
                params.put("title",getIntent().getStringExtra("title"));
                params.put("tags",getIntent().getStringExtra("tags"));
                params.put("description_short",getIntent().getStringExtra("description_short"));
                params.put("slug",Slug);
                params.put("banner_image",getIntent().getStringExtra("banner_image"));
                if(finalHtml!=null)
                    params.put("description",finalHtml);
                else
                    params.put("description",editor.getContentAsHTML());
                return params;
            }
        };

        requestQueue.add(myReq);
    }


    public void setContentToView(String content){

        Log.v("des1",content);
        //EditorExtendClass editorExtendClass= (EditorExtendClass) editor;
        Document doc = Jsoup.parse(content);
        Elements elements = doc.getAllElements();
        int pos=0;

        for(Element element :elements){
            Tag tag = element.tag();

            if(tag.getName().equalsIgnoreCase("a")){
                String name  = element.html();
                //String heading = element.select(tag.getName().toString()).text();

                if(name.contains("span")||name.contains("<i>")||name.contains("<b>")) {
                    continue;
                } else {
                    editor.getInputExtensions().insertEditText(pos, "", name);
                    pos++;
                }

            }

            else if(tag.getName().equalsIgnoreCase("b")){
                String title  = element.html();
                //String heading = element.select(tag.getName().toString()).text();
                Log.v("des2",title);
                if(title.contains("&nbsp")||title.contains("href")||title.equals("<br>")) {
                    continue;
                } else {
                    editor.getInputExtensions().insertEditText(pos,"",title);
                    pos++;
                    continue;
                }

            }

            else if(tag.getName().equalsIgnoreCase("p")){
                element.select("img").remove();
                String body= element.html();

                String[] parsedBody=body.split("\\.");
                StringBuilder builder = new StringBuilder();
                for(String s : parsedBody) {
                    Log.v("des3",s);
                    if(s.contains("&nbsp")||s.contains("<span")||s.contains("</span>")||s.contains("<br>")) {
                        continue;
                    }
                    else
                        builder.append(s+".");
                }
                String str = builder.toString();
                if(body.contains("href")||body.equals("<br>")||body.contains("<b>")) {
                    continue;
                }
                else {

                    editor.getInputExtensions().insertEditText(pos,"",title);
                    pos++;
                    continue;
                }
            }
            else if (tag.getName().equalsIgnoreCase("img")){
                String url  = element.select("img").attr("src");
                Log.v("des4",url);

                Glide.with(this)
                        .asBitmap()
                        .load(url)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                Bitmap bitmapResized = Bitmap.createScaledBitmap(resource,
                                        (int) (resource.getWidth() * 0.5), (int) (resource.getHeight() * 0.5), false);
                                imageString=getStringImage(resource);
                                editor.insertImage(bitmapResized);
                            }
                        });
                continue;
            }

            else if (tag.getName().equalsIgnoreCase("iframe")){
                String url  = element.select("iframe").attr("src");
                Log.v("des5",url);


                final WebView myWebView = (WebView) findViewById(R.id.articleVideoView);
                myWebView.setWebViewClient(new WebViewClient());
                myWebView.getSettings().setJavaScriptEnabled(true);
                myWebView.setWebChromeClient(new WebChromeClient());

                String[] stringId=url.split("/");
                String id=stringId[stringId.length-1];
                String src1="src="+'"'+"https://www.youtube.com/embed/"+id+'"';
                String html="<iframe width=\"100%\" height=\"400\" "+src1+"frameborder=\"0\" allowfullscreen=\"\"></iframe>";

                finalHtml="   <html>\n" +
                        "  <head>\n" +
                        "    <title>Combined</title>\n" +
                        "  </head>\n" +
                        "  <body>\n" +
                        "    <div id=\"page1\">\n" +
                        editor.getContentAsHTML() +
                        "    </div>\n" +
                        "    <div id=\"page2\">\n" +
                        html +
                        "    </div>\n" +
                        "  </body>\n" +
                        "</html>";

                myWebView.loadDataWithBaseURL(url,html, "text/html", "UTF-8", "");
                videoFrameLayout.setVisibility(View.VISIBLE);
//
                video_post_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        videoFrameLayout.setVisibility(View.GONE);
                    }
                });
                continue;
            }
        }
    }

    public void setContentToView1(String content){
        List<String> p = new ArrayList<>();
        List<String> src = new ArrayList<>();
        Document doc = Jsoup.parse(content);
        final int[] pos = {0};

        Elements elements = doc.getAllElements();

        for(Element element :elements){
            Tag tag = element.tag();

            if(tag.getName().equalsIgnoreCase("a")){
                String name  = element.html();
                //String heading = element.select(tag.getName().toString()).text();
                Log.v("des1",name);
                if(name.contains("span")||name.contains("<i>")||name.contains("<b>")) {
                    continue;
                } else {
                      editor.getInputExtensions().insertEditText(++pos[0],"",name);
                }

            } else if(tag.getName().equalsIgnoreCase("b")){
                String title  = element.html();
                //String heading = element.select(tag.getName().toString()).text();
                Log.v("des2",title);
                if(title.contains("href")||title.equals("<br>")) {
                    continue;
                } else {
                    // editor.s
                     editor.getInputExtensions().insertEditText(++pos[0],"",title);
                    continue;
                }

            }

            else if(tag.getName().equalsIgnoreCase("p")){
                element.select("img").remove();
                String body= element.html();
                String[] parsedBody=body.split("\\.");
                StringBuilder builder = new StringBuilder();
                for(String s : parsedBody) {
                    Log.v("des3",s);
                    if(s.contains("<span")||s.contains("</span>")||s.contains("<br>")) {
                        continue;
                    } else{
                        builder.append(s+".");
                    }

                }
                String str = builder.toString();
                if(body.contains("href")||body.equals("<br>")||body.contains("<b>")) {
                    continue;
                } else {
                     editor.getInputExtensions().insertEditText(++pos[0],"",str);
                    continue;
                }


            }
            else if (tag.getName().equalsIgnoreCase("img")){
                String url  = element.select("img").attr("src");
                Log.v("des4",url);

                Glide.with(this)
                        .asBitmap()
                        .load(url)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                Bitmap bitmapResized = Bitmap.createScaledBitmap(resource,
                                        (int) (resource.getWidth() * 0.5), (int) (resource.getHeight() * 0.5), false);
                                imageString=getStringImage(resource);
                                editor.insertImage(bitmapResized);
                            }
                        });
                continue;
            }
            else if (tag.getName().equalsIgnoreCase("iframe")){
                String url  = element.select("iframe").attr("src");
                Log.v("des5",url);
                final WebView myWebView = (WebView) findViewById(R.id.articleVideoView);
                myWebView.setWebViewClient(new WebViewClient());
                myWebView.getSettings().setJavaScriptEnabled(true);
                myWebView.setWebChromeClient(new WebChromeClient());
                String[] stringId=url.split("/");
                String id=stringId[stringId.length-1];
                String src1="src="+'"'+"https://www.youtube.com/embed/"+id+'"';
                String html="<iframe width=\"100%\" height=\"400\" "+src1+"frameborder=\"0\" allowfullscreen=\"\"></iframe>";

                finalHtml="   <html>\n" +
                        "  <head>\n" +
                        "    <title>Combined</title>\n" +
                        "  </head>\n" +
                        "  <body>\n" +
                        "    <div id=\"page1\">\n" +
                        editor.getContentAsHTML() +
                        "    </div>\n" +
                        "    <div id=\"page2\">\n" +
                        html +
                        "    </div>\n" +
                        "  </body>\n" +
                        "</html>";

                myWebView.loadDataWithBaseURL(url,html, "text/html", "UTF-8", "");
                videoFrameLayout.setVisibility(View.VISIBLE);
//
                video_post_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        videoFrameLayout.setVisibility(View.GONE);
                    }
                });
                continue;
            }
        }
    }



}


