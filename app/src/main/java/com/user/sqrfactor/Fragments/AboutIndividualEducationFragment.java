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
public class AboutIndividualEducationFragment extends Fragment implements About.OnAboutIndividualEducationDataReceived {


    private TextView about_course_text,about_college_text,about_admission_year_text,about_graduation_year_text;
    public AboutIndividualEducationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        About about = (About) getActivity();
        about.setAboutIndividualEducationDataListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_about_individual_education, container, false);

        about_course_text=view.findViewById(R.id.about_course_text);
        about_college_text=view.findViewById(R.id.about_college_text);
        about_admission_year_text=view.findViewById(R.id.about_admission_year_text);
        about_graduation_year_text=view.findViewById(R.id.about_graduation_year_text);
        return view;
    }

    @Override
    public void onDataReceived(FirmDetails firmDetails, Work_Individuals_User_Details_Class work_individuals_user_details_class) {
        if(work_individuals_user_details_class!=null){
           if(work_individuals_user_details_class.getCourse()!=null && !work_individuals_user_details_class.getCourse().equals("null")){
               about_course_text.setText(work_individuals_user_details_class.getCourse());
           }
           if(work_individuals_user_details_class.getCollege_university()!=null && !work_individuals_user_details_class.getCollege_university().equals("null")){
               about_college_text.setText(work_individuals_user_details_class.getCollege_university());
           }
           if(work_individuals_user_details_class.getYear_of_admission()!=null && !work_individuals_user_details_class.getYear_of_admission().equals("null")){
               about_admission_year_text.setText(work_individuals_user_details_class.getYear_of_admission());
           }
           if(work_individuals_user_details_class.getYear_of_graduation()!=null && !work_individuals_user_details_class.getYear_of_graduation().equals("null")){

               about_graduation_year_text.setText(work_individuals_user_details_class.getYear_of_graduation());
           }
        }


        Toast.makeText(getActivity(),"called received ind edu",Toast.LENGTH_LONG).show();
    }
}
