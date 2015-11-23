package com.ljmob.lovereadingphone;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ljmob.lovereadingphone.context.MyApplication;
import com.ljmob.lovereadingphone.entity.User;
import com.ljmob.lovereadingphone.net.NetConstant;
import com.ljmob.lovereadingphone.util.DefaultParam;
import com.londonx.lutil.Lutil;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;
import com.londonx.lutil.util.UserTool;

import java.util.ArrayList;
import java.util.List;

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
public class LoginActivity extends AppCompatActivity implements LRequestTool.OnResponseListener {
    private static final int USER_SIGN_IN = 1;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_login_etUserName)
    AutoCompleteTextView activityLoginEtUserName;
    @Bind(R.id.activity_login_imgCleanUserName)
    ImageView activityLoginImgCleanUserName;
    @Bind(R.id.activity_login_etPassword)
    EditText activityLoginEtPassword;
    @Bind(R.id.activity_login_imgCleanPassword)
    ImageView activityLoginImgCleanPassword;
    @Bind(R.id.activity_login_btnLogin)
    TextView activityLoginBtnLogin;
    @Bind(R.id.activity_login_frameErr)
    FrameLayout activityLoginFrameErr;

    LRequestTool requestTool;
    List<UserTool.SimpleUser> simpleUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isReLogin = getIntent().getBooleanExtra("isReLogin", false);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        requestTool = new LRequestTool(this);

        activityLoginEtUserName.postDelayed(new Runnable() {
            @Override
            public void run() {
                simpleUsers = UserTool.getSimpleUsers(LoginActivity.this);
                List<String> userNames = new ArrayList<>();
                for (UserTool.SimpleUser s : simpleUsers) {
                    userNames.add(s.username);
                }
                activityLoginEtUserName.setAdapter(new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, userNames));
                activityLoginEtUserName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        chooseUser(position);
                    }
                });
            }
        }, 16);
        if (isReLogin) {//为重登录
            ToastUtil.show(R.string.toast_login_timeout);
            MyApplication.currentUser = null;
            Lutil.preferences.edit().remove("UB64").apply();
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

    @Override
    public void onResponse(LResponse response) {
        activityLoginBtnLogin.setEnabled(true);
        if (response.responseCode == 0) {
            ToastUtil.show(R.string.toast_server_err_0);
            return;
        }
        if (response.responseCode != 200) {
            activityLoginFrameErr.setVisibility(View.VISIBLE);
            ToastUtil.serverErr(response);
            return;
        }
        if (!response.body.startsWith("[") && !response.body.startsWith("{")) {
            ToastUtil.show(R.string.toast_server_err_1);
            return;
        }
        activityLoginFrameErr.setVisibility(View.INVISIBLE);
        switch (response.requestCode) {
            case USER_SIGN_IN:
                try {
                    MyApplication.currentUser = new Gson().fromJson(response.body, User.class);
                } catch (Exception ignore) {
                    break;
                }
                String userB64 = Base64.encodeToString(response.body.getBytes(), Base64.DEFAULT);
                SharedPreferences.Editor editor = Lutil.preferences.edit();
                editor.putString("UB64", userB64);
                editor.apply();
                UserTool.rememberUser(this, activityLoginEtUserName.getText().toString(),
                        activityLoginEtPassword.getText().toString());
                finish();
                break;
        }
    }

    @OnClick(R.id.activity_login_btnLogin)
    protected void doLogin() {
        activityLoginBtnLogin.setEnabled(false);
        DefaultParam param = new DefaultParam();
        param.put("account", activityLoginEtUserName.getText().toString());
        param.put("password", activityLoginEtPassword.getText().toString());
        requestTool.doPost(NetConstant.ROOT_URL + NetConstant.USER_SIGN_IN, param, USER_SIGN_IN);
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

    private void chooseUser(int position) {
        UserTool.SimpleUser simpleUser = simpleUsers.get(position);
        activityLoginEtPassword.setText(simpleUser.password);
    }
}
