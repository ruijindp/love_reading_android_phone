package com.ljmob.lovereadingphone.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljmob.lovereadingphone.CategoryActivity;
import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.entity.Grade;
import com.ljmob.lovereadingphone.entity.Subject;
import com.ljmob.lovereadingphone.util.SimpleImageLoader;
import com.ljmob.lovereadingphone.view.UnScrollableGridView;
import com.londonx.lutil.adapter.LAdapter;
import com.londonx.lutil.entity.LEntity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Created by london on 15/10/28.
 * 分类
 *
 * @deprecated {@link Category2Adapter}
 */
@Deprecated
public class CategoryAdapter extends LAdapter {
    CategoryActivity categoryActivity;

    public CategoryAdapter(List<? extends LEntity> lEntities, CategoryActivity categoryActivity) {
        super(lEntities);
        this.categoryActivity = categoryActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_category, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.setCategory((Subject) lEntities.get(position));
        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_category.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    class ViewHolder {
        @Bind(R.id.item_category_imgCategory)
        ImageView itemCategoryImgCategory;
        @Bind(R.id.item_category_tvCategory)
        TextView itemCategoryTvCategory;
        @Bind(R.id.item_category_gridSubCate)
        UnScrollableGridView itemCategoryGridSubCate;

        Subject subject;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void setCategory(Subject subject) {
            this.subject = subject;
            SimpleImageLoader.displayImage(subject.img_url.img_url.normal.url,
                    itemCategoryImgCategory);
            itemCategoryTvCategory.setText(subject.name);
            if (categoryActivity.selectedSubject.id == subject.id) {
                itemCategoryGridSubCate.setAdapter(
                        new SubCateAdapter(subject.grades, categoryActivity.selectedGrade));
            } else {
                itemCategoryGridSubCate.setAdapter(
                        new SubCateAdapter(subject.grades, new Grade()));
            }
        }

        @OnItemClick(R.id.item_category_gridSubCate)
        protected void onGradeSelected(int position) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("subject", subject);
            resultIntent.putExtra("grade", subject.grades.get(position));
            categoryActivity.setResult(Activity.RESULT_OK, resultIntent);
            categoryActivity.finish();
        }
    }
}
