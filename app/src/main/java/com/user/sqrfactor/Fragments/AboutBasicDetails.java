package com.user.sqrfactor.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.Tracker;
import com.user.sqrfactor.Activities.About;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Constants.ServerConstants;
import com.user.sqrfactor.Extras.UserClass;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.FirmDetails;
import com.user.sqrfactor.Pojo.Work_Individuals_User_Details_Class;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutBasicDetails extends Fragment implements About.OnAboutBasicDataReceivedListener {

    Work_Individuals_User_Details_Class work_individuals_user_details_class=null;
    FirmDetails firmDetails=null;
    private MySharedPreferences mSp;

    TextView aboutName,aboutFollowers,aboutFollowing,about_city_text,about_shot_bio_text,about_company_type_text,about_company_name_text;
    TextView email,phoneNumber,about_nameofCompany_text,about_mobile_number_text,about_email_text,about_country_text,about_state_text,
            about_company_register_num_text;
    Toolbar toolbar;
    private int userId;


    private LinearLayout employee_member_details,ind_other_details,
            ind_professional_details,ind_basic_details,personal_info,basic_details,
            company_firm_details,basic_details_firm,ind_education,indviduals_basic_details,firm_basic_details;
    String usertype;
    TextView about_facebook_text,about_twitter_text,about_linkedin_text,about_instagram_text,employeemember_details,about_name_short_bio;


    //textview for ind_basic_details
    private TextView ind_basic_details_short_bio_text,ind_basic_details_dob_text,ind_basic_details_email_text,ind_basic_details_gender_text,
            ind_basic_details_phone_number_text,ind_basic_details_role_text,ind_basic_details_full_name_text;

    //textview for ind_proffestional details
    private TextView ind_professional_details_coa_reg_text,ind_professional_details_yearsr_since_service_text,ind_professional_details_role_text,
            ind_professional_details_company_firm_or_college_text,ind_professional_details_start_date_text,ind_professional_details_end_date_of_working_currently_text,
            ind_professional_details_salary_text;

    //textview for other details
    private TextView ind_other_details_text,ind_other_details_award_name_text,ind_other_details_project_name_text,ind_other_details_services_offered_text;

    //textview for ind_education

    private TextView ind_education_college_name_text,ind_education_startdate_enddate,ind_education_branch;

    private UserClass userClass;

    //textview for firm_basic_details
    private TextView basic_details_firm_instagram_text,basic_details_firm_linkedin_text,basic_details_firm_twitter_text,basic_details_firm_facebook_text,basic_details_firm_company_fullname_text,basic_details_firm_company_register_num_text,basic_details_firm_company_name_text
            ,basic_details_firm_company_type_text,basic_details_firm_shot_bio_text,basic_details_firm_email_text,basic_details_firm_address_text,basic_details_firm_business_description_text,basic_details_firm_pincode_text,basic_details_firm_mobile_number_text,basic_details_firm_nameofCompany_text;

    //textview for firm_member_details
    private TextView employee_firm_full_name_text,employee_firm_role_text,employee_firm_phone_number_text,employee_firm_aadhaar_id_text,employee_firm_email_text,
            employee_firm_country_text,employee_firm_state_text,employee_firm_city_text;


    private TextView basic_details_indviduals_full_name_text,basic_details_indviduals_mobile_number_text,
            basic_details_indviduals_gender_text,basic_details_indviduals_email_text,basic_details_indviduals_UID_text,
            basic_details_indviduals_address_text,basic_details_indviduals_occupation_text,basic_details_indviduals_shot_bio_text,
            basic_details_indviduals_business_description_text,basic_details_indviduals_company_type_text,
            basic_details_indviduals_company_name_text,basic_details_indviduals_company_register_num_text,
            basic_details_indviduals_facebook_text,basic_details_indviduals_twitter_text,basic_details_indviduals_linkedin_text,
            basic_details_indviduals_instagram_text;

    private Tracker mTracker;


    public AboutBasicDetails() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        About about = (About) getActivity();
        about.setOnAboutBasicDataReceivedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.basic_details, container, false);
        mSp = MySharedPreferences.getInstance(getApplicationContext());
//        Toast.makeText(getActivity(), About.work_individuals_user_details_class.toString(), Toast.LENGTH_SHORT).show();

        //   if(firmDetails!=null)
        //Toast.makeText(getActivity(), About.firmDetails1.toString(), Toast.LENGTH_SHORT).show();



//        about_name_short_bio=view.findViewById(R.id.about_name_short_bio);
//        //linear layour for firm
//
//
//        employee_member_details=view.findViewById(R.id.employee_member_details);

      //  indviduals_basic_details,firm_basic_details
        firm_basic_details=view.findViewById(R.id.firm_basic_details);
        indviduals_basic_details=view.findViewById(R.id.indviduals_basic_details);

        //textview for firm_member_details
//        employee_firm_full_name_text=view.findViewById(R.id.employee_firm_full_name_text);
//        employee_firm_role_text=view.findViewById(R.id.employee_firm_role_text);
//        employee_firm_phone_number_text=view.findViewById(R.id.employee_firm_phone_number_text);
//        employee_firm_aadhaar_id_text=view.findViewById(R.id.employee_firm_aadhaar_id_text);
//        employee_firm_email_text=view.findViewById(R.id.employee_firm_email_text);
//        employee_firm_country_text=view.findViewById(R.id.employee_firm_country_text);
//        employee_firm_state_text=view.findViewById(R.id.employee_firm_state_text);
//        employee_firm_city_text=view.findViewById(R.id.employee_firm_city_text);


        //textview for basic_details_firm_
        basic_details_firm_instagram_text=view.findViewById(R.id.basic_details_firm_instagram_text);
        basic_details_firm_linkedin_text=view.findViewById(R.id.basic_details_firm_linkedin_text);
        basic_details_firm_twitter_text=view.findViewById(R.id.basic_details_firm_twitter_text);
        basic_details_firm_facebook_text=view.findViewById(R.id.basic_details_firm_facebook_text);

        basic_details_firm_company_type_text=view.findViewById(R.id.basic_details_firm_company_type_text);
        basic_details_firm_company_name_text=view.findViewById(R.id.basic_details_firm_company_name_text);
        basic_details_firm_company_register_num_text=view.findViewById(R.id.basic_details_firm_company_register_num_text);
        basic_details_firm_company_fullname_text=view.findViewById(R.id.basic_details_firm_nameofCompany_text);


        basic_details_firm_shot_bio_text=view.findViewById(R.id.basic_details_firm_shot_bio_text);
        basic_details_firm_email_text=view.findViewById(R.id.basic_details_firm_email_text);
        basic_details_firm_mobile_number_text=view.findViewById(R.id.basic_details_firm_mobile_number_text);

        basic_details_firm_pincode_text=view.findViewById(R.id.basic_details_firm_pincode_text);
        basic_details_firm_business_description_text=view.findViewById(R.id.basic_details_firm_business_description_text);
        basic_details_firm_address_text=view.findViewById(R.id.basic_details_firm_address_text);



        //textview for basic details of individuals

        basic_details_indviduals_full_name_text=view.findViewById(R.id.basic_details_indviduals_full_name_text);
        basic_details_indviduals_mobile_number_text=view.findViewById(R.id.basic_details_indviduals_mobile_number_text);
        basic_details_indviduals_gender_text=view.findViewById(R.id.basic_details_indviduals_gender_text);
        basic_details_indviduals_email_text=view.findViewById(R.id.basic_details_indviduals_email_text);
        basic_details_indviduals_UID_text=view.findViewById(R.id.basic_details_indviduals_UID_text);
        basic_details_indviduals_address_text=view.findViewById(R.id.basic_details_indviduals_address_text);
        basic_details_indviduals_occupation_text=view.findViewById(R.id.basic_details_indviduals_occupation_text);
        basic_details_indviduals_shot_bio_text=view.findViewById(R.id.basic_details_indviduals_shot_bio_text);


        basic_details_indviduals_business_description_text=view.findViewById(R.id.basic_details_indviduals_business_description_text);
        basic_details_indviduals_company_type_text=view.findViewById(R.id.basic_details_indviduals_company_type_text);
        basic_details_indviduals_company_name_text=view.findViewById(R.id.basic_details_indviduals_company_name_text);
        basic_details_indviduals_company_register_num_text=view.findViewById(R.id.basic_details_indviduals_company_register_num_text);
        basic_details_indviduals_facebook_text=view.findViewById(R.id.basic_details_indviduals_facebook_text);
        basic_details_indviduals_twitter_text=view.findViewById(R.id.basic_details_indviduals_twitter_text);
        basic_details_indviduals_linkedin_text=view.findViewById(R.id.basic_details_indviduals_linkedin_text);
        basic_details_indviduals_instagram_text=view.findViewById(R.id.basic_details_indviduals_instagram_text);



        //



//        //linear layout for work_individuals
//        ind_basic_details=view.findViewById(R.id.ind_basic_details);
//        ind_professional_details=view.findViewById(R.id.ind_professional_details);
//        ind_other_details=view.findViewById(R.id.ind_other_details);
//        ind_education=view.findViewById(R.id.ind_education);
//
//        //textview for ind_education
//        ind_education_branch=view.findViewById(R.id.ind_education_branch);
//        ind_education_startdate_enddate=view.findViewById(R.id.ind_education_startdate_enddate);
//        ind_education_college_name_text=view.findViewById(R.id.ind_education_college_name_text);
//
//
//
//        //textview for other details
//        ind_other_details_text=view.findViewById(R.id.ind_other_details_award_text);
//        ind_other_details_award_name_text=view.findViewById(R.id.ind_other_details_award_name_text);
//        ind_other_details_project_name_text=view.findViewById(R.id.ind_other_details_project_name_text);
//        ind_other_details_services_offered_text=view.findViewById(R.id.ind_other_details_services_offered_text);
//
//
//
//        //textview for for ind_proffestional details
//        ind_professional_details_coa_reg_text=view.findViewById(R.id.ind_professional_details_coa_reg_text);
//        ind_professional_details_yearsr_since_service_text=view.findViewById(R.id.ind_professional_details_yearsr_since_service_text);
//        ind_professional_details_role_text=view.findViewById(R.id.ind_professional_details_role_text);
//        ind_professional_details_company_firm_or_college_text=view.findViewById(R.id.ind_professional_details_company_firm_or_college_text);
//        ind_professional_details_start_date_text=view.findViewById(R.id.ind_professional_details_start_date_text);
//        ind_professional_details_end_date_of_working_currently_text=fview.indViewById(R.id.ind_professional_details_end_date_of_working_currently_text);
//        ind_professional_details_salary_text=view.findViewById(R.id.ind_professional_details_salary_text);
//

//        //textview for ind_basic_details
//        ind_basic_details_short_bio_text=view.findViewById(R.id.ind_basic_details_short_bio_text);
//        ind_basic_details_dob_text=view.findViewById(R.id.ind_basic_details_dob_text);
//        ind_basic_details_email_text=view.findViewById(R.id.ind_basic_details_email_text);
//        ind_basic_details_gender_text=view.findViewById(R.id.ind_basic_details_gender_text);
//        ind_basic_details_phone_number_text=view.findViewById(R.id.ind_basic_details_phone_number_text);
//        ind_basic_details_role_text=view.findViewById(R.id.ind_basic_details_role_text);
//        ind_basic_details_full_name_text=view.findViewById(R.id.ind_basic_details_full_name_text);
//
//
//
//
//        employeemember_details=view.findViewById(R.id.employeemember_details);
//        aboutFollowers = view.findViewById(R.id.about_followers);
//        aboutFollowing = view.findViewById(R.id.about_following);
//        // email = findViewById(R.id.about_email_text);
//        // phoneNumber = findViewById(R.id.about_phone_number_text);
//        personal_info=view.findViewById(R.id.personal_info);
//        basic_details_firm=view.findViewById(R.id.basic_details_firm);
//        // company_firm_details=findViewById(R.id.company_firm_details);
//        employee_member_details=view.findViewById(R.id.employee_member_details);



        return view;
    }




    private void BindFirmUserData(FirmDetails firmdetails) {

        firm_basic_details.setVisibility(View.VISIBLE);
        indviduals_basic_details.setVisibility(View.GONE);
        Toast.makeText(getActivity(),"called received basic",Toast.LENGTH_LONG).show();

        if(firmdetails!=null) {
            if(firmdetails.getTypes_of_firm_company()!=null && !firmdetails.getTypes_of_firm_company().equals("null")) {
                basic_details_firm_company_type_text.setText(firmdetails.getTypes_of_firm_company());

            }
            if(firmdetails.getName()!=null && !firmdetails.getName().equals("null")) {
                basic_details_firm_company_name_text.setText(firmdetails.getName());
                //Toast.makeText(getActivity(),"called received basic6",Toast.LENGTH_LONG).show();
            }

            if(firmdetails.getFirm_or_company_registration_number()!=null && !firmdetails.getFirm_or_company_registration_number().equals("null")) {
                basic_details_firm_company_register_num_text.setText(firmdetails.getFirm_or_company_registration_number());
                //Toast.makeText(getActivity(),"called received basic7",Toast.LENGTH_LONG).show();
            }
            if(firmdetails.getName()!=null && !firmdetails.getName().equals("null")) {
                basic_details_firm_company_fullname_text.setText(firmdetails.getName());
                //Toast.makeText(getActivity(),"called received basic8",Toast.LENGTH_LONG).show();
            }


            if(firmdetails.getShort_bio()!=null && !firmdetails.getShort_bio().equals("null")) {
                basic_details_firm_shot_bio_text.setText(firmdetails.getShort_bio());
               // Toast.makeText(getActivity(),"called received basic9",Toast.LENGTH_LONG).show();
            }
            if(firmdetails.getEmail()!=null && !firmdetails.getEmail().equals("null")) {
                basic_details_firm_email_text.setText(firmdetails.getEmail());
               // Toast.makeText(getActivity(),"called received basic10",Toast.LENGTH_LONG).show();
            }
            if(firmdetails.getMobile_number()!=null && !firmdetails.getMobile_number().equals("null")) {
                basic_details_firm_mobile_number_text.setText(firmdetails.getMobile_number());
              //  Toast.makeText(getActivity(),"called received basic11",Toast.LENGTH_LONG).show();
            }

            if(firmdetails.getLinkedin_link()!=null && !firmdetails.getLinkedin_link().equals("null")) {
                basic_details_firm_linkedin_text.setText(firmdetails.getLinkedin_link());
               // Toast.makeText(getActivity(),"called received basic1",Toast.LENGTH_LONG).show();
            }
            if(firmdetails.getTwitter_link()!=null && !firmdetails.getTwitter_link().equals("null")) {
                basic_details_firm_twitter_text.setText(firmdetails.getTwitter_link());
                //Toast.makeText(getActivity(),"called received basic2",Toast.LENGTH_LONG).show();
            }
            if(firmdetails.getFacebook_link()!=null && !firmdetails.getFacebook_link().equals("null")) {
                basic_details_firm_facebook_text.setText(firmdetails.getFacebook_link());
               // Toast.makeText(getActivity(),"called received basic3",Toast.LENGTH_LONG).show();
            }
            if(firmdetails.getInstagram_link()!=null && !firmdetails.getInstagram_link().equals("null")) {
                basic_details_firm_instagram_text.setText(firmdetails.getInstagram_link());
                //Toast.makeText(getActivity(),"called received basic4",Toast.LENGTH_LONG).show();
            }

//        if(firmdetails.getPinCode()!=null && !firmdetails.getPinCode().equals("null")) {
//            basic_details_firm_pincode_text.setText(firmdetails.getMobile_number());
//        }
//
//        if(firmdetails.get!=null && !firmdetails.getMobile_number().equals("null")) {
//            basic_details_firm_business_description_text.setText(firmdetails.getMobile_number());
//        }

            if(firmdetails.getEmployee_country()!=null && !firmdetails.getEmployee_country().equals("null")) {
                basic_details_firm_address_text.setText(firmdetails.getEmployee_city()+" , "+firmdetails.getEmployee_country());
            }
        }


//
//        if(firmdetails.getFirm_or_company_name()!=null && !firmdetails.getFirm_or_company_name().equals("null")) {
//            basic_details_firm_nameofCompany_text.setText(firmdetails.getFirm_or_company_name());
//        }
//        if(firmdetails.getFirst_name()!=null && !firmdetails.getFirst_name().equals("null")&& firmdetails.getLast_name()!=null && !firmdetails.getLast_name().equals("null")) {
//            employee_firm_full_name_text.setText(firmdetails.getFirst_name()+" "+firmdetails.getLast_name());
//        }
//        if(firmdetails.getRole()!=null && !firmdetails.getRole().equals("null")) {
//            employee_firm_role_text.setText(firmdetails.getRole());
//        }
//        if(firmdetails.getPhone_number()!=null && !firmdetails.getPhone_number().equals("null")) {
//            employee_firm_phone_number_text.setText(firmdetails.getPhone_number());
//        }
//        if(firmdetails.getAadhar_id()!=null && !firmdetails.getAadhar_id().equals("null")) {
//            employee_firm_aadhaar_id_text.setText(firmdetails.getAadhar_id());
//        }
//        if(firmdetails.getEmployee_email()!=null && !firmdetails.getEmployee_email().equals("null")) {
//            employee_firm_email_text.setText(firmdetails.getEmployee_email());
//        }
//        if(firmdetails.getEmployee_city()!=null && !firmdetails.getEmployee_city().equals("null"));{
//            employee_firm_city_text.setText(firmdetails.getEmployee_city()+"");
//        }
//        if(firmdetails.getEmployee_state()!=null && !firmdetails.getEmployee_state().equals("null")) {
//            employee_firm_state_text.setText(firmdetails.getEmployee_state()+"");
//        }
//        if(firmdetails.getEmployee_country()!=null && !firmdetails.getEmployee_country().equals("null")) {
//            employee_firm_country_text.setText(firmdetails.getEmployee_country()+"");
//        }

    }

    private void BindIndividualUserData(Work_Individuals_User_Details_Class work_individuals_user_details_class) {
//        ind_basic_details.setVisibility(View.VISIBLE);
//        ind_professional_details.setVisibility(View.VISIBLE);
//        ind_other_details.setVisibility(View.VISIBLE);
//        ind_education.setVisibility(View.VISIBLE);
//        personal_info.setVisibility(View.VISIBLE);
//        aboutName.setText(work_individuals_user_details_class.getFull_name());

//
//        if(!work_individuals_user_details_class.getFollowerCount().equals("null")) {
//            aboutFollowers.setText(work_individuals_user_details_class.getFollowerCount());
//        }
//        if(!work_individuals_user_details_class.getFollowingCount().equals("null")) {
//            aboutFollowing.setText(work_individuals_user_details_class.getFollowerCount());
//        }
//        if(work_individuals_user_details_class.getShort_bio()!=null && !work_individuals_user_details_class.getShort_bio().equals("null")) {
//            about_name_short_bio.setText(work_individuals_user_details_class.getShort_bio());
//        }


        firm_basic_details.setVisibility(View.GONE);

        indviduals_basic_details.setVisibility(View.VISIBLE);



        if(work_individuals_user_details_class.getShort_bio()!=null && !work_individuals_user_details_class.getShort_bio().equals("null")) {
            basic_details_firm_shot_bio_text.setText(work_individuals_user_details_class.getShort_bio());
        }

//        if(work_individuals_user_details_class.getDate_of_birth()!=null && !work_individuals_user_details_class.getDate_of_birth().equals("null")) {
//            .setText(work_individuals_user_details_class.getDate_of_birth());
//        }
        if(work_individuals_user_details_class.getEmail()!=null && !work_individuals_user_details_class.getEmail().equals("null")) {
            basic_details_indviduals_email_text.setText(work_individuals_user_details_class.getEmail());
        }
        if(work_individuals_user_details_class.getGender()!=null && !work_individuals_user_details_class.getGender().equals("null")) {
            basic_details_indviduals_gender_text.setText(work_individuals_user_details_class.getGender());
        }
        if(work_individuals_user_details_class.getPhone_number()!=null && !work_individuals_user_details_class.getPhone_number().equals("null")) {
            basic_details_indviduals_mobile_number_text.setText(work_individuals_user_details_class.getPhone_number());
        }
        if(work_individuals_user_details_class.getOccupation()!=null && !work_individuals_user_details_class.getOccupation().equals("null")) {
            basic_details_indviduals_occupation_text.setText(work_individuals_user_details_class.getOccupation());
        }

        if(work_individuals_user_details_class.getFull_name()!=null && !work_individuals_user_details_class.getFull_name().equals("null")) {
            basic_details_indviduals_full_name_text.setText(work_individuals_user_details_class.getFull_name());
        }

////
//        if(work_individuals_user_details_class.()!=null && !work_individuals_user_details_class.getFull_name().equals("null")) {
//            basic_details_indviduals_full_name_text.setText(work_individuals_user_details_class.getFull_name());
//        }
//        if(work_individuals_user_details_class.getFull_name()!=null && !work_individuals_user_details_class.getFull_name().equals("null")) {
//            basic_details_indviduals_full_name_text.setText(work_individuals_user_details_class.getFull_name());
//        }
//        if(work_individuals_user_details_class.getFull_name()!=null && !work_individuals_user_details_class.getFull_name().equals("null")) {
//            basic_details_indviduals_full_name_text.setText(work_individuals_user_details_class.getFull_name());
//        }
//



//        if(work_individuals_user_details_class.getRole()!=null && !work_individuals_user_details_class.getRole().equals("null")) {
//            ind_professional_details_role_text.setText(work_individuals_user_details_class.getRole());
//        }
//        if(work_individuals_user_details_class.getCompany_firm_or_college_university()!=null && !work_individuals_user_details_class.getCompany_firm_or_college_university().equals("null")) {
//            ind_professional_details_company_firm_or_college_text.setText(work_individuals_user_details_class.getCompany_firm_or_college_university());
//        }
//        if(work_individuals_user_details_class.getStart_date()!=null && !work_individuals_user_details_class.getStart_date().equals("null")) {
//            ind_professional_details_start_date_text.setText(work_individuals_user_details_class.getStart_date());
//        }
//        if(work_individuals_user_details_class.getEnd_date_of_working_currently()!=null && !work_individuals_user_details_class.getEnd_date_of_working_currently().equals("null")) {
//            ind_professional_details_end_date_of_working_currently_text.setText(work_individuals_user_details_class.getEnd_date_of_working_currently());
//        }
//        if(work_individuals_user_details_class.getSalary_stripend()!=null && !work_individuals_user_details_class.getSalary_stripend().equals("null")) {
//            ind_professional_details_salary_text.setText(work_individuals_user_details_class.getSalary_stripend());
//        }
//        if(work_individuals_user_details_class.getYears_since_service()!=null && !work_individuals_user_details_class.getYears_since_service().equals("null")) {
//            ind_professional_details_yearsr_since_service_text.setText(work_individuals_user_details_class.getYears_since_service());
//        }
//
//
//        // private TextView ind_other_details_text,ind_other_details_award_name_text,ind_other_details_project_name_text,ind_other_details_services_offered_text;
//        if(work_individuals_user_details_class.getAward()!=null && !work_individuals_user_details_class.getAward().equals("null")) {
//            ind_other_details_text.setText(work_individuals_user_details_class.getAward());
//        }
//        if(work_individuals_user_details_class.getAward_name()!=null && !work_individuals_user_details_class.getAward_name().equals("null")) {
//            ind_other_details_award_name_text.setText(work_individuals_user_details_class.getAward_name());
//        }
//        if(work_individuals_user_details_class.getProject_name()!=null && !work_individuals_user_details_class.getProject_name().equals("null")) {
//            ind_other_details_project_name_text.setText(work_individuals_user_details_class.getProject_name());
//        }
//        if(work_individuals_user_details_class.getServices_offered()!=null && !work_individuals_user_details_class.getServices_offered().equals("null")) {
//            ind_other_details_services_offered_text.setText(work_individuals_user_details_class.getServices_offered());
//        }
//        if(work_individuals_user_details_class.getCollege_university()!=null && !work_individuals_user_details_class.getCollege_university().equals("null")) {
//            ind_education_college_name_text.setText(work_individuals_user_details_class.getCollege_university());
//        }
//        if(work_individuals_user_details_class.getYear_of_admission()!=null && !work_individuals_user_details_class.getYear_of_admission().equals("null")) { {
//            ind_education_startdate_enddate.setText(work_individuals_user_details_class.getYear_of_admission());
//        }
//            if(!work_individuals_user_details_class.getYear_of_graduation().equals("null")) {
//                ind_education_startdate_enddate.setText(work_individuals_user_details_class.getYear_of_admission()+"-"+work_individuals_user_details_class.getYear_of_graduation());
//            }
//        }
//        if(work_individuals_user_details_class.getCourse()!=null && !work_individuals_user_details_class.getCourse().equals("null")) {
//            ind_education_branch.setText(work_individuals_user_details_class.getCourse());
//        }
    }


    @Override
    public void onDataReceived(FirmDetails firmDetails, Work_Individuals_User_Details_Class work_individuals_user_details_class) {

        if(work_individuals_user_details_class!=null){
            BindIndividualUserData(work_individuals_user_details_class);
        }else if(firmDetails!=null) {
            BindFirmUserData(firmDetails);
        }

    }
}
