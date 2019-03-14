package com.user.sqrfactor.Activities.SettingModule.SettingViews.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.gson.Gson;
import com.user.sqrfactor.Activities.CompanyFirmDetails;
import com.user.sqrfactor.Activities.ProfileActivity;
import com.user.sqrfactor.Application.MyApplication;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Fragments.MDToast;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.UserData;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;
import com.user.sqrfactor.Storage.UserDataSharedpreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingCompanyFirmDetails.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SettingCompanyFirmDetails extends Fragment {

    private OnFragmentInteractionListener mListener;
    Toolbar toolbar;
    private MySharedPreferences mSp;
    private Tracker mTracker;

  private UserDataSharedpreferences userDataSharedpreferences=UserDataSharedpreferences.getInstance();
    private EditText yearInService,firmSize,serviceOffered,assetsServed,cityServed,awardName,projectName;
    private Button saveAllChanges;
    private Context context;

    public SettingCompanyFirmDetails() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        UserClass userClass= UtilsClass.GetUserClassFromSharedPreferences(context);
        if(userClass!=null){
            mTracker.setScreenName("CompanyFirmDetails /"+UtilsClass.getName(userClass.getFirst_name(),userClass.getLast_name(),userClass.getName(),userClass.getUser_name()));
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_setting_company_firm_details, container, false);
        //google anlytics
        MyApplication application = (MyApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

//
//        toolbar = (Toolbar) findViewById(R.id.company_firm_toolbar);
//        toolbar.setTitle("Profile");
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.back_arrow);
        mSp = MySharedPreferences.getInstance(context);

        yearInService= (EditText)view.findViewById(R.id.years_in_service_text);
        firmSize=(EditText)view.findViewById(R.id.firm_size_text);
        serviceOffered=(EditText)view.findViewById(R.id.service_offered_text);
        assetsServed=(EditText)view.findViewById(R.id.assets_served_text);
        cityServed=(EditText)view.findViewById(R.id.city_served_text);
        awardName=(EditText)view.findViewById(R.id.award_name_text);
        projectName=(EditText)view.findViewById(R.id.project_name_text);
        saveAllChanges=(Button) view.findViewById(R.id.save_changes);

        saveAllChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UtilsClass.IsConnected(context)) {
                    saveDataToServer();
                }
                else
                    MDToast.makeText(context, "Check Your Internet Connection", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
            }
        });

        GetDataFromServerAndBindtoview();
        return view;
    }



    private void GetDataFromServerAndBindtoview(){
        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();
        StringRequest myReq = new StringRequest(Request.Method.GET, "https://sqrfactor.com/api/profile/company_firm_detail",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
//                        Log.v("ResponseLike", s);
                        Toast.makeText(getApplicationContext(),"Response"+s,Toast.LENGTH_LONG).show();

                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            BindDataTOVIew(jsonObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

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

        //Adding request to the queue

        //Adding request to the queue
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);

    }

    private void BindDataTOVIew(JSONObject jsonObject)
    {
//
//        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserData",MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = sharedPreferences.getString("UserData","");
//        UserData userData = gson.fromJson(json,UserData.class);
        try {
            JSONObject company_firm_detail=jsonObject.getJSONObject("company_firm_detail");
            if(company_firm_detail!=null)
            {
                if (!company_firm_detail.getString("years_in_service").equals("null")){
                    yearInService.setText(company_firm_detail.getString("years_in_service"));
                }
                if (!company_firm_detail.getString("firm_size").equals("null")){
                    firmSize.setText(company_firm_detail.getString("firm_size"));
                }
                if (!company_firm_detail.getString("services_offered").equals("null")){
                    serviceOffered.setText(company_firm_detail.getString("services_offered"));
                }
//        if (!userData.getName_of_the_company().equals("null")){
//            serviceOffered.setText(userData.getServices_offered());
//        }
                if (!company_firm_detail.getString("asset_served").equals("null")){
                    assetsServed.setText(company_firm_detail.getString("asset_served"));
                }
                if (!company_firm_detail.getString("city_served").equals("null")){
                    cityServed.setText(company_firm_detail.getString("city_served"));
                }
                if (!company_firm_detail.getString("award_name").equals("null")){
                    awardName.setText(company_firm_detail.getString("award_name"));
                }
                if (!company_firm_detail.getString("project_name").equals("null")){
                    projectName.setText(company_firm_detail.getString("project_name"));
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public void saveDataToServer() {

//        RequestQueue requestQueue1 = Volley.newRequestQueue(getApplicationContext());
//

        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();
        StringRequest myReq = new StringRequest(Request.Method.POST, ServerConstants.BASE_URL +"profile/edit/company-firm-details",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
//                        Log.v("ResponseLike", s);
//                        Toast.makeText(context,"Response"+s,Toast.LENGTH_LONG).show();

                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            yearInService.setText("");
                            serviceOffered.setText("");
                            firmSize.setText("");
                            assetsServed.setText("");
                            cityServed.setText("");
                            awardName.setText("");
                            projectName.setText("");
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

                if (!TextUtils.isEmpty(yearInService.getText().toString())) {
                    params.put("year_in_service", yearInService.getText().toString());
                } else {
                    params.put("year_in_service", null+"");
                }

                if (!TextUtils.isEmpty(firmSize.getText().toString())) {
                    params.put("firm_size", firmSize.getText().toString());
                } else {
                    params.put("firm_size", null+"");
                }

                if (!TextUtils.isEmpty(serviceOffered.getText().toString())) {
                    params.put("services_offered", serviceOffered.getText().toString());
                } else {
                    params.put("services_offered", null+"");
                }

                if (!TextUtils.isEmpty(assetsServed.getText().toString())) {
                    params.put("asset_served", assetsServed.getText().toString());
                } else {
                    params.put("asset_served", null+"");
                }
                if (!TextUtils.isEmpty(cityServed.getText().toString())) {
                    params.put("city_served", cityServed.getText().toString());
                } else {
                    params.put("city_served", null+"");
                }
                if (!TextUtils.isEmpty(awardName.getText().toString())) {
                    params.put("award_name", awardName.getText().toString());
                } else {
                    params.put("award_name", null+"");
                }
                if (!TextUtils.isEmpty(projectName.getText().toString())) {
                    params.put("project_name", projectName.getText().toString());
                } else {
                    params.put("project_name", null+"");
                }

                return params;

            }
        };

        //Adding request to the queue

        //Adding request to the queue
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);

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
}
