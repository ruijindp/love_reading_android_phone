package com.happysong.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.happysong.android.adapter.IndexAdapter;
import com.happysong.android.adapter.RecommendAdapter;
import com.happysong.android.context.EasyLoadActivity;
import com.happysong.android.entity.Article;
import com.happysong.android.entity.Result;
import com.happysong.android.net.NetConstant;
import com.happysong.android.service.PlayerService;
import com.happysong.android.util.ArticleShelfWrapper;
import com.happysong.android.util.DefaultParam;
import com.londonx.lutil.adapter.LAdapter;
import com.londonx.lutil.entity.LResponse;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by london on 15/11/6.
 * 搜索
 */
public class SearchActivity extends EasyLoadActivity implements ServiceConnection {
    @Bind(R.id.activity_search_cardSearch)
    CardView activitySearchCardSearch;
    @Bind(R.id.activity_search_etKeyWord)
    EditText activitySearchEtKeyWord;

    private List<Result> results;
    private List<Article> articles;
    private LAdapter adapter;
    private boolean isAppBarHided = true;

    private PlayerService playerService;
    private boolean isArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        isArticles = getIntent().getBooleanExtra("article", false);
        ButterKnife.bind(this);
        bindService(new Intent(this, PlayerService.class), this, Context.BIND_AUTO_CREATE);

        int startOffset = getResources().getDimensionPixelSize(R.dimen.subject_list_refresh_start_offset);
        int endOffset = getResources().getDimensionPixelSize(R.dimen.subject_list_refresh_end_offset);
        primarySwipeRefreshLayout.setProgressViewOffset(false, startOffset, endOffset);
        View headEmpty = getLayoutInflater().inflate(R.layout.head_search, primaryListView, false);
        ((ListView) primaryListView).addHeaderView(headEmpty);
        if (isArticles) {
            activitySearchEtKeyWord.setHint(R.string.hint_search_article);
            ((ListView) primaryListView).setDividerHeight(0);
        }
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
    public void responseData(LResponse response) {
        if (isArticles) {
            List<Article> appendData = new Gson().fromJson(response.body, new TypeToken<List<Article>>() {
            }.getType());
            if (currentPage == 1) {
                articles = appendData;
                adapter = new IndexAdapter(ArticleShelfWrapper.wrap(articles));
                primaryListView.setAdapter(adapter);
            } else {
                articles.addAll(appendData);
                adapter.setNewData(ArticleShelfWrapper.wrap(articles));
            }
        } else {
            List<Result> appendData = new Gson().fromJson(response.body, new TypeToken<List<Result>>() {
            }.getType());
            if (currentPage == 1) {
                results = appendData;
                adapter = new RecommendAdapter(results);
                ((RecommendAdapter) adapter).setPlayerService(playerService);
                primaryListView.setAdapter(adapter);
            } else {
                results.addAll(appendData);
                adapter.setNewData(results);
            }
        }
    }

    @Override
    public void showAppBar() {
        if (isAppBarHided) {
            activitySearchCardSearch.animate().translationY(0)
                    .setInterpolator(new DecelerateInterpolator(2)).start();
            isAppBarHided = false;
        }
    }

    @Override
    public void hideAppBar() {
        if (!isAppBarHided) {
            activitySearchCardSearch.animate().translationY(-activitySearchCardSearch.getBottom())
                    .setInterpolator(new DecelerateInterpolator(2)).start();
            isAppBarHided = true;
        }
    }

    @OnClick(R.id.activity_search_imgBack)
    protected void goBack() {
        finish();
    }

    @OnClick(R.id.activity_search_imgSearch)
    protected void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @OnTextChanged(R.id.activity_search_etKeyWord)
    protected void makeSearch(CharSequence text) {
        if (text.length() == 0) {
            return;
        }
        currentPage = 1;
        DefaultParam param = new DefaultParam();
        param.put("parameter", text.toString());
        if (isArticles) {
            initData(NetConstant.API_SEARCH_ARTICLES, param);
        } else {
            initData(NetConstant.API_SEARCH_RESULTS, param);
        }
    }

    @OnItemClick(R.id.primaryAbsListView)
    protected void selectResult(int position) {
        if (isArticles) {
            return;
        }
        Intent intent = new Intent(this, ReadingActivity.class);
        intent.putExtra("result", results.get(position - 1));
        startActivity(intent);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if (isArticles) {
            return;
        }
        playerService = ((PlayerService.PlayerBinder) service).getService();
        if (adapter != null && ((RecommendAdapter) adapter).getPlayerService() == null) {
            ((RecommendAdapter) adapter).setPlayerService(playerService);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
