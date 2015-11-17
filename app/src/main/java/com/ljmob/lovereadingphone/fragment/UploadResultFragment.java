package com.ljmob.lovereadingphone.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.ReadingActivity;
import com.londonx.lutil.util.LMediaPlayer;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by london on 15/10/29.
 * 试听未上传
 */

public class UploadResultFragment extends Fragment implements LMediaPlayer.OnProgressChangeListener {
    View rootView;
    @Bind(R.id.view_upload_result_tvTimerCurrent)
    TextView viewUploadResultTvTimerCurrent;
    @Bind(R.id.view_upload_result_sbPlayer)
    AppCompatSeekBar viewUploadResultSbPlayer;
    @Bind(R.id.view_upload_result_tvTimerTotal)
    TextView viewUploadResultTvTimerTotal;
    @Bind(R.id.view_upload_result_imgPlay)
    ImageView viewUploadResultImgPlay;

    File recorderFile;
    LMediaPlayer player;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.view_upload_result, container, false);
        }
        ButterKnife.bind(this, rootView);
        player = new LMediaPlayer(null, viewUploadResultSbPlayer);
        player.setOnProgressChangeListener(this);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        player.stop();
        ButterKnife.unbind(this);
    }

    @Override
    public void progressChanged(int position, int duration) {
        if (duration - position <= 100) {
            viewUploadResultImgPlay.setImageResource(R.mipmap.icon_play);
        }
        if (viewUploadResultTvTimerCurrent != null) {
            viewUploadResultTvTimerCurrent.setText(DateFormat.format("mm:ss", position));
        }
    }

    @OnClick(R.id.view_upload_result_imgPlay)
    protected void playOrPause() {
        if (player == null) {
            return;
        }
        if (player.mediaPlayer.isPlaying()) {
            player.pause();
            viewUploadResultImgPlay.setImageResource(R.mipmap.icon_play);
        } else {
            player.play();
            ((ReadingActivity) getActivity()).startScrolling();
            viewUploadResultImgPlay.setImageResource(R.mipmap.icon_pause);
        }
    }

    @OnClick(R.id.view_upload_result_imgUpload)
    protected void showFeeling() {
        MaterialDialog feelingDialog = new MaterialDialog.Builder(getActivity())
                .theme(Theme.LIGHT)
                .title(R.string.dialog_feeling)
                .positiveText(R.string.dialog_feeling_confirm)
                .positiveColorRes(R.color.colorPrimary)
                .customView(R.layout.view_dialog_feeling, true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog,
                                        @NonNull DialogAction dialogAction) {
                        EditText etFeeling = (EditText) materialDialog.getCustomView();
                        String feeling = "";
                        if (etFeeling != null) {
                            feeling = etFeeling.getText().toString();
                        }
                        ((ReadingActivity) getActivity()).upload(recorderFile, feeling);
                    }
                })
                .build();
        feelingDialog.show();
    }

    @OnClick(R.id.view_upload_result_imgRetry)
    protected void retry() {
        player.stop();
        ((ReadingActivity) getActivity()).setCurrentStatus(ReadingActivity.Status.record);
    }

    public void setRecorderFile(File recorderFile) {
        this.recorderFile = recorderFile;
        if (recorderFile.exists()) {
            viewUploadResultSbPlayer.setSecondaryProgress(100);
        }
        viewUploadResultSbPlayer.setProgress(0);
        player.prepareUrl(this.recorderFile.getAbsolutePath());
        viewUploadResultTvTimerTotal.setText(DateFormat
                .format("mm:ss", player.mediaPlayer.getDuration()));
        if (viewUploadResultImgPlay != null) {
            playOrPause();
        }
    }

    public boolean isPlaying() {
        if (player == null || player.mediaPlayer == null) {
            return false;
        }
        return player.mediaPlayer.isPlaying();
    }
}
