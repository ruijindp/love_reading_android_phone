package com.happysong.android;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.happysong.android.adapter.FirstRunAdapter;
import com.happysong.android.context.FirstRunFragment;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnPageChange;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by london on 15/11/18.
 * 首次运行引导页
 */
public class FirstRunActivity extends AppCompatActivity {
    @Bind(R.id.activity_first_run_pager)
    ViewPager activityFirstRunPager;

    private FirstRunFragment fragment2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);
        ButterKnife.bind(this);

        FirstRunFragment fragment0 = new FirstRunFragment();
        FirstRunFragment fragment1 = new FirstRunFragment();
        fragment2 = new FirstRunFragment();
        activityFirstRunPager.setAdapter(new FirstRunAdapter(getSupportFragmentManager(),
                fragment0, fragment1, fragment2));

        fragment0.setImageRes(R.mipmap.img_loading2);
        fragment1.setImageRes(R.mipmap.img_loading3);
        fragment2.setImageRes(R.mipmap.img_loading4);
        fragment0.setTextRes(R.string.first_run1);
        fragment1.setTextRes(R.string.first_run2);
        fragment2.setTextRes(R.string.first_run3);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnPageChange(R.id.activity_first_run_pager)
    protected void pageChanged(int position) {
        if (position == 2) {
            fragment2.showButton();
        }
    }
}
