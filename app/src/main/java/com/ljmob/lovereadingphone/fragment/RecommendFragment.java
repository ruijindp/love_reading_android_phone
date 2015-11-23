package com.ljmob.lovereadingphone.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ljmob.lovereadingphone.FilterActivity;
import com.ljmob.lovereadingphone.MainActivity;
import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.ReadingActivity;
import com.ljmob.lovereadingphone.adapter.RecommendAdapter;
import com.ljmob.lovereadingphone.context.EasyLoadFragment;
import com.ljmob.lovereadingphone.context.MyApplication;
import com.ljmob.lovereadingphone.entity.City;
import com.ljmob.lovereadingphone.entity.District;
import com.ljmob.lovereadingphone.entity.Result;
import com.ljmob.lovereadingphone.entity.School;
import com.ljmob.lovereadingphone.entity.Subject;
import com.ljmob.lovereadingphone.net.NetConstant;
import com.ljmob.lovereadingphone.service.PlayerService;
import com.ljmob.lovereadingphone.util.DefaultParam;
import com.londonx.lutil.entity.LResponse;

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
public class RecommendFragment extends EasyLoadFragment implements ServiceConnection {
    public static final int ACTION_RECOMMEND_FILTER = 0xACEC;
    public static boolean hasDataChanged = false;
    private static final int API_SUBJECTS = 1;

    @Bind(R.id.view_recommend_frameTabs)
    FrameLayout viewRecommendFrameTabs;

    LayoutInflater inflater;
    View rootView;
    List<Result> results;
    List<View> itemViews;

    private HeadHolder headHolder;
    private List<Subject> subjects;
    private RecommendAdapter recommendAdapter;
    private boolean isShowingAppBar = true;

    private City selectedCity;
    private District selectedDistrict;
    private School selectedSchool;
    private Subject selectedSubject;

    private PlayerService playerService;

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
        getActivity().bindService(new Intent(getContext(), PlayerService.class), this, Context.BIND_AUTO_CREATE);
        initSubjects();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasDataChanged) {
            initSubjects();
        }
        hasDataChanged = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        selectedCity = (City) data.getSerializableExtra("selectedCity");
        selectedDistrict = (District) data.getSerializableExtra("selectedDistrict");
        selectedSchool = (School) data.getSerializableExtra("selectedSchool");

        currentPage = 1;
        initData(NetConstant.API_RECOMMEND, wrapParams());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        getActivity().unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        playerService = ((PlayerService.PlayerBinder) service).getService();
        if (recommendAdapter != null && recommendAdapter.getPlayerService() == null) {
            recommendAdapter.setPlayerService(playerService);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

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
                    recommendAdapter.setPlayerService(playerService);
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
                    .setInterpolator(new DecelerateInterpolator(2)).start();
        }
    }

    @OnItemClick(R.id.primaryAbsListView)
    protected void selectResult(int position) {
        Intent intent = new Intent(getContext(), ReadingActivity.class);
        intent.putExtra("result", results.get(position - 1));//有header
        startActivity(intent);
    }

    private DefaultParam wrapParams() {
        DefaultParam param = new DefaultParam();
        if (selectedCity == null) {
            param.put("city_id", 0);
        } else {
            param.put("city_id", selectedCity.id);
            if (headHolder != null) {
                if (selectedCity.id == 0) {
                    headHolder.headRecommendTvDataSource.setText(R.string.all);
                } else {
                    headHolder.headRecommendTvDataSource.setText(selectedCity.name);
                }
            }
        }
        if (selectedDistrict == null) {
            param.put("district_id", 0);
        } else {
            param.put("district_id", selectedDistrict.id);
            if (headHolder != null && selectedDistrict.id != 0) {
                headHolder.headRecommendTvDataSource.setText(selectedDistrict.name);
            }
        }
        if (selectedSchool == null) {
            param.put("school_id", 0);
        } else {
            param.put("school_id", selectedSchool.id);
            if (headHolder != null && selectedSchool.id != 0) {
                headHolder.headRecommendTvDataSource.setText(selectedSchool.name);
            }
        }
        if (selectedSubject != null) {
            param.put("subject_id", selectedSubject.id);
        }
        return param;
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
            View tabItem;
            if (subjects.size() < 5) {
                tabItem = inflater
                        .inflate(R.layout.view_tab_item, tabLinear, false);
                tabItem.setLayoutParams(layoutParams);
            } else {
                tabItem = inflater
                        .inflate(R.layout.view_tab_item_scrollable, tabLinear, false);
            }
            ((TextView) tabItem.findViewById(R.id.tvTab)).setText(s.name);
            tabLinear.addView(tabItem);
            itemViews.add(tabItem);
            tabItem.setOnClickListener(new TabClickListener(subjects.indexOf(s)));
        }
        selectSubject(0);
    }

    private void selectSubject(int index) {
        for (View v : itemViews) {
            if (itemViews.indexOf(v) == index) {
                ((TextView) v.findViewById(R.id.tvTab))
                        .setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                v.findViewById(R.id.viewTabIndicator).animate().alpha(1.0f).setDuration(200).start();
            } else {
                ((TextView) v.findViewById(R.id.tvTab))
                        .setTextColor(ContextCompat.getColor(getContext(), R.color.div_tab));
                v.findViewById(R.id.viewTabIndicator).animate().alpha(0.0f).setDuration(200).start();
            }
        }
        selectedSubject = subjects.get(index);
        currentPage = 1;
        initData(NetConstant.API_RECOMMEND, wrapParams());
    }

    private void initSubjects() {
        requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_SUBJECTS, new DefaultParam(), API_SUBJECTS);
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
            if (MyApplication.currentUser == null) {
                headRecommendTvDataSource.setText(R.string.all);
            } else {
                headRecommendTvDataSource.setText(R.string.my_school);
            }
        }

        @OnClick(R.id.head_recommend_lnFilter)
        protected void filter() {
            Intent filterIntent = new Intent(getContext(), FilterActivity.class);
            filterIntent.putExtra("isRecommend", true);
            startActivityForResult(filterIntent, ACTION_RECOMMEND_FILTER);
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
