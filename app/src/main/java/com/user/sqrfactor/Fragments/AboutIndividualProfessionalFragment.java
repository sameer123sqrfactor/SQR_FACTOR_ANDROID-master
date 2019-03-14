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
public class AboutIndividualProfessionalFragment extends Fragment implements About.OnAboutIndividualProffesionalDataReceived {

    private TextView about_coa_text,about_since_service_text,about_role_text,about_company_Or_college_text,
            about_startdate_text,about_end_working_date_text,about_salary_text;

    public AboutIndividualProfessionalFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        About about = (About) getActivity();
        about.setAboutIndividualProffesionalDataListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_about_individual_professional, container, false);
        about_coa_text=view.findViewById(R.id.about_coa_text);
        about_since_service_text=view.findViewById(R.id.about_since_service_text);
        about_role_text=view.findViewById(R.id.about_role_text);
        about_company_Or_college_text=view.findViewById(R.id.about_company_Or_college_text);
        about_startdate_text=view.findViewById(R.id.about_startdate_text);
        about_end_working_date_text=view.findViewById(R.id.about_end_working_date_text);
        about_salary_text=view.findViewById(R.id.about_salary_text);

        return view;
    }

    @Override
    public void onDataReceived(FirmDetails firmDetails, Work_Individuals_User_Details_Class work_individuals_user_details_class) {
         if(work_individuals_user_details_class!=null){
//             if(work_individuals_user_details_class.!=null && !work_individuals_user_details_class.getRole().equals("null")){
//                 about_since_service_text.setText(work_individuals_user_details_class.getRole());
//             }
             if(work_individuals_user_details_class.getRole()!=null && !work_individuals_user_details_class.getRole().equals("null")){
                 about_role_text.setText(work_individuals_user_details_class.getRole());
             }
             if(work_individuals_user_details_class.getCompany_firm_or_college_university()!=null && !work_individuals_user_details_class.getCompany_firm_or_college_university().equals("null")){
                 about_company_Or_college_text.setText(work_individuals_user_details_class.getRole());
             }
             if(work_individuals_user_details_class.getStart_date()!=null && !work_individuals_user_details_class.getStart_date().equals("null")){
                 about_startdate_text.setText(work_individuals_user_details_class.getStart_date());
             }
             if(work_individuals_user_details_class.getEnd_date_of_working_currently()!=null && !work_individuals_user_details_class.getEnd_date_of_working_currently().equals("null")){
                 about_end_working_date_text.setText(work_individuals_user_details_class.getEnd_date_of_working_currently());
             }
             if(work_individuals_user_details_class.getSalary_stripend()!=null && !work_individuals_user_details_class.getSalary_stripend().equals("null")){
                 about_salary_text.setText(work_individuals_user_details_class.getSalary_stripend());
             }
         }


        Toast.makeText(getActivity(),"called received proffesinal",Toast.LENGTH_LONG).show();
    }
}
