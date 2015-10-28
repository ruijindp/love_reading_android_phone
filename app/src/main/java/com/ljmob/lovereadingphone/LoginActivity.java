package com.ljmob.lovereadingphone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by london on 15/10/21.
 * 登录界面
 */
public class LoginActivity extends AppCompatActivity {
    @Bind(R.id.activity_login_btnLogin)
    Button activityLoginBtnLogin;
    private boolean isReLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        isReLogin = getIntent().getBooleanExtra("isReLogin", false);
    }

    @OnClick(R.id.activity_login_btnLogin)
    protected void doLogin() {
        if (isReLogin) {
            finish();
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
