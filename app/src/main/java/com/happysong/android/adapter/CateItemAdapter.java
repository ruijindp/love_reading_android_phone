package com.happysong.android.adapter;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happysong.android.R;
import com.happysong.android.entity.Category;
import com.happysong.android.entity.Grade;
import com.londonx.lutil.adapter.LAdapter;
import com.londonx.lutil.entity.LEntity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by london on 15/12/3.
 * 分类子项
 */
public class CateItemAdapter extends LAdapter {
    @ColorInt
    private int colorAccent;
    private LEntity selection;

    public CateItemAdapter(List<? extends LEntity> lEntities) {
        super(lEntities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (colorAccent == 0) {
            colorAccent = ContextCompat.getColor(parent.getContext(), R.color.colorAccent);
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sub_cate2, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ((ViewHolder) convertView.getTag()).setEntity(lEntities.get(position));
        return convertView;
    }

    public void setSelection(LEntity selection) {
        this.selection = selection;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_sub_cate2.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    class ViewHolder {
        @Bind(R.id.item_sub_cate2_tv)
        TextView itemSubCate2Tv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void setEntity(LEntity entity) {
            if (selection != null && selection.id == entity.id) {
                itemSubCate2Tv.setTextColor(Color.WHITE);
                itemSubCate2Tv.setBackgroundResource(R.color.colorPrimary);
            } else {
                itemSubCate2Tv.setTextColor(colorAccent);
                itemSubCate2Tv.setBackgroundResource(R.drawable.shape_accent_stroke);
            }
            if (entity instanceof Grade) {
                itemSubCate2Tv.setText(((Grade) entity).name);
            } else if (entity instanceof Category) {
                itemSubCate2Tv.setText(((Category) entity).name);
            } else {
                throw new IllegalStateException("entity must be the child of Grade or Category!!!");
            }
        }
    }
}
