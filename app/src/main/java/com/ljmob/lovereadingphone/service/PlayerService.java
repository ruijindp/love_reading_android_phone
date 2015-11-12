package com.ljmob.lovereadingphone.service;


import android.app.Service;
import android.content.Intent;
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
    private Result result;
    @NonNull
    private LMediaPlayer player = new LMediaPlayer(null, null);

    public void setResult(Result result) {
        this.result = result;
        if (this.result == null) {
            if (player.mediaPlayer.isPlaying()) {
                player.stop();
            }
            return;
        }
        if (player.mediaPlayer.isPlaying()) {
            player.stop();
        }
        player.prepareUrl(NetConstant.ROOT_URL + result.file_url);
        player.play();
    }

    public Result getResult() {
        return result;
    }

    @NonNull
    public LMediaPlayer getPlayer() {
        return player;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayerBinder();
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
