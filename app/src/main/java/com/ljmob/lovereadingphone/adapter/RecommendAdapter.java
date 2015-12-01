package com.ljmob.lovereadingphone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.entity.Result;
import com.ljmob.lovereadingphone.entity.TeamClass;
import com.ljmob.lovereadingphone.service.PlayerService;
import com.ljmob.lovereadingphone.util.SimpleImageLoader;
import com.londonx.lutil.adapter.LAdapter;
import com.londonx.lutil.entity.LEntity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by london on 15/10/26.
 * 推荐朗读
 */
public class RecommendAdapter extends LAdapter {
    private PlayerService playerService;
    private String mySchool = null;

    public RecommendAdapter(List<? extends LEntity> lEntities) {
        super(lEntities);
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public PlayerService getPlayerService() {
        return playerService;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mySchool == null) {
            mySchool = parent.getContext().getString(R.string.my_school);
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recommend, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ((ViewHolder) convertView.getTag()).setResult((Result) lEntities.get(position));
        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_recommend.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    class ViewHolder {
        @Bind(R.id.item_recommend_imgCover)
        ImageView itemRecommendImgCover;
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

        Result result;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void setResult(Result result) {
            this.result = result;
            SimpleImageLoader.displayImage(result.article.cover_img.cover_img.small.url,
                    itemRecommendImgCover);
            itemRecommendTvTitle.setText(result.article.title);
            if (result.score.size() == 0) {
                itemRecommendRating.setRating(0);
            } else {
                itemRecommendRating.setRating(result.score.get(0).score);
            }
            itemRecommendTvUser.setText(result.user.name);
            TeamClass t = result.user.team_classes.get(0);
            if (mySchool.equals(t.school.name)) {
                itemRecommendTvSchool.setText(String.format("%s%s", t.grade.name, t.name));
            } else {
                itemRecommendTvSchool.setText(t.school.name);
            }
            itemRecommendTvPraiseCount.setText(String.format("%d", result.votes));
        }

        @OnClick(R.id.item_recommend_imgPlay)
        protected void play() {
            if (playerService != null) {
                playerService.setResult(result);
            }
        }
    }
}
