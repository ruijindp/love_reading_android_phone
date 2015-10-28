package com.ljmob.lovereadingphone.entity;

import com.londonx.lutil.entity.LEntity;

import java.util.List;

/**
 * Created by london on 15/10/28.
 * 文章类型
 */
public class ArticleCategory extends LEntity {

    public List<SubCategory> sub_categories;

    public class SubCategory extends LEntity {

    }
}
