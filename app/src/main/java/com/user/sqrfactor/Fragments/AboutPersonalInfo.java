package com.user.sqrfactor.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.user.sqrfactor.Activities.About;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Pojo.FirmDetails;
import com.user.sqrfactor.Pojo.Work_Individuals_User_Details_Class;
import com.user.sqrfactor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutPersonalInfo extends Fragment implements About.OnAboutPersonalInfoDataReceivedListener {

  private TextView about_name,about_followers,about_following;

  private Work_Individuals_User_Details_Class work_individuals_user_details_class;
  private FirmDetails firmDetails;
    public AboutPersonalInfo() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        About about = (About) getActivity();
        about.setOnAboutPersonalInfoDataReceivedListener(this);
    }




//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        About about = (About)context;
//        about.setOnAboutPersonalInfoDataReceivedListener(this);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.personal_info, container, false);
        about_name=view.findViewById(R.id.about_name);
        about_followers=view.findViewById(R.id.about_followers);
        about_following=view.findViewById(R.id.about_following);


        return view;
    }



    @Override
    public void onDataReceived(FirmDetails firmDetails, Work_Individuals_User_Details_Class work_individuals_user_details_class1) {
       // Toast.makeText(getActivity(),"called received",Toast.LENGTH_LONG).show();

        if(work_individuals_user_details_class1!=null){
           // Work_Individuals_User_Details_Class work_individuals_user_details_class1=About.work_individuals_user_details_class;
            if(work_individuals_user_details_class1.getFull_name()!=null && !work_individuals_user_details_class1.getFull_name().equals("null")) {
                //Toast.makeText(getActivity(),work_individuals_user_details_class1.getFull_name(),Toast.LENGTH_LONG).show();
                about_name.setText(work_individuals_user_details_class1.getFull_name());
                //about_name.setText("sameer");
            }
            if(work_individuals_user_details_class1.getFollowerCount()!=null && work_individuals_user_details_class1.getFollowerCount().equals("null")) {
               // Toast.makeText(getActivity(),work_individuals_user_details_class.getFull_name(),Toast.LENGTH_LONG).show();
                about_followers.setText(work_individuals_user_details_class1.getFollowerCount()+"");
            }
            if(work_individuals_user_details_class1.getFollowingCount()!=null && work_individuals_user_details_class1.getFollowingCount().equals("null")) {
                //Toast.makeText(getActivity(),work_individuals_user_details_class.getFull_name(),Toast.LENGTH_LONG).show();

                about_following.setText(work_individuals_user_details_class1.getFollowingCount()+"");
            }

        }else if(firmDetails!=null){
            if(firmDetails.getName()!=null && !firmDetails.getName().equals("null")) {

                about_name.setText(firmDetails.getName());
            }
            if(firmDetails.getFollowers()!=0) {
                about_followers.setText(firmDetails.getFollowers()+"");
            }
            if(firmDetails.getFollowing()!=0){
                about_following.setText(firmDetails.getFollowing()+"");
            }
        }

}
}
