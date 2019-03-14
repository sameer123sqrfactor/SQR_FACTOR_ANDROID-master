package com.user.sqrfactor.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.user.sqrfactor.Adapters.CreditsAdapter;
import com.user.sqrfactor.Pojo.CreditsClass;
import com.user.sqrfactor.R;

import java.util.ArrayList;

public class CreditsFragment extends Fragment {


    public CreditsFragment() {
        // Required empty public constructor
    }


    private RecyclerView recyclerView;
    ArrayList<CreditsClass> creditsClassArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view  = (ViewGroup)inflater.inflate(R.layout.fragment_credits, container, false);

        recyclerView=(RecyclerView) view.findViewById(R.id.recyclerView_credit);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));


        //adding dummy data for testing
//        CreditsClass creditsClass=new CreditsClass("ther sbdhsd need to ambadb","25","26","$20 reedmens");
//        creditsClassArrayList.add(creditsClass);
//        CreditsClass creditsClass1=new CreditsClass("ther sbdhsd need to ambadb","25","26","$20 reedmens");
//        creditsClassArrayList.add(creditsClass1);

        CreditsAdapter creditsAdapter=new CreditsAdapter(getContext(),creditsClassArrayList);
        recyclerView.setAdapter(creditsAdapter);

        return view;
    }

}
