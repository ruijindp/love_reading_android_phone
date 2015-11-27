package com.ljmob.lovereadingphone.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.gson.Gson;
import com.ljmob.lovereadingphone.LoginActivity;
import com.ljmob.lovereadingphone.MusicActivity;
import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.ReadingActivity;
import com.ljmob.lovereadingphone.context.MyApplication;
import com.ljmob.lovereadingphone.entity.Result;
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
 * 看已评分结果
 */

public class RatedResultFragment extends Fragment
        implements LRequestTool.OnResponseListener, ServiceConnection, LMediaPlayer.OnProgressChangeListener {
    private static final int API_VOTES = 1;
    private static final int API_RESULTS = 2;

    View rootView;
    @Bind(R.id.view_rated_result_tvTimerCurrent)
    TextView viewRatedResultTvTimerCurrent;
    @Bind(R.id.view_rated_result_sbPlayer)
    AppCompatSeekBar viewRatedResultSbPlayer;
    @Bind(R.id.view_rated_result_tvTimerTotal)
    TextView viewRatedResultTvTimerTotal;
    @Bind(R.id.view_rated_result_tvLike)
    TextView viewRatedResultTvLike;
    @Bind(R.id.view_rated_result_imgLike)
    ImageView viewRatedResultImgLike;
    @Bind(R.id.view_rated_result_imgPlay)
    ImageView viewRatedResultImgPlay;

    private Result result;
    private LRequestTool requestTool;
    private PlayerService playerService;

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
        requestTool = new LRequestTool(this);
        ButterKnife.bind(this, rootView);
        if (result != null && result.score.size() != 0) {
            getActivity().bindService(new Intent(getContext(), PlayerService.class),
                    this, Context.BIND_AUTO_CREATE);
        }
        initViews();
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (result != null && result.score.size() != 0) {
            getActivity().unbindService(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (result == null) {
            return;
        }
        if (requestTool == null) {
            return;
        }
        //TODO
        DefaultParam params = new DefaultParam();
        params.put("result_id", result.id);
        requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_RESULTS, params, API_RESULTS);
    }

    @OnClick(R.id.view_rated_result_imgPlay)
    protected void playOrPause() {
        if (playerService == null) {
            return;
        }
        if (playerService.isPlaying()) {
            playerService.getPlayer().pause();
            viewRatedResultImgPlay.setImageResource(R.mipmap.icon_play);
        } else {
            playerService.getPlayer().play();
            ((ReadingActivity) getActivity()).startScrolling();
            viewRatedResultImgPlay.setImageResource(R.mipmap.icon_pause);
        }
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        playerService = ((PlayerService.PlayerBinder) service).getService();
        playerService.getPlayer().setSkbProgress(viewRatedResultSbPlayer);
        playerService.getPlayer().setOnProgressChangeListener(this);
        if (result != null) {
            if (playerService.getResult() != null) {
                if (result.id != playerService.getResult().id) {
                    playerService.setResult(result);
                    ((ReadingActivity) getActivity()).startScrolling();
                }
            } else {
                playerService.setResult(result);
                ((ReadingActivity) getActivity()).startScrolling();
            }
        }
        if (playerService.isPlaying()) {
            viewRatedResultImgPlay.setImageResource(R.mipmap.icon_pause);
            ((ReadingActivity) getActivity()).startScrolling();
        } else {
            viewRatedResultImgPlay.setImageResource(R.mipmap.icon_play);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }

    @Override
    public void progressChanged(int position, int duration) {
        if (viewRatedResultTvTimerCurrent == null) {
            return;
        }
        if (playerService == null) {
            return;
        }
        if (!playerService.isPlaying()) {
            return;
        }
        if (playerService.isPreparing()) {
            return;
        }
        viewRatedResultTvTimerCurrent.setText(DateFormat.format("mm:ss", position));
        viewRatedResultTvTimerTotal.setText(DateFormat.format("mm:ss", duration));
    }

    @Override
    public void onResponse(LResponse response) {
        if (response.responseCode == 400) {
            Intent loginIntent = new Intent(getContext(), LoginActivity.class);
            startActivity(loginIntent);
            return;
        }
        if (response.responseCode == 401) {
            Intent loginIntent = new Intent(getContext(), LoginActivity.class);
            loginIntent.putExtra("isReLogin", true);
            startActivity(loginIntent);
            return;
        }
        if (response.responseCode == 0) {
            ToastUtil.show(R.string.toast_server_err_0);
            return;
        }
        if (response.responseCode != 201 && response.responseCode != 200) {
            ToastUtil.serverErr(response);
            return;
        }
        if (!response.body.startsWith("[") && !response.body.startsWith("{")) {
            ToastUtil.show(R.string.toast_server_err_1);
            return;
        }
        MyReadingFragment.hasDataChanged = true;
        RecommendFragment.hasDataChanged = true;
        RankFragment.hasDataChanged = true;
        switch (response.requestCode) {
            case API_RESULTS:
                result = new Gson().fromJson(response.body, Result.class);
                if (viewRatedResultImgLike != null) {
                    initViews();
                }
                break;
        }
    }

    @OnClick(R.id.view_rated_result_imgLetMeTry)
    protected void letMeTry() {
        if (MyApplication.currentUser == null) {
            ToastUtil.show(R.string.toast_login_timeout);
            startActivity(new Intent(getContext(), LoginActivity.class));
            return;
        }
        if (!result.article.is_valid) {
            ToastUtil.show(R.string.toast_article_invalid);
            return;
        }
        Intent intent = new Intent(getContext(), MusicActivity.class);
        intent.putExtra("article", result.article);
        startActivity(intent);
        getActivity().finish();
    }

    @OnClick(R.id.view_rated_result_imgFeeling)
    protected void showFeeling() {
        MaterialDialog feelingDialog = new MaterialDialog.Builder(getActivity())
                .theme(Theme.LIGHT)
                .title(R.string.feeling)
                .content(result.feeling)
                .build();
        feelingDialog.show();
    }

    @OnClick(R.id.view_rated_result_imgLike)
    protected void like() {
        if (MyApplication.currentUser == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            return;
        }
        if (result.is_vote) {
            result.votes--;
            result.is_vote = false;
        } else {
            result.votes++;
            result.is_vote = true;
        }
        initViews();
        if (result.is_vote) {
            viewRatedResultImgLike.animate().scaleX(1.5f).scaleY(1.5f).setDuration(250)
                    .setInterpolator(new AccelerateInterpolator()).start();
            viewRatedResultImgLike.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewRatedResultImgLike.animate().scaleX(1.0f).scaleY(1.0f).setDuration(250)
                            .setInterpolator(new DecelerateInterpolator()).start();
                }
            }, 251);
        }

        DefaultParam param = new DefaultParam();
        param.put("result_id", result.id);
        requestTool.doPost(NetConstant.ROOT_URL + NetConstant.API_VOTES, param, API_VOTES);
    }

    public void setResult(Result result) {
        if (result == null) {
            return;
        }
        this.result = result;
        if (result.score.size() != 0) {
            Activity activity = getActivity();
            if (activity != null) {
                activity.bindService(new Intent(activity, PlayerService.class),
                        this, Context.BIND_AUTO_CREATE);
            }
        }
        if (playerService != null) {
            if (playerService.getResult() != null) {
                if (this.result.id != playerService.getResult().id) {
                    playerService.setResult(this.result);
                    ((ReadingActivity) getActivity()).startScrolling();
                }
            } else {
                playerService.setResult(this.result);
                ((ReadingActivity) getActivity()).startScrolling();
            }
        }
    }

    public Result getResult() {
        return result;
    }

    private void initViews() {
        if (result == null) {
            viewRatedResultTvLike.setText(getString(R.string.like_, 0));
            return;
        }
        if (result.is_vote) {
            viewRatedResultImgLike.setImageResource(R.mipmap.icon_love_s);
        } else {
            viewRatedResultImgLike.setImageResource(R.mipmap.icon_love_u);
        }
        viewRatedResultTvLike.setText(getString(R.string.like_, result.votes));
    }
}
