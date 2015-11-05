package com.ljmob.lovereadingphone.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.ReadingActivity;
import com.londonx.lutil.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by london on 15/10/29.
 * 看未评分结果（仅教师）
 */

public class NotRatedResultFragment extends Fragment {
    View rootView;
    @Bind(R.id.view_not_rated_result_tvTimerCurrent)
    TextView viewNotRatedResultTvTimerCurrent;
    @Bind(R.id.view_not_rated_result_sbPlayer)
    AppCompatSeekBar viewNotRatedResultSbPlayer;
    @Bind(R.id.view_not_rated_result_tvTimerTotal)
    TextView viewNotRatedResultTvTimerTotal;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.view_not_rated_result, container, false);
        }
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.view_not_rated_result_imgPlay)
    protected void playOrPause() {
        ToastUtil.show("playOrPause");
    }

    @OnClick(R.id.view_not_rated_result_imgRate)
    protected void makeRate() {
        MaterialDialog ratingDialog = new MaterialDialog.Builder(getActivity())
                .theme(Theme.LIGHT)
                .title(R.string.rate_plz)
                .customView(R.layout.view_dialog_rating, false)
                .positiveText(android.R.string.ok)
                .positiveColorRes(R.color.colorPrimary)
                .negativeText(android.R.string.cancel)
                .negativeColorRes(R.color.colorPrimary)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog,
                                        @NonNull DialogAction dialogAction) {
                        View rootView = materialDialog.getCustomView();
                        if (rootView == null) {
                            materialDialog.dismiss();
                            return;
                        }
                        float rating = ((RatingBar) rootView
                                .findViewById(R.id.view_dialog_rating_rb)).getRating();
                        ToastUtil.show("rating:" + rating);
                        ((ReadingActivity) getActivity()).setCurrentStatus(ReadingActivity.Status.ratedResult);
                        materialDialog.dismiss();
                    }
                })
                .build();
        ratingDialog.show();
    }

    @OnClick(R.id.view_not_rated_result_imgFeeling)
    protected void showFeeling() {
        MaterialDialog feelingDialog = new MaterialDialog.Builder(getActivity())
                .theme(Theme.LIGHT)
                .title(R.string.feeling)
                .content(R.string.test_feeling)
                .build();
        feelingDialog.show();
    }
}
