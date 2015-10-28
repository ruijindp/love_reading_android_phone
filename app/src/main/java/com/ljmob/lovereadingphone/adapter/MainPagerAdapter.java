package com.ljmob.lovereadingphone.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ljmob.lovereadingphone.fragment.IndexFragment;
import com.ljmob.lovereadingphone.fragment.RankFragment;
import com.ljmob.lovereadingphone.fragment.RecommendFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by london on 15/10/26.
 * 首页
 */
public class MainPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> fragments;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
        IndexFragment indexFragment = new IndexFragment();
        fragments.add(indexFragment);
        RecommendFragment recommendFragment = new RecommendFragment();
        fragments.add(recommendFragment);
        RankFragment rankFragment = new RankFragment();
        fragments.add(rankFragment);
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
