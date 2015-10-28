package com.ljmob.lovereadingphone;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ljmob.lovereadingphone.adapter.MyReadingPagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by london on 15/10/27.
 * 我的朗读
 */
public class MyReadingActivity extends AppCompatActivity {
    private static final int PAGE_NOT_RATED = 0;
    private static final int PAGE_RATED = 1;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_my_reading_tvNotRated)
    TextView activityMyReadingTvNotRated;
    @Bind(R.id.activity_my_reading_tvRated)
    TextView activityMyReadingTvRated;
    @Bind(R.id.activity_my_reading_pager)
    ViewPager activityMyReadingPager;
    @Bind(R.id.activity_my_reading_viewNotRated)
    View activityMyReadingViewNotRated;
    @Bind(R.id.activity_my_reading_viewRated)
    View activityMyReadingViewRated;
    @Bind(R.id.activity_my_reading_lnTab)
    LinearLayout activityMyReadingLnTab;

    private boolean isAppBarHided;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reading);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(R.string.activity_my_reading_teacher);
        }
        activityMyReadingPager.setAdapter(new MyReadingPagerAdapter(getSupportFragmentManager()));
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

    @OnClick(R.id.activity_my_reading_frameNotRated)
    protected void goNotRatedPage() {
        selectPage(PAGE_NOT_RATED);
    }

    @OnClick(R.id.activity_my_reading_frameRated)
    protected void goRatedPage() {
        selectPage(PAGE_RATED);
    }

    @OnPageChange(R.id.activity_my_reading_pager)
    protected void selectPage(int index) {
        showAppBar();
        switch (index) {
            case PAGE_NOT_RATED:
                activityMyReadingViewNotRated.setVisibility(View.VISIBLE);
                activityMyReadingTvNotRated.setAlpha(0.87f);

                activityMyReadingViewRated.setVisibility(View.INVISIBLE);
                activityMyReadingTvRated.setAlpha(0.54f);
                break;
            case PAGE_RATED:
                activityMyReadingViewNotRated.setVisibility(View.INVISIBLE);
                activityMyReadingTvNotRated.setAlpha(0.54f);

                activityMyReadingViewRated.setVisibility(View.VISIBLE);
                activityMyReadingTvRated.setAlpha(0.87f);
                break;
        }
        if (activityMyReadingPager.getCurrentItem() != index) {
            activityMyReadingPager.setCurrentItem(index);
        }
    }

    public void showAppBar() {
        if (isAppBarHided) {
            toolbar.animate().translationY(0)
                    .setInterpolator(new DecelerateInterpolator(2)).start();
            activityMyReadingLnTab.animate().translationY(0)
                    .setInterpolator(new DecelerateInterpolator(2)).start();
            isAppBarHided = false;
        }
    }

    public void hideAppBar() {
        if (!isAppBarHided) {
            toolbar.animate().translationY(-toolbar.getHeight())
                    .setInterpolator(new AccelerateInterpolator(2)).start();
            activityMyReadingLnTab.animate().translationY(-toolbar.getHeight())
                    .setInterpolator(new AccelerateInterpolator(2)).start();
            isAppBarHided = true;
        }
    }
}
