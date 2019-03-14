package com.user.sqrfactor.Activities;

import android.content.res.ColorStateList;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.user.sqrfactor.Adapters.ContributorsAdapter;
import com.user.sqrfactor.Constants.Constants;
import com.user.sqrfactor.Constants.SPConstants;
import com.user.sqrfactor.Extras.UtilsClass;
import com.user.sqrfactor.Fragments.ArchitectureCollege;
import com.user.sqrfactor.Fragments.ArchitectureFirms;
import com.user.sqrfactor.Fragments.ContributorsFragment;
import com.user.sqrfactor.Network.MyVolley;
import com.user.sqrfactor.Pojo.ContributorsClass;
import com.user.sqrfactor.Pojo.TokenClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Storage.MySharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class TopContributors extends AppCompatActivity {


    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private RecyclerView recyclerView;
    private ArrayList<ContributorsClass> contributorsClassArrayList =new ArrayList<>();
    private boolean isDataFetched;
    private boolean mIsVisibleToUser;
    private MySharedPreferences mSp;
    private ContributorsAdapter contributorsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_contributors);


        mSp = MySharedPreferences.getInstance(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        toolbar.setNavigationIcon(R.drawable.ic_back_black);
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
//            }
//        });

        recyclerView =findViewById(R.id.contributors_recycler);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        contributorsAdapter=new ContributorsAdapter(contributorsClassArrayList,this);
        recyclerView.setAdapter(contributorsAdapter);
        LoadData();


//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        viewPager = (ViewPager) findViewById(R.id.viewPager);
//        setupViewPager(viewPager);
//
//        tabLayout = (TabLayout) findViewById(R.id.top_tabs);
//        tabLayout.setTabTextColors(ColorStateList.valueOf(getResources().getColor(R.color.white)));
//        tabLayout.setupWithViewPager(viewPager);
    }

//    private void setupViewPager(ViewPager viewPager) {
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        adapter.addFragment(new ArchitectureCollege(), "Architecture Colleges");
//        adapter.addFragment(new ContributorsFragment(), "Top Contributors");
//        adapter.addFragment(new ArchitectureFirms(), "Architecture Firms");
//        viewPager.setAdapter(adapter);
//    }
//
//    class ViewPagerAdapter extends FragmentPagerAdapter {
//        private final List<Fragment> mFragmentList = new ArrayList<>();
//        private final List<String> mFragmentTitleList = new ArrayList<>();
//
//        public ViewPagerAdapter(FragmentManager manager) {
//            super(manager);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragmentList.size();
//        }
//
//        public void addFragment(Fragment fragment, String title) {
//            mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
//        }
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    public void LoadData()
    {

        RequestQueue  mRequestQueue = MyVolley.getInstance().getRequestQueue();
        StringRequest myReq = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"contributors",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        isDataFetched=true;
                        Log.v("ReponseFeed33", response);
//                        Toast.makeText(getActivity().getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //    JSONObject jsonObjectChat = jsonObject.getJSONObject("chats");
                            JSONArray jsonArrayData=jsonObject.getJSONArray("top_contributors");


                            for (int i = 0; i < jsonArrayData.length(); i++) {
                                //Log.v("Response",response);
                                ContributorsClass contributorsClass = new ContributorsClass(jsonArrayData.getJSONObject(i));
                                contributorsClassArrayList.add(contributorsClass);
                            }
                            contributorsAdapter.notifyDataSetChanged();



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Authorization", Constants.AUTHORIZATION_HEADER + mSp.getKey(SPConstants.API_KEY));
                return params;
            }

        };

//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
        myReq.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myReq);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
