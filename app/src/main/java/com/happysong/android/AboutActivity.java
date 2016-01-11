package com.happysong.android;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by london on 16/1/11.
 * 关于我们
 */
public class AboutActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_about_tvVersion)
    TextView activityAboutTvVersion;
    @Bind(R.id.activity_about_tvCopyright)
    TextView activityAboutTvCopyright;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        PackageManager manager = getPackageManager();
        String currentVersion = "1.0";
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            currentVersion = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        activityAboutTvVersion.setText(getString(R.string.about_version,
                getString(R.string.app_name),
                currentVersion));
        activityAboutTvCopyright.setText(getString(R.string.copyright,
                DateFormat.format("yyyy", System.currentTimeMillis())));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
