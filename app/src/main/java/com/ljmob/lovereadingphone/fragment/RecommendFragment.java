package com.ljmob.lovereadingphone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ljmob.lovereadingphone.MainActivity;
import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.ReadingActivity;
import com.ljmob.lovereadingphone.adapter.RecommendAdapter;
import com.ljmob.lovereadingphone.context.EasyLoadFragment;
import com.ljmob.lovereadingphone.entity.Result;
import com.ljmob.lovereadingphone.entity.Subject;
import com.ljmob.lovereadingphone.net.NetConstant;
import com.ljmob.lovereadingphone.util.DefaultParam;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by london on 15/10/26.
 * 美文推荐
 */
public class RecommendFragment extends EasyLoadFragment {
    private static final int API_SUBJECTS = 1;

    @Bind(R.id.view_recommend_frameTabs)
    FrameLayout viewRecommendFrameTabs;

    LayoutInflater inflater;
    View rootView;
    List<Result> results;
    List<TextView> itemViews;

    private HeadHolder headHolder;
    private List<Subject> subjects;
    private RecommendAdapter recommendAdapter;
    private boolean isShowingAppBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.view_recommend, container, false);
            setContentView(rootView);

            int startOffset = getResources().getDimensionPixelSize(R.dimen.subject_list_refresh_start_offset_tab);
            int endOffset = getResources().getDimensionPixelSize(R.dimen.subject_list_refresh_end_offset_tab);
            primarySwipeRefreshLayout.setProgressViewOffset(false, startOffset, endOffset);
            View headerView = inflater.inflate(R.layout.head_recommend, primaryListView, false);
            headHolder = new HeadHolder(headerView);
            ((ListView) primaryListView).addHeaderView(headerView);
        }
        ButterKnife.bind(this, rootView);

        requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_SUBJECTS, new DefaultParam(), API_SUBJECTS);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void responseData(LResponse response) {
        switch (response.requestCode) {
            case API_SUBJECTS:
                subjects = new Gson().fromJson(response.body, new TypeToken<List<Subject>>() {
                }.getType());
                initViewsWithSubjects();
                break;
            case GET_DATA:
                List<Result> appendData = new Gson().fromJson(response.body, new TypeToken<List<Result>>() {
                }.getType());
                if (currentPage == 1) {
                    results = appendData;
                    recommendAdapter = new RecommendAdapter(results);
                    primaryListView.setAdapter(recommendAdapter);
                } else {
                    results.addAll(appendData);
                    recommendAdapter.setNewData(results);
                }
                break;
        }
    }

    @Override
    public void showAppBar() {
        super.showAppBar();
        if (!isShowingAppBar) {
            isShowingAppBar = true;
            ((MainActivity) getActivity()).showAppBar();
            viewRecommendFrameTabs.animate().translationY(0)
                    .setInterpolator(new DecelerateInterpolator(2)).start();
        }
    }

    @Override
    public void hideAppBar() {
        super.hideAppBar();
        if (isShowingAppBar) {
            isShowingAppBar = false;
            ((MainActivity) getActivity()).hideAppBar();
            viewRecommendFrameTabs.animate().translationY(-getResources().getDimension(R.dimen.toolbar_h))
                    .setInterpolator(new AccelerateInterpolator(2)).start();
        }
    }

    @OnItemClick(R.id.primaryAbsListView)
    protected void selectResult(int position) {
        Intent intent = new Intent(getContext(), ReadingActivity.class);
        intent.putExtra("result", results.get(position - 1));//有header
        startActivity(intent);
    }

    private void initViewsWithSubjects() {
        if (subjects == null) {
            return;
        }
        View headTab;
        if (viewRecommendFrameTabs.getChildCount() != 0) {
            return;
        }
        if (subjects.size() < 5) {
            headTab = inflater.inflate(R.layout.head_tab,
                    viewRecommendFrameTabs, false);
        } else {
            headTab = inflater.inflate(R.layout.head_tab_scrollable,
                    viewRecommendFrameTabs, false);
        }
        viewRecommendFrameTabs.addView(headTab);
        LinearLayout tabLinear = (LinearLayout) headTab.findViewById(R.id.head_tab_ln);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        itemViews = new ArrayList<>();
        for (Subject s : subjects) {
            TextView tabItem;
            if (subjects.size() < 5) {
                tabItem = (TextView) inflater
                        .inflate(R.layout.view_tab_item, tabLinear, false);
                tabItem.setText(s.name);
                tabItem.setLayoutParams(layoutParams);
            } else {
                tabItem = (TextView) inflater
                        .inflate(R.layout.view_tab_item_scrollable, tabLinear, false);
                tabItem.setText(s.name);
            }
            tabLinear.addView(tabItem);
            itemViews.add(tabItem);
            tabItem.setOnClickListener(new TabClickListener(subjects.indexOf(s)));
        }
        selectSubject(0);
    }

    private void selectSubject(int index) {
        for (TextView tv : itemViews) {
            if (itemViews.indexOf(tv) == index) {
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            } else {
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.div_tab));
            }
        }
        DefaultParam param = new DefaultParam();
        param.put("city_id", 0);
        param.put("district_id", 0);
        param.put("school_id", 0);
        param.put("subject_id", subjects.get(index).id);
        currentPage = 1;
        initData(NetConstant.API_RECOMMEND, param);
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'head_recommend.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    class HeadHolder {
        @Bind(R.id.head_recommend_tvDataSource)
        TextView headRecommendTvDataSource;

        HeadHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.head_recommend_lnFilter)
        protected void filter() {
            ToastUtil.show("filter");
        }
    }

    private class TabClickListener implements View.OnClickListener {
        int index;

        public TabClickListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            selectSubject(index);
        }
    }
}
