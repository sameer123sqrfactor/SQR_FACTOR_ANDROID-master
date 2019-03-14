package com.user.sqrfactor.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.user.sqrfactor.Constants.BundleConstants;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Fragments.MDToast;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Utils.FileDownloader;
import com.user.sqrfactor.Utils.FileUtils;
import com.user.sqrfactor.Extras.UtilsClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SubmitActivity extends AppCompatActivity {
    private static final String TAG = "SubmitActivity";
    private static final int RESULT_COVER_IMAGE = 1;
    private static final int RESULT_PDF = 2;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private String getPdfPathFromServer=null;
    private Toolbar mToolbar;
    private RelativeLayout mCoverImageButton;
    private TextView mCoverImagePathTV;
    private RelativeLayout mDesignPdfButton;
    private TextView mPdfPathTV;
    private Button mSubmitButton;
    private Button sqrfactor_editor_btn;
    private Button submit_pdf_btn;
    private ImageView imageViewFormServer;

    private ProgressBar mPb;
    private MySharedPreferences mSp;
    private RelativeLayout mContentLayout;
    private RequestQueue mRequestQueue;
    private String mSelectedFilePath;
    private String mSelectedCoverImagePath;
    private EditText mDesignTitleET;
    private LinearLayout Pdf_Upload_layout,sqrfactor_editor;

    private String competition_submission_id=null;
    private String slug;
    private String title;
    private String coverImagePath;
    private String pdfPath;
    private String competitionId;
    private String participationId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.back_arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        mSp = MySharedPreferences.getInstance(this);
        mDesignTitleET = findViewById(R.id.submit_design_title);
        mCoverImageButton = findViewById(R.id.submit_design_cover_image);
        mCoverImagePathTV = findViewById(R.id.submit_design_cover_image_path);
        mDesignPdfButton = findViewById(R.id.submit_design_pdf);
        mPdfPathTV = findViewById(R.id.submit_design_pdf_path);
        Pdf_Upload_layout=findViewById(R.id.Pdf_Upload_layout);
        sqrfactor_editor_btn=findViewById(R.id.sqrfactor_editor_btn);
        submit_pdf_btn=findViewById(R.id.pdf_button);
       // mSubmitButton = findViewById(R.id.submit_design);

        imageViewFormServer=findViewById(R.id.imageViewFormServer);
       // ImageLoader imageLoader=new  ImageLoader(SubmitActivity.this);



        if(getIntent()!=null ) {
            slug = getIntent().getStringExtra(BundleConstants.SLUG);
            getIdDetails();
        }
        if(getIntent()!=null && getIntent().hasExtra(BundleConstants.IS_EDIT_SUBMISSION)) {
            slug = getIntent().getStringExtra(BundleConstants.SLUG);
            GetEditableDataFrom(getIntent().getStringExtra(BundleConstants.SUBMISSION_ID));
        }
        submit_pdf_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = mDesignTitleET.getText().toString();
                if(!title.isEmpty() &&
                        !mCoverImagePathTV.getText().toString().matches("No file chosen")){
                    if(title.length() < 6){
                        mDesignTitleET.setError("Atleast 6 in length");
                    }
                    else
                    {
                        //submissionDesignSaveApi();
                        Intent intent=new Intent(getApplicationContext(),UploadPdfEditor.class);
                        intent.putExtra("design_title",title);
                        intent.putExtra("design_cover_image",coverImagePath);
                        intent.putExtra("competition_id",competitionId);
                        intent.putExtra("competition_participation_id",participationId);
                        intent.putExtra("getPdfPathFromServer",mSelectedFilePath);
                        intent.putExtra("competition_submission_id",competition_submission_id);
                        intent.putExtra("slug",slug);
                        intent.putExtra("isPdf",true);
                        startActivity(intent);
                    }

                }
                else{
                    MDToast.makeText(SubmitActivity.this, "Enter Proper Details", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                }


            }
        });
        sqrfactor_editor_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                title = mDesignTitleET.getText().toString();
                if(!title.isEmpty() &&
                        !mCoverImagePathTV.getText().toString().matches("No file chosen")){
                    if(title.length() < 6){
                        mDesignTitleET.setError("Atleast 6 in length");
                    }
                    else {
                        Intent intent=new Intent(getApplicationContext(),EditorActivity.class);
                        intent.putExtra("design_title",title);
                        intent.putExtra("design_cover_image",coverImagePath);
                        intent.putExtra("competition_id",competitionId);
                        intent.putExtra("competition_participation_id",participationId);
                        intent.putExtra("slug",slug);
                        intent.putExtra("isEditor",true);
                        startActivity(intent);
                    }

                }
                else{
                    // Log.d(TAG, "+++++++++++++++++"+title+" "+mCoverImagePathTV.getText().toString()+" "+mPdfPathTV.getText().toString());
                    Toast.makeText(SubmitActivity.this, "Enter Proper Details", Toast.LENGTH_SHORT).show();
                }


            }
        });

        mCoverImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkPermissionREAD_EXTERNAL_STORAGE(SubmitActivity.this)){
                    // do your stuff..
                    chooseCoverImageIntent();
                }


            }
        });



    }

    private void GetEditableDataFrom(String submissionId) {
//        Toast.makeText(getApplicationContext(),submissionId+"",Toast.LENGTH_LONG).show();
        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        Log.d(TAG, "getIdDetails: =-=-=-=-=-"+slug+ServerConstants.SUBMISSION_ID_DETAILS);
        StringRequest idDetailRequest = new StringRequest(Request.Method.GET, ServerConstants.EETCH_EDITABLE_SUBMISSION_DATA+submissionId ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse"+response);
                        //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        try {
                            JSONObject detailResponse = new JSONObject(response);
                            JSONObject subResponse = detailResponse.getJSONObject("submission");

                            competitionId = subResponse.getString("competition_id");
                            participationId = subResponse.getString("competition_participation_id");
                            mDesignTitleET.setText(subResponse.getString("title"));
                            competition_submission_id=subResponse.getString("id");

                            mSelectedCoverImagePath = subResponse.getString("cover");

                            coverImagePath = mSelectedCoverImagePath;
                            String[] newOne = mSelectedCoverImagePath.split("/");
                            mCoverImagePathTV.setText((newOne[newOne.length - 1]));
                           // new DownloadingFile().execute(UtilsClass.getParsedImageUrl(UtilsClass.getParsedImageUrl(coverImagePath)), newOne[newOne.length - 1]);
                            SetImageToDisplayAndDownload(UtilsClass.getParsedImageUrl(mSelectedCoverImagePath),newOne[newOne.length - 1]);




                            mSelectedFilePath = subResponse.getString("pdf");

                            pdfPath = mSelectedFilePath;
                            Log.v("filepathname",pdfPath);
                            String[] newOneFileName = mSelectedFilePath.split("/");
//                            if (ContextCompat.checkSelfPermission(SubmitActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//
//                                new DownloadingFile().execute(UtilsClass.getParsedImageUrl(pdfPath), newOneFileName[newOneFileName.length - 1]);
//                            } else {
//                                // Request permission from the user
//                                ActivityCompat.requestPermissions(SubmitActivity.this,
//                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
//                            }

                            SetPdfToDownloadAndDisplay(UtilsClass.getParsedImageUrl(pdfPath), newOneFileName[newOneFileName.length - 1]);




                            //storage/competition_submission/37693cfc748049e45d87b8c7d8b9aacd/pdf/Gruq9Czh8XF3nwR6XJTa.pdf
                            Log.d(TAG, "onActivityResult: selected cover image path = " + mSelectedCoverImagePath);




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SubmitActivity.this, "Error getting details for submission", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
                headers.put(getString(R.string.accept), getString(R.string.application_json));
                headers.put(getString(R.string.authorization), Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
//                headers.put("Content-Type", contentType);
                return headers;
            }
        };
        idDetailRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(idDetailRequest);

    }

    private void SetPdfToDownloadAndDisplay(String pdfurl, String filename) {
        String extStorageDirectory = Environment.getExternalStorageDirectory()
                .toString();
        File folder = new File(extStorageDirectory, "SqrfactorPdf");
        folder.mkdir();
        File file = new File(folder, filename);
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
       // FileDownloader.DownloadFile(pdfurl, file);


    }

    private void SetImageToDisplayAndDownload(String imageUri,String filename){


        String extStorageDirectory = Environment.getExternalStorageDirectory()
                .toString();
        File folder = new File(extStorageDirectory, "SqrfactorImages");
        folder.mkdir();
        File file = new File(folder, filename);
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
      //  FileDownloader.DownloadFile(imageUri, file);

        showPdf(imageUri,filename);

//        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/SqrfactorPdf/" +filename);  // -> filename = maven.pdf
//        Uri path = Uri.fromFile(pdfFile);

//
//        coverImagePath=FileUtils.getPath(SubmitActivity.this,path);
//
//


    }

    public void showPdf(String imageUri,String fileName)
    {
        File file = new File(Environment.getExternalStorageDirectory()+"/SqrfactorImages/"+fileName);
        Uri uri = Uri.fromFile(file);
        coverImagePath = FileUtils.getPath(this, uri);
       // coverImagePath=FileUtils.getFile(this,uri);

        Log.v("coverimagepathdownload",coverImagePath);


        ImageRequest ir = new ImageRequest(imageUri, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imageViewFormServer.setVisibility(View.VISIBLE);
                imageViewFormServer.setImageBitmap(response);

            }
        }, 100, 100, null, null);

        mRequestQueue.add(ir);


    }



//    private void SetImageToDisplayAndDownload(String imageUri) {
//
//        imageViewFormServer.setVisibility(View.VISIBLE);
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
//                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
//                .diskCacheExtraOptions(480, 800, null)
//                .threadPoolSize(3) // default
//                .threadPriority(Thread.NORM_PRIORITY - 2) // default
//                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
//                .denyCacheImageMultipleSizesInMemory()
//                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
//                .memoryCacheSize(2 * 1024 * 1024)
//                .memoryCacheSizePercentage(13) // default
//                .diskCache(new UnlimitedDiskCache(getCacheDir())) // default
//                .diskCacheSize(50 * 1024 * 1024)
//                .diskCacheFileCount(100)
//                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
//                .imageDownloader(new BaseImageDownloader(getApplicationContext())) // default
//                .imageDecoder(new BaseImageDecoder(true)) // default
//                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
//                .writeDebugLogs()
//                .build();
//
//        imageLoader.getInstance().init(config);
//        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .resetViewBeforeLoading(false) // default
//                .delayBeforeLoading(1000)
//                .cacheInMemory(true) // default
//                .considerExifParams(false) // default
//                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
//                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
//                .displayer(new SimpleBitmapDisplayer()) // default
//                .handler(new Handler()) // default
//                .build();
//
//        imageLoader.displayImage(imageUri, imageViewFormServer, options, new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String imageUri, View view) {
//
//            }
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//
//            }
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//              Toast.makeText(getApplicationContext(),imageUri,Toast.LENGTH_LONG).show();
//            }
//            @Override
//            public void onLoadingCancelled(String imageUri, View view) {
//
//            }
//        }, new ImageLoadingProgressListener() {
//            @Override
//            public void onProgressUpdate(String imageUri, View view, int current, int total) {
//
//
//            }
//        });
//
//    }


    private void getIdDetails() {
//        /young-designers-award18/submit-design;
        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        Log.d(TAG, "getIdDetails: =-=-=-=-=-"+slug+ServerConstants.SUBMISSION_ID_DETAILS);
        StringRequest idDetailRequest = new StringRequest(Request.Method.GET, ServerConstants.SUBMISSION_ID_DETAILS + "/" + slug + "/submit-design",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: ================="+response);
                        try {
                            JSONObject detailResponse = new JSONObject(response);
                            JSONObject compResponse = detailResponse.getJSONObject("competition");
                            JSONObject compPermResponse = detailResponse.getJSONObject("competition_participation");
                            competitionId = compResponse.getString("id");
                            participationId = compPermResponse.getString("id");

                            Log.d(TAG, "........;;;;;;;;;;;;"+competitionId+participationId);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(SubmitActivity.this, "Error getting details for submission", Toast.LENGTH_SHORT).show();
                        }
                    }){
            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
                headers.put(getString(R.string.accept), getString(R.string.application_json));
                headers.put(getString(R.string.authorization), Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
//                headers.put("Content-Type", contentType);
                return headers;
            }
        };
        idDetailRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(idDetailRequest);
    }


    private void chooseCoverImageIntent() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, RESULT_COVER_IMAGE );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_PDF && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            mSelectedFilePath = FileUtils.getPath(SubmitActivity.this, uri);
            pdfPath = mSelectedFilePath;
            String[] newOne = mSelectedFilePath.split("/");
           // imageViewFormServer.setVisibility(View.VISIBLE);
            mPdfPathTV.setText((newOne[newOne.length - 1]));

        }
        else if (requestCode == RESULT_COVER_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            mSelectedCoverImagePath = FileUtils.getPath(this, uri);
            Log.d(TAG, "onActivityResult: selected cover image path = " + mSelectedCoverImagePath);

            coverImagePath = mSelectedCoverImagePath;
            String[] newOne = mSelectedCoverImagePath.split("/");
            mCoverImagePathTV.setText((newOne[newOne.length - 1]));
        }
    }


    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(SubmitActivity.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }




}
//storage/competition_submission/37693cfc748049e45d87b8c7d8b9aacd/cover_image/JKmjoKMgVYDiyETw3et5.jpg