package com.user.sqrfactor.Activities.SettingModule.SettingViews.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

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
import com.user.sqrfactor.Activities.CollegeBasicDetails;
import com.user.sqrfactor.Activities.ProfileActivity;
import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.CitiesClass;
import com.user.sqrfactor.Pojo.CountryClass;
import com.user.sqrfactor.Pojo.StateClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingCollegeBasicDetails.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SettingCollegeBasicDetails extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ArrayList<CountryClass> countryClassArrayList=new ArrayList<>();
    private ArrayList<String> countryName=new ArrayList<>();
    private ArrayList<StateClass> statesClassArrayList=new ArrayList<>();
    private ArrayList<String> statesName=new ArrayList<>();
    private ArrayList<CitiesClass> citiesClassArrayList=new ArrayList<>();
    private ArrayList<String> citiesName=new ArrayList<>();
    private EditText nextEmail1,nextEmail2,nextEmail3;
    private int CountryID=0,StateID=0,CityID=0;
    private int actualCityID,actualStateID,actualCountryId;
    private String country_val=null,state_val=null,city_val=null,gender_val=null,country_name,state_name,city_name,compnayFirm_val = null;
    Toolbar toolbar;
    private int countryId=1;
    private EditText nameOfCollege,mobileNumber,email,shortBio,collegeName,registerNumber,facbook,linkedin,instagram,twitter;
    private Button save,addEmail;
    private boolean email2=false,firsTimeLoading=false;;
    private boolean email3=false;
    private int count=0;
    Spinner spin;
    private boolean firstTimeState=false,firstTimeCity=false;
    Spinner countrySpinner,stateSpinner,citySpinner;
    String spin_val=null;
    ArrayList<String> countries = new ArrayList<String>();
    private Tracker mTracker;
    private MySharedPreferences mSp;
    private Context context;


    public SettingCollegeBasicDetails() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();
        UserClass userClass= UtilsClass.GetUserClassFromSharedPreferences(context);
        if(userClass!=null){
            mTracker.setScreenName("CollegeBasicDetails /"+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting_college_basic_details, container, false);
        //google anlytics
        MyApplication application = (MyApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        mSp = MySharedPreferences.getInstance(context);

        nameOfCollege = (EditText) view.findViewById(R.id.college_name_text);
        mobileNumber = (EditText)view. findViewById(R.id.college_mobile_Text);
        email = (EditText) view.findViewById(R.id.college_email_text);
        shortBio = (EditText) view.findViewById(R.id.college_shortBioText);
        collegeName = (EditText) view.findViewById(R.id.college_university_name_text);
        registerNumber = (EditText) view.findViewById(R.id.college_registerNumber_text);
        facbook = (EditText) view.findViewById(R.id.college_facebookLinktext);
        instagram = (EditText) view.findViewById(R.id.college_InstagramLinktext);
        linkedin = (EditText)view. findViewById(R.id.college_LinkedinLinktext);
        twitter = (EditText) view.findViewById(R.id.college_TwitterLinktext);
        save = (Button) view.findViewById(R.id.college_save_next);
        addEmail = (Button) view.findViewById(R.id.college_AddEmail);

        countrySpinner = (Spinner) view.findViewById(R.id.college_Country);
        stateSpinner = (Spinner) view.findViewById(R.id.college_State);
        citySpinner = (Spinner)view.findViewById(R.id.college_City);

        LoadCountryFromServer();

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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


        addEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AddPopupEmail();
            }
        });

        ArrayAdapter<String> spin_adapter1 = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, countries);
        countrySpinner.setAdapter(spin_adapter1);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadDatToServer();
            }
        });

        GetDataFromServerAndBindToView();
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

    public void LoadCountryFromServer()
    {

        RequestQueue mRequestQueue = MyVolley.getInstance().getRequestQueue();

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
                            citySpinner.setAdapter(spin_adapter3);
                            if(firstTimeCity) {
                                citySpinner.setSelection(CityID);
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
    public void AddPopupEmail() {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        final View promptsView = li.inflate(R.layout.addemailprompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getApplicationContext());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText nextEmail1 = (EditText) promptsView
                .findViewById(R.id.nextEmailText1);
        final Button addPromptButton = (Button) promptsView
                .findViewById(R.id.AddPromptButton);

        final LinearLayout linearLayoutPrompt1 = (LinearLayout) promptsView.findViewById(R.id.linearLayoutprompt1);
        linearLayoutPrompt1.setVisibility(View.GONE);

        final LinearLayout linearLayoutPrompt2 = (LinearLayout) promptsView.findViewById(R.id.linearLayoutprompt2);
        linearLayoutPrompt2.setVisibility(View.GONE);

        final Button removePromptButton1 = (Button) promptsView
                .findViewById(R.id.removeEmailButton1);

        final Button removePromptButton2 = (Button) promptsView
                .findViewById(R.id.removeEmailButton2);

        //final TextInputLayout nextEmail=(TextInputLayout) promptsView.findViewById(R.id.nextEmail);

        addPromptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if (!email2 && count == 1) {
                    linearLayoutPrompt1.setVisibility(View.VISIBLE);
                    email2 = true;
                }
                if (!email3 && count == 2) {
                    linearLayoutPrompt2.setVisibility(View.VISIBLE);
                    email3 = true;
                }
            }
        });

        removePromptButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email2) {
                    linearLayoutPrompt1.setVisibility(View.GONE);
                    email2 = false;
                    count--;
                }
            }
        });
        removePromptButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email3) {
                    linearLayoutPrompt2.setVisibility(View.GONE);
                    email3 = false;
                    count--;
                }
            }
        });




        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void LoadDatToServer()
    {
       // RequestQueue requestQueue1 = Volley.newRequestQueue(getApplicationContext());

        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();

        StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"profile/hire-organization-basic-detail",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.v("ResponseLike",s);

                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            Intent intent=new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(intent);
                            getActivity().finish();
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
//              // params.put("mobile",mobileNumber.getText().toString());
                // params.put("email",email.getText().toString());
                //params.put("email1",nextEmail1.getText().toString());
//                params.put("email2",nextEmail2.getText().toString());
//                Log.v("dataTosend",nameOfCollege.getText().toString()+" "+email.getText().toString()+""+country_val+""
//                +state_val+city_val+shortBio.getText().toString()+collegeName.getText().toString()+registerNumber.getText().toString()+facbook.getText().toString()
//                +twitter.getText().toString()+linkedin.getText().toString());

                if(!TextUtils.isEmpty(email.getText().toString())){
                    params.put("email[0]",email.getText().toString());
                }
                if(nextEmail1!=null){
                    params.put("email[1]",nextEmail1.getText().toString());
                }
                if(nextEmail2!=null){
                    params.put("email[2]",nextEmail2.getText().toString());
                }
                if(!TextUtils.isEmpty(nameOfCollege.getText().toString())){
                    params.put("name_of_the_company",nameOfCollege.getText().toString());
                } else{
                    params.put("name_of_the_company",null+"");
                }
                if(!TextUtils.isEmpty(shortBio.getText().toString())){
                    params.put("short_bio",shortBio.getText().toString());
                } else{
                    params.put("short_bio",null+"");
                }
                if(!TextUtils.isEmpty(collegeName.getText().toString())){
                    params.put("firm_or_company_name",collegeName.getText().toString());
                } else{
                    params.put("firm_or_company_name",null+"");
                }
                if(!TextUtils.isEmpty(registerNumber.getText().toString())){
                    params.put("firm_or_company_registration_number",registerNumber.getText().toString());
                } else{
                    params.put("firm_or_company_registration_number",null+"");
                }
                params.put("address",null+"");
                params.put("pin_code",null+"");
                params.put("business_description",null+"");
                params.put("webside",null+"");
                params.put("country",country_val);
                params.put("state",state_val);
                params.put("city",city_val);
                params.put("types_of_firm_company",null+"");
                params.put("facebook_link",facbook.getText().toString());
                params.put("twitter_link",twitter.getText().toString());
                params.put("linkedin_link",linkedin.getText().toString());
                params.put("instagram_link",instagram.getText().toString());
                //params.put("date_of_birth",instagram.getText().toString());
                return params;
            }
        };

        //Adding request to the queue
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);



    }


    private void GetDataFromServerAndBindToView() {

        UserClass userClass = UtilsClass.GetUserClassFromSharedPreferences(getApplicationContext());

       // RequestQueue requestQueue1 = Volley.newRequestQueue(getApplicationContext());

        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();
        StringRequest myReq = new StringRequest(Request.Method.GET, ServerConstants.FETCH_COLLEGE_DATA_FOR_EDIT+userClass.getUserId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject college_details=jsonObject.getJSONObject("college_details");
                            if(college_details.getString("full_name")!=null && !college_details.getString("full_name").equals("null")){
                                nameOfCollege.setText(college_details.getString("full_name"));
                                collegeName.setText(college_details.getString("full_name"));
                            }
                            if(college_details.getString("email")!=null && !college_details.getString("email").equals("null")){
                                email.setText(college_details.getString("email"));
                            }
                            if(college_details.getString("phone_number")!=null && !college_details.getString("phone_number").equals("null")){
                                mobileNumber.setText(college_details.getString("phone_number"));
                            }

                            JSONObject basic_details=college_details.getJSONObject("basic_details");

                            if(basic_details.getString("short_bio")!=null && !basic_details.getString("short_bio").equals("null")){
                                shortBio.setText(basic_details.getString("short_bio"));
                            }
                            if(basic_details.getString("facebook_link")!=null && !basic_details.getString("facebook_link").equals("null")){
                                facbook.setText(basic_details.getString("facebook_link"));
                            }
                            if(basic_details.getString("twitter_link")!=null && !basic_details.getString("twitter_link").equals("null")){
                                twitter.setText(basic_details.getString("twitter_link"));
                            }
                            if(basic_details.getString("linkedin_link")!=null && !basic_details.getString("linkedin_link").equals("null")){
                                linkedin.setText(basic_details.getString("linkedin_link"));
                            }
                            if(basic_details.getString("instagram_link")!=null && !basic_details.getString("instagram_link").equals("null")){
                                instagram.setText(basic_details.getString("instagram_link"));
                            }

                            if(basic_details.getString("country_id")!=null && !basic_details.getString("country_id").equals("null")) {
                                CountryID=Integer.parseInt(basic_details.getString("country_id"))-1;
                                StateID=Integer.parseInt(basic_details.getString("state_id"));
                                CityID=Integer.parseInt(basic_details.getString("city_id"));
                                firstTimeCity=true;
                                firstTimeState=true;
                                firsTimeLoading=true;
                                LoadCountryFromServer();
                                // LoadCountryFromServer(Integer.parseInt(userData.getCountry_id()),Integer.parseInt(userData.getState_id()),Integer.parseInt(userData.getCity_id()));
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

        //Adding request to the queue
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);

    }


}
