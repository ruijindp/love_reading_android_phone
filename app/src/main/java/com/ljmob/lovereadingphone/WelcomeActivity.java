package com.ljmob.lovereadingphone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.londonx.lutil.Lutil;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by london on 15/11/18.
 * 欢迎界面
 */
public class WelcomeActivity extends AppCompatActivity {
    @Bind(R.id.activity_welcome_tvVersion)
    TextView activityWelcomeTvVersion;

    private boolean isFinished;
    private boolean isFirstRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        isFirstRun = Lutil.preferences.getBoolean("isFirstRun", true);

        PackageInfo info = null;
        String currentVersion = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info != null) {
            currentVersion = info.versionName;
        }
        if (currentVersion == null || currentVersion.length() == 0) {
            activityWelcomeTvVersion.setVisibility(View.GONE);
        } else {
            activityWelcomeTvVersion.setText(getString(R.string.version_, currentVersion));
        }

        activityWelcomeTvVersion.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinished) {
                    if (isFirstRun) {
                        startActivity(new Intent(WelcomeActivity.this, FirstRunActivity.class));
                        SharedPreferences.Editor editor = Lutil.preferences.edit();
                        editor.putBoolean("isFirstRun", false);
                        editor.apply();
                    } else {
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    }
                }
                finish();
            }
        }, 2000);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isFinished = true;
    }
}
