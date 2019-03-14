package com.user.sqrfactor.Activities.SettingModule.SettingViews.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.user.sqrfactor.Activities.EducationDetailsActivity;
import com.user.sqrfactor.Activities.ProfileActivity;
import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.AllEducationDetailsClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingIndividualEducationDetails.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SettingIndividualEducationDetails extends Fragment {

    private OnFragmentInteractionListener mListener;
    private LinearLayout newForm;
    private Button add_eduDetails,remove_eduother,save_eduDetails;
    private boolean adDate1=false,adDate2=false;
    ArrayList<AllEducationDetailsClass> allEducationDetailsClassArrayList=new ArrayList<>();
    private Tracker mTracker;
    private MySharedPreferences mSp;
    //ArrayList<String> course=new ArrayList<>();

    private boolean gdDate1=false,gdDate2=false;
    private TextInputLayout admission;
    private EditText courseText,collegeText,admissionText,graduationText,courseText1,collegeText1,admissionText1,graduationText1;

    private Boolean isClicked= false;
    private Toolbar toolbar;
    private Context context;


    public SettingIndividualEducationDetails() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        UserClass userClass= UtilsClass.GetUserClassFromSharedPreferences(context);
        if(userClass!=null){
            mTracker.setScreenName("EducationDetailsActivity /"+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_setting_individual_education_details, container, false);



        //google anlytics
        MyApplication application = (MyApplication)getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mSp = MySharedPreferences.getInstance(context);
//
//        toolbar = (Toolbar) view.findViewById(R.id.edu_toolbar);
//        toolbar.setTitle("Profile");
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.back_arrow);



        admission=(TextInputLayout)view.findViewById(R.id.admission);
        save_eduDetails=(Button)view.findViewById(R.id.save_eduDetails);
        courseText=(EditText)view.findViewById(R.id.courseText);
        collegeText=(EditText)view.findViewById(R.id.collegeText);
        admissionText= (EditText)view.findViewById(R.id.admissionText);
        graduationText=(EditText)view.findViewById(R.id.graduationText);

        courseText1=(EditText)view.findViewById(R.id.courseText1);
        collegeText1=(EditText)view.findViewById(R.id.collegeText1);
        admissionText1= (EditText)view.findViewById(R.id.admissionText1);
        graduationText1=(EditText)view.findViewById(R.id.graduationText1);

        add_eduDetails=(Button)view.findViewById(R.id.Add_eduDetails);
        remove_eduother=(Button)view.findViewById(R.id.Remove_eduother);
        newForm=(LinearLayout)view.findViewById(R.id.linear_edu);



        save_eduDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToServer();
                Intent intent=new Intent(context, ProfileActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        newForm.setVisibility(View.GONE);
        add_eduDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isClicked)
                {
                    newForm.setVisibility(View.VISIBLE);
                    isClicked=true;
                }

            }
        });

        remove_eduother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newForm!=null && isClicked)
                {
                    newForm.setVisibility(View.GONE);
                    isClicked=false;
                }
            }
        });

        final Calendar myCalendar = Calendar.getInstance();


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(myCalendar);
            }

        };


        admissionText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                DatePickerDialog datePickerDialog= new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                if(hasFocus)
                {

                    adDate1=true;
                    adDate2=false;
                    gdDate1=false;
                    gdDate2=false;
                    datePickerDialog.show();
                }
                else {
                    datePickerDialog.hide();
                }

            }
        });

        admissionText1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                DatePickerDialog datePickerDialog= new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                if(hasFocus)
                {

                    adDate1=false;
                    adDate2=true;
                    gdDate1=false;
                    gdDate2=false;
                    datePickerDialog.show();
                }
                else {
                    datePickerDialog.hide();
                }

            }
        });
        graduationText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                DatePickerDialog datePickerDialog= new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                if(hasFocus)
                {

                    adDate1=false;
                    adDate2=false;
                    gdDate1=true;
                    gdDate2=false;
                    datePickerDialog.show();
                }
                else {
                    datePickerDialog.hide();
                }

            }
        });
        graduationText1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                DatePickerDialog datePickerDialog= new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                if(hasFocus)
                {

                    adDate1=false;
                    adDate2=false;
                    gdDate1=false;
                    gdDate2=true;
                    datePickerDialog.show();
                }
                else {
                    datePickerDialog.hide();
                }

            }
        });

        GetDataFromServerAndBindToView();

        return view;

    }
    public void GetDataFromServerAndBindToView()
    {

//        RequestQueue requestQueue = Volley.newRequestQueue(this);
        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();

        StringRequest myReq = new StringRequest(Request.Method.GET, ServerConstants.BASE_URL +"profile/edit/education-details",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray allEducationDetails=jsonObject.getJSONArray("allEducationDetails");
                            for(int i=0;i<allEducationDetails.length();i++) {
                                AllEducationDetailsClass allEducationDetailsClass=new AllEducationDetailsClass(allEducationDetails.getJSONObject(i));
                                allEducationDetailsClassArrayList.add(allEducationDetailsClass);

                            }

                            BindDataToView(allEducationDetailsClassArrayList);

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
        //requestQueue.add(myReq);
    }

    public void BindDataToView(ArrayList<AllEducationDetailsClass> allEducationDetailsClassArray)
    {

        for(int i=0;i<allEducationDetailsClassArray.size();i++) {
            if(i==0) {
                courseText.setText(allEducationDetailsClassArray.get(i).getCourse());
                collegeText.setText(allEducationDetailsClassArray.get(i).getCollege_university());
                admissionText.setText(allEducationDetailsClassArray.get(i).getYear_of_admission());
                graduationText.setText(allEducationDetailsClassArray.get(i).getYear_of_graduation());
            }
            if(i==1 && !isClicked) {
                newForm.setVisibility(View.VISIBLE);
                isClicked=true;
                courseText1.setText(allEducationDetailsClassArray.get(i).getCourse());
                collegeText1.setText(allEducationDetailsClassArray.get(i).getCollege_university());
                admissionText1.setText(allEducationDetailsClassArray.get(i).getYear_of_admission());
                graduationText1.setText(allEducationDetailsClassArray.get(i).getYear_of_graduation());
            }

        }
    }



    private void updateLabel(Calendar myCalendar) {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if(adDate1==true)
            admissionText.setText(sdf.format(myCalendar.getTime()));
        if(gdDate1==true)
            graduationText.setText(sdf.format(myCalendar.getTime()));
        if(adDate2==true)
            admissionText1.setText(sdf.format(myCalendar.getTime()));
        if(gdDate2==true)
            graduationText1.setText(sdf.format(myCalendar.getTime()));
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

    public void saveDataToServer()
    {
        //RequestQueue requestQueue1 = Volley.newRequestQueue(getApplicationContext());

        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();


        StringRequest myReq = new StringRequest(Request.Method.POST, ServerConstants.BASE_URL+"parse/work-education-detail",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.v("ResponseLike",s);
                        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            collegeText.setText("");
                            courseText.setText("");
                            admissionText.setText("");
                            graduationText.setText("");
                            collegeText1.setText("");
                            courseText1.setText("");
                            admissionText1.setText("");
                            graduationText1.setText("");

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
                params.put("Authorization",Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));

                return params;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if(!TextUtils.isEmpty(courseText.getText().toString())) {
                    params.put("course[0]",courseText.getText().toString());
                }
                if(!TextUtils.isEmpty(courseText1.getText().toString())) {
                    params.put("course[1]",courseText1.getText().toString());
                }
                if(!TextUtils.isEmpty(collegeText.getText().toString())) {
                    params.put("college_university[0]",collegeText.getText().toString());
                }
                if(!TextUtils.isEmpty(collegeText1.getText().toString())) {
                    params.put("college_university[1]",collegeText.getText().toString());
                }
                if(!TextUtils.isEmpty(admissionText.getText().toString())) {
                    params.put("year_of_admission[0]",admissionText.getText().toString());
                }
                if(!TextUtils.isEmpty(admissionText1.getText().toString())) {
                    params.put("year_of_admission[1]",admissionText1.getText().toString());
                }

                if(!TextUtils.isEmpty(graduationText.getText().toString())) {
                    params.put("year_of_graduation[0]",graduationText.getText().toString());
                }
                if(!TextUtils.isEmpty(graduationText1.getText().toString())) {
                    params.put("year_of_graduation[1]",graduationText1.getText().toString());
                }
                return params;
            }
        };


        //Adding request to the queue
     //   requestQueue1.add(stringRequest);
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);


    }
}
