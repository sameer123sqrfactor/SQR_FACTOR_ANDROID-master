package com.user.sqrfactor.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AboutFirmPagerAdaptor extends FragmentPagerAdapter {

    private final List<Fragment> mFragments = new ArrayList<Fragment>();
    private ArrayList<String> titleList=new ArrayList<String>() {
        {
            add("Personal Info");
            add("Basic Details");
            add("Company/Firm Details");
            add("Employee/Member Details");
            add("Documents");

        }
    };



    public AboutFirmPagerAdaptor(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}
