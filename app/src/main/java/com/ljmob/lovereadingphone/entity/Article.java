package com.ljmob.lovereadingphone.entity;

import com.londonx.lutil.entity.LEntity;

import java.util.List;

/**
 * Created by london on 15/10/22.
 * 文章
 */
public class Article extends LEntity {
    public String title;
    public String author;
    public CoverImg cover_img;
    public Grade grade;
    public ArticleType article_type;
    public Subject subject;
    public Unit unit;
    public List<Section> sections;
    public int count;

    /**
     * 封面
     */
    public static class CoverImg extends LEntity {
        public Image cover_img;
    }
}
