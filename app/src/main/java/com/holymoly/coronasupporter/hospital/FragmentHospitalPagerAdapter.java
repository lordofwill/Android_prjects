package com.holymoly.coronasupporter.hospital;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class FragmentHospitalPagerAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> fragments;
    private String[] title = new String[]{"국민안심병원", "선별진료소"};

    public FragmentHospitalPagerAdapter(FragmentManager manager) {
        super(manager);
        fragments = new ArrayList<>();
        fragments.add(new FragmentHospital1());
        fragments.add(new FragmentHospital2());
    }


    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

}
