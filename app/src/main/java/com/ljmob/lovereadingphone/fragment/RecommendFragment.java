package com.ljmob.lovereadingphone.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ljmob.lovereadingphone.MainActivity;
import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.adapter.RecommendAdapter;
import com.ljmob.lovereadingphone.context.EasyLoadFragment;
import com.ljmob.lovereadingphone.entity.Result;
import com.ljmob.lovereadingphone.util.DefaultParam;
import com.londonx.lutil.entity.LResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by london on 15/10/26.
 * 美文推荐
 */
public class RecommendFragment extends EasyLoadFragment {
    LayoutInflater inflater;
    View rootView;
    List<Result> results;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.view_primary_list_div, container, false);
            setContentView(rootView);

            int startOffset = getResources().getDimensionPixelSize(R.dimen.subject_list_refresh_start_offset);
            int endOffset = getResources().getDimensionPixelSize(R.dimen.subject_list_refresh_end_offset);
            primarySwipeRefreshLayout.setProgressViewOffset(false, startOffset, endOffset);
            View headerView = inflater.inflate(R.layout.toolbar_trans, primaryListView, false);
            ((ListView) primaryListView).addHeaderView(headerView);
        }
        initData("", new DefaultParam());
        return rootView;
    }

    @Override
    public void initData(String apiUrl, HashMap<String, Object> params) {
        super.initData(apiUrl, params);
        results = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            results.add(new Result());
        }
        primaryListView.setAdapter(new RecommendAdapter(results));
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

    @Override
    public void showAppBar() {
        super.showAppBar();
        ((MainActivity) getActivity()).showAppBar();
    }

    @Override
    public void hideAppBar() {
        super.hideAppBar();
        ((MainActivity) getActivity()).hideAppBar();
    }

}
