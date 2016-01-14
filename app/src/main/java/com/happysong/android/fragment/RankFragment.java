package com.happysong.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.happysong.android.MainActivity;
import com.happysong.android.R;
import com.happysong.android.ReadingActivity;
import com.happysong.android.adapter.RankAdapter;
import com.happysong.android.context.EasyLoadFragment;
import com.happysong.android.context.MyApplication;
import com.happysong.android.entity.City;
import com.happysong.android.entity.District;
import com.happysong.android.entity.Grade;
import com.happysong.android.entity.Result;
import com.happysong.android.entity.School;
import com.happysong.android.entity.Subject;
import com.happysong.android.entity.TeamClass;
import com.happysong.android.entity.User;
import com.happysong.android.net.NetConstant;
import com.happysong.android.util.DefaultParam;
import com.happysong.android.view.SimpleStringPopup;
import com.londonx.lutil.entity.LResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by london on 15/10/26.
 * 排行榜
 */
public class RankFragment extends EasyLoadFragment implements
        SimpleStringPopup.SimpleStringListener {
    public static final int ACTION_RANK_FILTER = 0xACAF;
    public static boolean hasDataChanged = false;
    private static final int API_SUBJECTS = 1;

    @Bind(R.id.view_recommend_frameTabs)
    FrameLayout viewRecommendFrameTabs;

    LayoutInflater inflater;
    ViewGroup container;
    View rootView;
    List<Result> results;

    HeadHolder headHolder;
    private RankAdapter rankAdapter;
    String currentApi = NetConstant.API_RANKS_WEEK;
    private View itemWeek;
    private View itemMonth;
    private boolean isShowingAppBar = true;
    private List<Subject> subjects;
    private SimpleStringPopup subjectsPopup;

    private City selectedCity;
    private District selectedDistrict;
    private School selectedSchool;
    private Grade selectedGrade;
    private TeamClass selectedTeamClass;
    private Subject selectedSubject;
    private List<String> subjectStrings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.view_rank, container, false);
            setContentView(rootView);

            int startOffset = getResources().getDimensionPixelSize(R.dimen.subject_list_refresh_start_offset_tab);
            int endOffset = getResources().getDimensionPixelSize(R.dimen.subject_list_refresh_end_offset_tab);
            primarySwipeRefreshLayout.setProgressViewOffset(false, startOffset, endOffset);
            View headerView = inflater.inflate(R.layout.head_rank, primaryListView, false);
            headHolder = new HeadHolder(headerView);
            ((ListView) primaryListView).addHeaderView(headerView);
            requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_SUBJECTS, new DefaultParam(), API_SUBJECTS);
        }
        ButterKnife.bind(this, rootView);
        if (MyApplication.currentUser != null && MyApplication.currentUser.role == User.Role.student) {
            selectedSchool = MyApplication.currentUser.team_classes.get(0).school;
        }
        initTabs();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasDataChanged) {
            refreshDataWithSubject();
        }
        hasDataChanged = false;
    }

    @Override
    public void responseData(LResponse response) {
        switch (response.requestCode) {
            case API_SUBJECTS:
                subjects = new Gson().fromJson(response.body, new TypeToken<List<Subject>>() {
                }.getType());
                subjectStrings = new ArrayList<>();
                for (Subject s : subjects) {
                    subjectStrings.add(s.name);
                }
                subjectsPopup = new SimpleStringPopup(getContext(), container);
                subjectsPopup.setSimpleStringListener(this);

                if (subjects.size() == 0) {
                    return;
                }
                selectedSubject = subjects.get(0);
                refreshDataWithSubject();
                break;
            case GET_DATA:
                List<Result> appendData = new Gson().fromJson(response.body, new TypeToken<List<Result>>() {
                }.getType());
                if (currentPage == 1) {
                    results = appendData;
                    rankAdapter = new RankAdapter(results);
                    primaryListView.setAdapter(rankAdapter);
                } else {
                    results.addAll(appendData);
                    rankAdapter.setNewData(results);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
        selectedGrade = (Grade) data.getSerializableExtra("selectedGrade");
        selectedTeamClass = (TeamClass) data.getSerializableExtra("selectedTeamClass");

        currentPage = 1;
        initData(currentApi, wrapParams());
    }

    @Override
    public void selectStringAt(SimpleStringPopup popup, int index) {
        selectedSubject = subjects.get(index);
        headHolder.headRankTvSelectedSubject.setText(selectedSubject.name);
        refreshDataWithSubject();
    }

    @OnItemClick(R.id.primaryAbsListView)
    protected void selectResult(int position) {
        Intent intent = new Intent(getContext(), ReadingActivity.class);
        intent.putExtra("result", results.get(position - 1));//header
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
                    headHolder.headRankTvDataSource.setText(R.string.all);
                } else {
                    headHolder.headRankTvDataSource.setText(selectedCity.name);
                }
            }
        }
        if (selectedDistrict == null) {
            param.put("district_id", 0);
        } else {
            param.put("district_id", selectedDistrict.id);
            if (headHolder != null && selectedDistrict.id != 0) {
                headHolder.headRankTvDataSource.setText(selectedDistrict.name);
            }
        }
        if (selectedSchool == null) {
            param.put("school_id", 0);
        } else {
            param.put("school_id", selectedSchool.id);
            if (headHolder != null && selectedSchool.id != 0) {
                if (MyApplication.currentUser != null &&
                        MyApplication.currentUser.role == User.Role.student) {
                    if (MyApplication.currentUser.team_classes.get(0).school.id == selectedSchool.id) {
                        headHolder.headRankTvDataSource.setText(R.string.my_school);
                    }
                } else {
                    headHolder.headRankTvDataSource.setText(selectedSchool.name);
                }
            }
        }
        if (selectedGrade == null) {
            param.put("grade_id", 0);
        } else {
            param.put("grade_id", selectedGrade.id);
            if (headHolder != null && selectedGrade.id != 0) {
                headHolder.headRankTvDataSource.setText(
                        String.format("%s%s", selectedSchool.name, selectedGrade.name));
            }
        }
        if (selectedTeamClass == null) {
            param.put("team_class_id", 0);
        } else {
            param.put("team_class_id", selectedTeamClass.id);
            if (headHolder != null && selectedTeamClass.id != 0) {
                headHolder.headRankTvDataSource.setText(String.format("%s%s%s",
                        selectedSchool.name, selectedGrade.name, selectedTeamClass.name));
            }
        }
        if (selectedSubject != null) {
            switch (selectedSubject.type) {
                case subject:
                    param.put("subject_id", selectedSubject.id);
                    param.put("category_id", 0);
                    break;
                case category:
                    param.put("subject_id", 0);
                    param.put("category_id", selectedSubject.id);
                    break;
            }
        }
        return param;
    }

    private void initTabs() {
        LinearLayout tabLinear = (LinearLayout) inflater
                .inflate(R.layout.head_tab, viewRecommendFrameTabs, false);
        if (viewRecommendFrameTabs.getChildCount() != 0) {
            return;
        }
        viewRecommendFrameTabs.addView(tabLinear);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        itemWeek = inflater.inflate(R.layout.view_tab_item, tabLinear, false);
        ((TextView) itemWeek.findViewById(R.id.tvTab)).setText(R.string.rank_week);
        itemWeek.setOnClickListener(new TabItemClickListener(NetConstant.API_RANKS_WEEK));
        itemWeek.setLayoutParams(layoutParams);
        itemMonth = inflater.inflate(R.layout.view_tab_item, tabLinear, false);
        ((TextView) itemMonth.findViewById(R.id.tvTab)).setText(R.string.rank_month);
        itemMonth.setOnClickListener(new TabItemClickListener(NetConstant.API_RANKS_MONTH));
        itemMonth.setLayoutParams(layoutParams);

        ((TextView) itemWeek.findViewById(R.id.tvTab))
                .setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        itemWeek.findViewById(R.id.viewTabIndicator)
                .animate().alpha(1.0f).setDuration(200).start();

        tabLinear.addView(itemWeek);
        tabLinear.addView(itemMonth);
    }

    private void refreshDataWithSubject() {
        if (selectedSubject == null) {
            return;
        }
        currentPage = 1;
        initData(currentApi, wrapParams());
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'head_rank.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    class HeadHolder {
        @Bind(R.id.head_rank_tvDataSource)
        TextView headRankTvDataSource;
        @Bind(R.id.head_rank_tvSelectedSubject)
        TextView headRankTvSelectedSubject;
        @Bind(R.id.head_rank_lnSubject)
        LinearLayout headRankLnSubject;

        HeadHolder(View view) {
            ButterKnife.bind(this, view);
            if (MyApplication.currentUser == null) {
                headRankTvDataSource.setText(R.string.all);
            } else {
                headRankTvDataSource.setText(R.string.my_school);
            }
        }

        @OnClick(R.id.head_rank_lnSubject)
        protected void showSubjects() {
            if (subjectsPopup == null) {
                return;
            }
            subjectsPopup.setStrings(subjectStrings);
            subjectsPopup.showAsDropDown(headRankLnSubject);
        }
    }

    private class TabItemClickListener implements View.OnClickListener {
        String api;

        public TabItemClickListener(String api) {
            this.api = api;
        }

        @Override
        public void onClick(View v) {
            currentApi = api;
            if (currentApi.equals(NetConstant.API_RANKS_WEEK)) {
                ((TextView) itemWeek.findViewById(R.id.tvTab))
                        .setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                ((TextView) itemMonth.findViewById(R.id.tvTab))
                        .setTextColor(ContextCompat.getColor(getContext(), R.color.div_tab));

                itemWeek.findViewById(R.id.viewTabIndicator)
                        .animate().alpha(1.0f).setDuration(200).start();
                itemMonth.findViewById(R.id.viewTabIndicator)
                        .animate().alpha(0.0f).setDuration(200).start();
            } else {
                ((TextView) itemWeek.findViewById(R.id.tvTab))
                        .setTextColor(ContextCompat.getColor(getContext(), R.color.div_tab));
                ((TextView) itemMonth.findViewById(R.id.tvTab))
                        .setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

                itemWeek.findViewById(R.id.viewTabIndicator)
                        .animate().alpha(0.0f).setDuration(200).start();
                itemMonth.findViewById(R.id.viewTabIndicator)
                        .animate().alpha(1.0f).setDuration(200).start();
            }
            refreshDataWithSubject();
        }
    }
}
