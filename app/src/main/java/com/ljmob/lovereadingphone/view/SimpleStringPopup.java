package com.ljmob.lovereadingphone.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.ljmob.lovereadingphone.R;

import java.util.List;

/**
 * Created by london on 15/7/24.
 * 1 line string popup
 */
public class SimpleStringPopup extends PopupWindow implements AdapterView.OnItemClickListener {
    Context context;
    View rootView;
    ListView pop_category_lv;
    SimpleStringListener simpleStringListener;
    List<String> strings;

    float oneDp;

    public View lastAnchor;

    public SimpleStringPopup(Context context, ViewGroup parent) {
        super(context);
        this.context = context;
        if (rootView == null) {
            rootView = LayoutInflater.from(context).inflate(R.layout.pop_category, parent, false);
            pop_category_lv = (ListView) rootView.findViewById(R.id.pop_category_lv);
            pop_category_lv.setOnItemClickListener(this);
            oneDp = context.getResources().getDisplayMetrics().density;
            setOutsideTouchable(true);
            setFocusable(true);
            setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);//lv margin is 8dp
        }
        setContentView(rootView);
    }

    @Override
    public void showAsDropDown(@NonNull View anchor) {
        setWidth((int) (anchor.getWidth() + 16 * oneDp));
        lastAnchor = anchor;
        super.showAsDropDown(anchor, (int) (-8 * oneDp), (int) (-8 * oneDp) - anchor.getHeight());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();
        if (simpleStringListener != null) {
            simpleStringListener.selectStringAt(this, position);
        }
    }

    public void setSimpleStringListener(SimpleStringListener simpleStringListener) {
        this.simpleStringListener = simpleStringListener;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
        pop_category_lv.setAdapter(new ArrayAdapter<>(context, R.layout.item_simple_string, strings));
        int height = (int) ((strings.size() * 40 + 24) * oneDp);//lv margin is 8dp
        setHeight(height > 320 * oneDp ? (int) (320 * oneDp) : height);
    }

    public interface SimpleStringListener {
        void selectStringAt(SimpleStringPopup popup, int index);
    }
}
