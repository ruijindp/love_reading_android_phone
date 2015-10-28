package com.ljmob.lovereadingphone;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.OnTouch;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by london on 15/10/27.
 * 修改密码
 */
public class ChangePasswordActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_change_password_etOld)
    EditText activityChangePasswordEtOld;
    @Bind(R.id.activity_change_password_etNew)
    EditText activityChangePasswordEtNew;
    @Bind(R.id.activity_change_password_imgShow)
    ImageView activityChangePasswordImgShow;
    @Bind(R.id.activity_change_password_etConfirm)
    EditText activityChangePasswordEtConfirm;
    @Bind(R.id.activity_change_password_frameErr)
    FrameLayout activityChangePasswordFrameErr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_change_password, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTouch(R.id.activity_change_password_imgShow)
    protected boolean imgShowTouched(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            activityChangePasswordEtNew.setTransformationMethod(null);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            activityChangePasswordEtNew.setTransformationMethod(new PasswordTransformationMethod());
        }
        return true;
    }

    @OnTextChanged(R.id.activity_change_password_etConfirm)
    protected void afterConfirmChanged(Editable editable) {
        if (editable.toString().equals(activityChangePasswordEtNew.getText().toString())) {
            activityChangePasswordFrameErr.setVisibility(View.INVISIBLE);
        } else {
            activityChangePasswordFrameErr.setVisibility(View.VISIBLE);
        }
    }

}
