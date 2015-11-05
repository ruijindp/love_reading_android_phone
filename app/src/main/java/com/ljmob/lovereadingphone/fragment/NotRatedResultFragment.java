package com.ljmob.lovereadingphone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.ljmob.lovereadingphone.LoginActivity;
import com.ljmob.lovereadingphone.MusicActivity;
import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.context.MyApplication;
import com.ljmob.lovereadingphone.entity.Result;
import com.ljmob.lovereadingphone.entity.User;
import com.ljmob.lovereadingphone.net.NetConstant;
import com.londonx.lutil.util.LMediaPlayer;
import com.londonx.lutil.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by london on 15/10/29.
 * 看未评分结果（仅教师）
 */

public class NotRatedResultFragment extends Fragment implements LMediaPlayer.OnProgressChangeListener {
    View rootView;
    @Bind(R.id.view_not_rated_result_tvTimerCurrent)
    TextView viewNotRatedResultTvTimerCurrent;
    @Bind(R.id.view_not_rated_result_sbPlayer)
    AppCompatSeekBar viewNotRatedResultSbPlayer;
    @Bind(R.id.view_not_rated_result_tvTimerTotal)
    TextView viewNotRatedResultTvTimerTotal;
    @Bind(R.id.view_not_rated_result_imgRateOrLetMeTry)
    ImageView viewNotRatedResultImgRateOrLetMeTry;
    @Bind(R.id.view_not_rated_result_tvRateOrLetMeTry)
    TextView viewNotRatedResultTvRateOrLetMeTry;
    @Bind(R.id.view_not_rated_result_tvFeeling)
    TextView viewNotRatedResultTvFeeling;
    @Bind(R.id.view_not_rated_result_imgPlay)
    ImageView viewNotRatedResultImgPlay;

    private Result result;
    LMediaPlayer player;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.view_not_rated_result, container, false);
        }
        ButterKnife.bind(this, rootView);

        if (result != null) {
            startPlay();
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        makeViewsByRole();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        release();
        ButterKnife.unbind(this);
    }

    @Override
    public void progressChanged(int position, int duration) {
        viewNotRatedResultTvTimerCurrent.setText(DateFormat.format("mm:ss", position));
        viewNotRatedResultTvTimerTotal.setText(DateFormat.format("mm:ss", duration));

        if (duration - position <= 100) {
            viewNotRatedResultImgPlay.setImageResource(R.mipmap.icon_play);
        }
    }

    @OnClick(R.id.view_not_rated_result_imgPlay)
    protected void playOrPause() {
        if (player == null) {
            return;
        }
        if (player.mediaPlayer.isPlaying()) {
            player.pause();
            viewNotRatedResultImgPlay.setImageResource(R.mipmap.icon_play);
        } else {
            player.play();
            viewNotRatedResultImgPlay.setImageResource(R.mipmap.icon_pause);
        }
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

    @OnClick(R.id.view_not_rated_result_imgRateOrLetMeTry)
    protected void rateOrLetMeTry() {
        if (MyApplication.currentUser == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            return;
        }
        if (MyApplication.currentUser.role == User.Role.student) {//学生
            Intent musicIntent = new Intent(getContext(), MusicActivity.class);
            musicIntent.putExtra("article", result.article);
            startActivity(musicIntent);
            getActivity().finish();
        } else {//老师
            makeRate();
        }
    }

    public void setResult(Result result) {
        this.result = result;
        if (viewNotRatedResultSbPlayer != null) {
            startPlay();
        }
    }

    private void startPlay() {
        if (player == null) {
            player = new LMediaPlayer(null, viewNotRatedResultSbPlayer);
            player.setOnProgressChangeListener(this);
        }
        player.prepareUrl(NetConstant.ROOT_URL + result.file_url);
        player.play();
    }

    private void makeViewsByRole() {
        if (MyApplication.currentUser == null) {//未登录
            viewNotRatedResultImgRateOrLetMeTry.setImageResource(R.mipmap.icon_sing);
            viewNotRatedResultTvRateOrLetMeTry.setText(R.string.let_me_try);
            return;
        }
        if (MyApplication.currentUser.role == User.Role.student) {//学生
            viewNotRatedResultImgRateOrLetMeTry.setImageResource(R.mipmap.icon_sing);
            viewNotRatedResultTvRateOrLetMeTry.setText(R.string.re_reading);
            viewNotRatedResultTvFeeling.setText(R.string.feeling_my);
        } else {//老师
            viewNotRatedResultImgRateOrLetMeTry.setImageResource(R.mipmap.icon_pingfen);
            viewNotRatedResultTvRateOrLetMeTry.setText(R.string.rate);
            viewNotRatedResultTvFeeling.setText(R.string.feeling);
        }
    }

    private void makeRate() {
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
//                        ((ReadingActivity) getActivity()).setCurrentStatus(ReadingActivity.Status.ratedResult);
                        materialDialog.dismiss();
                    }
                })
                .build();
        ratingDialog.show();
    }

    private void release() {
        if (player != null && player.mediaPlayer.isPlaying()) {
            player.stop();
        }
    }
}
