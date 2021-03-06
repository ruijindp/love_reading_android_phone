package com.happysong.android.service;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.happysong.android.entity.Result;
import com.happysong.android.net.NetConstant;
import com.londonx.lutil.util.LMediaPlayer;

/**
 * Created by london on 15/11/11.
 * 全局播放器
 */
public class PlayerService extends Service implements
        MediaPlayer.OnPreparedListener {
    public static final String ACTION_RESULT_CHANGED = "com.londonx.lutil.util.LMediaPlayer.ACTION_RESULT_CHANGED";
    private static Result result;
    private static LMediaPlayer player;
    private boolean isPreparing;

    @Override
    public void onCreate() {
        super.onCreate();
        if (player == null) {
            player = new LMediaPlayer(null, null, this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayerBinder();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        player.play();
        isPreparing = false;
        Intent playerIntent = new Intent(ACTION_RESULT_CHANGED);
        playerIntent.putExtra("result", PlayerService.result);
        sendBroadcast(playerIntent);
    }

    public void setResult(final Result result) {
        PlayerService.result = result;
        if (PlayerService.result == null) {
            if (isPlaying()) {
                player.stop();
            }
            return;
        }
        if (player.mediaPlayer == null) {
            System.gc();
            player.mediaPlayer = new MediaPlayer();
        }
        player.stop();
        isPreparing = true;
        player.playUrl(PlayerService.result.qiniu_url == null ?
                (NetConstant.ROOT_URL + PlayerService.result.file_url) :
                PlayerService.result.qiniu_url);
    }

    public Result getResult() {
        return result;
    }

    @NonNull
    public LMediaPlayer getPlayer() {
        return player;
    }

    public boolean isPreparing() {
        return isPreparing;
    }

    public boolean isPlaying() {
        if (player.mediaPlayer == null) {
            player.mediaPlayer = new MediaPlayer();
            return false;
        } else {
            boolean isMediaPlaying;
            try {
                isMediaPlaying = getPlayer().mediaPlayer.isPlaying();
            } catch (IllegalStateException ignore) {
                isMediaPlaying = false;
            }
            return isMediaPlaying || isPreparing;
        }
    }

    public long getDuration() {
        if (player.mediaPlayer == null) {
            return 0;
        }
        return player.mediaPlayer.getDuration();
    }

    /**
     * Created by london on 15/11/11.
     * 播放器控制器
     */
    public class PlayerBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }
}
