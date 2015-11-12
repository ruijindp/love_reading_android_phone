package com.ljmob.lovereadingphone.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ljmob.lovereadingphone.MainActivity;
import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.ReadingActivity;
import com.ljmob.lovereadingphone.service.PlayerService;
import com.ljmob.lovereadingphone.view.MarqueeTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by london on 15/10/27.
 * 迷你播放器
 */
public class PlayerBarFragment extends Fragment
        implements ServiceConnection {
    View rootView;
    @Bind(R.id.view_player_tvTitleReader)
    MarqueeTextView viewPlayerTvTitleReader;
    @Bind(R.id.view_player_imgPlay)
    ImageView viewPlayerImgPlay;
    @Bind(R.id.view_player_imgClose)
    ImageView viewPlayerImgClose;

    private PlayerService playerService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.view_player, container, false);
        }
        ButterKnife.bind(this, rootView);
        getActivity().bindService(new Intent(getContext(), PlayerService.class),
                this, Context.BIND_AUTO_CREATE);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (playerService == null) {
            return;
        }
        if (playerService.getResult() != null) {
            viewPlayerTvTitleReader.setText(String.format("%s - %s",
                    playerService.getResult().article.title, playerService.getResult().user.name));
        }
        if (playerService.getPlayer().mediaPlayer == null) {
            viewPlayerImgPlay.setImageResource(R.mipmap.icon_mini_stop);
            return;
        }
        if (playerService.getPlayer().mediaPlayer.isPlaying()) {
            viewPlayerImgPlay.setImageResource(R.mipmap.icon_mini_zanting);
        } else {
            viewPlayerImgPlay.setImageResource(R.mipmap.icon_mini_stop);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        getActivity().unbindService(this);
    }

    @OnClick(R.id.view_player_imgPlay)
    protected void playOrPause() {
        if (playerService == null || playerService.getResult() == null) {
            return;
        }
        if (playerService.getPlayer().mediaPlayer.isPlaying()) {
            playerService.getPlayer().pause();
            viewPlayerImgPlay.setImageResource(R.mipmap.icon_mini_stop);
        } else {
            playerService.getPlayer().play();
            viewPlayerImgPlay.setImageResource(R.mipmap.icon_mini_zanting);
        }
    }

    @OnClick(R.id.view_player_imgClose)
    protected void closePlayer() {
        ((MainActivity) getActivity()).closePlayerBar();
        if (playerService == null || playerService.getResult() == null) {
            return;
        }
        playerService.getPlayer().stop();
    }

    @OnClick(R.id.view_player_root)
    protected void goReadingActivity() {
        if (playerService == null || playerService.getResult() == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), ReadingActivity.class);
        intent.putExtra("result", playerService.getResult());
        getActivity().startActivity(intent);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        playerService = ((PlayerService.PlayerBinder) service).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if (playerService == null) {
            return;
        }
        playerService.getPlayer().stop();
        playerService = null;
    }
}
