package com.happysong.android.adapter;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.happysong.android.R;
import com.happysong.android.entity.Result;
import com.happysong.android.entity.TeamClass;
import com.happysong.android.util.SimpleImageLoader;
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
        holder.setResult((Result) lEntities.get(position));
        if (position < 3) {
            holder.itemRankTvRank.setTextColor(
                    ContextCompat.getColor(convertView.getContext(), R.color.colorPrimary));
        } else {
            holder.itemRankTvRank.setTextColor(
                    ContextCompat.getColor(convertView.getContext(), R.color.div_tab));
        }
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

        private String mySchool = null;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            if (mySchool == null) {
                mySchool = view.getContext().getString(R.string.my_school);
            }
        }

        public void setResult(Result result) {
            SimpleImageLoader.displayImage(result.article.cover_img.cover_img.small.url,
                    itemRankImgCover);
            itemRankTvTitle.setText(result.article.title);
            itemRankTvFeeling.setText(result.feeling);
            itemRankTvUser.setText(result.user.name);
            TeamClass t = result.user.team_classes.get(0);
            if (mySchool.equals(t.school.name)) {
                itemRankTvSchool.setText(String.format("%s%s", t.grade.name, t.name));
            } else {
                itemRankTvSchool.setText(t.school.name);
            }
            itemRankTvPraiseCount.setText(String.format("%d", result.votes));
        }
    }
}
