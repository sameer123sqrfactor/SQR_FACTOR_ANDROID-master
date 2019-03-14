package com.user.sqrfactor.Activities.SettingModule.SettingViews.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.user.sqrfactor.Activities.ProfileActivity;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Fragments.MDToast;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.TokenClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingChangePassword.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SettingChangePassword extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Toolbar toolbar;
    private EditText oldPassword,newPassword,confirmPassword;
    private Button save;
    int counter = 0;
    Thread t =null;
    ProgressDialog dialog = null;
    private Context context;
    private MySharedPreferences mSp;

    public SettingChangePassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting_change_password, container, false);

        mSp = MySharedPreferences.getInstance(context);
        save = (Button) view.findViewById(R.id.change_password_button);
        oldPassword = (EditText) view.findViewById(R.id.old_password_text);
        newPassword = (EditText) view.findViewById(R.id.new_password_text);
        confirmPassword = (EditText) view.findViewById(R.id.confirm_password_text);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateData();

            }
        });
        return view;

    }

    public void ValidateData() {
        if (!TextUtils.isEmpty(oldPassword.getText().toString())&&!TextUtils.isEmpty(newPassword.getText().toString())&&!TextUtils.isEmpty(confirmPassword.getText().toString())) {
            saveDataToServer();
        } else {

            MDToast.makeText(context, "Your password field in empty", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
            return;
        }
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

    public void saveDataToServer() {

      //  RequestQueue requestQueue1 = Volley.newRequestQueue(getApplicationContext());

        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();
        StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"parse/changepassword",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                       // Log.v("ResponseLike",s);
                        MDToast.makeText(context, "SUCCESS", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            oldPassword.setText("");
                            newPassword.setText("");
                            confirmPassword.setText("");
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
               // params.put("Authorization", "Bearer "+ TokenClass.Token);
                params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
                return params;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("old_password",oldPassword.getText().toString());
                params.put("new_password",newPassword.getText().toString());
                params.put("new_password_confirmation",confirmPassword.getText().toString());
                return params;
            }
        };

        //Adding request to the queue
//        requestQueue1.add(stringRequest);

        myReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
