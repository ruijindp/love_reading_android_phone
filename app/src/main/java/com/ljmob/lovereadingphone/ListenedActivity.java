package com.ljmob.lovereadingphone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.Snackbar.Callback;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ljmob.lovereadingphone.adapter.ListenedAdapter;
import com.ljmob.lovereadingphone.entity.Result;
import com.ljmob.lovereadingphone.net.NetConstant;
import com.ljmob.lovereadingphone.util.DefaultParam;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by london on 15/10/28.
 * 已听历史
 */
public class ListenedActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener, LRequestTool.OnResponseListener, ListenedAdapter.OnResultClickListener {
    public static boolean hasDataChanged;
    private static final int API_HISTORY = 1;
    private static final int API_HISTORY_DELETED = 2;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_listened_recyclerView)
    RecyclerView activityListenedRecyclerView;
    @Bind(R.id.primarySwipeRefreshLayout)
    SwipeRefreshLayout primarySwipeRefreshLayout;

    private int currentPage = 1;
    private boolean isLoading;
    private boolean hasMore = true;
    List<Result> results;
    private ListenedAdapter adapter;

    private int deletedIndex = -1;
    private Result deletedResult = null;
    private Snackbar snackbar;

    private boolean isSorting = false;

    private LRequestTool requestTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listened);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        primarySwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        primarySwipeRefreshLayout.setOnRefreshListener(this);

        activityListenedRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchCallback());
        itemTouchHelper.attachToRecyclerView(activityListenedRecyclerView);

        getData();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_listened, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            cleanAll();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasDataChanged) {
            currentPage = 1;
            getData();
        }
    }

    @Override
    public void onRefresh() {
        if (isLoading) {
            return;
        }
        isLoading = true;
        currentPage = 1;
        hasMore = true;
        getData();
    }

    @Override
    public void onResponse(LResponse response) {
        isLoading = false;
        primarySwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                primarySwipeRefreshLayout.setRefreshing(false);
            }
        }, 100);
        if (response.responseCode == 401) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        if (response.responseCode != 200 && response.responseCode != 201) {
            ToastUtil.serverErr(response);
            return;
        }
        switch (response.requestCode) {
            case API_HISTORY:
                List<Result> appendData = new Gson().fromJson(response.body, new TypeToken<List<Result>>() {
                }.getType());
                hasMore = appendData.size() == 10;
                if (currentPage == 1) {
                    results = appendData;
                    adapter = new ListenedAdapter(results, this);
                    activityListenedRecyclerView.setAdapter(adapter);
                } else {
                    results.addAll(appendData);
                    adapter.setNewData(results);
                }
                break;
        }
    }

    private void getData() {
        if (currentPage == 1 && !primarySwipeRefreshLayout.isRefreshing()) {
            primarySwipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    primarySwipeRefreshLayout.setRefreshing(true);
                }
            }, 16);
        }
        if (requestTool == null) {
            requestTool = new LRequestTool(this);
        }
        DefaultParam param = new DefaultParam();
        param.put("page", currentPage);
        requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_HISTORY, param, API_HISTORY);
    }

    private void cleanAll() {
        DefaultParam param = new DefaultParam();
        param.put("result_id", 0);
        requestTool.doPost(NetConstant.ROOT_URL + NetConstant.API_HISTORY_DELETED,
                param, API_HISTORY_DELETED);
        adapter.notifyItemRangeRemoved(0, results.size());
        results.clear();
    }

    @Override
    public void onClick(View v) {
        if (deletedResult == null || deletedIndex == -1) {
            return;
        }
        results.add(deletedIndex, deletedResult);
        adapter.notifyItemInserted(deletedIndex);
        activityListenedRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        }, 1000);
        deletedIndex = -1;
        deletedResult = null;
        snackbar.dismiss();
    }

    @Override
    public void onResultClick(int position) {
        Intent intent = new Intent(this, ReadingActivity.class);
        intent.putExtra("result", results.get(position));
        startActivity(intent);
        hasDataChanged = true;
    }

    private class ItemTouchCallback extends ItemTouchHelper.SimpleCallback {

        public ItemTouchCallback() {
            super(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            if (adapter == null) {
                return;
            }
            deletedIndex = viewHolder.getAdapterPosition();
            deletedResult = results.get(deletedIndex);
            results.remove(deletedIndex);
            adapter.notifyItemRemoved(deletedIndex);
            if (!isSorting) {
                isSorting = true;
                activityListenedRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        isSorting = false;
                    }
                }, 1000);
            }

            snackbar = Snackbar.make(activityListenedRecyclerView, "",
                    Snackbar.LENGTH_LONG);
            ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                    .setTextColor(Color.WHITE);
            snackbar.setAction(R.string.undone, ListenedActivity.this);
            snackbar.setText(getString(R.string.deleted_) + deletedResult.article.title);
            snackbar.setCallback(new Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    super.onDismissed(snackbar, event);
                    if (deletedIndex == -1 || deletedResult == null) {
                        return;
                    }
                    DefaultParam param = new DefaultParam();
                    param.put("result_id", deletedResult.id);
                    requestTool.doPost(NetConstant.ROOT_URL + NetConstant.API_HISTORY_DELETED,
                            param, API_HISTORY_DELETED);
                }
            });
            snackbar.show();
        }
    }
}
