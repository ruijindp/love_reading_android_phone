package com.happysong.android.context;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.AbsListView;

import com.happysong.android.LoginActivity;
import com.happysong.android.R;
import com.happysong.android.net.NetConstant;
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
public abstract class EasyLoadActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        LRequestTool.OnResponseListener,
        AbsListView.OnScrollListener {
    public static final int GET_DATA = 0xEDAA;
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

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        primarySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.primarySwipeRefreshLayout);
        primaryListView = (AbsListView) findViewById(R.id.primaryAbsListView);

        if (primarySwipeRefreshLayout == null) {
            throw new NullPointerException("must have a SwipeRefreshLayout named R.id.primarySwipeRefreshLayout");
        }
        if (primaryListView == null) {
            throw new NullPointerException("must have a ListView named R.id.primaryListView");
        }
        requestTool = new LRequestTool(this);
        primarySwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        primarySwipeRefreshLayout.setOnRefreshListener(this);
        primaryListView.setOnScrollListener(this);
    }

    public abstract void responseData(LResponse response);

    public void onRefreshData() {
    }

    public void onLoadMore() {
    }

    public void showAppBar() {

    }

    public void hideAppBar() {

    }

    public void initData(String apiUrl, HashMap<String, Object> params) {
        if (!primarySwipeRefreshLayout.isRefreshing()) {
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
        if (MyApplication.currentUser != null && !this.params.containsKey("token")) {
            this.params.put("token", MyApplication.currentUser.token);
        }
        if (MyApplication.currentUser == null && this.params.containsKey("token")) {
            this.params.remove("token");
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

            if (primarySwipeRefreshLayout != null && primarySwipeRefreshLayout.isRefreshing()) {
                primarySwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (primarySwipeRefreshLayout != null) {
                            primarySwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
            if (response.responseCode == 401) {
                Intent loginIntent = new Intent(this, LoginActivity.class);
                loginIntent.putExtra("isReLogin", true);
                startActivity(loginIntent);
                return;
            }
            if (response.responseCode == 0) {
                ToastUtil.show(R.string.toast_server_err_0);
                return;
            }
            if (response.responseCode != 200) {
                ToastUtil.serverErr(response);
                return;
            }
            if (!response.body.startsWith("[") && !response.body.startsWith("{")) {
                ToastUtil.show(R.string.toast_server_err_0);
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
}
