package com.user.sqrfactor.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.user.sqrfactor.Activities.About;
import com.user.sqrfactor.Pojo.FirmDetails;
import com.user.sqrfactor.Pojo.Work_Individuals_User_Details_Class;
import com.user.sqrfactor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutDocumentFragment extends Fragment  {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        About about = (About) getActivity();
      //  about.setAboutFirmDataListener(this);
    }

    public AboutDocumentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_document, container, false);
    }

//    @Override
//    public void onDataReceived(FirmDetails firmDetails, Work_Individuals_User_Details_Class work_individuals_user_details_class, boolean isIndividual) {
//        if(isIndividual)
//            Toast.makeText(getActivity(),work_individuals_user_details_class.toString(),Toast.LENGTH_LONG).show();
//        else
//            Toast.makeText(getActivity(),firmDetails.toString(),Toast.LENGTH_LONG).show();
//    }
}
