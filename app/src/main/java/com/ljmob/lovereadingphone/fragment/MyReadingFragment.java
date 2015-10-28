package com.ljmob.lovereadingphone.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ljmob.lovereadingphone.MyReadingActivity;
import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.adapter.MyReadingAdapter;
import com.ljmob.lovereadingphone.context.EasyLoadFragment;
import com.ljmob.lovereadingphone.entity.Result;
import com.ljmob.lovereadingphone.util.DefaultParam;
import com.londonx.lutil.entity.LResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by london on 15/10/27.
 * 我的朗读
 */
public class MyReadingFragment extends EasyLoadFragment {
    public Type type;
    View rootView;
    List<Result> results;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.view_primary_list_div_inset_72, container, false);
            setContentView(rootView);

            int startOffset = getResources().getDimensionPixelSize(R.dimen.subject_list_refresh_start_offset_tab);
            int endOffset = getResources().getDimensionPixelSize(R.dimen.subject_list_refresh_end_offset_tab);
            primarySwipeRefreshLayout.setProgressViewOffset(false, startOffset, endOffset);

            View headEmpty = inflater.inflate(R.layout.head_my_reading, primaryListView, false);

            ((ListView) primaryListView).addHeaderView(headEmpty);
            initData("", new DefaultParam());
        }
        return rootView;
    }

    @Override
    public void initData(String apiUrl, HashMap<String, Object> params) {
        super.initData(apiUrl, params);
        results = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            results.add(new Result());
        }
        primaryListView.setAdapter(new MyReadingAdapter(results, type));
    }

    @Override
    public void onRefreshData() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void responseData(LResponse response) {

    }

    public enum Type {
        notRated, rated
    }

    @Override
    public void showAppBar() {
        super.showAppBar();
        ((MyReadingActivity) getActivity()).showAppBar();
    }

    @Override
    public void hideAppBar() {
        super.hideAppBar();
        ((MyReadingActivity) getActivity()).hideAppBar();
    }
}
