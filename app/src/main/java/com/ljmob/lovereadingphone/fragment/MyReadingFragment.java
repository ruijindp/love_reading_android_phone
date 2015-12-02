package com.ljmob.lovereadingphone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ljmob.lovereadingphone.MyReadingActivity;
import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.ReadingActivity;
import com.ljmob.lovereadingphone.adapter.MyReadingAdapter;
import com.ljmob.lovereadingphone.context.EasyLoadFragment;
import com.ljmob.lovereadingphone.context.MyApplication;
import com.ljmob.lovereadingphone.entity.Result;
import com.ljmob.lovereadingphone.entity.Subject;
import com.ljmob.lovereadingphone.entity.User;
import com.ljmob.lovereadingphone.net.NetConstant;
import com.ljmob.lovereadingphone.util.DefaultParam;
import com.londonx.lutil.entity.LResponse;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Created by london on 15/10/27.
 * 我的朗读
 */
public class MyReadingFragment extends EasyLoadFragment {
    public static boolean hasDataChanged;

    public Type type;
    View rootView;
    List<Result> results;
    private Subject subject;
    MyReadingAdapter myReadingAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.view_primary_list_div_inset_72, container, false);
            setContentView(rootView);

            int startOffset = getResources().getDimensionPixelSize(R.dimen.subject_list_refresh_start_offset_tab);
            int endOffset = getResources().getDimensionPixelSize(R.dimen.subject_list_refresh_end_offset_tab);
            primarySwipeRefreshLayout.setProgressViewOffset(false, startOffset, endOffset);

            View headEmpty = inflater.inflate(R.layout.head_my_reading, primaryListView, false);

            ((ListView) primaryListView).addHeaderView(headEmpty);

            initData();
        }
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasDataChanged) {
            onRefresh();
        }
        hasDataChanged = false;
    }

    @Override
    public void showAppBar() {
        super.showAppBar();
        ((MyReadingActivity) getActivity()).showAppBar();
    }

    @Override
    public void hideAppBar() {
        super.hideAppBar();
        ((MyReadingActivity) getActivity()).hideAppBar();
    }

    @Override
    public void responseData(LResponse response) {
        if (response.requestCode == EasyLoadFragment.GET_DATA) {
            List<Result> appendData = new Gson().fromJson(response.body, new TypeToken<List<Result>>() {
            }.getType());
            if (currentPage == 1) {
                results = appendData;
                myReadingAdapter = new MyReadingAdapter(results, type);
                primaryListView.setAdapter(myReadingAdapter);
            } else {
                results.addAll(appendData);
                myReadingAdapter.setNewData(results);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnItemClick(R.id.primaryAbsListView)
    protected void inspectResult(int position) {
        if (position < 1) {
            return;
        }
        Intent intent = new Intent(getContext(), ReadingActivity.class);
        intent.putExtra("result", results.get(position - 1));//有header
        startActivity(intent);
    }

    private void initData() {
        if (subject == null) {
            return;
        }
        if (rootView == null) {
            return;
        }
        DefaultParam param = new DefaultParam();
        param.put("subject_id", subject.id);
        if (type == Type.rated) {
            param.put("is_check", true);
        } else {
            param.put("is_check", false);
        }
        if (MyApplication.currentUser.role == User.Role.student) {
            initData(NetConstant.API_MY_RESULTS, param);
        } else {
            initData(NetConstant.API_STUDENT_RESULTS, param);
        }
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
        initData();
    }

    public enum Type {
        notRated, rated
    }
}
