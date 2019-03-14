package com.user.sqrfactor.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.user.sqrfactor.Activities.About;
import com.user.sqrfactor.Pojo.FirmDetails;
import com.user.sqrfactor.Pojo.Work_Individuals_User_Details_Class;
import com.user.sqrfactor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutIndividualOtherDetails extends Fragment implements About.OnAboutIndividualOtherDetailsDataReceived {
    private TextView about_awards_text,about_award_name_text,about_project_name_text,about__services_offered_text;


    public AboutIndividualOtherDetails() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        About about = (About) getActivity();
        about.setAboutIndividualOtherDetailsDataListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_about_individual_other_details, container, false);
        about_awards_text=view.findViewById(R.id.about_awards_text);
        about_award_name_text=view.findViewById(R.id.about_award_name_text);
        about_project_name_text=view.findViewById(R.id.about_project_name_text);
        about__services_offered_text=view.findViewById(R.id.about__services_offered_text);

        return view;
    }

    @Override
    public void onDataReceived(FirmDetails firmDetails, Work_Individuals_User_Details_Class work_individuals_user_details_class) {
      if(work_individuals_user_details_class!=null){
          if(work_individuals_user_details_class.getAward()!=null && !work_individuals_user_details_class.getAward().equals("null")){
              about_awards_text.setText(work_individuals_user_details_class.getAward());
          }
          if(work_individuals_user_details_class.getAward_name()!=null && !work_individuals_user_details_class.getAward_name().equals("null")){
              about_award_name_text.setText(work_individuals_user_details_class.getAward_name());
          }
          if(work_individuals_user_details_class.getProject_name()!=null && !work_individuals_user_details_class.getProject_name().equals("null")){
              about_project_name_text.setText(work_individuals_user_details_class.getProject_name());
          }
          if(work_individuals_user_details_class.getServices_offered()!=null && !work_individuals_user_details_class.getServices_offered().equals("null")){
              about__services_offered_text.setText(work_individuals_user_details_class.getServices_offered());
          }
      }


       // Toast.makeText(getActivity(),"called received otherdetails",Toast.LENGTH_LONG).show();
    }
}
