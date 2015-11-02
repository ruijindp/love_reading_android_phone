package com.ljmob.lovereadingphone.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ljmob.lovereadingphone.CategoryActivity;
import com.ljmob.lovereadingphone.LoginActivity;
import com.ljmob.lovereadingphone.MainActivity;
import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.adapter.IndexAdapter;
import com.ljmob.lovereadingphone.adapter.IndexUnitAdapter;
import com.ljmob.lovereadingphone.entity.Article;
import com.ljmob.lovereadingphone.entity.Grade;
import com.ljmob.lovereadingphone.entity.Subject;
import com.ljmob.lovereadingphone.entity.Unit;
import com.ljmob.lovereadingphone.net.NetConstant;
import com.ljmob.lovereadingphone.util.ArticleShelfWrapper;
import com.ljmob.lovereadingphone.util.DefaultParam;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by london on 15/10/26.
 * 第一页
 */
public class IndexFragment extends Fragment implements
        LRequestTool.OnResponseListener,
        SwipeRefreshLayout.OnRefreshListener,
        AbsListView.OnScrollListener {
    public static final int ACTION_CATEGORY = 0xDE;
    private static final int API_ARTICLES = 2;
    private static final int API_ARTICLE_UNITS = 3;
    private static final int PAGE_SIZE = 10;

    LayoutInflater inflater;
    View rootView;

    @Bind(R.id.primaryAbsListView)
    ListView primaryAbsListView;
    @Bind(R.id.primarySwipeRefreshLayout)
    SwipeRefreshLayout primarySwipeRefreshLayout;
    HeadHolder headHolder;

    int currentPage = 1;
    int currentVisiblePosition = -1;
    boolean isLoading;
    boolean hasMore = true;

    LRequestTool requestTool;
    List<Article> articles;
    List<Unit> units;
    IndexAdapter adapter;

    Subject selectedSubject;
    Grade selectedGrade;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.view_primary_list, container, false);
        }
        ButterKnife.bind(this, rootView);
        initViews(inflater);
        getData();
        return rootView;
    }

    public void getData() {
        if (currentPage == 1) {
            if (!primarySwipeRefreshLayout.isRefreshing()) {
                primarySwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        primarySwipeRefreshLayout.setRefreshing(true);
                    }
                }, 100);
            }
        }
        if (requestTool == null) {
            requestTool = new LRequestTool(this);
        }

        DefaultParam param = new DefaultParam();
        if (selectedGrade != null && selectedSubject != null) {
            param.put("subject_id", selectedSubject.id);
            param.put("grade_id", selectedGrade.id);
            requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_ARTICLE_UNITS, param, API_ARTICLE_UNITS);
        } else {
            param.put("page", currentPage);
            requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_ARTICLES, param, API_ARTICLES);
        }
    }

    public void initViews(LayoutInflater inflater) {
        primarySwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        int startOffset = getResources().getDimensionPixelSize(R.dimen.subject_list_refresh_start_offset);
        int endOffset = getResources().getDimensionPixelSize(R.dimen.subject_list_refresh_end_offset);
        primarySwipeRefreshLayout.setProgressViewOffset(false, startOffset, endOffset);
        View headEmpty = inflater.inflate(R.layout.toolbar_trans, primaryAbsListView, false);
        View headMain = inflater.inflate(R.layout.head_index, primaryAbsListView, false);
        headHolder = new HeadHolder(headMain);
        headHolder.headMainTvCurrentCate.setText(R.string.all);

        if (primaryAbsListView.getHeaderViewsCount() == 0) {
            primaryAbsListView.addHeaderView(headEmpty);
            primaryAbsListView.addHeaderView(headMain);
        }

        primarySwipeRefreshLayout.setOnRefreshListener(this);
        primaryAbsListView.setOnScrollListener(this);
    }

    public void onLoadMore() {
        currentPage++;
        getData();
    }

    @Override
    public void onResponse(LResponse response) {
        isLoading = false;
        if (primarySwipeRefreshLayout != null) {
            primarySwipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    primarySwipeRefreshLayout.setRefreshing(false);
                }
            }, 100);
        }
        if (response.responseCode == 401) {
            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
            loginIntent.putExtra("isReLogin", true);
            startActivity(loginIntent);
            return;
        }

        if (response.responseCode != 200) {
            ToastUtil.serverErr(response);
            return;
        }

        switch (response.requestCode) {
            case API_ARTICLES:
                List<Article> appendData = new Gson()
                        .fromJson(response.body, new TypeToken<List<Article>>() {
                        }.getType());
                hasMore = appendData.size() == PAGE_SIZE;
                if (currentPage == 1) {
                    articles = appendData;
                    adapter = new IndexAdapter(getActivity(), ArticleShelfWrapper.wrap(articles),
                            (selectedGrade != null && selectedSubject != null));
                    primaryAbsListView.setAdapter(adapter);
                } else {
                    articles.addAll(appendData);
                    adapter.setNewData(ArticleShelfWrapper.wrap(articles));
                }
                break;
            case API_ARTICLE_UNITS:
                units = new Gson().fromJson(response.body, new TypeToken<List<Unit>>() {
                }.getType());
                primaryAbsListView.setAdapter(new IndexUnitAdapter(units));
                break;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //hide AppBar
        if (currentVisiblePosition == -1) {
            currentVisiblePosition = firstVisibleItem;
        } else {
            if (currentVisiblePosition > firstVisibleItem) {
                ((MainActivity) getActivity()).showAppBar();
            } else if (currentVisiblePosition < firstVisibleItem) {
                ((MainActivity) getActivity()).hideAppBar();
            }
            currentVisiblePosition = firstVisibleItem;
        }
        if (isLoading || view.getCount() == 0) {
            return;
        }
        if (selectedSubject != null && selectedGrade != null) {
            return;
        }
        //Load more when scrolling over last PAGE_SIZE / 2 items;
        boolean isDivDPage = firstVisibleItem + visibleItemCount > totalItemCount - PAGE_SIZE / 2;
        if (isDivDPage && hasMore) {
            onLoadMore();
            currentPage++;
            getData();
            isLoading = true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != ACTION_CATEGORY) {
            return;
        }
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        selectedGrade = (Grade) data.getSerializableExtra("grade");
        selectedSubject = (Subject) data.getSerializableExtra("subject");
        headHolder.headMainTvCurrentCate.setText(String.format("%s%s",
                selectedGrade.name, selectedSubject.name));

        currentPage = 1;
        getData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        getData();
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'head_index.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    class HeadHolder {
        @Bind(R.id.head_main_tvCurrentCate)
        TextView headMainTvCurrentCate;

        HeadHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.head_main_tvCate)
        protected void openCateActivity() {
            IndexFragment.this.startActivityForResult(
                    new Intent(getActivity(), CategoryActivity.class), ACTION_CATEGORY);
        }
    }
}
