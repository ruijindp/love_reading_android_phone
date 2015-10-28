package com.ljmob.lovereadingphone;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.ljmob.lovereadingphone.adapter.CategoryAdapter;
import com.ljmob.lovereadingphone.entity.ArticleCategory;

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
public class CategoryActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.primaryAbsListView)
    ListView primaryAbsListView;

    HeadHolder headHolder;

    List<ArticleCategory> categories;

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
        primaryAbsListView.addHeaderView(headView);
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
        categories = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            ArticleCategory articleCategory = new ArticleCategory();
            articleCategory.sub_categories = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                ArticleCategory.SubCategory subCategory = articleCategory.new SubCategory();
                articleCategory.sub_categories.add(subCategory);
            }
            categories.add(articleCategory);
        }
        primaryAbsListView.setAdapter(new CategoryAdapter(categories));
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
