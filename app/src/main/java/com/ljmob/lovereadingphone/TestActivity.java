package com.ljmob.lovereadingphone;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.ljmob.lovereadingphone.net.NetConstant;
import com.ljmob.lovereadingphone.util.DefaultParam;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by london on 15/11/13.
 * 测试
 */
public class TestActivity extends AppCompatActivity implements LRequestTool.OnResponseListener {
    @Bind(R.id.tv)
    TextView tv;

    LRequestTool requestTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        if (getIntent() == null) {
            return;
        }
        String[] urlFragment = getIntent().getData().toString().split("//");
        if (urlFragment.length != 2) {
            finish();
            return;
        }
        String b64 = urlFragment[1];
        String id = new String(Base64.decode(b64, Base64.DEFAULT));
        Log.i("LondonX", "onCreate: id=" + id);
        tv.setText(id);
        requestTool = new LRequestTool(this);
        DefaultParam param = new DefaultParam();
        param.put("result_id", id);

        requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_RESULTS,
                param,
                0);
    }

    @Override
    public void onResponse(final LResponse response) {
        tv.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv.setText(response.body);
            }
        }, 1000);
    }
}
