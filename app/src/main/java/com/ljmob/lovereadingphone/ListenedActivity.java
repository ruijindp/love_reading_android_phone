package com.ljmob.lovereadingphone;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;

import com.ljmob.lovereadingphone.adapter.ListenedAdapter;
import com.ljmob.lovereadingphone.entity.Result;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by london on 15/10/28.
 * 已听历史
 */
public class ListenedActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_listened_recyclerView)
    RecyclerView activityListenedRecyclerView;
    @Bind(R.id.primarySwipeRefreshLayout)
    SwipeRefreshLayout primarySwipeRefreshLayout;

    private int currentPage = 1;
    List<Result> results;
    private ListenedAdapter adapter;

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

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                results.remove(viewHolder.getAdapterPosition());
                if (adapter != null) {
//                    adapter.notifyDataSetChanged();
                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(activityListenedRecyclerView);

        getData();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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

}
