package com.ljmob.lovereadingphone.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.entity.Category;
import com.ljmob.lovereadingphone.entity.Edition;
import com.ljmob.lovereadingphone.entity.Grade;
import com.ljmob.lovereadingphone.entity.Subject;
import com.ljmob.lovereadingphone.view.SimpleStringPopup;
import com.ljmob.lovereadingphone.view.UnScrollableGridView;
import com.londonx.lutil.adapter.LAdapter;
import com.londonx.lutil.entity.LEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by london on 15/12/2.
 * 文章分类
 */
public class Category2Adapter extends LAdapter {
    private Subject.Type selectedType = null;
    private Subject selectedSubject = null;
    private HashMap<Integer, Edition> subjectIdWithEdition;
    private Grade selectedGrade = null;
    private Category selectedCategory = null;

    Drawable selectedSubjectBg;
    int horizontalSpace4;
    int horizontalSpace3;
    int horizontalSpace2;

    private Context context;
    private List<Integer> divIndexes;
    private Category2SelectedListener listener;

    public Category2Adapter(List<? extends LEntity> lEntities, Category2SelectedListener listener) {
        super(lEntities);
        this.listener = listener;
        subjectIdWithEdition = new HashMap<>();
        divIndexes = new ArrayList<>();
        Subject.Type lastType = null;
        for (int i = 0; i < lEntities.size(); i++) {
            Subject subject = (Subject) lEntities.get(i);
            if (subject.type == lastType) {
                continue;
            }
            lastType = subject.type;
            divIndexes.add(i);
        }
    }

    public void setSelection(Subject.Type selectedType,
                             Subject selectedSubject,
                             Edition selectedEdition,
                             Grade selectedGrade,
                             Category selectedCategory) {
        this.selectedType = selectedType;
        this.selectedSubject = selectedSubject;
        this.selectedGrade = selectedGrade;
        this.selectedCategory = selectedCategory;
        if (this.selectedSubject != null) {
            subjectIdWithEdition.put(this.selectedSubject.id, selectedEdition);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (context == null) {
            context = parent.getContext();

            selectedSubjectBg = ContextCompat.getDrawable(context, R.color.colorPrimary);
            selectedSubjectBg = ContextCompat.getDrawable(context, R.drawable.shape_primary_stroke);

            horizontalSpace4 = context.getResources()
                    .getDimensionPixelSize(R.dimen.horizontal_space_4);
            horizontalSpace3 = context.getResources()
                    .getDimensionPixelSize(R.dimen.horizontal_space_3);
            horizontalSpace2 = context.getResources()
                    .getDimensionPixelSize(R.dimen.horizontal_space_2);
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_category2, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ((ViewHolder) convertView.getTag()).setSubject(position, (Subject) getItem(position));
        return convertView;
    }

    private boolean isContainSubject(Subject subject) {
        return subject.type != Subject.Type.category && subjectIdWithEdition.containsKey(subject.id);
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_category2.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    class ViewHolder {
        @Bind(R.id.item_cate2_tvLibTitle)
        TextView itemCate2TvLibTitle;
        @Bind(R.id.item_cate2_lnLibTitle)
        LinearLayout itemCate2LnLibTitle;
        @Bind(R.id.item_cate2_tvSubject)
        TextView itemCate2TvSubject;
        @Bind(R.id.item_cate2_tvCate)
        TextView itemCate2TvCate;
        @Bind(R.id.item_cate2_flCate)
        FrameLayout itemCate2FlCate;
        @Bind(R.id.item_cate2_gvItems)
        UnScrollableGridView itemCate2GvItems;
        @Bind(R.id.item_cate2_lnEdition)
        LinearLayout itemCate2LnEdition;
        @Bind(R.id.item_cate2_tvEdition)
        TextView itemCate2TvEdition;

        SimpleStringPopup editionPop;

        Subject subject;
        Edition selectedEdition;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void setSubject(int position, Subject subject) {
            this.subject = subject;
            if (itemCate2TvLibTitle == null) {
                return;
            }
            if (divIndexes.contains(position)) {
                itemCate2LnLibTitle.setVisibility(View.VISIBLE);
            } else {
                itemCate2LnLibTitle.setVisibility(View.GONE);
            }

            switch (subject.type) {
                case subject:
                    itemCate2TvLibTitle.setText(R.string.library_in_class);
                    itemCate2GvItems.setHorizontalSpacing(horizontalSpace4);
                    itemCate2GvItems.setNumColumns(4);

                    itemCate2TvSubject.setVisibility(View.VISIBLE);
                    itemCate2FlCate.setVisibility(View.GONE);

                    CateItemAdapter cateItemGradeAdapter = new CateItemAdapter(subject.grades);
                    cateItemGradeAdapter.setSelection(selectedGrade);
                    itemCate2GvItems.setAdapter(cateItemGradeAdapter);

                    if (selectedSubject != null && selectedSubject.id == subject.id && selectedGrade == null) {
                        itemCate2TvSubject.setBackgroundResource(R.color.colorPrimary);
                        itemCate2TvSubject.setTextColor(Color.WHITE);
                    } else {
                        if (selectedSubject == null && selectedGrade == null && selectedCategory == null) {
                            if (subject.id == 1) {
                                itemCate2TvSubject.setBackgroundResource(R.color.colorPrimary);
                                itemCate2TvSubject.setTextColor(Color.WHITE);
                            } else {
                                itemCate2TvSubject.setBackgroundResource(R.drawable.shape_primary_stroke);
                                itemCate2TvSubject.setTextColor(Color.BLACK);
                            }
                        }
                    }

                    if (isContainSubject(subject)) {
                        selectedEdition = subjectIdWithEdition.get(subject.id);
                    } else {
                        if (subject.editions.size() == 0) {
                            selectedEdition = null;
                        } else {
                            subjectIdWithEdition.put(subject.id, subject.editions.get(0));
                            selectedEdition = subject.editions.get(0);
                        }
                    }
                    if (selectedEdition != null) {
                        itemCate2TvEdition.setText(selectedEdition.name);
                        itemCate2LnEdition.setVisibility(View.VISIBLE);
                    } else {
                        itemCate2LnEdition.setVisibility(View.INVISIBLE);
                    }
                    break;
                case category:
                    itemCate2TvLibTitle.setText(R.string.library_out_class);
                    if (subject.name.equals("活动")) {
                        itemCate2GvItems.setHorizontalSpacing(horizontalSpace2);
                        itemCate2GvItems.setNumColumns(2);
                    } else {
                        itemCate2GvItems.setHorizontalSpacing(horizontalSpace4);
                        itemCate2GvItems.setNumColumns(3);
                    }

                    itemCate2TvSubject.setVisibility(View.GONE);
                    itemCate2FlCate.setVisibility(View.VISIBLE);
                    itemCate2LnEdition.setVisibility(View.INVISIBLE);

                    CateItemAdapter cateItemAdapter = new CateItemAdapter(subject.cate_items);
                    cateItemAdapter.setSelection(selectedCategory);
                    itemCate2GvItems.setAdapter(cateItemAdapter);
                    break;
            }
            itemCate2TvSubject.setText(subject.name);
            itemCate2TvCate.setText(subject.name);
        }

        @OnItemClick(R.id.item_cate2_gvItems)
        protected void selectCategory(int position) {
            selectedType = subject.type;
            if (subject.type == Subject.Type.subject) {
                selectedSubject = subject;
                selectedGrade = subject.grades.get(position);
                selectedCategory = null;
            } else {
                selectedSubject = null;
                selectedGrade = null;
                selectedCategory = subject.cate_items.get(position);
            }
            if (listener != null) {
                listener.onCategory2Selected(selectedType, selectedSubject, selectedEdition, selectedGrade, selectedCategory);
            }
        }

        @OnClick(R.id.item_cate2_tvSubject)
        protected void selectSubject() {
            selectedType = Subject.Type.subject;
            selectedSubject = subject;
            selectedGrade = null;
            selectedCategory = null;
            if (listener != null) {
                listener.onCategory2Selected(selectedType, selectedSubject, selectedEdition, null, null);
            }
        }

        @OnClick(R.id.item_cate2_lnEdition)
        protected void showEditions() {
            if (subject.editions.size() == 0) {
                return;
            }
            List<String> editionStrings = new ArrayList<>();
            for (Edition e : subject.editions) {
                editionStrings.add(e.name);
            }
            if (editionPop == null) {
                editionPop = new SimpleStringPopup(context, itemCate2LnEdition);
                editionPop.setSimpleStringListener(new EditionPopSelectedListener());
            }
            editionPop.setStrings(editionStrings);
            editionPop.showAsDropDown(itemCate2LnEdition);
        }

        class EditionPopSelectedListener implements SimpleStringPopup.SimpleStringListener {
            @Override
            public void selectStringAt(SimpleStringPopup popup, int index) {
                selectedEdition = subject.editions.get(index);
                subjectIdWithEdition.put(subject.id, selectedEdition);
                itemCate2TvEdition.setText(selectedEdition.name);
            }
        }
    }

    public interface Category2SelectedListener {
        void onCategory2Selected(Subject.Type selectType,
                                 Subject selectedSubject,
                                 Edition selectedEdition,
                                 Grade selectedGrade,
                                 Category selectedCategory);
    }
}
