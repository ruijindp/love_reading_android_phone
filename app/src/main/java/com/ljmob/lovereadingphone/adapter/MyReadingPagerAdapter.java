package com.ljmob.lovereadingphone.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ljmob.lovereadingphone.fragment.MyReadingFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by london on 15/10/27.
 * 我的朗读
 */
public class MyReadingPagerAdapter extends FragmentPagerAdapter {
    List<MyReadingFragment> myReadingFragments;

    public MyReadingPagerAdapter(FragmentManager fm, boolean isRated) {
        super(fm);
        myReadingFragments = new ArrayList<>();
        MyReadingFragment notRatedFragment = new MyReadingFragment();
        notRatedFragment.type = isRated ?
                MyReadingFragment.Type.rated :
                MyReadingFragment.Type.notRated;
        myReadingFragments.add(notRatedFragment);
        MyReadingFragment ratedFragment = new MyReadingFragment();
        ratedFragment.type = isRated ?
                MyReadingFragment.Type.rated :
                MyReadingFragment.Type.notRated;
        myReadingFragments.add(ratedFragment);
    }

    @Override
    public Fragment getItem(int position) {
        return myReadingFragments.get(position);
    }

    @Override
    public int getCount() {
        return myReadingFragments.size();
    }
}
