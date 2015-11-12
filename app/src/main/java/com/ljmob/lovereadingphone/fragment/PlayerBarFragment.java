package com.ljmob.lovereadingphone.fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.ReadingActivity;
import com.ljmob.lovereadingphone.entity.Result;
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
    private PlayerFragmentReceiver receiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.view_player, container, false);
        }
        ButterKnife.bind(this, rootView);
        getActivity().bindService(new Intent(getContext(), PlayerService.class),
                this, Context.BIND_AUTO_CREATE);
        receiver = new PlayerFragmentReceiver();
        IntentFilter filter = new IntentFilter(PlayerService.ACTION_RESULT_CHANGED);
        getActivity().registerReceiver(receiver, filter);
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
            viewPlayerImgPlay.setImageResource(R.mipmap.icon_mini_play);
            return;
        }
        if (playerService.isPlaying()) {
            viewPlayerImgPlay.setImageResource(R.mipmap.icon_mini_pause);
        } else {
            viewPlayerImgPlay.setImageResource(R.mipmap.icon_mini_play);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        getActivity().unbindService(this);
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        playerService = ((PlayerService.PlayerBinder) service).getService();
        if (playerService.getResult() == null) {
            if (rootView.getVisibility() == View.VISIBLE) {
                hideView(false);
            }
            return;
        }
        viewPlayerTvTitleReader.setText(String.format("%s - %s",
                playerService.getResult().article.title,
                playerService.getResult().user.name));
        showView();
        if (playerService.isPlaying()) {
            viewPlayerImgPlay.setImageResource(R.mipmap.icon_mini_pause);
        } else {
            viewPlayerImgPlay.setImageResource(R.mipmap.icon_mini_play);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if (playerService == null) {
            return;
        }
        playerService.getPlayer().stop();
        playerService = null;
    }

    @OnClick(R.id.view_player_imgPlay)
    protected void playOrPause() {
        if (playerService == null || playerService.getResult() == null) {
            return;
        }
        if (playerService.isPlaying()) {
            playerService.getPlayer().pause();
            viewPlayerImgPlay.setImageResource(R.mipmap.icon_mini_play);
        } else {
            playerService.getPlayer().play();
            viewPlayerImgPlay.setImageResource(R.mipmap.icon_mini_pause);
        }
    }

    @OnClick(R.id.view_player_imgClose)
    protected void closePlayer() {
        hideView(true);
        if (playerService == null || playerService.getResult() == null) {
            return;
        }
        playerService.getPlayer().stop();
        playerService.setResult(null);
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

    public void hideView(boolean withAnim) {
        if (rootView == null) {
            return;
        }
        if (withAnim) {
            rootView.setVisibility(View.VISIBLE);
            rootView.post(new Runnable() {
                @Override
                public void run() {
                    rootView.animate().translationY(rootView.getHeight())
                            .setInterpolator(new AccelerateInterpolator(2)).start();
                }
            });
        } else {
            rootView.setVisibility(View.INVISIBLE);
        }
    }

    public void showView() {
        if (rootView == null) {
            return;
        }
        rootView.setVisibility(View.VISIBLE);
        rootView.post(new Runnable() {
            @Override
            public void run() {
                rootView.animate().translationY(0)
                        .setInterpolator(new DecelerateInterpolator(2)).setStartDelay(100).start();
            }
        });
    }

    private class PlayerFragmentReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (viewPlayerTvTitleReader == null) {
                return;
            }
            if (intent.getAction().equals(PlayerService.ACTION_RESULT_CHANGED)) {
                Result result = (Result) intent.getSerializableExtra("result");
                viewPlayerTvTitleReader.setText(String.format("%s - %s", result.article.title, result.user.name));
                if (playerService == null) {
                    return;
                }
                showView();
                if (playerService.isPlaying()) {
                    viewPlayerImgPlay.setImageResource(R.mipmap.icon_mini_pause);
                } else {
                    viewPlayerImgPlay.setImageResource(R.mipmap.icon_mini_play);
                }
            }
        }
    }
}
