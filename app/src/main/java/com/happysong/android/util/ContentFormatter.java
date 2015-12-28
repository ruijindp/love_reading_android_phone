package com.happysong.android.util;

import android.view.Gravity;
import android.widget.TextView;

import com.happysong.android.entity.ArticleType;

/**
 * Created by london on 15/11/3.
 * 根据文体格式化内容
 */
public class ContentFormatter {
    public static void formatSection(ArticleType articleType, TextView title, TextView content) {
        if (articleType.name.equals("诗歌")) {
            title.setGravity(Gravity.CENTER_HORIZONTAL);
            content.setGravity(Gravity.CENTER_HORIZONTAL);
        }
    }
}
