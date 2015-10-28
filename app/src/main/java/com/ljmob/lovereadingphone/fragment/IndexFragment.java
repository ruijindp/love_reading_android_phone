package com.ljmob.lovereadingphone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ljmob.lovereadingphone.CategoryActivity;
import com.ljmob.lovereadingphone.MainActivity;
import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.adapter.IndexAdapter;
import com.ljmob.lovereadingphone.context.EasyLoadFragment;
import com.ljmob.lovereadingphone.entity.Article;
import com.ljmob.lovereadingphone.entity.ArticleShelf;
import com.ljmob.lovereadingphone.util.DefaultParam;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by london on 15/10/26.
 * 第一页
 */
public class IndexFragment extends EasyLoadFragment {
    public static final int ACTION_CATEGORY = 1;
    LayoutInflater inflater;
    View rootView;

    HeadHolder headHolder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.view_primary_list, container, false);
            setContentView(rootView);
            int startOffset = getResources().getDimensionPixelSize(R.dimen.subject_list_refresh_start_offset);
            int endOffset = getResources().getDimensionPixelSize(R.dimen.subject_list_refresh_end_offset);
            primarySwipeRefreshLayout.setProgressViewOffset(false, startOffset, endOffset);

            View headEmpty = inflater.inflate(R.layout.toolbar_trans, primaryListView, false);
            View headMain = inflater.inflate(R.layout.head_index, primaryListView, false);
            headHolder = new HeadHolder(headMain);

            ((ListView) primaryListView).addHeaderView(headEmpty);
            ((ListView) primaryListView).addHeaderView(headMain);
        }
        initData("", new DefaultParam());
        return rootView;
    }

    @Override
    public void initData(String apiUrl, HashMap<String, Object> params) {
        super.initData(apiUrl, params);

        //TODO test
        List<ArticleShelf> shelf = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            ArticleShelf articleShelf = new ArticleShelf();
            articleShelf.article[0] = new Article();
            articleShelf.article[0].author = "莫迫桑";
            articleShelf.article[0].content = getString(R.string.test_content);
            articleShelf.article[0].cover_img = R.mipmap.test_cover + "";
            articleShelf.article[0].title = "我的叔叔于勒";
            articleShelf.article[1] = new Article();
            articleShelf.article[1].author = "莫迫桑";
            articleShelf.article[1].content = getString(R.string.test_content);
            articleShelf.article[1].cover_img = R.mipmap.test_cover + "";
            articleShelf.article[1].title = "我的叔叔于勒";
            shelf.add(articleShelf);
        }
        primaryListView.setAdapter(new IndexAdapter(getActivity(), shelf));
    }

    @Override
    public void onRefreshData() {
        ToastUtil.show("onRefreshData");
    }

    @Override
    public void onLoadMore() {
        ToastUtil.show("onLoadMore");
    }

    @Override
    public void responseData(LResponse response) {

    }

    @Override
    public void showAppBar() {
        super.showAppBar();
        ((MainActivity) getActivity()).showAppBar();
    }

    @Override
    public void hideAppBar() {
        super.hideAppBar();
        ((MainActivity) getActivity()).hideAppBar();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ToastUtil.show("IndexFragment.onActivityResult:" + resultCode);
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'head_indexx.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    class HeadHolder {
        @Bind(R.id.head_main_tvCurrentCate)
        TextView headMainTvCurrentCate;
        @Bind(R.id.head_main_tvCate)
        TextView headMainTvCate;

        HeadHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.head_main_tvCate)
        protected void openCateActivity() {
            startActivityForResult(new Intent(getActivity(), CategoryActivity.class), ACTION_CATEGORY);
        }
    }
}
