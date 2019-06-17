package com.example.reetesh_ranjan.diaryfeed.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.reetesh_ranjan.diaryfeed.R;
import com.example.reetesh_ranjan.diaryfeed.adapter.SelectionFragmentAdapter;


public class PostFragment extends Fragment {
    private View myFragment;
    private ViewPager viewPager;
    private TabLayout tabLayout;


    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragment= inflater.inflate(R.layout.fragment_post, container, false);
        viewPager=myFragment.findViewById(R.id.viewPager);
        tabLayout=myFragment.findViewById(R.id.tabLayout);
        return myFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setUpViewPager(ViewPager viewPager) {
        SelectionFragmentAdapter adapter=new SelectionFragmentAdapter(getChildFragmentManager());
        adapter.addFragment(new DraftsFragment(),"Drafts");
        adapter.addFragment(new PublishFragment(),"Publish");
        viewPager.setAdapter(adapter);
    }
}
