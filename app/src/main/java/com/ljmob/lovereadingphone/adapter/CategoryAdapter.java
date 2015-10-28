package com.ljmob.lovereadingphone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.entity.ArticleCategory;
import com.ljmob.lovereadingphone.view.UnScrollableGridView;
import com.londonx.lutil.adapter.LAdapter;
import com.londonx.lutil.entity.LEntity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by london on 15/10/28.
 * 分类
 */
public class CategoryAdapter extends LAdapter {
    public CategoryAdapter(List<? extends LEntity> lEntities) {
        super(lEntities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_category, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.setCategory((ArticleCategory) lEntities.get(position));
        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_category.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.item_category_imgCategory)
        ImageView itemCategoryImgCategory;
        @Bind(R.id.item_category_tvCategory)
        TextView itemCategoryTvCategory;
        @Bind(R.id.item_category_gridSubCate)
        UnScrollableGridView itemCategoryGridSubCate;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void setCategory(ArticleCategory category) {
            itemCategoryGridSubCate.setAdapter(new SubCateAdapter(category.sub_categories));
        }
    }
}
