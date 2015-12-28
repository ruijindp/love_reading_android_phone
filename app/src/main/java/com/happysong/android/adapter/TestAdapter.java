package com.happysong.android.adapter;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by london on 15/10/22.
 * test adapter
 */
public class TestAdapter extends BaseAdapter {
    @LayoutRes
    int itemRes;

    public TestAdapter(@LayoutRes int itemRes) {
        this.itemRes = itemRes;
    }

    @Override
    public int getCount() {
        return 50;
    }

    @Override
    public Object getItem(int position) {
        return itemRes;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(itemRes, parent, false);
        }
        return convertView;
    }
}
