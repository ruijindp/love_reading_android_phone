package com.ljmob.lovereadingphone.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.ReadingActivity;
import com.ljmob.lovereadingphone.context.MyApplication;
import com.ljmob.lovereadingphone.entity.Article;
import com.ljmob.lovereadingphone.entity.Music;
import com.ljmob.lovereadingphone.util.HeadSetTool;
import com.ljmob.lovereadingphone.util.RecorderFileUtil;
import com.ljmob.lovereadingphone.view.RecorderRipple;
import com.londonx.lmp3recorder.LRecorder;
import com.londonx.lmp3recorder.listener.AmplitudeListener;
import com.londonx.lmp3recorder.util.Mp3Decoder;
import com.londonx.lmp3recorder.util.SoundMixer;
import com.londonx.lutil.util.FileUtil;
import com.londonx.lutil.util.LMediaPlayer;
import com.londonx.lutil.util.ToastUtil;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by london on 15/10/29.
 * 录音机
 */
public class RecorderFragment extends Fragment implements AmplitudeListener, Runnable {
    View rootView;

    @Bind(R.id.view_recorder_ripple)
    RecorderRipple viewRecorderRipple;
    @Bind(R.id.view_recorder_tvTime)
    TextView viewRecorderTvTime;

    Music music;
    Article article;
    File musicFile;

    LMediaPlayer player;
    LRecorder recorder;
    File recordedFile;
    File uploadFile;

    Status currentStatus = Status.notReady;
    private boolean isRunning;
    private long recTime;

    private boolean isRetry;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.view_recorder, container, false);
        }
        ButterKnife.bind(this, rootView);
        countDown();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (recorder != null && recorder.isRecording()) {
            release();
        }
        ButterKnife.unbind(this);
    }

    @Override
    public void onAmplitude(float amplitude) {
        if (viewRecorderRipple == null) {
            return;
        }
        viewRecorderRipple.setAmplitude(amplitude);
    }

    @Override
    public void run() {
        isRunning = true;
        recTime = 0;
        while (isRunning) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!isRunning) {
                break;
            }
            if (currentStatus == Status.recording) {
                recTime += 200;
                if (viewRecorderTvTime == null) {
                    break;
                }
                viewRecorderTvTime.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isRunning) {
                            viewRecorderTvTime.setText(DateFormat.format("mm:ss", recTime));
                        }
                    }
                });
            }
        }
    }

    @OnClick(R.id.view_recorder_imgRetry)
    protected void retry() {
//        recTime = 0;
//        viewRecorderTvTime.setText(R.string.timer);
//        if (player != null) {
//            player.mediaPlayer.seekTo(0);
//        }
        setMetaData(music, article);
    }

    @OnClick(R.id.view_recorder_imgRecord)
    protected void recordOrPause() {
        switch (currentStatus) {
            case ready:
                player.play();
                recorder.start(recordedFile);

                ((ReadingActivity) getActivity()).startScrolling();
                viewRecorderRipple.setRunning(true);

                new Thread(this).start();

                currentStatus = Status.recording;
                break;
            case recording:
                recorder.pause();
                player.mediaPlayer.pause();
                viewRecorderRipple.setRunning(false);

                currentStatus = Status.pausing;
                break;
            case pausing:
                recorder.resume();
                player.mediaPlayer.start();
                viewRecorderRipple.setRunning(true);
                ((ReadingActivity) getActivity()).startScrolling();

                currentStatus = Status.recording;
                break;
            case notReady:
                ToastUtil.show(R.string.toast_recording_not_ready);
                break;
        }
    }

    @OnClick(R.id.view_recorder_imgDone)
    protected void finishRecording() {
        release();
        currentStatus = Status.finished;
        recorder.stop();

        if (HeadSetTool.isHeadSetConnected(getContext())) {// 耳机才做混音
            ((ReadingActivity) getActivity()).showMixDialog();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long mixStart = System.currentTimeMillis();
                    uploadFile = new File(recordedFile.getAbsolutePath().replace(".wav", "_mix.wav"));
                    File musicWav = null;
                    try {
                        musicWav = Mp3Decoder.syncDecode(musicFile, recTime);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.i("LondonX", "decode finish in :" + (System.currentTimeMillis() - mixStart) + "ms");
                    mixStart = System.currentTimeMillis();

                    new SoundMixer(musicWav, recordedFile, uploadFile).syncMixWav();

                    Log.i("LondonX", "mix finish in :" + (System.currentTimeMillis() - mixStart) + "ms");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ReadingActivity) getActivity()).setCurrentStatus(ReadingActivity.Status.upload);
                            ((ReadingActivity) getActivity()).dismissMixDialog();
                            ((ReadingActivity) getActivity()).setRecorderFile(uploadFile);
                        }
                    });
                }
            }).start();
        } else {
            uploadFile = recordedFile;
            ((ReadingActivity) getActivity()).setCurrentStatus(ReadingActivity.Status.upload);
            ((ReadingActivity) getActivity()).setRecorderFile(uploadFile);
        }
    }

    public void setMetaData(Music music, Article article) {
        if (currentStatus == Status.counting) {
            return;
        }
        this.music = music;
        this.article = article;
        try {
            this.musicFile = FileUtil.getDownloadFile(music.file_url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        recordedFile = RecorderFileUtil.getDefaultRecorderFile(MyApplication.currentUser, this.music, this.article);
        if (player == null) {
            player = new LMediaPlayer(null, null);
        }
        if (recorder == null) {
            recorder = new LRecorder();
            recorder.setAmplitudeListener(this);
        }
        if (viewRecorderTvTime != null) {
            viewRecorderTvTime.setText(R.string.timer);
        }
        player.prepareUrl(this.musicFile.getAbsolutePath());
        if (recorder.isRecording()) {
            boolean isDeleted = recorder.stop().delete();
            if (!isDeleted) {
                Log.e("LondonX", "old recFile cannot be deleted. RecorderFragment");
            }
        }
        countDown();
    }

    public boolean isRecording() {
        return currentStatus == Status.recording;
    }

    public void release() {
        isRunning = false;
        if (player != null) {
            player.stop();
        }
    }

    private void countDown() {
        currentStatus = Status.counting;
        if (viewRecorderRipple == null) {
            return;
        }
        if (music == null) {
            return;
        }
        ((ReadingActivity) getActivity()).startCountDown();
        viewRecorderRipple.setRunning(false);
        isRunning = false;

        viewRecorderRipple.postDelayed(new Runnable() {
            @Override
            public void run() {
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                isRetry = true;//再次startCountDown一定是重读
                ((ReadingActivity) activity).stopCountDown();
                currentStatus = Status.ready;
                recordOrPause();
            }
        }, isRetry ? 3500 : 3000);
    }

    public enum Status {
        notReady, counting, ready, recording, pausing, finished
    }
}
