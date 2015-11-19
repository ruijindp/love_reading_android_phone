package com.ljmob.lovereadingphone.service;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ljmob.lovereadingphone.entity.Result;
import com.ljmob.lovereadingphone.net.NetConstant;
import com.londonx.lutil.util.LMediaPlayer;

/**
 * Created by london on 15/11/11.
 * 全局播放器
 */
public class PlayerService extends Service {
    public static final String ACTION_RESULT_CHANGED = "com.londonx.lutil.util.LMediaPlayer.ACTION_RESULT_CHANGED";
    private static Result result;
    private static LMediaPlayer player;

    @Override
    public void onCreate() {
        super.onCreate();
        if (player == null) {
            player = new LMediaPlayer(null, null);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayerBinder();
    }

    public void setResult(Result result) {
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
        if (player.mediaPlayer.isPlaying()) {
            player.stop();
        }
        player.prepareUrl(NetConstant.ROOT_URL + PlayerService.result.file_url);
        player.play();
        Intent playerIntent = new Intent(ACTION_RESULT_CHANGED);
        playerIntent.putExtra("result", PlayerService.result);
        sendBroadcast(playerIntent);
    }

    public Result getResult() {
        return result;
    }

    @NonNull
    public LMediaPlayer getPlayer() {
        return player;
    }

    public boolean isPlaying() {
        if (player.mediaPlayer == null) {
            getPlayer().mediaPlayer = new MediaPlayer();
            return false;
        } else {
            boolean isMediaPlaying;
            try {
                isMediaPlaying = getPlayer().mediaPlayer.isPlaying();
            } catch (IllegalStateException ignore) {
                isMediaPlaying = false;
            }
            return isMediaPlaying;
        }
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
