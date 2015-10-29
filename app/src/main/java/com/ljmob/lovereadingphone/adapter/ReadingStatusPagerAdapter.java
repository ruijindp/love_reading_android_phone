package com.ljmob.lovereadingphone.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ljmob.lovereadingphone.fragment.NotRatedResultFragment;
import com.ljmob.lovereadingphone.fragment.RatedResultFragment;
import com.ljmob.lovereadingphone.fragment.RecorderFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by london on 15/10/29.
 * 朗读界面状态切换
 */
public class ReadingStatusPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> fragments;

    public ReadingStatusPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
        fragments.add(new RecorderFragment());
        fragments.add(new NotRatedResultFragment());
        fragments.add(new RatedResultFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
