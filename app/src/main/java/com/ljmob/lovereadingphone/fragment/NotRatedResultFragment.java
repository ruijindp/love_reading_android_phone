package com.ljmob.lovereadingphone.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
import com.google.gson.Gson;
import com.ljmob.lovereadingphone.LoginActivity;
import com.ljmob.lovereadingphone.MusicActivity;
import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.ReadingActivity;
import com.ljmob.lovereadingphone.context.MyApplication;
import com.ljmob.lovereadingphone.entity.Result;
import com.ljmob.lovereadingphone.entity.Score;
import com.ljmob.lovereadingphone.entity.User;
import com.ljmob.lovereadingphone.net.NetConstant;
import com.ljmob.lovereadingphone.service.PlayerService;
import com.ljmob.lovereadingphone.util.DefaultParam;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LMediaPlayer;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by london on 15/10/29.
 * 看未评分结果（仅教师）
 */

public class NotRatedResultFragment extends Fragment implements
        LMediaPlayer.OnProgressChangeListener,
        LRequestTool.OnResponseListener,
        ServiceConnection {
    private static final int API_SCORE = 1;

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
    LRequestTool requestTool;
    boolean isCommitting;
    private MaterialDialog ratingDialog;

    private PlayerService playerService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.view_not_rated_result, container, false);
        }
        ButterKnife.bind(this, rootView);
        if (result != null && result.score.size() == 0) {
            getActivity().bindService(new Intent(getContext(), PlayerService.class), this, Context.BIND_AUTO_CREATE);
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
        ButterKnife.unbind(this);
        if (result != null && result.score.size() == 0) {
            getActivity().unbindService(this);
        }
    }

    @Override
    public void progressChanged(int position, int duration) {
        if (viewNotRatedResultTvTimerCurrent == null) {
            return;
        }
        viewNotRatedResultTvTimerCurrent.setText(DateFormat.format("mm:ss", position));
        viewNotRatedResultTvTimerTotal.setText(DateFormat.format("mm:ss", duration));

        if (duration - position <= 100) {
            viewNotRatedResultImgPlay.setImageResource(R.mipmap.icon_play);
        }
    }

    @Override
    public void onResponse(LResponse response) {
        ratingDialog.setCancelable(true);
        isCommitting = false;
        if (response.responseCode == 401) {
            ToastUtil.show(R.string.toast_login_timeout);
            startActivity(new Intent(getContext(), LoginActivity.class));
            return;
        }
        switch (response.requestCode) {
            case API_SCORE:
                ToastUtil.show(R.string.committed);
                ratingDialog.dismiss();
                MyReadingFragment.hasDataChanged = true;
                Score score = new Gson().fromJson(response.body, Score.class);
                result.score.add(score);
                ((ReadingActivity) getActivity()).setRatedResult(result);
                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        playerService = ((PlayerService.PlayerBinder) service).getService();
        playerService.getPlayer().setSkbProgress(viewNotRatedResultSbPlayer);
        playerService.getPlayer().setOnProgressChangeListener(this);
        startPlay();
        if (playerService.isPlaying()) {
            viewNotRatedResultImgPlay.setImageResource(R.mipmap.icon_pause);
            ((ReadingActivity) getActivity()).startScrolling();
        } else {
            viewNotRatedResultImgPlay.setImageResource(R.mipmap.icon_play);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @OnClick(R.id.view_not_rated_result_imgPlay)
    protected void playOrPause() {
        if (playerService == null) {
            return;
        }
        if (playerService.isPlaying()) {
            playerService.getPlayer().pause();
            viewNotRatedResultImgPlay.setImageResource(R.mipmap.icon_play);
        } else {
            playerService.getPlayer().play();
            ((ReadingActivity) getActivity()).startScrolling();
            viewNotRatedResultImgPlay.setImageResource(R.mipmap.icon_pause);
        }
    }

    @OnClick(R.id.view_not_rated_result_imgFeeling)
    protected void showFeeling() {
        MaterialDialog feelingDialog = new MaterialDialog.Builder(getActivity())
                .theme(Theme.LIGHT)
                .title(R.string.feeling)
                .content(result.feeling)
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
            getActivity().bindService(new Intent(getContext(), PlayerService.class), this, Context.BIND_AUTO_CREATE);
            startPlay();
        }
    }

    private void startPlay() {
        if (playerService == null || result == null) {
            return;
        }
        if (playerService.getResult() == null) {
            playerService.setResult(result);
            ((ReadingActivity) getActivity()).startScrolling();
        } else if (playerService.getResult().id != result.id) {
            playerService.setResult(result);
            ((ReadingActivity) getActivity()).startScrolling();
        }
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
        ratingDialog = new MaterialDialog.Builder(getActivity())
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
                        if (isCommitting) {
                            ToastUtil.show(R.string.committing);
                            return;
                        }
                        View rootView = materialDialog.getCustomView();
                        if (rootView == null) {
                            materialDialog.dismiss();
                            return;
                        }
                        RatingBar ratingBar = ((RatingBar) rootView
                                .findViewById(R.id.view_dialog_rating_rb));
                        float rating = ratingBar.getRating();
                        ToastUtil.show("rating:" + rating);
                        ratingBar.setIsIndicator(true);
                        uploadScore(rating);
                    }
                })
                .build();
        ratingDialog.show();
    }

    private void uploadScore(float rating) {
        isCommitting = true;
        ratingDialog.setCancelable(false);
        if (requestTool == null) {
            requestTool = new LRequestTool(this);
        }
        DefaultParam param = new DefaultParam();
        param.put("result_id", result.id);
        param.put("score", rating);
        requestTool.doPost(NetConstant.ROOT_URL + NetConstant.API_SCORE, param, API_SCORE);
    }
}
