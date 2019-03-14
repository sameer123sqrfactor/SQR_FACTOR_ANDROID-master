package com.user.sqrfactor.Activities.SettingModule.SettingViews.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.user.sqrfactor.Activities.OtherDetailsActivity;
import com.user.sqrfactor.Activities.ProfileActivity;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Fragments.MDToast;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.TokenClass;
import com.user.sqrfactor.Pojo.allOtherDetailsClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingIndividualOtherDetails.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SettingIndividualOtherDetails extends Fragment {

    private MySharedPreferences mSp;
    private OnFragmentInteractionListener mListener;
    private LinearLayout newForm;
    private Button add,remove,save_otherDetails;
    private TextInputLayout startDate,endDate;
    private Boolean isClicked= false;
    private Toolbar toolbar;
    private EditText awardsText,awardsNameText,projectText,service_offered,awardsText1,awardsNameText1,projectText1,service_offered1;
    private Context context;


    public SettingIndividualOtherDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_setting_individual_other_details, container, false);

//        toolbar = (Toolbar) findViewById(R.id.other_toolbar);
//        toolbar.setTitle("Profile");
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.back_arrow);

        mSp = MySharedPreferences.getInstance(context);

        add=(Button)view.findViewById(R.id.Add_otherDetails);
        remove=(Button)view.findViewById(R.id.Remove_other);
        newForm=(LinearLayout)view.findViewById(R.id.linear_other);

        awardsText=(EditText)view.findViewById(R.id.awardsText);
        awardsText1=(EditText)view.findViewById(R.id.awardsText1);
        awardsNameText=(EditText)view.findViewById(R.id.awardsNameText);
        awardsNameText1=(EditText)view.findViewById(R.id.awardsNameText1);
        projectText=(EditText)view.findViewById(R.id.projectText);
        projectText1=(EditText)view.findViewById(R.id.projectText1);
        service_offered=(EditText)view.findViewById(R.id.other_service_offered);
        service_offered1=(EditText)view.findViewById(R.id.service_offered1);

        save_otherDetails=(Button)view.findViewById(R.id.save_otherDetails);


        save_otherDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(awardsText.getText().toString()))
                {
                    awardsText.setError("Awards is Required");
                    //MDToast.makeText(OtherDetailsActivity.this, "Awards is Required", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                }
                else if(TextUtils.isEmpty(awardsNameText.getText().toString()))
                {
                    awardsNameText.setError("Awards Name is Required");
                    //MDToast.makeText(OtherDetailsActivity.this, "Awards Name is Required", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                }
                else if(TextUtils.isEmpty(projectText.getText().toString()))
                {
                    projectText.setError("Project Name is Required");
//                    MDToast.makeText(OtherDetailsActivity.this, "Project Name is Required", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                }
                else if(TextUtils.isEmpty(service_offered.getText().toString()))
                {
                    service_offered.setError("Services Offered is Required");
                    //MDToast.makeText(OtherDetailsActivity.this, "Services Offered is Required", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                }
                else {
                    SaveDataTOServer();
                }
//


            }
        });

        newForm.setVisibility(View.GONE);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isClicked)
                {
                    newForm.setVisibility(View.VISIBLE);
                    isClicked=true;
                }

            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newForm!=null && isClicked)
                {
                    newForm.setVisibility(View.GONE);
                    isClicked=false;
                }
            }
        });

        BindDataToViewFromServer();

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


    public void SaveDataTOServer()
    {
//
//        RequestQueue requestQueue1 = Volley.newRequestQueue(getApplicationContext());

        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();

        StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"parse/work-other-detail",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
//                        Log.v("ResponseLike",s);
//                        Toast.makeText(OtherDetailsActivity.this,s,Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            awardsNameText.setText("");
                            awardsNameText1.setText("");
                            awardsText.setText("");
                            awardsText1.setText("");
                            projectText.setText("");
                            projectText1.setText("");
                            service_offered.setText("");
                            service_offered1.setText("");
                            Intent intent=new Intent(context, ProfileActivity.class);
                            startActivity(intent);

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
              //  params.put("Authorization", "Bearer "+TokenClass.Token);
                params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));

                return params;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                params.put("award[0]",awardsText.getText().toString());
                // params.put("award[1]",awardsText1.getText().toString());
                params.put("award_name[0]",awardsNameText.getText().toString());
                // params.put("award_name[1]",awardsNameText1.getText().toString());
                params.put("project_name[0]",projectText.getText().toString());
                // params.put("project_name[1]",projectText1.getText().toString());
                params.put("services_offered[0]",service_offered.getText().toString());

                if(!TextUtils.isEmpty(awardsText1.getText().toString()))
                {
                    params.put("award[1]",awardsNameText1.getText().toString());
                }

                if(!TextUtils.isEmpty(awardsNameText1.getText().toString()))
                {
                    params.put("award_name[1]",awardsNameText1.getText().toString());
                }
//


                if(!TextUtils.isEmpty(projectText1.getText().toString()))
                {
                    params.put("project_name[1]",projectText1.getText().toString());
                }




                if(!TextUtils.isEmpty(service_offered1.getText().toString()))
                {
                    params.put("services_offered[1]",service_offered1.getText().toString());
                }



                return params;
            }
        };

        //Adding request to the queue
        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);

    }

    private void BindDataToViewFromServer() {
       // RequestQueue requestQueue1 = Volley.newRequestQueue(getApplicationContext());
        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();
        StringRequest myReq = new StringRequest(Request.Method.GET, UtilsClass.baseurl+"profile/edit/other-details",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
//                        Log.v("ResponseLike",s);
                        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray allOtherDetails=jsonObject.getJSONArray("allOtherDetail");

                            for(int i=0;i<allOtherDetails.length();i++)
                            {
                                allOtherDetailsClass allOtherDetailsClass1=new allOtherDetailsClass(allOtherDetails.getJSONObject(i));
                                if(i==0)
                                {
                                    awardsText.setText(allOtherDetailsClass1.getAward());
                                    awardsNameText.setText(allOtherDetailsClass1.getAward_name());
                                    projectText.setText(allOtherDetailsClass1.getProject_name());
                                    service_offered.setText(allOtherDetailsClass1.getServices_offered());
                                }
                                if (i==1)
                                {
                                    newForm.setVisibility(View.VISIBLE);
                                    awardsText1.setText(allOtherDetailsClass1.getAward());
                                    awardsNameText1.setText(allOtherDetailsClass1.getAward_name());
                                    projectText1.setText(allOtherDetailsClass1.getProject_name());
                                    service_offered1.setText(allOtherDetailsClass1.getServices_offered());
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
               // params.put("Authorization", "Bearer "+ TokenClass.Token);
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
