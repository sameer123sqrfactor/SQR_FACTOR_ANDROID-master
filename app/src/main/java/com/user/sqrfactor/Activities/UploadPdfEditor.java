package com.user.sqrfactor.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import com.user.sqrfactor.Constants.BundleConstants;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Fragments.MDToast;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Utils.FileUtils;
import com.user.sqrfactor.Utils.NetworkUtil;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;



public class UploadPdfEditor extends AppCompatActivity {

    private static final String TAG = "SubmitActivity";
    private static final int RESULT_COVER_IMAGE = 1;
    private static final int RESULT_PDF = 2;
    Integer pageNumber = 0;
    private Toolbar mToolbar;
    private RelativeLayout mCoverImageButton;
    private TextView mCoverImagePathTV;
    private RelativeLayout mDesignPdfButton;
    private TextView mPdfPathTV;
    private Button mSubmitButton;
    private Button sqrfactor_editor_btn;
    private Button submit_pdf_btn;
    private WebView myWebView;
    private boolean isEditedSubmission=false;

    private ProgressBar mPb;
    private MySharedPreferences mSp;
    private LinearLayout mContentLayout;
    private RequestQueue mRequestQueue;
    private String mSelectedFilePath;
    private String mSelectedCoverImagePath;
    private LinearLayout Pdf_Upload_layout,sqrfactor_editor;

   // private PDFView pdfView;
    private String getPdfPathFromServer;
    private String slug;
    private String title;
    private String coverImagePath;
    private String pdfPath;
    private String competitionId;
    private String participationId;
    private String submissionId;
    private int pageIndex;
    private ImageView imageViewPdf;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor parcelFileDescriptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf_editor);

        mPb = findViewById(R.id.pb);
        mContentLayout = findViewById(R.id.Pdf_Upload_layout);
        pageIndex = 0;

       // myWebView=findViewById(R.id.webViewPdf);

        mSubmitButton=findViewById(R.id.submit_design);

       // pdfView=findViewById(R.id.pdfView);

       // imageViewPdf=findViewById(R.id.pdf_image);

        mSp = MySharedPreferences.getInstance(this);
        mPdfPathTV=findViewById(R.id.submit_design_pdf_path);
        mPdfPathTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePdfIntent();
            }
        });
        Intent intent=new Intent();

        //new design 23 1929 storage/competition_submission/37693cfc748049e45d87b8c7d8b9aacd/cover_image/akNbZDyhnV4mI81ZLjDM.jpg storage/competition_submission/37693cfc748049e45d87b8c7d8b9aacd/pdf/ciyUwdZBBaZnYS6NwOCa.pdf  734
        //new design 23 1929 /storage/emulated/0/DCIM/Camera/IMG_20190110_183648.jpg /data/user/0/com.user.sqrfactor/cache/documents/selfdeclaration(2).pdf  null
        if(intent!=null) {
            title=getIntent().getStringExtra("design_title");
            coverImagePath=getIntent().getStringExtra("design_cover_image");
            competitionId=getIntent().getStringExtra("competition_id");
            participationId=getIntent().getStringExtra("competition_participation_id");
            submissionId=getIntent().getStringExtra("competition_submission_id");
            slug = getIntent().getStringExtra("slug");

            getPdfPathFromServer=getIntent().getStringExtra("getPdfPathFromServer");
            if(getPdfPathFromServer!=null){
                isEditedSubmission=true;
                mSelectedFilePath = getPdfPathFromServer;
                pdfPath = mSelectedFilePath;

                String[] newOne = mSelectedFilePath.split("/");
                mPdfPathTV.setText((newOne[newOne.length - 1]));

                File file = new File(Environment.getExternalStorageDirectory()+"/SqrfactorPdf/"+(newOne[newOne.length - 1]));
                Uri uri = Uri.fromFile(file);
                pdfPath = FileUtils.getPath(this, uri);


                Log.v("downloadespdfpath",pdfPath+"");

                getPdfPathFromServer="https://sqrfactor.com/"+getPdfPathFromServer;
                myWebView.setVisibility(View.VISIBLE);
                myWebView.setWebViewClient(new WebViewClient());
                myWebView.setWebChromeClient(new WebChromeClient(){});
                myWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
                myWebView.getSettings().setJavaScriptEnabled(true);
                myWebView.getSettings().setLoadWithOverviewMode(true);
                myWebView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + getPdfPathFromServer);


            }

        }

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditedSubmission) {
                    submitEditedDesignSubmission();
                } else {
                    submissionDesignSaveApi();
                }

            }
        });
    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    protected void onStart() {
//        super.onStart();
//        try {
//            openRenderer(getApplicationContext(),"testing.pdf");
//            showPage(pageIndex);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    public void onStop() {
//        try {
//            closeRenderer();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        super.onStop();
//    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @OnClick(R.id.button_pre_doc)
//    public void onPreviousDocClick(){
//        showPage(currentPage.getIndex() - 1);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @OnClick(R.id.button_next_doc)
//    public void onNextDocClick(){
//        showPage(currentPage.getIndex() + 1);
//    }




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openRenderer(Context context,Uri uri) throws IOException {
        // In this sample, we read a PDF from the assets directory.

      //  File file = new File(context.getCacheDir(), "testing.pdf");


        //File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+fileName);

        //File file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+);

//        Uri fileurl1 = (Uri.fromFile(file1));
//        Uri fileurl2 = (Uri.fromFile(file));
//        Log.v("uploadPdf1",file.toString()+"**"+file1.toString()+"***"+fileurl1+"***"+fileurl2);
//


//
//        if (!file.exists()) {
//            // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
//            // the cache directory.
//            InputStream asset = context.getAssets().open("testing.pdf");
//
//
//            FileOutputStream output = new FileOutputStream(file);
//            final byte[] buffer = new byte[1024];
//            int size;
//            while ((size = asset.read(buffer)) != -1) {
//                output.write(buffer, 0, size);
//            }
//            asset.close();
//            output.close();
//        }
//        parcelFileDescriptor = ParcelFileDescriptor.open(uri, ParcelFileDescriptor.MODE_READ_ONLY);
//        // This is the PdfRenderer we use to render the PDF.
//        if (parcelFileDescriptor != null) {
//            pdfRenderer = new PdfRenderer(parcelFileDescriptor);
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void closeRenderer() throws IOException {
        if (null != currentPage) {
            currentPage.close();
        }
        pdfRenderer.close();
        parcelFileDescriptor.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showPage(int index) {
        if (pdfRenderer.getPageCount() <= index) {
            return;
        }
        // Make sure to close the current page before opening another one.
        if (null != currentPage) {
            currentPage.close();
        }
        // Use `openPage` to open a specific page in PDF.
        currentPage = pdfRenderer.openPage(index);
        // Important: the destination bitmap must be ARGB (not RGB).
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(),
                Bitmap.Config.ARGB_8888);
        // Here, we render the page onto the Bitmap.
        // To render a portion of the page, use the second and third parameter. Pass nulls to get
        // the default result.
        // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        // We are ready to show the Bitmap to user.
        imageViewPdf.setImageBitmap(bitmap);
        updateUi();
    }

    /**
     * Updates the state of 2 control buttons in response to the current page index.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateUi() {
        int index = currentPage.getIndex();
        int pageCount = pdfRenderer.getPageCount();
       // prePageButton.setEnabled(0 != index);
        //nextPageButton.setEnabled(index + 1 < pageCount);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int getPageCount() {
        return pdfRenderer.getPageCount();
    }




    private void submitEditedDesignSubmission() {
        mPb.setVisibility(View.VISIBLE);
        mContentLayout.setVisibility(View.GONE);

        if (!NetworkUtil.isNetworkAvailable()) {
            MDToast.makeText(UploadPdfEditor.this, "No internet", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
            mPb.setVisibility(View.GONE);
            mContentLayout.setVisibility(View.VISIBLE);
            return;
        }

        try {
            final String uploadId = UUID.randomUUID().toString();

            final MultipartUploadRequest request = new MultipartUploadRequest(this, uploadId, ServerConstants.SUBMISSION_EDITED_DESIGN_SAVE);
            Log.i(TAG, "onProgress2: "+title+" "+competitionId+" "+participationId+" "+coverImagePath+" "+
            pdfPath+"  "+submissionId);
            request.addParameter("design_title",title)
                    .addParameter("competition_id",competitionId)
                    .addParameter("competition_participation_id", participationId)
                    .addParameter("submission_id", submissionId+"")
                    .addParameter("design_body", "")
                    .addFileToUpload(coverImagePath,"design_cover_image")
                    .addFileToUpload(pdfPath,"design_pdf")
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

                            MDToast.makeText(UploadPdfEditor.this, serverResponse.getBodyAsString(), MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                            Log.d(TAG, "onError: server response = " + serverResponse.getBodyAsString());
                            if(exception != null)
                                Log.d(TAG, "onError: exception = " + exception.toString());
                            else
                                MDToast.makeText(UploadPdfEditor.this, "Error uploading documents", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                            mPb.setVisibility(View.GONE);
                            mContentLayout.setVisibility(View.VISIBLE);

                            Log.d(TAG, "onCompleted: upload successful");
                            Log.d(TAG, "onCompleted: " + serverResponse.getBodyAsString());

                            try {
                                MDToast.makeText(UploadPdfEditor.this, serverResponse.getBodyAsString(), MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                                JSONObject responseObject = new JSONObject(serverResponse.getBodyAsString());
                                String sl = responseObject.getString("message");

                                if(sl.equals("success"))
                                {
                                    MDToast.makeText(UploadPdfEditor.this, "Design Upload Successful", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                    startActivity(new Intent(UploadPdfEditor.this,CompetitionDetailActivity.class)
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

    private void choosePdfIntent() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("application/pdf");
        startActivityForResult(i, RESULT_PDF );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_PDF && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();




            mSelectedFilePath = FileUtils.getPath(UploadPdfEditor.this, uri);

            pdfPath = mSelectedFilePath;



            String[] newOne=null;
            if(mSelectedFilePath.length()>0){
                newOne= mSelectedFilePath.split("/");
            }

            mPdfPathTV.setText((newOne[newOne.length - 1]));

            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+(newOne[newOne.length - 1]));
//            Uri fileurl = (Uri.fromFile(file));
            Log.v("uploadPdf",uri.toString()+" **"+pdfPath+"***"+(newOne[newOne.length - 1]));

            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

//            Uri apkURI = FileProvider.getUriForFile(
//                    this,
//                    getApplicationContext()
//                            .getPackageName() + ".provider", file);
            install.setDataAndType(uri,"application/pdf");
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
// End New Approach
            startActivity(install);

        }
    }

    private void submissionDesignSaveApi() {
        mPb.setVisibility(View.VISIBLE);
        mContentLayout.setVisibility(View.GONE);

        if (!NetworkUtil.isNetworkAvailable()) {
            MDToast.makeText(UploadPdfEditor.this, "No internet", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
            mPb.setVisibility(View.GONE);
            mContentLayout.setVisibility(View.VISIBLE);
            return;
        }

        try {
            final String uploadId = UUID.randomUUID().toString();
            Log.i(TAG, "onProgress1: "+title+" "+competitionId+" "+participationId+" "+coverImagePath+" "+
                    pdfPath+"  "+submissionId);

            final MultipartUploadRequest request = new MultipartUploadRequest(this, uploadId, ServerConstants.SUBMISSION_DESIGN_SAVE);
            request.addParameter("design_title",title)
                    .addParameter("competition_id",competitionId)
                    .addParameter("competition_participation_id", participationId)
                    .addFileToUpload(coverImagePath,"design_cover_image")
                    .addFileToUpload(pdfPath,"design_pdf")
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


                            if(exception != null)
                                Log.d(TAG, "onError: exception = " + exception.toString());
                            else
                                MDToast.makeText(UploadPdfEditor.this, "Error uploading documents", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                            mPb.setVisibility(View.GONE);
                            mContentLayout.setVisibility(View.VISIBLE);

//                            Log.d(TAG, "onCompleted: upload successful");
//                            Log.d(TAG, "onCompleted: " + serverResponse.getBodyAsString());

                            try {
                                JSONObject responseObject = new JSONObject(serverResponse.getBodyAsString());
                                String sl = responseObject.getString("message");
                                if(sl.equals("success"))
                                {
                                    MDToast.makeText(UploadPdfEditor.this, "Design Upload Successful", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                    startActivity(new Intent(UploadPdfEditor.this,CompetitionDetailActivity.class)
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
}
