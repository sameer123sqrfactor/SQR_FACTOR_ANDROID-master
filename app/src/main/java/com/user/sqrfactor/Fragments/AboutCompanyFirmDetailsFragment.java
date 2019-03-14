package com.user.sqrfactor.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.user.sqrfactor.Activities.About;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.FirmDetails;
import com.user.sqrfactor.Pojo.Work_Individuals_User_Details_Class;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutCompanyFirmDetailsFragment extends Fragment implements About.OnAboutCompanyFirmDataReceived {


    private TextView about_year_service_text,about_firm_size_text,about_service_offered_text,
            about_asset_served_text,about_city_served_text,about_award_name_text,
            about_project_name_text;
    private Context context;
    private  MySharedPreferences mSp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        About about = (About) getActivity();
        about.setAboutCompanyFirmDataListener(this);
    }

    public AboutCompanyFirmDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_about_company_firm_details, container, false);
        mSp = MySharedPreferences.getInstance(context);

        about_year_service_text=view.findViewById(R.id.about_year_service_text);
        about_firm_size_text=view.findViewById(R.id.about_firm_size_text);
        about_service_offered_text=view.findViewById(R.id.about_service_offered_text);
        about_asset_served_text=view.findViewById(R.id.about_asset_served_text);
        about_city_served_text=view.findViewById(R.id.about_city_served_text);
        about_award_name_text=view.findViewById(R.id.about_award_name_text);
        about_project_name_text=view.findViewById(R.id.about_project_name_text);
        GetDataFromServerAndBindtoview();
        return view;
    }

    @Override
    public void onDataReceived(FirmDetails firmDetails, Work_Individuals_User_Details_Class work_individuals_user_details_class) {


    }

    private void GetDataFromServerAndBindtoview(){
        RequestQueue mRequestQueue = MyVolley.getInstance().getRequestQueue();
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
                    about_year_service_text.setText(company_firm_detail.getString("years_in_service"));
                }
                if (!company_firm_detail.getString("firm_size").equals("null")){
                    about_firm_size_text.setText(company_firm_detail.getString("firm_size"));
                }
                if (!company_firm_detail.getString("services_offered").equals("null")){
                    about_service_offered_text.setText(company_firm_detail.getString("services_offered"));
                }
//        if (!userData.getName_of_the_company().equals("null")){
//            serviceOffered.setText(userData.getServices_offered());
//        }
                if (!company_firm_detail.getString("asset_served").equals("null")){
                    about_asset_served_text.setText(company_firm_detail.getString("asset_served"));
                }
                if (!company_firm_detail.getString("city_served").equals("null")){
                    about_city_served_text.setText(company_firm_detail.getString("city_served"));
                }
                if (!company_firm_detail.getString("award_name").equals("null")){
                    about_award_name_text.setText(company_firm_detail.getString("award_name"));
                }
                if (!company_firm_detail.getString("project_name").equals("null")){
                    about_project_name_text.setText(company_firm_detail.getString("project_name"));
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
