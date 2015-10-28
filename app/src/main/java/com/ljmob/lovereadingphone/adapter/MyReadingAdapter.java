package com.ljmob.lovereadingphone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.context.MyApplication;
import com.ljmob.lovereadingphone.entity.User;
import com.ljmob.lovereadingphone.fragment.MyReadingFragment;
import com.londonx.lutil.adapter.LAdapter;
import com.londonx.lutil.entity.LEntity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by london on 15/10/27.
 * 我的朗读
 */
public class MyReadingAdapter extends LAdapter {
    MyReadingFragment.Type type;

    public MyReadingAdapter(List<? extends LEntity> lEntities, MyReadingFragment.Type type) {
        super(lEntities);
        this.type = type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_reading, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        switch (type) {
            case notRated:
                if (MyApplication.currentUser.role == User.Role.student) {//学生未评分
                    holder.itemMyReadingLnTeacher.setVisibility(View.GONE);
                    holder.itemMyReadingLnStudent.setVisibility(View.VISIBLE);
                    holder.itemMyRbRating.setVisibility(View.INVISIBLE);
                } else {//教师未评分
                    holder.itemMyReadingLnTeacher.setVisibility(View.VISIBLE);
                    holder.itemMyReadingLnStudent.setVisibility(View.GONE);
                    holder.itemMyReadingViewAnchorRated.setVisibility(View.GONE);
                    holder.itemMyReadingViewAnchorNotRated.setVisibility(View.VISIBLE);
                    holder.itemMyRbRatingTeacher.setVisibility(View.GONE);
                }
                break;
            case rated:
                if (MyApplication.currentUser.role == User.Role.student) {//学生已评分
                    holder.itemMyReadingLnTeacher.setVisibility(View.GONE);
                    holder.itemMyReadingLnStudent.setVisibility(View.VISIBLE);
                    holder.itemMyRbRating.setVisibility(View.VISIBLE);
                } else {//教师已评分
                    holder.itemMyReadingLnTeacher.setVisibility(View.VISIBLE);
                    holder.itemMyReadingLnStudent.setVisibility(View.GONE);
                    holder.itemMyReadingViewAnchorRated.setVisibility(View.VISIBLE);
                    holder.itemMyReadingViewAnchorNotRated.setVisibility(View.GONE);
                    holder.itemMyRbRatingTeacher.setVisibility(View.VISIBLE);
                }
                break;
        }
        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_my_reading.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.item_my_reading_imgCover)
        ImageView itemMyReadingImgCover;
        @Bind(R.id.item_my_reading_tvTitle)
        TextView itemMyReadingTvTitle;
        @Bind(R.id.item_my_reading_tvTime)
        TextView itemMyReadingTvTime;
        @Bind(R.id.item_my_rbRating)
        RatingBar itemMyRbRating;
        @Bind(R.id.item_my_reading_lnStudent)
        LinearLayout itemMyReadingLnStudent;
        @Bind(R.id.item_my_rbRatingTeacher)
        RatingBar itemMyRbRatingTeacher;
        @Bind(R.id.item_my_reading_viewAnchorRated)
        View itemMyReadingViewAnchorRated;
        @Bind(R.id.item_my_reading_tvReader)
        TextView itemMyReadingTvReader;
        @Bind(R.id.item_my_reading_viewAnchorNotRated)
        View itemMyReadingViewAnchorNotRated;
        @Bind(R.id.item_my_reading_tvTimeTeacher)
        TextView itemMyReadingTvTimeTeacher;
        @Bind(R.id.item_my_reading_lnTeacher)
        LinearLayout itemMyReadingLnTeacher;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
