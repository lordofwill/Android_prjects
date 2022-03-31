package com.holymoly.coronasupporter.hospital;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.holymoly.coronasupporter.R;

public class HospitalFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hospital, container, false);

        ViewPager pager = view.findViewById(R.id.pager);
        FragmentHospitalPagerAdapter adapter = new FragmentHospitalPagerAdapter(getActivity().getSupportFragmentManager());
        pager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager);
        return view;
    }


}