package com.ljmob.lovereadingphone.entity;

import com.londonx.lutil.entity.LEntity;

/**
 * Created by london on 15/10/22.
 * 文章
 */
public class Article extends LEntity {
    public String cover_img;
    public String title;
    public String author;
    public String content;

    public int read_count;

    public Type type;

    public class Type extends LEntity {
        public String name;
    }
}
