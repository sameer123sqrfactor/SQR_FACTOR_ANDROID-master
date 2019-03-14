package com.user.sqrfactor.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.user.sqrfactor.Activities.ContributorsActivity;
import com.user.sqrfactor.Activities.HomeScreen;
import com.user.sqrfactor.Activities.TopContributors;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Activities.RedActivity;
import com.user.sqrfactor.Extras.UserClass;

import static android.content.Context.MODE_PRIVATE;

public class NewsFeedFragment extends Fragment {


    private EditText writePost;
    private ImageView profileImage;
    Button button1,button2,button3;
    public int flag=0;
    private Context context;

    public NewsFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = (View)inflater.inflate(R.layout.fragment_news_feed, container, false);

        getChildFragmentManager().beginTransaction().replace(R.id.fragment, new StatusFragment()).addToBackStack(null).commit();

//        SharedPreferences mPrefs = getActivity().getSharedPreferences("User",MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = mPrefs.getString("MyObject", "");
//        UserClass userClass = gson.fromJson(json, UserClass.class);


        button1 = view.findViewById(R.id.newsFeedbtn);
        button2 = view.findViewById(R.id.whatsRedbtn);
        button3 = view.findViewById(R.id.topContributorsRedbtn);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                button2.setBackgroundColor(getResources().getColor(R.color.grey_btn));
                button1.setBackgroundColor(getResources().getColor(R.color.sqr));
                getChildFragmentManager().beginTransaction().replace(R.id.fragment, new StatusFragment()).addToBackStack(null).commit();
            }


        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity().getApplicationContext(), RedActivity.class);
//                getActivity().startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);

                button2.setBackgroundColor(getResources().getColor(R.color.sqr));
                button1.setBackgroundColor(getResources().getColor(R.color.grey_btn));
                getChildFragmentManager().beginTransaction().replace(R.id.fragment, new WhatsRedFragment()).addToBackStack(null).commit();

            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TopContributors.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }
}
