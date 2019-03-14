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
public class AboutEmployeeDetails extends Fragment implements About.OnAboutEmployeeDataReceived {


    private TextView employee_firm_first_name_text,employee_firm_last_name_text,
            employee_firm_attachment_name_text,employee_firm_role_text,employee_firm_phone_number_text,
            employee_firm_aadhaar_id_text,employee_firm_email_text,employee_firm_country_text,employee_firm_state_text,
            employee_firm_city_text;
    public AboutEmployeeDetails() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        About about = (About) getActivity();
        about.setAboutEmployeeDataListener(this);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.employee_details, container, false);
        employee_firm_first_name_text=view.findViewById(R.id.employee_firm_first_name_text);
        employee_firm_last_name_text=view.findViewById(R.id.employee_firm_last_name_text);
        employee_firm_attachment_name_text=view.findViewById(R.id.employee_firm_attachment_name_text);
        employee_firm_role_text=view.findViewById(R.id.employee_firm_role_text);
        employee_firm_phone_number_text=view.findViewById(R.id.employee_firm_phone_number_text);
        employee_firm_aadhaar_id_text=view.findViewById(R.id.employee_firm_aadhaar_id_text);
        employee_firm_email_text=view.findViewById(R.id.employee_firm_email_text);
        employee_firm_country_text=view.findViewById(R.id.employee_firm_country_text);
        employee_firm_state_text=view.findViewById(R.id.employee_firm_state_text);
        employee_firm_city_text=view.findViewById(R.id.employee_firm_city_text);


        return view;
    }


//

    @Override
    public void onDataReceived(FirmDetails firmDetails, Work_Individuals_User_Details_Class work_individuals_user_details_class) {

       if(firmDetails!=null){
            Toast.makeText(getActivity(),"called received employee ",Toast.LENGTH_LONG).show();

           if(firmDetails.getFirst_name()!=null && !firmDetails.getFirst_name().equals("null")){
               Toast.makeText(getActivity(),"called received employee ",Toast.LENGTH_LONG).show();

               employee_firm_first_name_text.setText(firmDetails.getFirst_name());
           }
           if(firmDetails.getLast_name()!=null && !firmDetails.getLast_name().equals("null")){
               employee_firm_last_name_text.setText(firmDetails.getLast_name());
           }
           if(firmDetails.getAadhar_id()!=null && !firmDetails.getAadhar_id().equals("null")){
               employee_firm_aadhaar_id_text.setText(firmDetails.getAadhar_id());
           }
           if(firmDetails.getEmployee_email()!=null && !firmDetails.getEmployee_email().equals("null")){
               employee_firm_email_text.setText(firmDetails.getEmployee_email());
           }
           if(firmDetails.getEmployee_country()!=null && !firmDetails.getEmployee_country().equals("null")){
               employee_firm_country_text.setText(firmDetails.getEmployee_country());
           }
           if(firmDetails.getEmployee_city()!=null && !firmDetails.getEmployee_city().equals("null")){
               employee_firm_city_text.setText(firmDetails.getEmployee_city());
           }
           if(firmDetails.getEmployee_state()!=null && !firmDetails.getEmployee_state().equals("null")){
               employee_firm_state_text.setText(firmDetails.getEmployee_state());
           }
           if(firmDetails.getPhone_number()!=null && !firmDetails.getPhone_number().equals("null")){
               employee_firm_phone_number_text.setText(firmDetails.getPhone_number());
           }
           if(firmDetails.getRole()!=null && !firmDetails.getRole().equals("null")){
               employee_firm_role_text.setText(firmDetails.getRole());
           }

       }
        // Toast.makeText(getActivity(),"called received employee",Toast.LENGTH_LONG).show();
    }
}
