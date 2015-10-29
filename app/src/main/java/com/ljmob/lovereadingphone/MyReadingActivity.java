package com.ljmob.lovereadingphone;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.ljmob.lovereadingphone.adapter.MyReadingPagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnPageChange;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by london on 15/10/27.
 * 我的朗读
 */
public class MyReadingActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_my_reading_frameTabs)
    FrameLayout activityMyReadingFrameTabs;
    @Bind(R.id.activity_my_reading_pager)
    ViewPager activityMyReadingPager;

    private boolean isAppBarHided;
    private boolean isRated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRated = getIntent().getBooleanExtra("isRated", false);
        setContentView(R.layout.activity_my_reading);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(R.string.activity_my_reading_teacher);
        }
        activityMyReadingPager
                .setAdapter(new MyReadingPagerAdapter(getSupportFragmentManager(), isRated));
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

    @OnPageChange(R.id.activity_my_reading_pager)
    protected void selectPage(int index) {
        showAppBar();
        if (activityMyReadingPager.getCurrentItem() != index) {
            activityMyReadingPager.setCurrentItem(index);
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
                    .setInterpolator(new AccelerateInterpolator(2)).start();
            activityMyReadingFrameTabs.animate().translationY(-toolbar.getHeight())
                    .setInterpolator(new AccelerateInterpolator(2)).start();
            isAppBarHided = true;
        }
    }
}
