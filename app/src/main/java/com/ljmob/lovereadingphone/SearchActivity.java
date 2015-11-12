package com.ljmob.lovereadingphone;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ljmob.lovereadingphone.adapter.RecommendAdapter;
import com.ljmob.lovereadingphone.context.EasyLoadActivity;
import com.ljmob.lovereadingphone.entity.Result;
import com.ljmob.lovereadingphone.net.NetConstant;
import com.ljmob.lovereadingphone.service.PlayerService;
import com.ljmob.lovereadingphone.util.DefaultParam;
import com.londonx.lutil.entity.LResponse;

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

    private List<Result> results;
    private RecommendAdapter recommendAdapter;
    private boolean isAppBarHided = true;

    private PlayerService playerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        bindService(new Intent(this, PlayerService.class), this, Context.BIND_AUTO_CREATE);

        int startOffset = getResources().getDimensionPixelSize(R.dimen.subject_list_refresh_start_offset);
        int endOffset = getResources().getDimensionPixelSize(R.dimen.subject_list_refresh_end_offset);
        primarySwipeRefreshLayout.setProgressViewOffset(false, startOffset, endOffset);
        View headEmpty = getLayoutInflater().inflate(R.layout.head_search, primaryListView, false);
        ((ListView) primaryListView).addHeaderView(headEmpty);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void responseData(LResponse response) {
        List<Result> appendData = new Gson().fromJson(response.body, new TypeToken<List<Result>>() {
        }.getType());
        if (currentPage == 1) {
            results = appendData;
            recommendAdapter = new RecommendAdapter(results);
            recommendAdapter.setPlayerService(playerService);
            primaryListView.setAdapter(recommendAdapter);
        } else {
            results.addAll(appendData);
            recommendAdapter.setNewData(results);
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
                    .setInterpolator(new AccelerateInterpolator(2)).start();
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
//        if (text.length() == 0) {
//            return;
//        }
        currentPage = 1;
        DefaultParam param = new DefaultParam();
        param.put("parameter", text.toString());
        initData(NetConstant.API_SEARCH, param);
    }

    @OnItemClick(R.id.primaryAbsListView)
    protected void selectResult(int position) {
        Intent intent = new Intent(this, ReadingActivity.class);
        intent.putExtra("result", results.get(position - 1));
        startActivity(intent);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        playerService = ((PlayerService.PlayerBinder) service).getService();
        if (recommendAdapter != null && recommendAdapter.getPlayerService() == null) {
            recommendAdapter.setPlayerService(playerService);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
