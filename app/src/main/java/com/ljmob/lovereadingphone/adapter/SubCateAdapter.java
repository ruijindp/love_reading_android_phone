package com.ljmob.lovereadingphone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ljmob.lovereadingphone.R;
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
    public SubCateAdapter(List<? extends LEntity> lEntities) {
        super(lEntities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sub_cate, parent, false);
            convertView.setTag(new ViewHolder(convertView));
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
