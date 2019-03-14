package com.user.sqrfactor.Activities.SettingModule.SettingViews.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.user.sqrfactor.Activities.EmployeeMemberDetails;
import com.user.sqrfactor.Activities.ProfileActivity;
import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Fragments.MDToast;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.CitiesClass;
import com.user.sqrfactor.Pojo.CountryClass;
import com.user.sqrfactor.Pojo.StateClass;
import com.user.sqrfactor.Pojo.UserData;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingEmployeeMemberDetails.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SettingEmployeeMemberDetails extends Fragment {

    private OnFragmentInteractionListener mListener;
    private boolean firstTimeState=true;
    private  boolean firstTimeCity=true;
    Toolbar toolbar;
    private EditText firstName,lastName,role,phoneNumber,aadhaarId,email;
    private TextView employeeAttachment;
    private Button employeeAddbtn;
    private int countryId=1;
    private UserData userData;
    private String image;
    private ImageView attachedImage,attachment;
    private Spinner employee_CountrySpinner,employee_StateSpinner,employee_CitySpinner;
    private ArrayList<CountryClass> countryClassArrayList=new ArrayList<>();
    private ArrayList<String> countryName=new ArrayList<>();
    private ArrayList<StateClass> statesClassArrayList=new ArrayList<>();
    private ArrayList<String> statesName=new ArrayList<>();
    private ArrayList<CitiesClass> citiesClassArrayList=new ArrayList<>();
    private ArrayList<String> citiesName=new ArrayList<>();
    private int CountryID=1,StateID=1,CityID=1,actualCityID,actualStateID,actualCountryId;
    private String country_val=1+"",state_val=1+"",city_val=1+"",gender_val=null,country_name,state_name,city_name;
    private Tracker mTracker;
    private MySharedPreferences mSp;
    private Context context;

    public SettingEmployeeMemberDetails() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        UserClass userClass= UtilsClass.GetUserClassFromSharedPreferences(getApplicationContext());
        if(userClass!=null){
            mTracker.setScreenName("EmployeeMemberDetails /"+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_setting_employee_member_details, container, false);
        //google anlytics
        MyApplication application = (MyApplication)getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        mSp = MySharedPreferences.getInstance(context);
        employee_CountrySpinner=(Spinner)view.findViewById(R.id.employee_Country);
        employee_StateSpinner=(Spinner)view.findViewById(R.id.employee_State);
        employee_CitySpinner=(Spinner)view.findViewById(R.id.employee_City);
        employeeAttachment=(TextView)view.findViewById(R.id.employee_attachment);
        attachedImage=(ImageView)view.findViewById(R.id.employee_attachment_image);
        attachment = (ImageView)view.findViewById(R.id.employee_attachment_icon);
        //attachmentImage = (ImageView)findViewById(R.id.employee_attachment_image);
        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        firstName =(EditText)view.findViewById(R.id.employee_firstName_text);
        lastName =(EditText)view.findViewById(R.id.employee_firstLast_text);
        role =(EditText)view.findViewById(R.id.employee_role_text);
        phoneNumber =(EditText)view.findViewById(R.id.employee_number_text);
        aadhaarId =(EditText)view.findViewById(R.id.employee_aadhaar_text);
        email =(EditText)view.findViewById(R.id.employee_email_text);
        employeeAddbtn =(Button)view.findViewById(R.id.employee_add_button);



        employee_CountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                if (countryName.size() > 0) {
                    country_name = countryName.get(position);
                    country_val = position + 1 + "";
                    LoadStateFromServer();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        employee_StateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                if (statesName.size() > 0) {
                    state_name = statesName.get(position);
                    country_val = statesClassArrayList.get(position).getCountryId() + "";
                    state_val = actualStateID + position + "";
                    LoadCitiesFromServer();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });

        employee_CitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                if (citiesName.size() > 0) {
                    city_name = citiesName.get(position);
                    state_val = citiesClassArrayList.get(position).getStateId() + "";
                    country_val = citiesClassArrayList.get(position).getCountryId() + "";
                    city_val = actualCityID + position + "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });
        LoadCountryFromServer();

        employeeAddbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(TextUtils.isEmpty(firstName.getText().toString())) {
                    firstName.setError("First name is required");
                   // MDToast.makeText(EmployeeMemberDetails.this, "First name is required", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                }
                else if(TextUtils.isEmpty(lastName.getText().toString())) {
                    lastName.setError("Last name is required");
                    //MDToast.makeText(EmployeeMemberDetails.this, "Last name is required", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                }
                else if(TextUtils.isEmpty(role.getText().toString())) {
                    role.setError("role field is required");
                   // MDToast.makeText(EmployeeMemberDetails.this, "role field is required", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                }
                else if(TextUtils.isEmpty(phoneNumber.getText().toString())) {
                    phoneNumber.setError("Mobile No. field is required");
                   // MDToast.makeText(EmployeeMemberDetails.this, "Mobile No. field is required", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                }
                else if(TextUtils.isEmpty(email.getText().toString())) {
                    email.setError("Email field is required");
                    //MDToast.makeText(EmployeeMemberDetails.this, "Email field is required", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                }
                else if(country_val==null) {
                    MDToast.makeText(context, "Country field is required", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                }
                else if(state_val==null) {
                    MDToast.makeText(context, "State field is required", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                }
                else if(city_val==null) {
                    MDToast.makeText(context, "City field is required", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                }
                else {
                    SendDataToServer();
                }
            }
        });

        return view;
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
    public void SendDataToServer() {

       // RequestQueue requestQueue1 = Volley.newRequestQueue(this);

        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();
      //  Toast.makeText(context,"calling",Toast.LENGTH_LONG).show();
        StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"profile/add/member-detail",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.v("employess",s);

                        try {
                            JSONObject jsonObject = new JSONObject(s);

                            if(jsonObject.has("return")) {
                                MDToast.makeText(context, "Phone number or Email has already been taken", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                            }
                            else {
                                firstName.setText("");
                                lastName.setText("");
                                role.setText("");
                                phoneNumber.setText("");
                                aadhaarId.setText("");
                                email.setText("");
                                MDToast.makeText(context, "Member details added Successfully", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                                Intent intent =new Intent(getApplicationContext(), ProfileActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        NetworkResponse response = volleyError.networkResponse;
                        if (volleyError instanceof ServerError && response != null) {
                            try {


                                String res = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));

                                JSONObject obj = new JSONObject(res);
                            } catch (UnsupportedEncodingException e1) {
//
                                e1.printStackTrace();
                            } catch (JSONException e2) {
//
                                e2.printStackTrace();
                            }
                        }
                    }
                }){
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

//                Log.v("emp",image);
//                Log.v("emp1",firstName.getText().toString());
//                Log.v("emp2",lastName.getText().toString());
//                Log.v("emp3",aadhaarId.getText().toString());
//                Log.v("emp4",phoneNumber.getText().toString());
//                Log.v("emp5",email.getText().toString());
//                Log.v("emp6",role.getText().toString());
//                Log.v("emp7",country_val+state_val+city_val);

//
                params.put("first_name",firstName.getText().toString());
                params.put("last_name",lastName.getText().toString());
                params.put("country",country_val+"");
                params.put("profile","data:image/jpeg;base64,"+image);
                params.put("state",state_val+"");
                params.put("city",city_val+"");
                params.put("role",role.getText().toString());
                params.put("phone_number",phoneNumber.getText().toString());
                params.put("aadhar_id",aadhaarId.getText().toString());
                params.put("email",email.getText().toString());

                return params;
            }
        };

        //Adding request to the queue
        //requestQueue1.add(stringRequest);
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);


    }

    public void LoadCountryFromServer()
    {

        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();

        StringRequest myReq = new StringRequest(Request.Method.GET, ServerConstants.BASE_URL+"event/country",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray countries=jsonObject.getJSONArray("countries");
                            countryName.clear();
                            countryClassArrayList.clear();
                            for (int i=0;i<countries.length();i++) {
                                CountryClass countryClass=new CountryClass(countries.getJSONObject(i));
                                countryClassArrayList.add(countryClass);
                                countryName.add(countryClass.getName());
                            }
                            ArrayAdapter<String> spin_adapter1 = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,countryName);
                            employee_CountrySpinner.setAdapter(spin_adapter1);
                            employee_CountrySpinner.setSelection(CountryID);
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
                return params;
            }

        };

        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);


    }

    public void LoadStateFromServer()
    {
        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();
        StringRequest myReq = new StringRequest(Request.Method.POST, ServerConstants.BASE_URL+"profile/state",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray states=jsonObject.getJSONArray("states");
                            statesName.clear();
                            statesClassArrayList.clear();
                            for (int i=0;i< states.length();i++) {
                                StateClass stateClass=new StateClass(states.getJSONObject(i));
                                statesClassArrayList.add(stateClass);
                                statesName.add(stateClass.getName());
                            }
                            ArrayAdapter<String> spin_adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,statesName);
                            employee_StateSpinner.setAdapter(spin_adapter2);
                            if(statesClassArrayList!=null && statesClassArrayList.size()>0) {
                                actualStateID=statesClassArrayList.get(0).getId();
                                StateID=StateID-statesClassArrayList.get(0).getId();
                                Log.v("statecoe",StateID+" ");
                            }
                            if(firstTimeState) {
                                employee_StateSpinner.setSelection(StateID);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("country",country_val);
                return params;
            }
        };

        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);

    }

    public void LoadCitiesFromServer()
    {

        // RequestQueue requestQueue1 = Volley.newRequestQueue(getApplicationContext());


        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();

        StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"profile/city",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray cities=jsonObject.getJSONArray("cities");
                            citiesName.clear();
                            citiesClassArrayList.clear();
                            for (int i=0;i< cities.length();i++) {
                                CitiesClass citiesClass=new CitiesClass(cities.getJSONObject(i));
                                citiesClassArrayList.add(citiesClass);
                                citiesName.add(citiesClass.getName());
                            }


                            if(citiesClassArrayList!=null&& citiesClassArrayList.size()>0) {
                                actualCityID=citiesClassArrayList.get(0).getId();
                                CityID=CityID-citiesClassArrayList.get(0).getId();
                            }
                            ArrayAdapter<String> spin_adapter3 = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,citiesName);
                            employee_CitySpinner.setAdapter(spin_adapter3);

                            if(firstTimeCity) {
                                employee_CitySpinner.setSelection(CityID);
                                firstTimeCity=false;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));

                return params;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//                Log.v("state+country",state_val+" "+country_val);
                params.put("state",state_val);
                params.put("country",country_val);
                return params;
            }
        };

        //Adding request to the queue
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);


    }


    private void selectImage() {



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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                File f = new File(Environment.getExternalStorageDirectory().toString());

                for (File temp : f.listFiles()) {

                    if (temp.getName().equals("temp.jpg")) {

                        f = temp;

                        break;

                    }

                }

                try {

                    Bitmap bitmap;

                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();



                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),

                            bitmapOptions);

                    image = getStringImage(bitmap);
                    //params.put("profile_image","data:image/jpeg;base64,"+image );


                    attachedImage.setImageBitmap(bitmap);



                    String path = android.os.Environment

                            .getExternalStorageDirectory()

                            + File.separator

                            + "Phoenix" + File.separator + "default";
                    employeeAttachment.setText(String.valueOf(System.currentTimeMillis()) + ".jpg");


                    f.delete();

                    OutputStream outFile = null;

                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");

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

                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));

                // Log.w("path of image from gallery......******************.........", fileName[fileName.length-1]+"");

                image=getStringImage(thumbnail);
                attachedImage.setImageBitmap(thumbnail);
                employeeAttachment.setText(fileName[fileName.length-1]+"");

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

}
