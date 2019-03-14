package com.user.sqrfactor.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.user.sqrfactor.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class BluePrintFragment extends Fragment {


    public BluePrintFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_blue_print, container, false);
    return v;
    }

}
