package com.ljmob.lovereadingphone.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.context.MyApplication;
import com.ljmob.lovereadingphone.entity.Result;
import com.ljmob.lovereadingphone.entity.User;
import com.londonx.lutil.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by london on 15/10/29.
 * 看已评分结果
 */

public class RatedResultFragment extends Fragment {
    View rootView;
    @Bind(R.id.view_rated_result_tvTimerCurrent)
    TextView viewRatedResultTvTimerCurrent;
    @Bind(R.id.view_rated_result_sbPlayer)
    AppCompatSeekBar viewRatedResultSbPlayer;
    @Bind(R.id.view_rated_result_tvTimerTotal)
    TextView viewRatedResultTvTimerTotal;
    @Bind(R.id.view_rated_result_tvLike)
    TextView viewRatedResultTvLike;
    private Result result;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            if (MyApplication.currentUser == null) {//未登录
                rootView = inflater.inflate(R.layout.view_rated_result_student, container, false);
            } else if (MyApplication.currentUser.role == User.Role.student) {//学生
                rootView = inflater.inflate(R.layout.view_rated_result_student, container, false);
            } else {
                rootView = inflater.inflate(R.layout.view_rated_result_teacher, container, false);
            }
        }
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.view_rated_result_imgPlay)
    protected void playOrPause() {
        ToastUtil.show("playOrPause");
    }

    @OnClick(R.id.view_rated_result_imgLetMeTry)
    protected void letMeTry() {
        ToastUtil.show("letMeTry");
    }

    @OnClick(R.id.view_rated_result_imgFeeling)
    protected void showFeeling() {
        MaterialDialog feelingDialog = new MaterialDialog.Builder(getActivity())
                .theme(Theme.LIGHT)
                .title(R.string.feeling)
                .content(R.string.test_feeling)
                .build();
        feelingDialog.show();
    }

    @OnClick(R.id.view_rated_result_tvLike)
    protected void like() {
        ToastUtil.show("like");
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
