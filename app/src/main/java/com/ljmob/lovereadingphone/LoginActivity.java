package com.ljmob.lovereadingphone;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.OnTouch;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by london on 15/10/21.
 * 登录界面
 */
public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_login_etUserName)
    AutoCompleteTextView activityLoginEtUserName;
    @Bind(R.id.activity_login_imgCleanUserName)
    ImageView activityLoginImgCleanUserName;
    @Bind(R.id.activity_login_etPassword)
    EditText activityLoginEtPassword;
    @Bind(R.id.activity_login_imgShowPassword)
    ImageView activityLoginImgShowPassword;
    @Bind(R.id.activity_login_imgCleanPassword)
    ImageView activityLoginImgCleanPassword;
    @Bind(R.id.activity_login_btnLogin)
    TextView activityLoginBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.activity_login_btnLogin)
    protected void doLogin() {
        finish();
    }

    @OnClick(R.id.activity_login_imgCleanUserName)
    protected void cleanUsername() {
        activityLoginEtUserName.setText("");
    }

    @OnClick(R.id.activity_login_imgCleanPassword)
    protected void cleanPassword() {
        activityLoginEtPassword.setText("");
    }

    @OnTouch(R.id.activity_login_imgShowPassword)
    protected boolean imgShowTouched(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            activityLoginEtPassword.setTransformationMethod(null);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            activityLoginEtPassword.setTransformationMethod(new PasswordTransformationMethod());
        }
        return true;
    }

    @OnTextChanged(R.id.activity_login_etUserName)
    protected void afterUsernameChanged(Editable editable) {
        if (editable.length() == 0) {
            activityLoginImgCleanUserName.setVisibility(View.GONE);
        } else {
            activityLoginImgCleanUserName.setVisibility(View.VISIBLE);
        }
    }

    @OnTextChanged(R.id.activity_login_etPassword)
    protected void afterPasswordChanged(Editable editable) {
        if (editable.length() == 0) {
            activityLoginImgCleanPassword.setVisibility(View.GONE);
        } else {
            activityLoginImgCleanPassword.setVisibility(View.VISIBLE);
        }
    }
}
