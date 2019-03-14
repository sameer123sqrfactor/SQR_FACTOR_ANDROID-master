package com.user.sqrfactor.Activities.SettingModule.SettingViews.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

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
import com.google.gson.Gson;
import com.user.sqrfactor.Activities.BasicDetails;
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
import com.user.sqrfactor.Pojo.User_basic_detail;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingIndividualBasicDetails.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SettingIndividualBasicDetails extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Tracker mTracker;
    private Button addEmail,AddOccupation,SaveandNext;
    private ArrayList<CountryClass> countryClassArrayList=new ArrayList<>();
    private ArrayList<String> countryName=new ArrayList<>();
    private ArrayList<StateClass> statesClassArrayList=new ArrayList<>();
    private ArrayList<String> statesName=new ArrayList<>();
    private ArrayList<CitiesClass> citiesClassArrayList=new ArrayList<>();
    private ArrayList<String> citiesName=new ArrayList<>();
    private ArrayList<String> emails=new ArrayList<>();
    private boolean firstTimeState=false,firstTimeCity=false;
    private EditText nextEmail1,nextEmail2,nextEmail3;
    private int CountryID=0,StateID=0,CityID=0,actualCityID,actualStateID,actualCountryId;
    private UserClass userClass;
    private UserData userData;
    private Spinner countrySpinner,gender,spin,stateSpinner,citySpinner;
    private String country_val=null,state_val=null,city_val=null,gender_val=null,country_name,state_name,city_name;
    private RadioGroup radioGroup;
    private EditText Occupation,fNametext,lNametext,Emailtext,mobileText,dateOfBirthText,UIDtext,shortBioTecxt,
            facebookLinktext,TwitterLinktext,LinkedinLinktext,InstagramLinktext,email,emaileidttext1,emaileidttext2,emaileidttext3;

    private LinearLayout linearLayout2;
    private MySharedPreferences mSp;
    private boolean email2=false;
    private boolean firsTimeLoading=false;
    private boolean email3=false;
    private int countryId=1;
    private CheckBox checkBox1,checkBox2,checkBox3,checkBox4,checkBox5,checkBox6;
    private int count=0;
    private Toolbar toolbar;
    private SharedPreferences mPrefs;
    Gson gson;
    String json;
    private TextInputLayout emailText1,emailText2,emailText3;

    ArrayList<String> countries = new ArrayList<String>();
    private Context context;


    public SettingIndividualBasicDetails() {
        // Required empty public constructor
    }




    @Override
    public void onResume() {
        super.onResume();
        UserClass userClass= UtilsClass.GetUserClassFromSharedPreferences(getActivity());
        if(userClass!=null){
            mTracker.setScreenName("SrringIndividualsBasicDetails /"+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_setting_individual_basic_details, container, false);




       // toolbar = (Toolbar) view.findViewById(R.id.basic_toolbar);
       // toolbar.setTitle("Profile");
      //  setSupportActionBar(toolbar);
        //toolbar.setNavigationIcon(R.drawable.back_arrow);

        mSp = MySharedPreferences.getInstance(getActivity());

        //google anlytics
        MyApplication application = (MyApplication)getActivity().getApplication();
        mTracker = application.getDefaultTracker();

//
//        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = mPrefs.getString("MyObject", "");
//        UserClass userClass = gson.fromJson(json, UserClass.class);

        emailText1=view.findViewById(R.id.email1);
        emailText2=view.findViewById(R.id.email2);
        emailText3=view.findViewById(R.id.email3);

        emaileidttext1=view.findViewById(R.id.Emailtext1);
        emaileidttext2=view.findViewById(R.id.Emailtext2);
        emaileidttext3=view.findViewById(R.id.Emailtext3);

        Occupation=(EditText)view.findViewById(R.id.otherOccupationtext);
        email=(EditText)view.findViewById(R.id.Emailtext);
        InstagramLinktext=(EditText)view.findViewById(R.id.InstagramLinktext);
        LinkedinLinktext=(EditText)view.findViewById(R.id.LinkedinLinktext);
        TwitterLinktext=(EditText)view.findViewById(R.id.TwitterLinktext);
        facebookLinktext=(EditText)view.findViewById(R.id.facebookLinktext);
        shortBioTecxt=(EditText)view.findViewById(R.id.shortBioTecxt);
        fNametext=(EditText)view.findViewById(R.id.fNametext);
        lNametext=(EditText)view.findViewById(R.id.lNametext);
        Emailtext=(EditText)view.findViewById(R.id.Emailtext);
        mobileText=(EditText)view.findViewById(R.id.mobileText);
        dateOfBirthText=(EditText)view.findViewById(R.id.dateOfBirthText);
        UIDtext=(EditText)view.findViewById(R.id.UIDtext);
        SaveandNext=(Button)view.findViewById(R.id.SaveandNext);
        AddOccupation=(Button)view.findViewById(R.id.AddOccupation);



        countrySpinner = (Spinner)view.findViewById(R.id.Country);
        stateSpinner = (Spinner)view.findViewById(R.id.State);
        citySpinner = (Spinner)view.findViewById(R.id.City);

        addEmail = (Button)view.findViewById(R.id.AddEmail);
        linearLayout2 =(LinearLayout)view.findViewById(R.id.linearLayout2);
        linearLayout2.setVisibility(View.GONE);
        addEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AddEmailPopup();
            }
        });
        final String[] gender = { "Male", "Female" };
        spin = (Spinner) view.findViewById(R.id.gender);

        LoadCountryFromServer();

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int  position, long id) {
                gender_val = gender[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        ArrayAdapter<String> spin_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, gender);
        spin.setAdapter(spin_adapter);


        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int  position, long id) {
                if(countryName.size()>0) {
                    country_name = countryName.get(position);
                    country_val=position+1+"";
                    LoadStateFromServer();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int  position, long id) {
                if(statesName.size()>0) {
                    state_name = statesName.get(position);
                    country_val=statesClassArrayList.get(position).getCountryId()+"";
                    if(firstTimeState) {
                        state_val=actualStateID+position+"";
                        firstTimeState=false;
                    } else {
                        state_val=actualStateID+position+"";
                    }
                    LoadCitiesFromServer();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int  position, long id) {
                if (citiesName.size()>0) {
                    city_name = citiesName.get(position);
                    state_val=citiesClassArrayList.get(position).getStateId()+"";
                    country_val=citiesClassArrayList.get(position).getCountryId()+"";
                    city_val=actualCityID+position+"";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
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
                updateLabe(myCalendar);
            }

        };

        dateOfBirthText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                DatePickerDialog datePickerDialog= new DatePickerDialog(getApplicationContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                if(hasFocus) {
                    datePickerDialog.show();
                } else {
                    datePickerDialog.hide();
                }

            }
        });


        checkBox1= (CheckBox) view.findViewById(R.id.checkbox1);
        checkBox2= (CheckBox) view.findViewById(R.id.checkbox2);
        checkBox3= (CheckBox) view.findViewById(R.id.checkbox3);
        checkBox4= (CheckBox) view.findViewById(R.id.checkbox4);
        checkBox5= (CheckBox) view.findViewById(R.id.checkbox5);
        checkBox6= (CheckBox) view.findViewById(R.id.checkbox6);

        checkBox6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    linearLayout2.setVisibility(View.VISIBLE);
                } else {
                    linearLayout2.setVisibility(View.GONE);
                }
            }
        });

        SaveandNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Replace all toast with Mdtoast
                if(UtilsClass.IsConnected(context))
                {
                    if(TextUtils.isEmpty(fNametext.getText().toString())) {
                        fNametext.setError("First name is required");
                       // MDToast.makeText(context, "First name is required", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                    } else if(TextUtils.isEmpty(lNametext.getText().toString())) {
                        lNametext.setError("Last name is required");
                        // MDToast.makeText(context, "Last name is required", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                    } else if(country_val==null) {
                        MDToast.makeText(context, "Country field is required", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                    } else if(state_val==null) {
                        MDToast.makeText(context, "State field is required", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                    } else if(city_val==null) {
                        MDToast.makeText(context, "City field is required", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                    } else if(gender_val==null) {
                        MDToast.makeText(context, "Gender field is required", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                    } else {
                        saveDataToServer();
                    }
                } else {
                    MDToast.makeText(context, "Check Your Internet Connection", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                }
            }
        });
        BindDataTOviews();
        return view;
    }

//
//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
//    }



    public void AddEmailPopup()
    {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        final View promptsView = li.inflate(R.layout.addemailprompt, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        nextEmail1 = (EditText) promptsView
                .findViewById(R.id.nextEmailText1);
        nextEmail2 = (EditText) promptsView
                .findViewById(R.id.nextEmailText2);
        nextEmail3 = (EditText) promptsView
                .findViewById(R.id.nextEmailText3);
        final Button addPromptButton= (Button) promptsView.
                findViewById(R.id.AddPromptButton);
        final LinearLayout linearLayoutPrompt1=(LinearLayout) promptsView.
                findViewById(R.id.linearLayoutprompt1);
        linearLayoutPrompt1.setVisibility(View.GONE);
        final LinearLayout linearLayoutPrompt2=(LinearLayout) promptsView.
                findViewById(R.id.linearLayoutprompt2);

        linearLayoutPrompt2.setVisibility(View.GONE);
        final Button removePromptButton1= (Button) promptsView
                .findViewById(R.id.removeEmailButton1);
        final Button removePromptButton2= (Button) promptsView
                .findViewById(R.id.removeEmailButton2);
        addPromptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count++;
                if(!email2 && count==1) {
                    linearLayoutPrompt1.setVisibility(View.VISIBLE);
                    email2=true;
                }
                if(!email3 && count==2) {
                    linearLayoutPrompt2.setVisibility(View.VISIBLE);
                    email3=true;
                }

            }
        });

        removePromptButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email2) {
                    linearLayoutPrompt1.setVisibility(View.GONE);
                    email2=false;
                    count--;
                }
            }
        });
        removePromptButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email3) {
                    linearLayoutPrompt2.setVisibility(View.GONE);
                    email3=false;
                    count--;
                }
            }
        });



        final AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
        final Button saveButton= (Button) promptsView
                .findViewById(R.id.SaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count==0 && !TextUtils.isEmpty(nextEmail1.getText().toString())) {
                    emailText1.setVisibility(View.VISIBLE);
                    emaileidttext1.setText(nextEmail1.getText().toString());
                    emaileidttext1.setEnabled(false);
                }
                if(count==1 && email2 &&!TextUtils.isEmpty(nextEmail2.getText().toString())) {
                    emailText2.setVisibility(View.VISIBLE);
                    emaileidttext2.setText(nextEmail2.getText().toString());
                    emaileidttext2.setEnabled(false);
                }
                if(count==2 && email3 && !TextUtils.isEmpty(nextEmail3.getText().toString())) {
                    emailText3.setVisibility(View.VISIBLE);
                    emaileidttext3.setText(nextEmail3.getText().toString());
                    emaileidttext3.setEnabled(false);
                }
                alertDialog.dismiss();

            }
        });
        final Button cancelButton= (Button) promptsView
                .findViewById(R.id.CancelButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(BasicDetails.this,"cancel",Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
            }
        });
    }

    private void updateLabe(Calendar myCalendar) {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateOfBirthText.setText(sdf.format(myCalendar.getTime()));
    }



//      return view;
//
//
//    }

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

    public void LoadCountryFromServer() {

       // RequestQueue mRequestQueue = Volley.newRequestQueue;

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
                            countrySpinner.setAdapter(spin_adapter1);
                            countrySpinner.setSelection(CountryID);
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
        // Log.v("state ",countryID+stateID+cityID+"");
     //   RequestQueue mRequestQueue = Volley.newRequestQueue(BasicDetails.this);
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
                            stateSpinner.setAdapter(spin_adapter2);
                            if(statesClassArrayList!=null && statesClassArrayList.size()>0) {
                                actualStateID=statesClassArrayList.get(0).getId();
                                StateID=StateID-statesClassArrayList.get(0).getId();
                                Log.v("statecoe",StateID+" ");
                            }
                            if(firstTimeState) {
                                stateSpinner.setSelection(StateID);
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



    public void LoadCitiesFromServer() {
         RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();
       // RequestQueue mRequestQueue = Volley.newRequestQueue(BasicDetails.this);
        StringRequest myReq = new StringRequest(Request.Method.POST, ServerConstants.BASE_URL +"profile/city",
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
                            ArrayAdapter<String> spin_adapter3 = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,citiesName);
                            citySpinner.setAdapter(spin_adapter3);
                            if(citiesClassArrayList!=null&& citiesClassArrayList.size()>0) {
                                actualCityID=citiesClassArrayList.get(0).getId();
                                CityID=CityID-citiesClassArrayList.get(0).getId();
                                if(firstTimeCity) {
                                    citySpinner.setSelection(CityID);
                                    firstTimeCity=false;
                                }
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
                Log.v("stateandcountrypostion",state_val+" "+country_val);
                params.put("state",state_val);
                params.put("country",country_val);
                return params;
            }
        };
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);

    }
    public void saveDataToServer() {

      //  RequestQueue mRequestQueue = Volley.newRequestQueue(BasicDetails.this);

        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();


        StringRequest myReq = new StringRequest(Request.Method.POST, ServerConstants.BASE_URL+"parse/work-individual-basic",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        MDToast.makeText(context, "Profile updated successfully"+s, MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = mPrefs.getString("MyObject", "");
                        UserClass userClass = gson.fromJson(json, UserClass.class);
                        userClass.setFirst_name(fNametext.getText().toString());
                        userClass.setLast_name(lNametext.getText().toString());
                        userClass.setShort_bio(shortBioTecxt.getText().toString());
                        userClass.setUser_address(city_name+", "+state_name+", "+country_name);
                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
                        json = gson.toJson(userClass);
                        prefsEditor.putString("MyObject", json);
                        prefsEditor.apply();

                        fNametext.setText("");
                        lNametext.setText("");
                        mobileText.setText("");
                        dateOfBirthText.setText("");
                        email.setText("");
                        UIDtext.setText("");
                        shortBioTecxt.setText("");
                        facebookLinktext.setText("");
                        LinkedinLinktext.setText("");
                        TwitterLinktext.setText("");
                        InstagramLinktext.setText("");
                        Intent intent=new Intent(context, ProfileActivity.class);
                        startActivity(intent);
                        getActivity().finish();
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
//                                Log.v("chat",res);
//                                Toast.makeText(getApplicationContext(),res,Toast.LENGTH_LONG).show();
                                // Now you can use any deserializer to make sense of data
                                JSONObject obj = new JSONObject(res);
                            } catch (UnsupportedEncodingException e1) {
                                // Toast.makeText(getApplicationContext(),e1.toString(),Toast.LENGTH_LONG).show();
                                // Couldn't properly decode data to string
                                e1.printStackTrace();
                            } catch (JSONException e2) {
                                // Toast.makeText(getApplicationContext(),e2.toString(),Toast.LENGTH_LONG).show();
                                // returned data is not JSONObject?
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
                if(nextEmail1!=null)
                    params.put("email[1]",nextEmail1.getText().toString());
                if(nextEmail2!=null)
                    params.put("email[2]",nextEmail2.getText().toString());
                if(nextEmail3!=null)
                    params.put("email[3]",nextEmail2.getText().toString());
                if(checkBox1.isChecked())
                    params.put("occupation[0]",checkBox1.getText().toString());
                if(checkBox2.isChecked())
                    params.put("occupation[1]",checkBox2.getText().toString());
                if(checkBox3.isChecked())
                    params.put("occupation[2]",checkBox3.getText().toString());
                if(checkBox4.isChecked())
                    params.put("occupation[3]",checkBox4.getText().toString());
                if(checkBox5.isChecked())
                    params.put("occupation[4]",checkBox5.getText().toString());
                if(checkBox6.isChecked())
                    params.put("occupation[5]",checkBox6.getText().toString());
                params.put("first_name",fNametext.getText().toString());
                params.put("last_name",lNametext.getText().toString());
                params.put("country",country_val+"");
                params.put("state",state_val+"");
                params.put("city",city_val+"");
                params.put("gender",gender_val);
                params.put("aadhar_id",UIDtext.getText().toString());
                params.put("occupation",Occupation.getText().toString());
                params.put("mobile_number",mobileText.getText().toString());
                params.put("looking_for_an_architect","No");
                params.put("email[0]",email.getText().toString());
                params.put("short_bio",shortBioTecxt.getText().toString());
                params.put("facebook_link",facebookLinktext.getText().toString());
                params.put("twitter_link",TwitterLinktext.getText().toString());
                params.put("linkedin_link",LinkedinLinktext.getText().toString());
                params.put("instagram_link",InstagramLinktext.getText().toString());
                params.put("date_of_birth",dateOfBirthText.getText().toString());
                return params;
            }
        };
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);
    }


    private void BindDataTOviews() {

       // RequestQueue mRequestQueue = Volley.newRequestQueue(BasicDetails.this);
         RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();

        StringRequest myReq = new StringRequest(Request.Method.GET, ServerConstants.BASE_URL+"profile/edit",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("User Data");
                            UserData  userData = new UserData(jsonArray.getJSONObject(0));

                            User_basic_detail user_basic_detail=new User_basic_detail(jsonObject.getJSONObject("User_basic_detail"));
                            userData.setUser_basic_detail(user_basic_detail);
                            JSONArray emailsObject=jsonObject.getJSONArray("emails");

                            for(int i=0;i<emailsObject.length();i++) {
                                if(i==0) {
                                    email.setText(emailsObject.getJSONObject(i).getString("email"));
                                }if(i==1) {
                                    emailText1.setVisibility(View.VISIBLE);
                                    emaileidttext1.setText(emailsObject.getJSONObject(i).getString("email"));
                                    emaileidttext1.setEnabled(false);
                                }if(i==2) {
                                    emailText2.setVisibility(View.VISIBLE);
                                    emaileidttext2.setText(emailsObject.getJSONObject(i).getString("email"));
                                    emaileidttext2.setEnabled(false);
                                }if(i==3) {
                                    emailText3.setVisibility(View.VISIBLE);
                                    emaileidttext3.setText(emailsObject.getJSONObject(i).getString("email"));
                                    emaileidttext3.setEnabled(false);
                                }
                            }
                            if(!userData.getOccupation().equals("null")) {
                                String[] occupation=userData.getOccupation().split(",");
                                if(occupation.length>0) {
                                    MarkOccupationCheckBox(occupation);
                                }
                            }
                            if (!userData.getUser_basic_detail().getFirst_name().equals("null")) {
                                fNametext.setText(userData.getUser_basic_detail().getFirst_name());
                            }
                            if (!userData.getUser_basic_detail().getLast_name().equals("null")) {
                                lNametext.setText(userData.getUser_basic_detail().getLast_name());
                            }
                            if (!userData.getShot_bio().equals("null")) {
                                shortBioTecxt.setText(userData.getShot_bio());
                            }
                            if (!userData.getDate_of_birth().equals("null")) {
                                dateOfBirthText.setText(userData.getDate_of_birth());
                            }
                            if (!userData.getFacebook_link().equals("null")) {
                                facebookLinktext.setText(userData.getFacebook_link());
                            }
                            if (!userData.getLinkedin_link().equals("null")) {
                                LinkedinLinktext.setText(userData.getLinkedin_link());
                            }
                            if (!userData.getTwitter_link().equals("null")) {
                                TwitterLinktext.setText(userData.getTwitter_link());
                            }
                            if (!userData.getInstagram_link().equals("null")) {
                                InstagramLinktext.setText(userData.getInstagram_link());
                            }
                            if (!userData.getAadhaar_id().equals("null")) {
                                UIDtext.setText(userData.getAadhaar_id());
                            }
                            if (!userData.getUser_basic_detail().getMobile_number().equals("null")) {
                                mobileText.setText(userData.getUser_basic_detail().getMobile_number());
                            }
                            if(!userData.getCountry_id().equals("null")&&!userData.getState_id().equals("null")&&!userData.getState_id().equals("null")) {
                                CountryID=Integer.parseInt(userData.getCountry_id())-1;
                                StateID=Integer.parseInt(userData.getState_id());
                                CityID=Integer.parseInt(userData.getCity_id());
                                firstTimeCity=true;
                                firstTimeState=true;
                                firsTimeLoading=true;
                                LoadCountryFromServer();
                            } else {
                                LoadCountryFromServer();
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
//                                Log.v("chat",res);
//                                Toast.makeText(getApplicationContext(),res,Toast.LENGTH_LONG).show();
                                // Now you can use any deserializer to make sense of data
                                JSONObject obj = new JSONObject(res);
                            } catch (UnsupportedEncodingException e1) {
                                // Toast.makeText(getApplicationContext(),e1.toString(),Toast.LENGTH_LONG).show();
                                // Couldn't properly decode data to string
                                e1.printStackTrace();
                            } catch (JSONException e2) {
                                // Toast.makeText(getApplicationContext(),e2.toString(),Toast.LENGTH_LONG).show();
                                // returned data is not JSONObject?
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

        };
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);

    }


    private void MarkOccupationCheckBox(String[] occupation) {
        for(int i=0;i<occupation.length;i++) {
            if(occupation[i].equals("Architect")||occupation[i].equals(" Architect")){
                checkBox1.setChecked(true);
            } else if(occupation[i].equals("Design Student")||occupation[i].equals(" Design Student") ){
                checkBox2.setChecked(true);
            } else if(occupation[i].equals("Academician")||occupation[i].equals(" Academician")){
                checkBox3.setChecked(true);
            } else if(occupation[i].equals("Interior Designer")||occupation[i].equals(" Interior Designer")){
                checkBox4.setChecked(true);
            } else if(occupation[i].equals("Landscape Architect")||occupation[i].equals(" Landscape Architect")){
                checkBox5.setChecked(true);
            }
        }
    }

}
