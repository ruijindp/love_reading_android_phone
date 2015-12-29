package com.happysong.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.happysong.android.R;
import com.happysong.android.context.MyApplication;
import com.happysong.android.entity.Result;
import com.happysong.android.entity.TeamClass;
import com.happysong.android.entity.User;
import com.happysong.android.service.PlayerService;
import com.happysong.android.util.SimpleImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by london on 15/10/28.
 * 已听历史
 */
public class ListenedAdapter extends RecyclerView.Adapter {
    List<Result> results;
    OnResultClickListener onResultClickListener;

    private PlayerService playerService;

    public ListenedAdapter(List<Result> results, OnResultClickListener onResultClickListener) {
        this.results = results;
        this.onResultClickListener = onResultClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_listened, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Result result = results.get(position);
        ViewHolder holder = ((ViewHolder) viewHolder);
        holder.setPosition(position);

        SimpleImageLoader.displayImage(result.article.qiniu_url == null ?
                        result.article.cover_img.cover_img.small.url :
                        result.article.qiniu_url,
                holder.itemListenedImgCover);
        holder.itemListenedTvTitle.setText(result.article.title);
        if (result.score.size() == 0) {
            holder.itemListenedRbRating.setVisibility(View.INVISIBLE);
        } else {
            holder.itemListenedRbRating.setVisibility(View.VISIBLE);
            holder.itemListenedRbRating.setRating(result.score.get(0).score);
        }
        holder.itemListenedTvUser.setText(result.user.name);
        if (MyApplication.currentUser.role == User.Role.student) {
            if (result.user.team_classes.get(0).school.id
                    == MyApplication.currentUser.team_classes.get(0).school.id) {
                TeamClass t = result.user.team_classes.get(0);
                holder.itemListenedTvSchool.setText(String.format("%s%s", t.grade.name, t.name));
            }
        } else {
            holder.itemListenedTvSchool.setText(result.user.team_classes.get(0).school.name);
        }
        holder.itemListenedTvLikeCount.setText(String.format("%d", result.votes));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public void setNewData(List<Result> results) {
        this.results = results;
        notifyDataSetChanged();
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public PlayerService getPlayerService() {
        return playerService;
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
        @Bind(R.id.item_listened_tvTitle)
        TextView itemListenedTvTitle;
        @Bind(R.id.item_listened_rbRating)
        RatingBar itemListenedRbRating;
        @Bind(R.id.item_listened_tvUser)
        TextView itemListenedTvUser;
        @Bind(R.id.item_listened_tvSchool)
        TextView itemListenedTvSchool;
        @Bind(R.id.item_listened_tvLikeCount)
        TextView itemListenedTvLikeCount;

        int position = -1;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setPosition(int position) {
            this.position = position;
        }

        @OnClick(R.id.item_listened_viewRoot)
        protected void onItemClick() {
            if (position == -1) {
                return;
            }
            onResultClickListener.onResultClick(position);
        }

        @OnClick(R.id.item_listened_imgPlay)
        protected void play() {
            if (playerService != null) {
                playerService.setResult(results.get(position));
            }
        }
    }

    public interface OnResultClickListener {
        void onResultClick(int position);
    }
}
