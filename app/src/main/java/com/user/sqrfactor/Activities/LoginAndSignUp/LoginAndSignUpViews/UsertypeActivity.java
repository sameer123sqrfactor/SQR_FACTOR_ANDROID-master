package com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpViews;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.user.sqrfactor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsertypeActivity extends AppCompatActivity {

    private Button individual_btn,company_firm_btn,college_btn,organisation_btn;

    private OnUserTypeFragmentInteractionListener mListener;
    public UsertypeActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_type);
        individual_btn=findViewById(R.id.individual_btn);
        company_firm_btn=findViewById(R.id.company_firm_btn);
        college_btn=findViewById(R.id.college_btn);
        organisation_btn=findViewById(R.id.organisation_btn);

        individual_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),IndividualsSignUpActivity.class));
            }
        });
        company_firm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CompanySignUpActivity.class));
            }
        });
        college_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), OnCollegeSignUp.class));
            }
        });
        organisation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),OrganisationSignup.class));
            }
        });


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
    public interface OnUserTypeFragmentInteractionListener {
        // TODO: Update argument type and name
        void onUserTypeFragmentInteraction(String userType);
    }

}
