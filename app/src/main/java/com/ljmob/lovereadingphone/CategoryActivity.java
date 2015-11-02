package com.ljmob.lovereadingphone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ljmob.lovereadingphone.adapter.CategoryAdapter;
import com.ljmob.lovereadingphone.entity.ArticleType;
import com.ljmob.lovereadingphone.entity.Category;
import com.ljmob.lovereadingphone.entity.Grade;
import com.ljmob.lovereadingphone.entity.Subject;
import com.ljmob.lovereadingphone.net.NetConstant;
import com.ljmob.lovereadingphone.util.DefaultParam;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by london on 15/10/26.
 * 分类
 */
public class CategoryActivity extends AppCompatActivity implements LRequestTool.OnResponseListener {
    private static final int API_SUBJECTS = 1;
    private static final int API_GRADES = 2;
    private static final int API_ARTICLE_TYPES = 3;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.primaryAbsListView)
    ListView primaryAbsListView;

    HeadHolder headHolder;

    List<Subject> subjects;
    List<Grade> grades;
    List<ArticleType> articleTypes;
    List<Category> categories;
    LRequestTool requestTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        View headView = getLayoutInflater()
                .inflate(R.layout.head_category, primaryAbsListView, false);
        headHolder = new HeadHolder(headView);
        initData();
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

    private void initData() {
        if (requestTool == null) {
            requestTool = new LRequestTool(this);
        }
        requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_ARTICLE_TYPES, new DefaultParam(), API_ARTICLE_TYPES);
        requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_GRADES, new DefaultParam(), API_GRADES);
        requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_SUBJECTS, new DefaultParam(), API_SUBJECTS);
    }

    @Override
    public void onResponse(LResponse response) {
        if (response.responseCode == 401) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.putExtra("isReLogin", true);
            startActivity(loginIntent);
            return;
        }

        if (response.responseCode != 200) {
            ToastUtil.serverErr(response);
            return;
        }
        switch (response.requestCode) {
            case API_ARTICLE_TYPES:
                articleTypes = new Gson().fromJson(response.body, new TypeToken<List<ArticleType>>() {
                }.getType());
                if (subjects != null && grades != null) {
                    showData();
                }
                break;
            case API_GRADES:
                grades = new Gson().fromJson(response.body, new TypeToken<List<Grade>>() {
                }.getType());
                if (subjects != null && articleTypes != null) {
                    showData();
                }
                break;
            case API_SUBJECTS:
                subjects = new Gson().fromJson(response.body, new TypeToken<List<Subject>>() {
                }.getType());
                if (grades != null && articleTypes != null) {
                    showData();
                }
                break;
        }
    }

    private void showData() {
//        articleTypes is Header
        categories = new ArrayList<>();
        for (Subject s : subjects) {
            Category category = new Category();
            category.grades = grades;
            category.subject = s;
            categories.add(category);
        }
        primaryAbsListView.setAdapter(new CategoryAdapter(categories, this));
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'head_category.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    class HeadHolder {
        HeadHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.head_category_tvAll)
        protected void selectAll() {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}
