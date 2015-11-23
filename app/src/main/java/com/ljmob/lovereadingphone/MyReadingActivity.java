package com.ljmob.lovereadingphone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ljmob.lovereadingphone.adapter.MyReadingPagerAdapter;
import com.ljmob.lovereadingphone.context.MyApplication;
import com.ljmob.lovereadingphone.entity.Subject;
import com.ljmob.lovereadingphone.entity.User;
import com.ljmob.lovereadingphone.net.NetConstant;
import com.ljmob.lovereadingphone.util.DefaultParam;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnPageChange;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by london on 15/10/27.
 * 我的朗读
 */
public class MyReadingActivity extends AppCompatActivity implements LRequestTool.OnResponseListener {
    private static final int API_SUBJECTS = 1;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_my_reading_frameTabs)
    FrameLayout activityMyReadingFrameTabs;
    @Bind(R.id.activity_my_reading_pager)
    ViewPager activityMyReadingPager;

    LinearLayout tabLinear;
    List<View> itemViews;

    private boolean isAppBarHided;
    private boolean isRated;

    LRequestTool requestTool;
    List<Subject> subjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRated = getIntent().getBooleanExtra("isRated", false);
        if (MyApplication.currentUser == null) {
            ToastUtil.show(R.string.toast_login_timeout);
            finish();
            return;
        }
        setContentView(R.layout.activity_my_reading);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            if (MyApplication.currentUser.role == User.Role.student) {
                ab.setTitle(R.string.activity_my_reading);
            } else {
                ab.setTitle(R.string.activity_my_reading_teacher);
            }
        }
        requestTool = new LRequestTool(this);
        initData();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(LResponse response) {
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
        }
        if (!response.body.startsWith("[") && !response.body.startsWith("{")) {
            ToastUtil.show(R.string.toast_server_err_1);
            return;
        }
        switch (response.requestCode) {
            case API_SUBJECTS:
                subjects = new Gson().fromJson(response.body, new TypeToken<List<Subject>>() {
                }.getType());
                initViewsWithSubjects();
                break;
        }
    }

    @OnPageChange(R.id.activity_my_reading_pager)
    protected void selectPage(int index) {
        showAppBar();
        if (activityMyReadingPager.getCurrentItem() != index) {
            activityMyReadingPager.setCurrentItem(index);
        }
        if (itemViews.size() >= index) {
            for (View v : itemViews) {
                if (itemViews.indexOf(v) == index) {
                    ((TextView) v.findViewById(R.id.tvTab))
                            .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    v.findViewById(R.id.viewTabIndicator)
                            .animate().alpha(1).setDuration(200).start();

                } else {
                    ((TextView) v.findViewById(R.id.tvTab))
                            .setTextColor(ContextCompat.getColor(this, R.color.div_tab));
                    v.findViewById(R.id.viewTabIndicator)
                            .animate().alpha(0).setDuration(200).start();
                }
            }
        }
    }

    public void showAppBar() {
        if (isAppBarHided) {
            toolbar.animate().translationY(0)
                    .setInterpolator(new DecelerateInterpolator(2)).start();
            activityMyReadingFrameTabs.animate().translationY(0)
                    .setInterpolator(new DecelerateInterpolator(2)).start();
            isAppBarHided = false;
        }
    }

    public void hideAppBar() {
        if (!isAppBarHided) {
            toolbar.animate().translationY(-toolbar.getHeight())
                    .setInterpolator(new DecelerateInterpolator(2)).start();
            activityMyReadingFrameTabs.animate().translationY(-toolbar.getHeight())
                    .setInterpolator(new DecelerateInterpolator(2)).start();
            isAppBarHided = true;
        }
    }

    private void initData() {
        requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_SUBJECTS, new DefaultParam(), API_SUBJECTS);
    }

    private void initViewsWithSubjects() {
        if (subjects == null) {
            return;
        }
        View headTab;
        if (activityMyReadingFrameTabs.getChildCount() != 0) {
            return;
        }
        if (subjects.size() < 5) {
            headTab = getLayoutInflater().inflate(R.layout.head_tab,
                    activityMyReadingFrameTabs, false);
        } else {
            headTab = getLayoutInflater().inflate(R.layout.head_tab_scrollable,
                    activityMyReadingFrameTabs, false);
        }
        activityMyReadingFrameTabs.addView(headTab);
        tabLinear = (LinearLayout) headTab.findViewById(R.id.head_tab_ln);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        itemViews = new ArrayList<>();
        for (Subject s : subjects) {
            View tabItem;
            if (subjects.size() < 5) {
                tabItem = getLayoutInflater()
                        .inflate(R.layout.view_tab_item, tabLinear, false);
                tabItem.setLayoutParams(layoutParams);
            } else {
                tabItem = getLayoutInflater()
                        .inflate(R.layout.view_tab_item_scrollable, tabLinear, false);
            }
            ((TextView) tabItem.findViewById(R.id.tvTab)).setText(s.name);
            tabLinear.addView(tabItem);
            itemViews.add(tabItem);
            tabItem.setOnClickListener(new TabClickListener(subjects.indexOf(s)));
        }
        activityMyReadingPager
                .setAdapter(new MyReadingPagerAdapter(getSupportFragmentManager(), subjects, isRated));
        selectPage(0);
    }

    private class TabClickListener implements View.OnClickListener {
        int index;

        public TabClickListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            selectPage(index);
        }
    }
}
