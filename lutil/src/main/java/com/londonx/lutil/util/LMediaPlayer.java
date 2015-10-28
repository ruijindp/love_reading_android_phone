package com.londonx.lutil.util;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by london on 15/7/8.
 * 多媒体播放器
 * Update at 2015-07-24 17:26:11
 */
public class LMediaPlayer implements MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
        Handler.Callback,
        SurfaceHolder.Callback, SeekBar.OnSeekBarChangeListener {
    public MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private SeekBar skbProgress;

    /**
     * Media player
     *
     * @param surfaceView null if you want to play Audio
     * @param skbProgress show play and buffering progress
     */
    public LMediaPlayer(SurfaceView surfaceView, SeekBar skbProgress) {
        setSeekBar(skbProgress);
        if (surfaceView != null) {
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
        }
        mediaPlayer = new MediaPlayer();
        Timer mTimer = new Timer();
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    public void setSeekBar(SeekBar skbProgress) {
        this.skbProgress = skbProgress;
        skbProgress.setOnSeekBarChangeListener(this);
    }

    /**
     * ****************************************************
     * 通过定时器和Handler来更新进度条
     * ****************************************************
     */
    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (mediaPlayer == null)
                return;
            if (mediaPlayer.isPlaying() && !skbProgress.isPressed()) {
                handleProgress.sendEmptyMessage(0);
            }
        }
    };

    Handler handleProgress = new Handler(this);
    //*****************************************************

    /**
     * start playing
     */
    public void play() {
        mediaPlayer.start();
    }

    /**
     * online media url
     *
     * @param videoUrl media(video and mp3) url
     */
    public void playUrl(String videoUrl) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.prepareAsync();//prepare之后自动播放
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * pause playing
     */
    public void pause() {
        mediaPlayer.pause();
    }

    /**
     * stop playing and auto mediaPlayer.release();
     */
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        try {
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
    }


    @Override
    /**
     * 通过onPrepared播放
     */
    public void onPrepared(MediaPlayer arg0) {
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        if (videoHeight != 0 && videoWidth != 0) {
            arg0.start();
        }
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
    }

    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
        skbProgress.setSecondaryProgress(bufferingProgress);
    }

    @Override
    public boolean handleMessage(Message msg) {
        int position = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();

        if (duration > 0) {
            skbProgress.setMax(duration);
            skbProgress.setProgress(position);
        }
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mediaPlayer.seekTo(seekBar.getProgress());
    }
}
