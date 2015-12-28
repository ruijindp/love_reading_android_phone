package com.happysong.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.happysong.android.context.FirstRunFragment;

/**
 * Created by london on 15/11/18.
 * 首次运行引导界面
 */
public class FirstRunAdapter extends FragmentPagerAdapter {
    FirstRunFragment[] fragments;

    public FirstRunAdapter(FragmentManager fm, FirstRunFragment... fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
