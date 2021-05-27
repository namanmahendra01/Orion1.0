package com.orion.orion.Adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SectionStatePagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mfragmentlist = new ArrayList<>();
    private final HashMap<Fragment,Integer> mfragments =new HashMap<>();
    private final HashMap<String,Integer> mfragmentnummbers = new HashMap<>();
    private final HashMap<Integer,String> mfragmentNames = new HashMap<>();

    public SectionStatePagerAdapter(@NonNull FragmentManager fm) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return -1;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mfragmentlist.get(position);
    }

    @Override
    public int getCount() {
        return mfragmentlist.size();
    }
    public void addFragment(Fragment fragment ,String fragmentname){
        mfragmentlist.add(fragment);
        mfragments.put(fragment,mfragmentlist.size()-1);
        mfragmentnummbers.put(fragmentname,mfragmentlist.size()-1);
        mfragmentNames.put(mfragmentlist.size()-1,fragmentname);

    }
//    return the fragment with name
    public Integer getFragmentNumber(String FragmentName){
        if(mfragmentnummbers.containsKey(FragmentName)) return mfragmentnummbers.get(FragmentName);
        else return null;

    }
    //    return the fragment with name
    public Integer getFragmentNumber(Fragment fragment){
        if(mfragmentnummbers.containsKey(fragment)) return mfragmentnummbers.get(fragment);
        else return null;

    }
    //    return the fragment with name
    public String getFragmentName(Integer fragmentnumber){
        if(mfragmentNames.containsKey(fragmentnumber)) return mfragmentNames.get(fragmentnumber);
        else return null;

    }
}
