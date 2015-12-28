package com.happysong.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.happysong.android.adapter.Category2Adapter;
import com.happysong.android.entity.Category;
import com.happysong.android.entity.Edition;
import com.happysong.android.entity.Grade;
import com.happysong.android.entity.Subject;
import com.happysong.android.net.NetConstant;
import com.happysong.android.util.DefaultParam;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by london on 15/12/2.
 * 分类2
 * 切换教材版本和课外文章
 */
public class Category2Activity extends AppCompatActivity implements
        LRequestTool.OnResponseListener,
        Category2Adapter.Category2SelectedListener {
    private static final int API_SUBJECTS = 1;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_cate2_lv)
    ListView activityCate2Lv;

    LRequestTool requestTool;

    private Subject.Type selectedType = null;
    private Subject selectedSubject = null;
    private Edition selectedEdition = null;
    private Grade selectedGrade = null;
    private Category selectedCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedSubject = (Subject) getIntent().getSerializableExtra("selectedSubject");
        selectedEdition = (Edition) getIntent().getSerializableExtra("selectedEdition");
        selectedGrade = (Grade) getIntent().getSerializableExtra("selectedGrade");
        selectedCategory = (Category) getIntent().getSerializableExtra("selectedCategory");
        if (selectedSubject != null) {
            selectedType = selectedSubject.type;
            if (selectedEdition == null) {
                if (selectedSubject.editions.size() != 0) {
                    selectedEdition = selectedSubject.editions.get(0);
                }
            }
        } else {
            selectedType = Subject.Type.category;
        }
        setContentView(R.layout.activity_category2);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        View footerView = getLayoutInflater().inflate(R.layout.foot_category2, activityCate2Lv, false);
        activityCate2Lv.addFooterView(footerView);
        requestTool = new LRequestTool(this);
        requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_SUBJECTS, new DefaultParam(), API_SUBJECTS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.action_switch_version:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(LResponse response) {
        if (response.responseCode == 401) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.putExtra("isReLogin", true);
            startActivity(loginIntent);
            return;
        }
        if (response.responseCode == 0) {
            ToastUtil.show(R.string.toast_server_err_0);
            return;
        }
        if (response.responseCode != 200) {
            ToastUtil.serverErr(response);
            return;
        }
        if (!response.body.startsWith("[") && !response.body.startsWith("{")) {
            ToastUtil.show(R.string.toast_server_err_1);
            return;
        }
        switch (response.requestCode) {
            case API_SUBJECTS:
                List<Subject> subjects = new Gson().fromJson(response.body, new TypeToken<List<Subject>>() {
                }.getType());
                Category2Adapter category2Adapter = new Category2Adapter(subjects, this);
                category2Adapter.setSelection(selectedType, selectedSubject, selectedEdition,
                        selectedGrade, selectedCategory);
                activityCate2Lv.setAdapter(category2Adapter);
                break;
        }
    }

    @Override
    public void onCategory2Selected(Subject.Type selectType,
                                    Subject selectedSubject,
                                    Edition selectedEdition,
                                    Grade selectedGrade,
                                    Category selectedCategory) {
        Intent selections = new Intent();
        selections.putExtra("selectType", selectType);
        selections.putExtra("selectedSubject", selectedSubject);
        selections.putExtra("selectedEdition", selectedEdition);
        selections.putExtra("selectedGrade", selectedGrade);
        selections.putExtra("selectedCategory", selectedCategory);
        setResult(RESULT_OK, selections);
        finish();
    }
}
