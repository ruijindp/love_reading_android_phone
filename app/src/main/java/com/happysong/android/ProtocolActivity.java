package com.happysong.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

import com.google.gson.Gson;
import com.happysong.android.entity.Result;
import com.happysong.android.net.NetConstant;
import com.happysong.android.util.DefaultParam;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

/**
 * Created by london on 15/11/27.
 * 自定义协议
 * happysong://Mzc=
 */
public class ProtocolActivity extends AppCompatActivity implements LRequestTool.OnResponseListener {
    LRequestTool requestTool;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null || getIntent().getData() == null) {
            return;
        }
        String[] urlFragment = getIntent().getData().toString().split("//");
        if (urlFragment.length != 2) {
            finish();
            return;
        }
        requestTool = new LRequestTool(this);
        String b64 = urlFragment[1];
        String idString = new String(Base64.decode(b64, Base64.DEFAULT));
        int id;
        try {
            id = Integer.parseInt(idString);
            if (id == 0) {
                throw new Exception("id is 0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show(R.string.toast_protocol_error);
            finish();
            return;
        }

        requestTool = new LRequestTool(this);
        DefaultParam param = new DefaultParam();
        param.put("result_id", id);
        requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_RESULTS,
                param,
                0);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.entering));
        progressDialog.show();
    }

    @Override
    public void onResponse(LResponse response) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (response.responseCode != 200) {
            ToastUtil.show(R.string.toast_protocol_error);
        }
        startActivity(new Intent(this, MainActivity.class));
        Intent resultIntent = new Intent(this, ReadingActivity.class);
        resultIntent.putExtra("result", new Gson().fromJson(response.body, Result.class));
        startActivity(resultIntent);
        finish();
    }
}
