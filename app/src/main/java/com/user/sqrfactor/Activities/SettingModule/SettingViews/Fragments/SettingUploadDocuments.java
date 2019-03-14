package com.user.sqrfactor.Activities.SettingModule.SettingViews.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.user.sqrfactor.Activities.ProfileActivity;
import com.user.sqrfactor.Activities.UploadDocumentActivity;
import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Fragments.MDToast;
import com.user.sqrfactor.Pojo.DocumentDataClass;
import com.user.sqrfactor.Pojo.TokenClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Utils.FileUtils;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingUploadDocuments.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SettingUploadDocuments extends Fragment {

    private OnFragmentInteractionListener mListener;
    public static final int TAKE_PHOTO = 1;
    private static final String TAG = "UploadDocumentActivity";
    public static final int CHOSSE_FROM_GALLARY = 2;
    private Uri camaraPicUri=null;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3;
    public  static final int RequestPermissionCode  = 4 ;
    private boolean isCompanyRegistrationClicked=false, idIdentityProofClicked=false,isGraduationCertificateClicked=false,isArchitectLicense=false;
    private MySharedPreferences mSp;
    private Bitmap bitmap;
    private String mSelectedCoverImagePathIdentity=null,mSelectedCoverImagePathGraduation=null,mSelectedCoverImagePathLicense=null,
            mSelectedCoverImagePathRegistration=null;
    private String mSelectedCoverImagePath1,mSelectedCoverImagePath2,mSelectedCoverImagePath3;
    private Button uploadDocumentBtn;
    private ImageView image_identity_proof,icon_identity_proof,image_graduation_certificate,icon_graduation_certificate,
            image_architect_license,icon_architect_license,image_company_registration_certificate,icon_image_company_registration_certificate;
    private ImageView identity_proof_mage,graduation_certificate_image,architect_license_image,company_registration_certificate_image;
    private TextView text_identity_proof,text_graduation_certificate,text_architect_license,text_company_registration_certificate;
    private RelativeLayout submit_identity_proof_document,submit_graduation_certificate_document,submit_architect_license_document
            ,submit_company_registration_certificate;
    private FrameLayout identity_proof_image_frame,graduation_certificate_image_frame,architect_license_image_frame,company_registration_image_frame;

    private Toolbar toolbar;
    private ImageButton identity_proof_image_remove,graduation_certificate_image_remove,architect_license_image_remove
            ,company_registration_image_remove;

    private Tracker mTracker;
    private boolean isEdit=false;
    private Context context;


    public SettingUploadDocuments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_setting_upload_documents, container, false);

        mSp = MySharedPreferences.getInstance(context);
//        toolbar = (Toolbar) view.findViewById(R.id.basic_toolbar);
//        toolbar.setTitle("Document Verification");
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.back_arrow);


        //google analytics code
        MyApplication application = (MyApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        submit_identity_proof_document=view.findViewById(R.id.submit_identity_proof_document);
        submit_graduation_certificate_document=view.findViewById(R.id.submit_graduation_certificate_document);
        submit_architect_license_document=view.findViewById(R.id.submit_architect_license_document);
        submit_company_registration_certificate=view.findViewById(R.id.submit_company_registration_certificate);
        Intent intent=getActivity().getIntent();

        identity_proof_image_remove=view.findViewById(R.id.identity_proof_image_remove);
        graduation_certificate_image_remove=view.findViewById(R.id.graduation_certificate_image_remove);
        architect_license_image_remove=view.findViewById(R.id.architect_license_image_remove);
        company_registration_image_remove=view.findViewById(R.id.company_registration_image_remove);



        identity_proof_image_frame=view.findViewById(R.id.identity_proof_image_frame);
        graduation_certificate_image_frame=view.findViewById(R.id.graduation_certificate_image_frame);
        architect_license_image_frame=view.findViewById(R.id.architect_license_image_frame);
        company_registration_image_frame=view.findViewById(R.id.company_registration_image_frame);

        uploadDocumentBtn=view.findViewById(R.id.uploadDocument);


        image_identity_proof=view.findViewById(R.id.image_identity_proof);
        icon_identity_proof=view.findViewById(R.id.icon_identity_proof);
        image_graduation_certificate=view.findViewById(R.id.image_graduation_certificate);
        icon_graduation_certificate=view.findViewById(R.id.icon_graduation_certificate);
        image_architect_license=view.findViewById(R.id.image_architect_license);
        icon_architect_license=view.findViewById(R.id.icon_architect_license);
        image_company_registration_certificate=view.findViewById(R.id.image_company_registration_certificate);
        icon_image_company_registration_certificate=view.findViewById(R.id.icon_image_company_registration_certificate);

        identity_proof_mage=view.findViewById(R.id.identity_proof_image);
        graduation_certificate_image=view.findViewById(R.id.graduation_certificate_image);
        architect_license_image=view.findViewById(R.id.architect_license_image);
        company_registration_certificate_image=view.findViewById(R.id.company_registration_certificate_image);


        text_identity_proof=view.findViewById(R.id.text_identity_proof);
        text_graduation_certificate=view.findViewById(R.id.text_graduation_certificate);
        text_architect_license=view.findViewById(R.id.text_architect_license);
        text_company_registration_certificate=view.findViewById(R.id.text_company_registration_certificate);



//        submit_identity_proof_document.setVisibility(View.VISIBLE);
//                submit_graduation_certificate_document.setVisibility(View.VISIBLE);
//              //  submit_architect_license_document.setVisibility(View.VISIBLE);
//                submit_company_registration_certificate.setVisibility(View.GONE);
//        isEdit=true;

        if(intent!=null && intent.hasExtra("isIndividuals")){
            if(intent.getBooleanExtra("isIndividuals",true)){
                submit_identity_proof_document.setVisibility(View.VISIBLE);
                submit_graduation_certificate_document.setVisibility(View.VISIBLE);
                //  submit_architect_license_document.setVisibility(View.VISIBLE);
                submit_company_registration_certificate.setVisibility(View.GONE);

            }else {
                submit_identity_proof_document.setVisibility(View.GONE);
                submit_graduation_certificate_document.setVisibility(View.GONE);
                //  submit_architect_license_document.setVisibility(View.VISIBLE);
                submit_company_registration_certificate.setVisibility(View.VISIBLE);
            }
        }





        // bindEditableDataToView
        GetDocumentDataFromServer();


        identity_proof_image_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_identity_proof.setText("Identity Proof");
                identity_proof_mage.setImageDrawable(null);
                identity_proof_image_frame.setVisibility(View.GONE);
            }
        });
        graduation_certificate_image_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_graduation_certificate.setText("Graduation Certificate");
                graduation_certificate_image.setImageDrawable(null);
                graduation_certificate_image_frame.setVisibility(View.GONE);
            }
        });

        architect_license_image_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_architect_license.setText("Architect License");
                architect_license_image.setImageDrawable(null);
                architect_license_image_frame.setVisibility(View.GONE);
            }
        });

        company_registration_image_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_company_registration_certificate.setText("Company Registration Certificate");
                company_registration_certificate_image.setImageDrawable(null);
                company_registration_image_frame.setVisibility(View.GONE);
            }
        });





        text_identity_proof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IdentityClicked();
            }
        });
        image_identity_proof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IdentityClicked();
            }
        });

        icon_identity_proof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IdentityClicked();
            }
        });


        text_graduation_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraduationCertificateClicked();
            }
        });
        image_graduation_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraduationCertificateClicked();
            }
        });

        icon_graduation_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraduationCertificateClicked();
            }
        });



        text_architect_license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArchitectLicenseCliked();
            }
        });

        image_architect_license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArchitectLicenseCliked();
            }
        });

        icon_architect_license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArchitectLicenseCliked();
            }
        });

        text_company_registration_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompanyRegistrationClicked();
            }
        });


        image_company_registration_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompanyRegistrationClicked();
            }
        });

        icon_image_company_registration_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompanyRegistrationClicked();
            }
        });





        uploadDocumentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                identity_proof_mage=findViewById(R.id.identity_proof_image);
//                graduation_certificate_image=findViewById(R.id.graduation_certificate_image);
//                architect_license_image=findViewById(R.id.architect_license_image);

                if(isEdit){
                    UploadEditedDocumetToServer();
                }
                else {
                    if(identity_proof_mage.getDrawable()!= null && graduation_certificate_image.getDrawable()!= null && architect_license_image.getDrawable()!= null){
                        Bitmap bitmap1 = ((BitmapDrawable)identity_proof_mage.getDrawable()).getBitmap();
                        Bitmap bitmap2 = ((BitmapDrawable)graduation_certificate_image.getDrawable()).getBitmap();
                        Bitmap bitmap3 = ((BitmapDrawable)architect_license_image.getDrawable()).getBitmap();

                        UploadDocumetToServer(getStringImage(bitmap1),getStringImage(bitmap2),getStringImage(bitmap3));
                    }else if(architect_license_image.getDrawable()!= null && company_registration_certificate_image.getDrawable()!=null) {
//                    Bitmap bitmap1 = ((BitmapDrawable)identity_proof_mage.getDrawable()).getBitmap();
//                    Bitmap bitmap2 = ((BitmapDrawable)graduation_certificate_image.getDrawable()).getBitmap();
                        Bitmap bitmap1 = ((BitmapDrawable)architect_license_image.getDrawable()).getBitmap();
                        Bitmap bitmap2 = ((BitmapDrawable)company_registration_certificate_image.getDrawable()).getBitmap();

                        UploadCompanyDocumetToServer(getStringImage(bitmap1),getStringImage(bitmap2));
                        //MDToast.makeText(UploadDocumentActivity.getApplicationContext(), "Select all the three documents", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                    }else {
                        MDToast.makeText(getApplicationContext(), "Select all the three documents", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                    }
                }



            }
        });

        return view;
    }


    private void GetDocumentDataFromServer() {

        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest myReq = new StringRequest(Request.Method.GET,"https://sqrfactor.com/api/profile/get_documents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            DocumentDataClass documentDataClass=new DocumentDataClass(jsonObject.getJSONObject("data"));
                            BindDataToView(documentDataClass);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismissing the progress dialog
                        // loading.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),"TimeOut" ,Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(getApplicationContext(),"aurherror" ,Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(getApplicationContext(),"servererroo" ,Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            //TODO
                            Toast.makeText(getApplicationContext(),"network error" ,Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(getApplicationContext(),"pareserError" ,Toast.LENGTH_LONG).show();
                        }


//
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Authorization","Bearer "+ TokenClass.Token);
                //  params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
                return params;
            }

        };

        //Adding request to the queue

        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);
    }


    private void BindDataToView(DocumentDataClass documentDataClass) {

        if(documentDataClass.getId_proof()!=null && !documentDataClass.getId_proof().equals("null")){

            isEdit=true;
            String[] imagePath=documentDataClass.getId_proof().split("/");
            identity_proof_image_frame.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(documentDataClass.getId_proof())
                    .into(identity_proof_mage);
            text_identity_proof.setText(imagePath[imagePath.length-1]);

        }
        if(documentDataClass.getGraduation_proof()!=null && !documentDataClass.getGraduation_proof().equals("null")){
            isEdit=true;
            String[] imagePath=documentDataClass.getGraduation_proof().split("/");
            graduation_certificate_image_frame.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(documentDataClass.getGraduation_proof())
                    .into(graduation_certificate_image);
            text_graduation_certificate.setText(imagePath[imagePath.length-1]);
        }
        if(documentDataClass.getArchitect_proof()!=null && !documentDataClass.getArchitect_proof().equals("null")){
            isEdit=true;
            String[] imagePath=documentDataClass.getArchitect_proof().split("/");
            architect_license_image_frame.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(documentDataClass.getArchitect_proof())
                    .into(architect_license_image);
            text_architect_license.setText(imagePath[imagePath.length-1]);
        }
        if(documentDataClass.getRegistration_certificate()!=null && !documentDataClass.getRegistration_certificate().equals("null")){
            isEdit=true;
            String[] imagePath=documentDataClass.getRegistration_certificate().split("/");
            company_registration_image_frame.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(documentDataClass.getRegistration_certificate())
                    .into(company_registration_certificate_image);
            text_company_registration_certificate.setText(imagePath[imagePath.length-1]);

        }

    }


    private void IdentityClicked(){
        idIdentityProofClicked=true;
        isCompanyRegistrationClicked=false;
        isArchitectLicense=false;
        isGraduationCertificateClicked=false;

        CallImagePickerAfterPermission();

    }

    private void GraduationCertificateClicked(){
        isGraduationCertificateClicked=true;
        idIdentityProofClicked=false;
        isCompanyRegistrationClicked=false;
        isArchitectLicense=false;

        CallImagePickerAfterPermission();
    }

    private void ArchitectLicenseCliked(){
        isArchitectLicense=true;
        isGraduationCertificateClicked=false;
        idIdentityProofClicked=false;
        isCompanyRegistrationClicked=false;

        CallImagePickerAfterPermission();
    }
    private void CompanyRegistrationClicked(){
        isCompanyRegistrationClicked=true;
        isArchitectLicense=false;
        isGraduationCertificateClicked=false;
        idIdentityProofClicked=false;

        CallImagePickerAfterPermission();
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public void CallImagePickerAfterPermission(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                android.Manifest.permission.CAMERA))
        {

            Toast.makeText(getApplicationContext(),"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(getActivity(),new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);

        }
        pickImage("IdentityProof");
    }


    public void pickImage(String imageType) {


        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
//
//                    Intent pictureIntent = new Intent(
//                            MediaStore.ACTION_IMAGE_CAPTURE
//                    );
//                    if(pictureIntent.resolveActivity(getPackageManager()) != null) {
//                        startActivityForResult(pictureIntent,
//                                1);
//                    }

                }

                else if (options[item].equals("Choose from Gallery")) {

                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }

                else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode ,Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {


            if (requestCode == 1) {
                //  Uri selectedImage = data.getData();
                File f = new File(Environment.getExternalStorageDirectory().toString());

                for (File temp : f.listFiles()) {

                    if (temp.getName().equals("temp.jpg")) {

                        f = temp;

                        break;

                    }

                }

                try {

                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);


                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";




                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    //Uri selectedImage = data.getData();
                    // displayImage.setImageBitmap(bitmap);
                    if(idIdentityProofClicked){
                        identity_proof_image_frame.setVisibility(View.VISIBLE);
                        identity_proof_mage.setImageBitmap(bitmap);
                        text_identity_proof.setText(path);
                        //mSelectedCoverImagePathIdentity =FileUtils.getPath(getApplicationContext(), selectedImage);
                    }else if(isGraduationCertificateClicked){
                        graduation_certificate_image_frame.setVisibility(View.VISIBLE);
                        graduation_certificate_image.setImageBitmap(bitmap);
                        text_graduation_certificate.setText(path);
                        // mSelectedCoverImagePathGraduation = file.getAbsolutePath();//FileUtils.getPath(getApplicationContext(),  i.getData());//f.getAbsolutePath();//FileUtils.getPath(getApplicationContext(), data.getData());;
                    }else if(isArchitectLicense){


                        architect_license_image_frame.setVisibility(View.VISIBLE);
                        architect_license_image.setImageBitmap(bitmap);
                        text_architect_license.setText(path);
                        //mSelectedCoverImagePathLicense =file.getAbsolutePath(); //FileUtils.getPath(getApplicationContext(),  i.getData());

                    }else if(isCompanyRegistrationClicked){
                        company_registration_image_frame.setVisibility(View.VISIBLE);
                        company_registration_certificate_image.setImageBitmap(bitmap);
                        text_company_registration_certificate.setText(path);
                        //mSelectedCoverImagePathRegistration = file.getAbsolutePath();//FileUtils.getPath(getApplicationContext(),  i.getData());//FileUtils.getPath(getApplicationContext(), data.getData());;
                    }


                    Log.d(TAG, "onActivityResult:Camara "+path+" "+data.getData()+" "+Uri.fromFile(file));
                    try {

                        outFile = new FileOutputStream(file);

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);

                        outFile.flush();

                        outFile.close();

                    } catch (FileNotFoundException e) {

                        e.printStackTrace();

                    } catch (IOException e) {

                        e.printStackTrace();

                    } catch (Exception e) {

                        e.printStackTrace();

                    }

                } catch (Exception e) {

                    e.printStackTrace();

                }

            } else if (requestCode == 2) {



                Uri selectedImage = data.getData();

                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = context.getContentResolver().query(selectedImage,filePath, null, null, null);

                c.moveToFirst();

                int columnIndex = c.getColumnIndex(filePath[0]);

                String picturePath = c.getString(columnIndex);
                String[] fileName = picturePath.split("/");
                c.close();

                bitmap = (BitmapFactory.decodeFile(picturePath));


                // displayImage.setImageBitmap(bitmap);
                if(idIdentityProofClicked){
                    identity_proof_image_frame.setVisibility(View.VISIBLE);
                    identity_proof_mage.setImageBitmap(bitmap);
                    text_identity_proof.setText(fileName[fileName.length-1]);
                    mSelectedCoverImagePathIdentity = FileUtils.getPath(getApplicationContext(), selectedImage);
                }else if(isGraduationCertificateClicked){
                    graduation_certificate_image_frame.setVisibility(View.VISIBLE);
                    graduation_certificate_image.setImageBitmap(bitmap);
                    text_graduation_certificate.setText(fileName[fileName.length-1]);
                    mSelectedCoverImagePathGraduation = FileUtils.getPath(getApplicationContext(), selectedImage);
                }else if(isArchitectLicense){

                    mSelectedCoverImagePathLicense = FileUtils.getPath(getApplicationContext(), selectedImage);
                    architect_license_image_frame.setVisibility(View.VISIBLE);
                    architect_license_image.setImageBitmap(bitmap);
                    text_architect_license.setText(fileName[fileName.length-1]);
                }else if(isCompanyRegistrationClicked){

                    mSelectedCoverImagePathRegistration = FileUtils.getPath(getApplicationContext(), selectedImage);
                    company_registration_image_frame.setVisibility(View.VISIBLE);
                    company_registration_certificate_image.setImageBitmap(bitmap);
                    text_company_registration_certificate.setText(fileName[fileName.length-1]);

                }
                // displayImage.setImageBitmap(bitmap);

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




    private void UploadDocumetToServer(final String identity_proof, final String graduation_certificate, final String architecture_license) {

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(context,"Uploading...","Please wait...",false,false);

        try{
            final String uploadId = UUID.randomUUID().toString();

            final MultipartUploadRequest request = new MultipartUploadRequest(getApplicationContext(), uploadId, ServerConstants.DOCUMENT_UPLOAD );

            request.addFileToUpload(mSelectedCoverImagePathIdentity,"id_proof")
                    .addFileToUpload(mSelectedCoverImagePathGraduation,"graduation_proof")
                    .addFileToUpload(mSelectedCoverImagePathLicense,"architect_proof")
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .setDelegate(new UploadStatusDelegate() {

                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {
                            //Log.i(TAG, "onProgress: ");

                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
//                        mPb.setVisibility(View.GONE);
//                        mContentLayout.setVisibility(View.VISIBLE);

                            loading.cancel();
                            MDToast.makeText(getApplicationContext(), serverResponse.getBodyAsString(), MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                            // Log.d(TAG, "onError: server response = " + serverResponse.getBodyAsString());
                            if(exception != null)
                                Log.d(TAG, "onError: exception = " + exception.toString());
                            else
                                MDToast.makeText(getApplicationContext(), "Error uploading documents", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
////                        mPb.setVisibility(View.GONE);
////                        mContentLayout.setVisibility(View.VISIBLE);
//
//                        Log.d(TAG, "onCompleted: upload successful");
//                        Log.d(TAG, "onCompleted: " + serverResponse.getBodyAsString());

                            loading.cancel();
                            try {
                                // MDToast.makeText(getApplicationContext(), serverResponse.getBodyAsString(), MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                                JSONObject responseObject = new JSONObject(serverResponse.getBodyAsString());
                                String sl = responseObject.getString("message");

                                if(sl.equals("success")) {

                                    MDToast.makeText(getApplicationContext(), "Design Upload Successful", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                    Intent intent=new Intent(getApplicationContext(), ProfileActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                    //finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {
                            Log.i(TAG, "onCancelled: ");
                        }
                    });
            request.addHeader("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY))
                    .addHeader("Accept", "application/json")
                    .startUpload();


        } catch (
                MalformedURLException e) {
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void UploadEditedDocumetToServer() {


        //Toast.makeText(getApplicationContext(),"edited apis called",Toast.LENGTH_LONG).show();
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getApplicationContext(),"Uploading...","Please wait...",false,false);

        try{
            final String uploadId = UUID.randomUUID().toString();

            final MultipartUploadRequest request = new MultipartUploadRequest(getApplicationContext(), uploadId,ServerConstants.EDITED_DOCUMENT_UPLOAD );

            if(mSelectedCoverImagePathIdentity!=null){
                request.addFileToUpload(mSelectedCoverImagePathIdentity,"id_proof");
            }
            if(mSelectedCoverImagePathGraduation!=null){
                request.addFileToUpload(mSelectedCoverImagePathGraduation,"graduation_proof");
            }
            if(mSelectedCoverImagePathLicense!=null){
                request.addFileToUpload(mSelectedCoverImagePathLicense,"architect_proof");
            }
            if(mSelectedCoverImagePathRegistration!=null){
                request.addFileToUpload(mSelectedCoverImagePathRegistration,"registration_certificate");
            }



            request.setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .setDelegate(new UploadStatusDelegate() {

                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {
                            //Log.i(TAG, "onProgress: ");

                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
//                        mPb.setVisibility(View.GONE);
//                        mContentLayout.setVisibility(View.VISIBLE);

                            loading.cancel();
                            MDToast.makeText(getApplicationContext(), serverResponse.getBodyAsString(), MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                            Log.d(TAG, "onError: "+serverResponse.getBodyAsString());
                            // Log.d(TAG, "onError: server response = " + serverResponse.getBodyAsString());
                            if(exception != null)
                                Log.d(TAG, "onError: exception = " + exception.toString());
                            else
                                MDToast.makeText(getApplicationContext(), "Error uploading documents", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
////                        mPb.setVisibility(View.GONE);
////                        mContentLayout.setVisibility(View.VISIBLE);
//
//                        Log.d(TAG, "onCompleted: upload successful");
//                        Log.d(TAG, "onCompleted: " + serverResponse.getBodyAsString());

                            loading.cancel();
                            MDToast.makeText(getApplicationContext(), "Design Upload Successful", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();

                            try {
                                // MDToast.makeText(getApplicationContext(), serverResponse.getBodyAsString(), MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                                JSONObject responseObject = new JSONObject(serverResponse.getBodyAsString());
                                String sl = responseObject.getString("message");

                                if(sl.equals("success")) {

//                                MDToast.makeText(getApplicationContext(), "Design Upload Successful", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                    Intent intent=new Intent(context,ProfileActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                    //finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {
                            Log.i(TAG, "onCancelled: ");
                        }
                    });
            request.addHeader("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY))
                    .addHeader("Accept", "application/json")
                    .startUpload();
//
//  request.addHeader("Authorization", Constants.AUTHORIZATION_HEADER + "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjE2MGI0MTgxODQ2YmExOWRlMjkwNmY0Nzk4NTczOTQxZDc0NmI1ZWVkYWRiNTAyOWE1MWQxOGI3MGExODA3YWU3MjI2MWVhOTUyYzJmMWYzIn0.eyJhdWQiOiIzIiwianRpIjoiMTYwYjQxODE4NDZiYTE5ZGUyOTA2ZjQ3OTg1NzM5NDFkNzQ2YjVlZWRhZGI1MDI5YTUxZDE4YjcwYTE4MDdhZTcyMjYxZWE5NTJjMmYxZjMiLCJpYXQiOjE1NTAxMzAzMTgsIm5iZiI6MTU1MDEzMDMxOCwiZXhwIjoxNTgxNjY2MzE4LCJzdWIiOiI1NDEyIiwic2NvcGVzIjpbXX0.oCOq4e0iJNoppcAoepHWbCsAFg-KQAyPFo1QGgEMfOjuSTSIrxcaGx0c2iHVJyBT4oQzaUbfNmLMk8dpPL9Q5Mrb7n-cTohCwnx7GOr_YWPaNbzRGW2P7d6npzGSoPZ8z2Ub26Eh81joKfB-mfyGWp_-2OSUhzhFw-3vMcziyCKktCH_nBfoH_I2UIa1i65K-5ig1N1bcHQlM9Z2n6UBKDClypggcKs7D8oeY9XBDPPDvaJPoXDh3xo1lFRXa_1cKKvCa60dgfTwzZFn89kuOfpSGTcQmN6ly_VLhgllkajXtP1ksWof-mTKAOT3bWczsVJwstgCZA8E3jSkpvV8QftLcKpHNhq7Y7Dmj9s6hEGWtCWn1cjKaEc2vODqbt-0t3jRXX8j6K_-gkdne0Ii1J0MRBllFRFb73xzxM-Ce8NqdvrIMAquz0diMdnkkGPLeejo26peoC5cBr6OEKolaNkPI-VvuTia8b6yb7dU3nl3UgP0OvZtFtZCYmz9FLr52Fu5Sre7df56yzXOxaWWNC7qIEphI5ZXQzXG7Te0MppX_dJRJ5xzIE6tKfzDTmr7hW6Itaftckeb6IbUqkmllcZNWRgwMLIyDuZjm88SmVJ68JZYNGepk-OkVCwFrspAb08dCREWJdAeU3RNOlMsRTuPMQj0zCG1omVXj_GlTZo")
//                .addHeader("Accept", "application/json")
//                .startUpload();



        } catch (
                MalformedURLException e) {
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void UploadCompanyDocumetToServer(final String architecture_license,final String registration_certificate) {

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getApplicationContext(),"Uploading...","Please wait...",false,false);
        //Creating a Request Queue
        try{
            final String uploadId = UUID.randomUUID().toString();

            final MultipartUploadRequest request = new MultipartUploadRequest(getApplicationContext(), uploadId,ServerConstants.DOCUMENT_UPLOAD);

            request.addFileToUpload(mSelectedCoverImagePathRegistration,"registration_certificate")
                    .addFileToUpload(mSelectedCoverImagePathLicense,"architect_proof")
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .setDelegate(new UploadStatusDelegate() {

                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {
                            //Log.i(TAG, "onProgress: ");
                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
//                        mPb.setVisibility(View.GONE);
//                        mContentLayout.setVisibility(View.VISIBLE);

                            loading.cancel();
                            MDToast.makeText(getApplicationContext(), serverResponse.getBodyAsString(), MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
//                        mPb.setVisibility(View.GONE);
//                        mContentLayout.setVisibility(View.VISIBLE);

                            loading.cancel();
                            try {
                                //  MDToast.makeText(getApplicationContext(), serverResponse.getBodyAsString(), MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                                JSONObject responseObject = new JSONObject(serverResponse.getBodyAsString());
                                String sl = responseObject.getString("message");


                                MDToast.makeText(getApplicationContext(), "Design Upload Successful", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                                if(sl.equals("success"))
                                {
//                                    MDToast.makeText(getApplicationContext(), "Design Upload Successful", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
//                                    Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
//                                    startActivity(intent);
//                                    finish();
                                    //finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {
                            Log.i(TAG, "onCancelled: ");
                        }
                    });
            request.addHeader("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY))
                    .addHeader("Accept", "application/json")
                    .startUpload();



        } catch (
                MalformedURLException e) {
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        UserClass userClass= UtilsClass.GetUserClassFromSharedPreferences(getApplicationContext());
//        if(userClass!=null){
//            mTracker.setScreenName("UploadDocumentActivity /"+" /"+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
//            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
//        }
//
//
//    }


    @Override
    public void onResume() {
        super.onResume();
        UserClass userClass= UtilsClass.GetUserClassFromSharedPreferences(getApplicationContext());
        if(userClass!=null){
            mTracker.setScreenName("UploadDocumentFargment /"+" /"+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
//
    }
}
