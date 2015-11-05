package com.ljmob.lovereadingphone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljmob.lovereadingphone.R;
import com.londonx.lutil.adapter.LAdapter;
import com.londonx.lutil.entity.LEntity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by london on 15/10/26.
 * 推荐朗读
 */
public class RankAdapter extends LAdapter {

    public RankAdapter(List<? extends LEntity> lEntities) {
        super(lEntities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_rank, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.itemRankTvRank.setText(String.format("%d", position + 1));
        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_rank.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.item_rank_tvRank)
        TextView itemRankTvRank;
        @Bind(R.id.item_rank_imgCover)
        ImageView itemRankImgCover;
        @Bind(R.id.item_rank_tvTitle)
        TextView itemRankTvTitle;
        @Bind(R.id.item_rank_tvFeeling)
        TextView itemRankTvFeeling;
        @Bind(R.id.item_rank_tvUser)
        TextView itemRankTvUser;
        @Bind(R.id.item_rank_tvSchool)
        TextView itemRankTvSchool;
        @Bind(R.id.item_rank_tvPraiseCount)
        TextView itemRankTvPraiseCount;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
