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
import android.widget.TextView;

import com.ljmob.lovereadingphone.net.NetConstant;
import com.ljmob.lovereadingphone.util.DefaultParam;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.OnTouch;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by london on 15/10/27.
 * 修改密码
 */
public class ChangePasswordActivity extends AppCompatActivity implements LRequestTool.OnResponseListener {
    private static final int API_PASSWORD = 1;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_change_password_etOld)
    EditText activityChangePasswordEtOld;
    @Bind(R.id.activity_change_password_etNew)
    EditText activityChangePasswordEtNew;
    @Bind(R.id.activity_change_password_etConfirm)
    EditText activityChangePasswordEtConfirm;
    @Bind(R.id.activity_change_password_frameErr)
    FrameLayout activityChangePasswordFrameErr;
    @Bind(R.id.activity_change_password_tvErr)
    TextView activityChangePasswordTvErr;

    LRequestTool requestTool;
    boolean isChanging;

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
        requestTool = new LRequestTool(this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_change_password, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            if (isChanging) {
                return super.onOptionsItemSelected(item);
            } else {
                isChanging = true;
                change();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResponse(LResponse response) {
        isChanging = false;
        if (response.responseCode == 403) {
            activityChangePasswordFrameErr.setVisibility(View.VISIBLE);
            activityChangePasswordTvErr.setText(R.string.change_pwd_old);
            return;
        }
        if (response.responseCode == 503) {
            activityChangePasswordFrameErr.setVisibility(View.VISIBLE);
            activityChangePasswordTvErr.setText(R.string.change_pwd_new_too_short);
            return;
        }
        if (response.responseCode == 0) {
            ToastUtil.show(R.string.toast_server_err_0);
            return;
        }
        if (response.responseCode != 200 && response.responseCode != 201) {
            ToastUtil.serverErr(response);
            return;
        }
        if (!response.body.startsWith("[") && !response.body.startsWith("{")) {
            ToastUtil.show(R.string.toast_server_err_1);
            return;
        }
        switch (response.requestCode) {
            case API_PASSWORD:
                activityChangePasswordFrameErr.setVisibility(View.INVISIBLE);
                ToastUtil.show(R.string.toast_password_ok);
                finish();
                break;
        }
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
            activityChangePasswordTvErr.setText(R.string.change_pwd_confirm);
        }
    }

    private void change() {
        String old = activityChangePasswordEtOld.getText().toString();
        String newP = activityChangePasswordEtNew.getText().toString();
        String confirm = activityChangePasswordEtConfirm.getText().toString();
        if (old.length() == 0) {
            activityChangePasswordFrameErr.setVisibility(View.VISIBLE);
            activityChangePasswordTvErr.setText(R.string.change_pwd_old);
            isChanging = false;
            return;
        }
        if (newP.length() == 0) {
            activityChangePasswordFrameErr.setVisibility(View.VISIBLE);
            activityChangePasswordTvErr.setText(R.string.change_pwd_new);
            isChanging = false;
            return;
        }

        if (!newP.equals(confirm)) {
            activityChangePasswordFrameErr.setVisibility(View.VISIBLE);
            activityChangePasswordTvErr.setText(R.string.change_pwd_confirm);
            isChanging = false;
            return;
        }

        activityChangePasswordFrameErr.setVisibility(View.INVISIBLE);

        DefaultParam param = new DefaultParam();
        param.put("old_password", old);
        param.put("password", newP);
        param.put("password_confirmation", confirm);
        requestTool.doPost(NetConstant.ROOT_URL + NetConstant.API_PASSWORD, param, API_PASSWORD);
    }
}
