package com.ljmob.lovereadingphone.adapter;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.entity.Grade;
import com.londonx.lutil.adapter.LAdapter;
import com.londonx.lutil.entity.LEntity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by london on 15/10/28.
 * 子类型（语文类的一年级等）
 */
public class SubCateAdapter extends LAdapter {
    Grade selectedGrade;

    public SubCateAdapter(List<? extends LEntity> lEntities, Grade selectedGrade) {
        super(lEntities);
        this.selectedGrade = selectedGrade;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sub_cate, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.itemSubCateTvCate.setText(((Grade) getItem(position)).name);
        if (((Grade) getItem(position)).id == selectedGrade.id) {
            holder.itemSubCateTvCate.setBackgroundResource(R.drawable.selector_btn_primary);
            holder.itemSubCateTvCate.setTextColor(
                    ContextCompat.getColor(parent.getContext(), android.R.color.white));
        } else {
            holder.itemSubCateTvCate.setBackgroundResource(R.drawable.selector_cate_stroke);
            holder.itemSubCateTvCate.setTextColor(
                    ContextCompat.getColor(parent.getContext(), R.color.colorAccent));
        }
        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_sub_cate.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.item_sub_cate_tvCate)
        TextView itemSubCateTvCate;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
