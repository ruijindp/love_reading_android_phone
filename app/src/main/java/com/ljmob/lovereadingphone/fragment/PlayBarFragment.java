package com.ljmob.lovereadingphone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ljmob.lovereadingphone.MainActivity;
import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.ReadingActivity;
import com.ljmob.lovereadingphone.view.MarqueeTextView;
import com.londonx.lutil.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by london on 15/10/27.
 * 迷你播放器
 */
public class PlayBarFragment extends Fragment {
    View rootView;
    @Bind(R.id.view_player_tvTitleReader)
    MarqueeTextView viewPlayerTvTitleReader;
    @Bind(R.id.view_player_imgPlay)
    ImageView viewPlayerImgPlay;
    @Bind(R.id.view_player_imgClose)
    ImageView viewPlayerImgClose;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.view_player, container, false);
        }
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.view_player_imgPlay)
    protected void playOrPause() {
        ToastUtil.show("playOrPause");
    }

    @OnClick(R.id.view_player_imgClose)
    protected void closePlayer() {
        ((MainActivity) getActivity()).closePlayBar();
    }

    @OnClick(R.id.view_player_root)
    protected void goReadingActivity() {
        getActivity().startActivity(new Intent(getActivity(), ReadingActivity.class));
    }
}
