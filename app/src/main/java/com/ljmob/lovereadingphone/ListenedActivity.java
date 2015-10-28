package com.ljmob.lovereadingphone;

import android.content.Context;
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

import com.ljmob.lovereadingphone.adapter.ListenedAdapter;
import com.ljmob.lovereadingphone.entity.Result;
import com.londonx.lutil.util.ToastUtil;

import java.util.ArrayList;
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
        View.OnClickListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_listened_recyclerView)
    RecyclerView activityListenedRecyclerView;
    @Bind(R.id.primarySwipeRefreshLayout)
    SwipeRefreshLayout primarySwipeRefreshLayout;

    private int currentPage = 1;
    List<Result> results;
    private ListenedAdapter adapter;

    private int deletedIndex = -1;
    private Result deletedResult = null;
    private Snackbar snackbar;

    private boolean isSorting = false;

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
    public void onRefresh() {

    }


    private void getData() {
        if (results == null) {
            results = new ArrayList<>();
        }
        for (int i = 0; i < 50; i++) {
            Result r = new Result();
            r.id = i;
            results.add(r);
        }
        adapter = new ListenedAdapter(results);
        activityListenedRecyclerView.setAdapter(adapter);
    }

    private void cleanAll() {
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
            snackbar.setText(getString(R.string.deleted_) + "标题" + deletedResult.id);
            snackbar.setCallback(new Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    super.onDismissed(snackbar, event);
                    if (deletedIndex == -1 || deletedResult == null) {
                        return;
                    }
                    //TODO send deleted to server
                    ToastUtil.show("commit delete");
                }
            });
            snackbar.show();
        }
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
}
