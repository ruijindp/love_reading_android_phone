package com.ljmob.lovereadingphone.context;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;

import com.ljmob.lovereadingphone.LoginActivity;
import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.net.NetConstant;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

/**
 * Created by london on 15/10/21.
 * easy to pull refresh and load more
 */
public abstract class EasyLoadFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        LRequestTool.OnResponseListener,
        AbsListView.OnScrollListener {
    public static final int GET_DATA = 0xEDFA;
    public static final int PAGE_SIZE = 10;

    private boolean isLoading = false;
    private boolean hasMore = true;
    protected LRequestTool requestTool;
    protected HashMap<String, Object> params;
    protected String apiUrl;
    protected int currentPage = 1;

    protected int currentVisiblePosition = -1;

    protected SwipeRefreshLayout primarySwipeRefreshLayout;
    protected AbsListView primaryListView;

    public void setContentView(View rootView) {
        primarySwipeRefreshLayout = (SwipeRefreshLayout) rootView
                .findViewById(R.id.primarySwipeRefreshLayout);
        primaryListView = (AbsListView) rootView
                .findViewById(R.id.primaryAbsListView);

        if (primarySwipeRefreshLayout == null) {
            throw new NullPointerException("must have a SwipeRefreshLayout " +
                    "named R.id.primarySwipeRefreshLayout and call setContentView()");
        }
        if (primaryListView == null) {
            throw new NullPointerException("must have a ListView named R.id.primaryListView" +
                    " and call setContentView()");
        }
        requestTool = new LRequestTool(this);
        primarySwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        primarySwipeRefreshLayout.setOnRefreshListener(this);
        primaryListView.setOnScrollListener(this);
    }

    public void onRefreshData() {

    }

    public void onLoadMore() {

    }

    public abstract void responseData(LResponse response);

    public void showAppBar() {

    }

    public void hideAppBar() {

    }

    public void initData(String apiUrl, HashMap<String, Object> params) {
        if (primarySwipeRefreshLayout != null && !primarySwipeRefreshLayout.isRefreshing()) {
            primarySwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    primarySwipeRefreshLayout.setRefreshing(true);
                }
            });
        }
        this.apiUrl = apiUrl;
        this.params = params;
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        this.params.put("page", currentPage);
        requestTool.doGet(NetConstant.ROOT_URL + this.apiUrl, this.params, GET_DATA);
        isLoading = true;
    }

    @Override
    public void onRefresh() {
        onRefreshData();
        currentPage = 1;
        initData(this.apiUrl, this.params);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (currentVisiblePosition == -1) {
            currentVisiblePosition = firstVisibleItem;
        } else {
            if (currentVisiblePosition > firstVisibleItem) {
                showAppBar();
            } else if (currentVisiblePosition < firstVisibleItem) {
                hideAppBar();
            }
            currentVisiblePosition = firstVisibleItem;
        }
        if (isLoading || view.getCount() == 0) {
            return;
        }

        //Load more when scrolling over last PAGE_SIZE / 2 items;
        boolean isDivDPage = firstVisibleItem + visibleItemCount > totalItemCount - PAGE_SIZE / 2;
        if (isDivDPage && hasMore) {
            onLoadMore();
            currentPage++;
            initData(this.apiUrl, this.params);
            isLoading = true;
        }
    }

    @Override
    public void onResponse(LResponse response) {
        if (response.requestCode == GET_DATA) {
            isLoading = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    primarySwipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            primarySwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }).start();
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

        if (response.requestCode == GET_DATA) {
            if (response.body.startsWith("[")) {
                try {
                    JSONArray jsonArray = new JSONArray(response.body);
                    hasMore = jsonArray.length() == PAGE_SIZE;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        responseData(response);
    }
}
