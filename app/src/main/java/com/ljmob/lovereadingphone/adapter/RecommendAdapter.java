package com.ljmob.lovereadingphone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
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
public class RecommendAdapter extends LAdapter {

    public RecommendAdapter(List<? extends LEntity> lEntities) {
        super(lEntities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recommend, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }

        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_recommend.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.item_recommend_imgCover)
        ImageView itemRecommendImgCover;
        @Bind(R.id.item_recommend_imgPlay)
        ImageView itemRecommendImgPlay;
        @Bind(R.id.item_recommend_framePlay)
        FrameLayout itemRecommendFramePlay;
        @Bind(R.id.item_recommend_tvTitle)
        TextView itemRecommendTvTitle;
        @Bind(R.id.item_recommend_rating)
        RatingBar itemRecommendRating;
        @Bind(R.id.item_recommend_tvUser)
        TextView itemRecommendTvUser;
        @Bind(R.id.item_recommend_tvSchool)
        TextView itemRecommendTvSchool;
        @Bind(R.id.item_recommend_tvPraiseCount)
        TextView itemRecommendTvPraiseCount;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
