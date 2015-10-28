package com.ljmob.lovereadingphone.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.entity.Result;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by london on 15/10/28.
 * 已听历史
 */
public class ListenedAdapter extends RecyclerView.Adapter {
    List<Result> results;

    public ListenedAdapter(List<Result> results) {
        this.results = results;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_listened, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Result result = results.get(position);
        ((ViewHolder) holder).itemListenedTvTitle.setText(String.format("标题标题标题%d", result.id));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_listened.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_listened_imgCover)
        ImageView itemListenedImgCover;
        @Bind(R.id.item_listened_imgPlay)
        ImageView itemListenedImgPlay;
        @Bind(R.id.item_listened_tvTitle)
        TextView itemListenedTvTitle;
        @Bind(R.id.item_listened_rbRating)
        RatingBar itemListenedRbRating;
        @Bind(R.id.item_listened_tvUser)
        TextView itemListenedTvUser;
        @Bind(R.id.item_listened_tvSchool)
        TextView itemListenedTvSchool;
        @Bind(R.id.item_listened_imgLike)
        ImageView itemListenedImgLike;
        @Bind(R.id.item_listened_imgLikeCount)
        TextView itemListenedImgLikeCount;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
